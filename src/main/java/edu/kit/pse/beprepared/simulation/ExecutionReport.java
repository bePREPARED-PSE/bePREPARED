package edu.kit.pse.beprepared.simulation;

/**
 * This class represents a report of the execution of a {@link RequestRunner}.
 */
public class ExecutionReport {

    /**
     * The {@link RequestRunner} this report belongs to.
     */
    private final RequestRunner runner;
    /**
     * The {@link ExecutionStatus} the corresponding {@link RequestRunner} terminated with.
     */
    private final ExecutionStatus status;
    /**
     * The {@link Throwable} in case the {@link RequestRunner} terminated exceptionally.
     */
    private final Throwable throwable;
    /**
     * The time of the execution.
     */
    private final long pointInTime;


    /**
     * Constructor.
     *
     * @param runner      the {@link RequestRunner} this report belongs to.
     * @param status      the {@link ExecutionStatus} the corresponding {@link RequestRunner} terminated with
     * @param throwable   the {@link Throwable} in case the {@link RequestRunner} terminated exceptionally
     * @param pointInTime the time of the execution
     */
    public ExecutionReport(RequestRunner runner, ExecutionStatus status, Throwable throwable, long pointInTime) {
        this.runner = runner;
        this.status = status;
        this.throwable = throwable;
        this.pointInTime = pointInTime;
    }

    /**
     * Getter for {@link this#runner}.
     *
     * @return the value of {@link this#runner}
     */
    public RequestRunner getRunner() {
        return runner;
    }

    /**
     * Getter for {@link this#status}.
     *
     * @return the value of {@link this#status}
     */
    public ExecutionStatus getStatus() {
        return status;
    }

    /**
     * Getter for {@link this#throwable}.
     *
     * @return the value of {@link this#throwable}
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Getter for {@link this#pointInTime}.
     *
     * @return the value of {@link this#pointInTime}
     */
    public long getPointInTime() {
        return pointInTime;
    }
}
