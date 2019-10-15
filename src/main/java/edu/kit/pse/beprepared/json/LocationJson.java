package edu.kit.pse.beprepared.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a location. It is based on the GeoJSON format.
 */
public class LocationJson {

    /**
     * The type.
     */
    private String type;
    /**
     * The coordinates (lon, lat).
     */
    private double[] coordinates;


    /**
     * Constructor.
     * <p>
     * Constructs a new LocationJSON.
     *
     * @param type        the type
     * @param coordinates the coordinates (lon, lat)
     */
    @JsonCreator
    public LocationJson(@JsonProperty(value = "type") String type,
                        @JsonProperty(value = "coordinates") double[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    /**
     * Getter for {@link this#type}.
     *
     * @return the value of {@link this#type}
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for {@link this#type}.
     *
     * @param type the new value for {@link this#type}
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for {@link this#coordinates}.
     *
     * @return the value of {@link this#coordinates}
     */
    public double[] getCoordinates() {
        return coordinates;
    }

    /**
     * Setter for {@link this#coordinates}.
     *
     * @param coordinates the new value for {@link this#coordinates}
     */
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Convenience method for getting the longitude.
     *
     * @return the first value in {@link this#coordinates}
     */
    public double getLongitude() {

        return this.coordinates[0];
    }

    /**
     * Convenience method for getting the latitude.
     *
     * @return the second value in {@link this#coordinates}
     */
    public double getLatitude() {

        return this.coordinates[1];
    }

}
