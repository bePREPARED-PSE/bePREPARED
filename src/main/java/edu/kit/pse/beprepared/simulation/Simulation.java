package edu.kit.pse.beprepared.simulation;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.Phase;
import edu.kit.pse.beprepared.model.Scenario;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class represents a simulation.
 */
public class Simulation implements Runnable {

    /**
     * The next id.
     */
    private static int nextId = 0;

    /**
     * Time in ms that run() is put to sleep when simulation is paused before checking the simulation state again
     */
    private final long SLEEP_INTERVAL_WHEN_PAUSED = 1000;
    /**
     * The {@link Logger} used by objects of this class.
     */
    private final Logger log = Logger.getLogger(Simulation.class);
    /**
     * The {@link ExecutorService} that runs the {@link RequestRunner}s.
     */
    private final ExecutorService threadPool;
    /**
     * The id of this phase.
     */
    private final int id;
    /**
     * The scenario that is simulated.
     */
    private final Scenario scenario;
    /**
     * The queue of {@link RequestRunner}s.
     */
    private final LinkedList<RequestRunner> runnerQueue;
    /**
     * Threshold used to check whether the speed has changed
     */
    private final double THRESHOLD = .0001;
    /**
     * The start time of the simulation.
     */
    private long simulationStarttime;
    /**
     * The current state of this {@link Simulation}.
     */
    private SimulationState simulationState;
    /**
     * The amount of time the simulation has been on hold.
     */
    private long pausedMillis;
    /**
     * The point in time where the simulation has been put on hold.
     */
    private long putOnHoldOn;
    /**
     * The time the simulation terminated or finished.
     */
    private long simulationEndTime;
    /**
     * List of {@link java.util.concurrent.Future}s for further use.
     */
    private LinkedList<Future<ExecutionReport>> futures;
    /**
     * The configuration this simulation uses.
     */
    private Configuration configuration;
    /**
     * The phases this simulation runs.
     */
    private LinkedList<Phase> selectedPhases;
    /**
     * The current simulation speed.
     */
    private double currentSpeed;
    /**
     * Used for correct time calculation when changing the simulation speed while the simulation is running
     */
    private long speedUpMillis;
    /**
     * Used for correct time calculation when fast forwarding the simulation.
     */
    private long fastForwardedMillis;

    /**
     * The thread the simulation is running in.
     */
    private Thread thread;

    /**
     * Constructor.
     * <p>
     * Initializes all attributes and constructs the {@link RequestRunner}s for all the events in the selected phases.
     *
     * @param scenario       the scenario that is simulated
     * @param selectedPhases the phases that should be simulated
     * @param configuration  the {@link Configuration} of the simulation
     * @param initialSpeed   the initial speed of the simulation
     */
    public Simulation(final Scenario scenario, final Collection<Phase> selectedPhases,
                      final Configuration configuration, final double initialSpeed) {

        this.id = Simulation.nextId++;
        this.scenario = scenario;
        this.runnerQueue = new LinkedList<>();
        this.pausedMillis = 0;
        this.simulationEndTime = 0;
        this.simulationState = SimulationState.INITIALIZED;
        this.configuration = configuration;
        this.selectedPhases = new LinkedList<>(selectedPhases);
        this.simulationStarttime = 0;
        this.futures = new LinkedList<>();
        this.threadPool = Executors.newCachedThreadPool();
        this.currentSpeed = initialSpeed;
        this.speedUpMillis = 0;
        this.fastForwardedMillis = 0;

        final LinkedList<Event> events = new LinkedList<>();
        selectedPhases.forEach(p -> events.addAll(p.getAllEvents()));

        Collections.sort(events);

        events.forEach(e -> runnerQueue.add(e.getEventTypeInstance().getRequestRunnerFor(e, configuration)));

        this.thread = new Thread(this);

        log.info("Created simulation " + id);
        log.debug("Created simulation " + this.toString());
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        while (this.simulationState == SimulationState.RUNNING || this.simulationState == SimulationState.PAUSED) {
            if (this.simulationState == SimulationState.RUNNING) {

                synchronized (this) {
                    if (this.runnerQueue.isEmpty()) {
                        //no elements left in the queue. The {@link Simulation} is finished.
                        this.simulationEndTime = System.currentTimeMillis();
                        this.simulationState = SimulationState.FINISHED;
                        break;
                    }
                }
                RequestRunner runner = this.runnerQueue.removeFirst();
                log.trace("Trying to run " + runner.toString());

                if (this.getPointInTime() < runner.event.getPointInTime()) {
                    log.trace("current point in time " + this.getPointInTime() + " was smaller" +
                            " than event point in time " + runner.event.getPointInTime());
                    this.runnerQueue.addFirst(runner);
                    log.trace("runner queue after re-queueing: " + runnerQueue.toString());
                    try {
                        /*go to sleep till the next event is due. This will be interrupted if fastForward, stop or
                        pause is called or if the speed is changed.*/
                        long pit;
                        if (runner.event.getPointInTime() > (pit = this.getPointInTime())) {
                            Thread.sleep((long) ((runner.event.getPointInTime() - pit) / currentSpeed));
                        }
                    } catch (InterruptedException e) {
                        log.trace(e);
                    }
                    continue;
                }

                futures.add(threadPool.submit(runner));

            } else {
                try {
                    /* Sleep for a while in order to save resources. This will be interrupted if fastForward, stop or
                        pause is called or if the speed is changed.
                     */
                    Thread.sleep(this.SLEEP_INTERVAL_WHEN_PAUSED);
                } catch (InterruptedException e) {
                    log.trace(e);
                }
            }
        }

        this.simulationEndTime = System.currentTimeMillis();
    }

