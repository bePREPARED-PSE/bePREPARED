package edu.kit.pse.beprepared.eventTypes.teamPosition;

import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;


public class TeamPositionRequestRunner extends RequestRunner {

    static final String HTTPREQUEST_HEADER_ACCEPT = "Accept";
    static final String HTTPREQUEST_TYPE_JSON = "application/json";
    static final String TEAMPOSITION_PREFIX = "/servlet/is/rest/entry/4554/TeamPosition";

    /**
     * The {@link Logger} instance used by objects of this class.
     */
    private final Logger log = Logger.getLogger(TeamPositionRequestRunner.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();


    /**
     * Constructor.
     *
     * @param event         the {@link Event} this {@link RequestRunner} belongs to
     * @param configuration the {@link Configuration} this {@link RequestRunner} should refer to
     */
    public TeamPositionRequestRunner(TeamPositionEvent event, Configuration configuration) {
        super(event, configuration);
    }


    static String loginPostfix(String user, String key) {
        return "/servlet/is/rest/login?user=" + user + "&key=" + key + "&aspect=doLogin";
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

        if (!(this.event instanceof TeamPositionEvent)) {
            log.error("Bad event type: " + this.event.getClass().getName());
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, null, System.currentTimeMillis());
        }

        String serverBase = this.configuration.getPropertyValue("frostServerUrl").toString();
        TeamPositionEvent event = (TeamPositionEvent) this.event;

        try {
            sendRequest(serverBase, event);
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_NORMAL, null, System.currentTimeMillis());
        } catch (IOException | HttpException e) {
            log.error(e);
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, e, System.currentTimeMillis());
        }
    }


    private void sendRequest(String serverBase, TeamPositionEvent event) throws IOException, HttpException {
        login(serverBase, this.configuration.getPropertyValue("tpUname").toString(),
                this.configuration.getPropertyValue("tpPswd").toString());
        HttpPost httpPost = new HttpPost(serverBase + TEAMPOSITION_PREFIX);

        String json =
                "{\n" + "               \"position\": {\n" + "                              \"latitude\": " + event
                        .getLat() + ",\n" + "                              \"longitude\": " + event.getLon() + "\n"
                        + "               },\n" + "               \"status\": \"Not ready\",\n"
                        + "               \"description\": \"\"\n" + "}\n";
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            httpClient.close();
            throw new HttpException("Request failed with status code: " + statusCode);
        }
        httpClient.close();
    }


    public boolean login(String baseServer, String username, String password) {
        try {
            final HttpGet loginGet = new HttpGet(baseServer + loginPostfix(username, password));
            loginGet.setHeader(HTTPREQUEST_HEADER_ACCEPT, HTTPREQUEST_TYPE_JSON);
            httpClient.execute(loginGet);
            return true;
        } catch (IOException e) {
            log.info(e.getMessage(), e);
            return false;
        }
    }
}
