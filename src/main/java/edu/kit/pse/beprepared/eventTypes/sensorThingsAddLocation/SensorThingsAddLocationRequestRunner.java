package edu.kit.pse.beprepared.eventTypes.sensorThingsAddLocation;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.builder.LocationBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractLocationBuilder;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.simulation.ExecutionReport;
import edu.kit.pse.beprepared.simulation.ExecutionStatus;
import edu.kit.pse.beprepared.simulation.RequestRunner;
import org.apache.log4j.Logger;
import org.geojson.Point;

import java.net.MalformedURLException;
import java.net.URI;

public class SensorThingsAddLocationRequestRunner extends RequestRunner {

    /**
     * The {@link Logger} instance used by objects of this class.
     */
    private final Logger log = Logger.getLogger(SensorThingsAddLocationRequestRunner.class);

    /**
     * Constructor.
     *
     * @param event         the {@link Event} this {@link RequestRunner} belongs to
     * @param configuration the {@link Configuration} this {@link RequestRunner} should refer to
     */
    public SensorThingsAddLocationRequestRunner(SensorThingsAddLocationEvent event, Configuration configuration) {
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

        if (!(this.event instanceof SensorThingsAddLocationEvent)) {
            log.error("Bad event type: " + this.event.getClass().getName());
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, null, System.currentTimeMillis());
        }

        SensorThingsAddLocationEvent event = (SensorThingsAddLocationEvent) this.event;

        SensorThingsService service;
        try {

            service = new SensorThingsService(URI.create(this.configuration.getPropertyValue("frostServerUrl")
                    .toString()));

            Location location = LocationBuilder.builder()
                    .name(event.getName())
                    .description(event.getDescription())
                    .location(new Point(event.getLon(), event.getLat()))
                    .encodingType(AbstractLocationBuilder.ValueCode.GeoJSON)
                    .build();

            service.things().find(event.getThingId()).locations().create(location);

            return new ExecutionReport(this, ExecutionStatus.COMPLETED_NORMAL, null, System.currentTimeMillis());

        } catch (MalformedURLException | ServiceFailureException e) {
            log.error(e);
            return new ExecutionReport(this, ExecutionStatus.COMPLETED_EXCEPTIONALLY, e, System.currentTimeMillis());
        }

    }
}