    /**
     * Starts this {@link Simulation}, if it hasn't been started before.
     *
     * @return the state the simulation is in
     */
    public synchronized SimulationState start() {
        if (this.simulationState == SimulationState.INITIALIZED) {
            this.simulationState = SimulationState.RUNNING;
            this.simulationStarttime = System.currentTimeMillis();
            thread.start();
            log.info("Started simulation " + this.id);
        }
        return this.simulationState;
    }

    /**
     * Get the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Toggles the state of the {@link Simulation} between running and paused.
     * Does nothing if the simulation hasn't been started before or has already finished or terminated.
     */
    public synchronized SimulationState togglePause() {

        if (this.simulationState == SimulationState.RUNNING) {
            this.simulationState = SimulationState.PAUSED;
            this.putOnHoldOn = System.currentTimeMillis();
            log.info("Paused simulation " + id);
        } else if (this.simulationState == SimulationState.PAUSED) {
            this.pausedMillis += System.currentTimeMillis() - this.putOnHoldOn;
            this.simulationState = SimulationState.RUNNING;
            log.info("Restarted simulation " + id + " from paused state");
            log.debug("pausedMillis is now " + this.pausedMillis);
            log.debug("currentPointInTime is now " + this.getPointInTime());
        }
        this.thread.interrupt(); //wake the thread up
        return this.simulationState;
    }

    /**
     * Terminates this simulation.
     *
     * @return the simulation state
     */
    public synchronized SimulationState stop() {
        if (this.simulationState != SimulationState.FINISHED) {
            this.simulationState = SimulationState.TERMINATED;
            this.thread.interrupt(); //wake the thread up
        }
        this.simulationEndTime = System.currentTimeMillis();
        return this.simulationState;
    }

    /**
     * Fast-forwards the simulation to a supplied event.
     *
     * @param eventId the id of the event the simulation should be forwarded to
     * @return the simulation state
     * @throws IllegalArgumentException if the supplied eventId does not belong to any event of this simulation
     */
    public synchronized SimulationState fastForwardTo(final long eventId) {

        boolean paused = this.simulationState == SimulationState.PAUSED;

        if (!paused) {
            this.togglePause();
        }

        boolean eventExists = false;
        for (RequestRunner requestRunner : runnerQueue) {
            if (requestRunner.event.getId() == eventId) {
                eventExists = true;
                break;
            }
        }

        if (!eventExists) {
            this.togglePause();
            throw new IllegalArgumentException("there is no event with id " + eventId + " that belongs to this sim");
        }

        while (this.runnerQueue.getFirst().event.getId() != eventId) {

            this.futures.add(this.threadPool.submit(this.runnerQueue.removeFirst()));

        }

        this.fastForwardedMillis += (this.runnerQueue.getFirst().event.getPointInTime() - this.getPointInTime());

        if (!paused) {
            this.togglePause();
        }

        this.thread.interrupt(); //wake the thread up

        return this.simulationState;
    }

