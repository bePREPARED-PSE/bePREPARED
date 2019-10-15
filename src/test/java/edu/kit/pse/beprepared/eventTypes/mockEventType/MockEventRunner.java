package edu.kit.pse.beprepared.eventTypes.mockEventType;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;

public class MockEventRunner extends RequestRunner {

    private long executedAt;

    /**
     * Constructor.
     *
     * @param event         the event that should be simulated
     * @param configuration the configuration for the simulation
     */
    public MockEventRunner(Event event, Configuration configuration) {
        super(event, configuration);
        this.executedAt = -1L;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public ExecutionReport call() throws Exception {

        this.executedAt = System.currentTimeMillis();

        return new ExecutionReport(this, ExecutionStatus.COMPLETED_NORMAL, null, this.executedAt);

    }

    /**
     * Getter for {@link this#executedAt}.
     *
     * @return the value of {@link this#executedAt}
     */
    public long getExecutedAt() {
        return executedAt;
    }
}
