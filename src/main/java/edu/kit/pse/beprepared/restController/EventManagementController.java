package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.EventJson;
import edu.kit.pse.beprepared.json.EventSeriesJson;
import edu.kit.pse.beprepared.json.EventTypeJson;
import edu.kit.pse.beprepared.model.Event;
import edu.kit.pse.beprepared.model.EventType;
import edu.kit.pse.beprepared.restController.exceptions.BadRequestException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.EventManagementService;
import edu.kit.pse.beprepared.services.exceptions.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * This is the REST controller for the event management.
 */
@RestController
public class EventManagementController {

    /**
     * The logger used by this class.
     */
    private final Logger log = Logger.getLogger(EventManagementController.class);

    /**
     * The corresponding {@link EventManagementService}.
     */
    private final EventManagementService service;


    /**
     * Constructor.
     *
     * @param service the corresponding {@link EventManagementService}.
     */
    @Autowired
    public EventManagementController(final EventManagementService service) {
        this.service = service;
    }


    /**
     * Mapping for {@code /eventTypes}.
     *
     * @return a {@link Collection<EventTypeJson>} containing all available {@link EventType}s.
     */
    @RequestMapping(value = "/eventTypes", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<EventTypeJson> getEventTypes() {

        log.info("Received request: \"/eventTypes\" Method: GET");

        return service.getEventTypes().stream().map(EventTypeJson::new)
                .sorted(Comparator.comparing(EventTypeJson::getName)).collect(Collectors.toList());
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/events} (POST).
     *
     * @param phaseId    the pathVariable {@code phaseId}
     * @param scenarioId the pathVariable {@code scenarioId}
     * @param eventJson  the requestBody
     * @return a Json representation of the newly generated {@link Event}
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/events", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public @ResponseBody
    EventJson createEvent(@PathVariable int scenarioId, @PathVariable int phaseId, @RequestBody EventJson eventJson) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/" + phaseId
                + "/events\" Method: POST");
        log.debug("Received requestBody: \"" + eventJson.toString() + "\"");

        Event event;
        try {
            event = service.createEvent(scenarioId, phaseId, eventJson.getType(), eventJson.getData(),
                    eventJson.getPointInTime());
        } catch (PhaseNotFoundException e) {
            log.warn("Phase with id " + phaseId + " does not exist!", e);
            throw new NotFoundException(e);
        } catch (IllegalEventTypeException e) {
            log.warn("EventType with typeName \"" + eventJson.getType() + "\" does not exist", e);
            throw new BadRequestException(e);
        } catch (ScenarioNotFoundException e) {
            log.warn("Scenario with id " + scenarioId + " does not exist!", e);
            throw new NotFoundException(e);
        }

        log.debug("Created event: " + event.toString());

        return new EventJson(event);
    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/eventSeries} (POST).
     *
     * @param scenarioId      the id of the scenario containing the phase
     * @param phaseId         the id of the phase
     * @param eventSeriesJson the json representation of the event series that should be created
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/eventSeries", method = RequestMethod.POST,
            consumes = "application/json")
    public void createEventSeries(@PathVariable int scenarioId, @PathVariable int phaseId,
                                  @RequestBody EventSeriesJson eventSeriesJson) {

        log.info("Received request\"/scenarios/" + scenarioId + "/phases/" + phaseId + "/eventSeries\" Method: POST");
        log.debug("Received requestBody: \"" + eventSeriesJson.toString() + "\"");

        try {

            this.service.createEventSeries(scenarioId, phaseId, eventSeriesJson.getNumOfEvents(),
                    eventSeriesJson.getType(), eventSeriesJson.getPointInTimeFunction(),
                    eventSeriesJson.getEventData());

        } catch (ScenarioNotFoundException | PhaseNotFoundException e) {

            log.warn(e);
            throw new NotFoundException(e);
        } catch (IllegalMathExpException | IllegalEventTypeException e) {

            log.warn(e);
            throw new BadRequestException(e);
        }

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/events/{eventId}} (PUT).
     *
     * @param scenarioId the id of the scenario the phase containing the event is assigned to
     * @param phaseId    the phase the event that should be edited is assigned to
     * @param eventId    the event that should be edited
     * @param eventJson  the request body
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/events/{eventId}", method = RequestMethod.PUT,
            consumes = "application/json")
    public void editEvent(@PathVariable int scenarioId, @PathVariable int phaseId, @PathVariable int eventId,
                          @RequestBody EventJson eventJson) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/" + phaseId + "/events/" + eventId
                + "\" Method: PUT");
        log.debug("Received requestBody: \"" + eventJson.toString() + "\"");

        try {

            this.service.editEvent(scenarioId, phaseId, eventId, eventJson.getData(), eventJson.getPointInTime());

        } catch (EventNotFoundException | PhaseNotFoundException | ScenarioNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{oldPhaseId}/events/{eventId}/reassignToPhase/{newPhaseId}}
     * (PATCH).
     *
     * @param scenarioId the scenario the two phases are assigned to
     * @param oldPhaseId the phase the event should be removed from
     * @param eventId    the event that should be reassigned
     * @param newPhaseId the phase the event should be assigned to
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{oldPhaseId}/events/{eventId}/reassignToPhase/{newPhaseId}",
            method = RequestMethod.PATCH)
    public void reassignEvent(@PathVariable int scenarioId, @PathVariable int oldPhaseId, @PathVariable long eventId,
                              @PathVariable int newPhaseId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/" + oldPhaseId + "/events/" + eventId
                + "/reassignToPhase/" + newPhaseId + "\" Method: PATCH");

        try {
            this.service.reassignEvent(scenarioId, oldPhaseId, newPhaseId, eventId);
        } catch (ScenarioNotFoundException | PhaseNotFoundException | EventNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

    /**
     * Mapping for {@code /scenarios/{scenarioId}/phases/{phaseId}/events/{eventId}} (DELETE).
     *
     * @param scenarioId the id of the scenario the phase containing the event to delete is assigned to
     * @param phaseId    the id of the phase the event to delete is assigned to
     * @param eventId    the id of the event to delete
     */
    @RequestMapping(value = "/scenarios/{scenarioId}/phases/{phaseId}/events/{eventId}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable int scenarioId, @PathVariable int phaseId, @PathVariable long eventId) {

        log.info("Received request: \"/scenarios/" + scenarioId + "/phases/" + phaseId + "/events/" + eventId
                + "\" Method: DELETE");

        try {
            this.service.deleteEvent(scenarioId, phaseId, eventId);
        } catch (PhaseNotFoundException | ScenarioNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        }

    }

}
