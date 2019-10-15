package edu.kit.pse.beprepared.eventTypes.teamPosition;

import edu.kit.pse.beprepared.model.frontendDescriptors.*;
import org.apache.log4j.Logger;
import edu.kit.pse.beprepared.json.LocationJson;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.model.Scenario;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * This class is an {@link EventType} to sent new team positions.
 *
 * @author Philipp Hertweck (Fraunhofer IOSB)
 */
public class TeamPositionEventType extends EventType {

    public static final String TEAM_ID_KEY  = "teamID";
    public static final String LOCATION_KEY = "geo";
    /**
     * The {@link Logger} instance used by objects of this class.
     */
    private final Logger       log          = Logger.getLogger(
            TeamPositionEventType.class);

    /**
     * Initializes {@link this#typeName}, {@link this#ico}, {@link this#fastViewDescriptor},
     * {@link this#inputFormDescriptor}.
     */
    @Override
    protected void init() {

        this.typeName = "Team position";
        this.ico = "img/geolocation_ico.png";
        this.fastViewDescriptor = new FastViewDescriptor(new LinkedList<>() {{
            add("teamID");
        }});
        this.inputFormDescriptor = new InputFormDescriptor(new LinkedList<>() {{
            add(new InputField(TEAM_ID_KEY, "team35", true));
            add(new GeolocationInputField(LOCATION_KEY, true));
        }});
        this.configurationInputFields = new LinkedList<>(){{
            add(new UrlInputField("frostServerUrl", "https://localhost", true));
            add(new InputField("tpUname", "user", true));
            add(new PasswordInputField("tpPswd", "passwd", true));
        }};

    }

    /**
     * Creates a new {@link Event} with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        map with the data from the JSON Object of the event
     * @return the newly created event
     */
    @Override
    public Event createEvent(long pointInTime, HashMap<String, Object> data) {

        String teamID = (String) data.get(TEAM_ID_KEY);

        LocationJson location = EventType.getLocationFromEventData(LOCATION_KEY, data);

        return new TeamPositionEvent(this, pointInTime, teamID, location.getLatitude(),
                location.getLongitude());
    }

    /**
     * Replaces the data of the given event with the given data.
     *
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param data        the new data for the event
     * @param event       the event that should be edited
     * @return the edited event
     */
    @Override
    public Event editEvent(long pointInTime, HashMap<String, Object> data, Event event) {

        TeamPositionEvent e = (TeamPositionEvent) event;
        e.setPointInTime(pointInTime);
        e.setTeamID((String) data.get(TEAM_ID_KEY));

        LocationJson locationJson = EventType.getLocationFromEventData(LOCATION_KEY, data);

        if (locationJson != null) {
            e.setLat(locationJson.getLatitude());
            e.setLon(locationJson.getLongitude());
        }

        return e;
    }

    /**
     * Returns a {@link RequestRunner} for the given event.
     *
     * @param event         the event for which the runner is required
     * @param configuration the {@link Configuration} this {@link RequestRunner} should use
     * @return the request runner for the event
     */
    @Override
    public RequestRunner getRequestRunnerFor(Event event, Configuration configuration) {
        return new TeamPositionRequestRunner((TeamPositionEvent) event, configuration);
    }
}
