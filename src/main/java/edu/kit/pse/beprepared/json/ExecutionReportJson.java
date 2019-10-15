package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;

/**
 * JSON representation of an {@link edu.kit.pse.beprepared.simulation.ExecutionReport}.
 */
public class ExecutionReportJson {

    private RequestRunnerJson requestRunner;
    private ExecutionStatus executionStatus;
    private String throwable;
    private long pointInTime;

    /**
     * Constructor.
     *
     * @param requestRunner   a JSON representation of the {@link RequestRunner} this report belongs to
     * @param executionStatus the status of the request runner
     * @param throwable       the throwable that may have occured during the execution
     */
    @JsonCreator
    public ExecutionReportJson(@JsonProperty(value = "requestRunner") RequestRunnerJson requestRunner,
                               @JsonProperty(value = "executionStatus") ExecutionStatus executionStatus,
                               @JsonProperty(value = "throwable") String throwable,
                               @JsonProperty(value = "pointInTime") long pointInTime) {
        this.requestRunner = requestRunner;
        this.executionStatus = executionStatus;
        this.throwable = throwable;
        this.pointInTime = pointInTime;
    }

    /**
     * Constructor.
     * <p>
     * Constructs a new {@link ExecutionReportJson} from the supplied {@link ExecutionReport}.
     *
     * @param report the report this Json representation belongs to
     */
    public ExecutionReportJson(ExecutionReport report) {

        this.requestRunner = new RequestRunnerJson(report.getRunner());
        this.executionStatus = report.getStatus();
        if (report.getThrowable() != null) {
            this.throwable = report.getThrowable().getClass().getSimpleName() + ": " + report.getThrowable().getMessage();
        }
        this.pointInTime = report.getPointInTime();

    }

    /**
     * Getter for {@link this#requestRunner}.
     *
     * @return the value of {@link this#requestRunner}
     */
    public RequestRunnerJson getRequestRunner() {
        return requestRunner;
    }

    /**
     * Setter for {@link this#requestRunner}.
     *
     * @param requestRunner the new value for {@link this#requestRunner}
     */
    public void setRequestRunner(RequestRunnerJson requestRunner) {
        this.requestRunner = requestRunner;
    }

    /**
     * Getter for {@link this#executionStatus}.
     *
     * @return the value of {@link this#executionStatus}
     */
    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    /**
     * Setter for {@link this#executionStatus}.
     *
     * @param executionStatus the new value for {@link this#executionStatus}
     */
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    /**
     * Getter for {@link this#throwable}.
     *
     * @return the value of {@link this#throwable}
     */
    public String getThrowable() {
        return throwable;
    }

    /**
     * Setter for {@link this#throwable}.
     *
     * @param throwable the new value for {@link this#throwable}
     */
    public void setThrowable(String throwable) {
        this.throwable = throwable;
    }

    /**
     * Getter for {@link this#pointInTime}.
     *
     * @return the value of {@link this#pointInTime}
     */
    public long getPointInTime() {
        return pointInTime;
    }

    /**
     * Setter for {@link this#pointInTime}.
     *
     * @param pointInTime the new value for {@link this#pointInTime}
     */
    public void setPointInTime(long pointInTime) {
        this.pointInTime = pointInTime;
    }
}