    /**
     * Getter for {@link this#scenario}.
     *
     * @return {@link this#scenario}
     */
    public Scenario getScenario() {
        return scenario;
    }

    /**
     * Getter for {@link this#simulationState}
     *
     * @return {@link this#simulationState}
     */
    public SimulationState getSimulationState() {
        return simulationState;
    }

    /**
     * Getter for {@link this#currentSpeed}.
     *
     * @return the value of {@link this#currentSpeed}
     */
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * Getter for {@link this#configuration}
     *
     * @return {@link this#configuration}
     */
    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * Getter for {@link this#selectedPhases}
     *
     * @return {@link this#selectedPhases}
     */
    public Collection<Phase> getSelectedPhases() {
        return new LinkedList<>(this.selectedPhases);
    }

    /**
     * Getter for the current relative point in time.
     *
     * @return the current relative point in time
     */
    public long getPointInTime() {
        switch (this.simulationState) {
            case RUNNING:
                return (long) ((System.currentTimeMillis() - this.simulationStarttime - this.pausedMillis)
                        * this.currentSpeed) + this.speedUpMillis + this.fastForwardedMillis;
            case PAUSED:
                return (long) ((this.putOnHoldOn - this.simulationStarttime - this.pausedMillis)
                        * this.currentSpeed) + this.speedUpMillis + this.fastForwardedMillis;
            case FINISHED:
            case TERMINATED:
                return (long) ((this.simulationEndTime - this.simulationStarttime - this.pausedMillis)
                        * this.currentSpeed) + this.speedUpMillis + this.fastForwardedMillis;
            default:
                return 0;
        }
    }

    /**
     * Call this method when the simulation speed has changed.
     */
    public synchronized void changeSpeed(double newSpeed) {
        if (!(this.simulationState == SimulationState.FINISHED || this.simulationState == SimulationState.TERMINATED)) {

            double oldSpeed = this.currentSpeed;
            if (Math.abs(newSpeed - oldSpeed) > THRESHOLD) {
                //do stuff
                log.debug("Changing speed from " + oldSpeed + " to " + newSpeed + "!");
                long pointInTime = this.getPointInTime();
                log.debug("Before speed change: pointInTime=" + pointInTime + ", speedUpMillis=" + speedUpMillis);
                speedUpMillis = pointInTime - this.fastForwardedMillis
                        - (long) ((pointInTime - this.fastForwardedMillis - this.speedUpMillis) / oldSpeed * newSpeed);
                this.currentSpeed = newSpeed;
                log.debug("After speed change: pointInTime=" + this.getPointInTime() + ", speedUpMillis=" + speedUpMillis);
            }
            this.thread.interrupt();  //wake the thread up
        }
    }

    /**
     * Getter for {@link this#futures}.
     *
     * @return the value of {@link this#futures}
     */
    public Collection<Future<ExecutionReport>> getFutures() {
        return futures;
    }

    /**
     * Getter for a {@link String} representation of this simulation.
     *
     * @return a {@link String} representation of this simulation
     */
    @Override
    public String toString() {
        return "Simulation{" +
                "SLEEP_INTERVAL_WHEN_PAUSED=" + SLEEP_INTERVAL_WHEN_PAUSED +
                ", threadPool=" + threadPool +
                ", id=" + id +
                ", scenario=" + scenario +
                ", runnerQueue=" + runnerQueue +
                ", THRESHOLD=" + THRESHOLD +
                ", simulationStarttime=" + simulationStarttime +
                ", simulationState=" + simulationState +
                ", pausedMillis=" + pausedMillis +
                ", putOnHoldOn=" + putOnHoldOn +
                ", simulationEndTime=" + simulationEndTime +
                ", futures=" + futures +
                ", configuration=" + configuration +
                ", selectedPhases=" + selectedPhases +
                ", currentSpeed=" + currentSpeed +
                ", speedUpMillis=" + speedUpMillis +
                ", fastForwardedMillis=" + fastForwardedMillis +
                ", thread=" + thread +
                '}';
    }
}
