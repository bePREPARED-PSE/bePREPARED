package edu.kit.pse.beprepared.eventTypes.sensorThingsCreateObservation;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.TimeObject;
import de.fraunhofer.iosb.ilt.sta.model.builder.ObservationBuilder;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class is a {@link RequestRunner} to create sensor events using the SensorThings API on a FROST-Server.
 */
public class SensorThingsCreateObservationRequestRunner extends RequestRunner {

    /**
     * The logger instance used by this {@link RequestRunner}.
     */
    private final Logger log = Logger.getLogger(SensorThingsCreateObservationRequestRunner.class);

    /**
     * Constructor.
     *
     * @param event         the event that should be simulated
     * @param configuration the configuration for the simulation
     */
    public SensorThingsCreateObservationRequestRunner(Event event, Configuration configuration) {
        super(event, configuration);
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
    public ExecutionReport call() {

        log.info("Running " + this.toString());

        if (!(this.event instanceof SensorThingsCreateObservationEvent)) {
            log.error("Bad event type: " + this.event.getClass().getName());
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, null, System.currentTimeMillis());
        }

        SensorThingsCreateObservationEvent event = (SensorThingsCreateObservationEvent) this.event;

        SensorThingsService service;
        try {

            service = new SensorThingsService(URI.create(this.configuration.getPropertyValue("frostServerUrl")
                    .toString()));
            Observation observation = ObservationBuilder.builder()
                    .phenomenonTime(new TimeObject(ZonedDateTime.ofInstant(Instant.ofEpochMilli(getAbsoluteEventTime()),
                            ZoneId.of("UTC"))))
                    .result(event.getResult())
                    .build();

            service.datastreams().find(event.getDataStreamId()).observations()
                    .create(observation);

            return new ExecutionReport(this, ExecutionStatus.COMPLETED_NORMAL, null, System.currentTimeMillis());

        } catch (MalformedURLException | ServiceFailureException e) {
            log.error(e);
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, e, System.currentTimeMillis());
        }

    }
}
