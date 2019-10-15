package edu.kit.pse.beprepared.eventTypes.teamPosition;

import edu.kit.pse.beprepared.json.LocationJson;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import java.util.HashMap;


/**
 * Event: sending the position of a team
 * @author Philipp Hertweck (Fraunhofer IOSB)
 */
public class TeamPositionEvent extends Event {
    private String teamID;
    private double lat;
    private double lon;

    public TeamPositionEvent(EventType eventType, long pointInTime, String teamID, double lat, double lon) {
        super(eventType, pointInTime);
        this.lat = lat;
        this.lon = lon;
        this.teamID = teamID;
    }

    public String getTeamID() {
        return teamID;
    }


    public void setTeamID(String name) {
        this.teamID = name;
    }


    public double getLat() {
        return lat;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }


    public double getLon() {
        return lon;
    }


    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Getter for all the attributes of this event as key-value-pairs.
     *
     * @return all the attributes of this event as key-value-pairs
     */
    @Override
    public HashMap<String, Object> getData() {
        return new HashMap<>() {{
            put(TeamPositionEventType.TEAM_ID_KEY, teamID);
            put("locations", new HashMap<String, Object>() {{
                put(TeamPositionEventType.LOCATION_KEY, new LocationJson("Point", new double[] {lon, lat}));
            }});
        }};
    }

    /**
     * Getter for a {@link String} representation of this event.
     *
     * @return a {@link String} representation of this event
     */
    @Override
    public String toString() {
        return "TeamPositionEvent{" +
                TeamPositionEventType.TEAM_ID_KEY + "='" + teamID + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
