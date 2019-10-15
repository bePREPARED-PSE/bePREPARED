package edu.kit.pse.beprepared.services;

import edu.kit.pse.beprepared.model.*;
import edu.kit.pse.beprepared.services.exceptions.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class manages all {@link Event}-related activities.
 */
@Service
public class EventManagementService {

    /**
     * The {@link EventTypeManager} used to access the different {@link edu.kit.pse.beprepared.model.EventType}s.
     */
    private final EventTypeManager eventTypeManager;
    /**
     * The {@link ScenarioRepository} used to access the different {@link Scenario}s.
     */
    private final ScenarioRepository scenarioRepository;

    /**
     * Constructor.
     */
    @Autowired
    public EventManagementService(final ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
        this.eventTypeManager = EventTypeManager.getInstance();
    }

    /**
     * Get a collection of all the available {@link EventType}s
     *
     * @return a collection of all the available EventTypes
     */
    public Collection<EventType> getEventTypes() {
        return this.eventTypeManager.getEventTypes().values();
    }

    /**
     * Create a new {@link Event} and assign it to a {@link Phase}.
     *
     * @param phaseId       the phase to assign the event to
     * @param eventTypeName the event type of the event to create
     * @param data          the data the event consist of. Must comply with the event type
     * @param pointInTime   the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @param scenarioId    the id of the scenario the phase the event should be added to is assigned to
     * @return the newly created event
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     * @throws PhaseNotFoundException    if no phase with the supplied id can be found
     * @throws IllegalEventTypeException if no {@link EventType} with the supplied typeName exists
     */
    public Event createEvent(final int scenarioId, final int phaseId, final String eventTypeName,
                             final HashMap<String, Object> data, final long pointInTime)
            throws PhaseNotFoundException, IllegalEventTypeException, ScenarioNotFoundException {
        //get the eventType:
        EventType eventType = this.eventTypeManager.getEventTypeByName(eventTypeName);
        if (eventType == null) {
            throw new IllegalEventTypeException("Invalid EventType!");
        }
        //get the phase
        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("scenario with id " + scenarioId + " could not be found in repository");
        }
        Phase phase = scenario.getPhaseById(phaseId);
        if (phase == null) {
            throw new PhaseNotFoundException("Phase with id " + phaseId + " not found!");
        }

        //create the event and add it to the phase
        Event e = eventType.createEvent(pointInTime, data);
        phase.addEvent(e);
        return e;
    }

    /**
     * Edit a specific, existing {@link Event}
     *
     * @param scenarioId  the id of the scenario the phase the event is assigned to is assigned to
     * @param phaseId     the phase the event is in
     * @param eventId     the event to edit
     * @param data        the new data for the event
     * @param pointInTime the point in time of the event relative to the start of the {@link Scenario} in ms.
     * @return {@code true} if the edit was successful, {@code false} otherwise
     * @throws PhaseNotFoundException if no phase with the supplied id can be found
     * @throws EventNotFoundException if no event with the supplied id can be found
     */
    public boolean editEvent(final int scenarioId, final int phaseId, final long eventId,
                             final HashMap<String, Object> data, final long pointInTime)
            throws EventNotFoundException, PhaseNotFoundException, ScenarioNotFoundException {
        //try to get the event
        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("can not find scenario with id " + scenarioId + " in repository");
        }
        Phase phase = scenario.getPhaseById(phaseId);
        if (phase == null) {
            throw new PhaseNotFoundException("Invalid phaseId!");
        }
        Event event = phase.getEventById(eventId);
        if (event == null) {
            throw new EventNotFoundException("Invalid eventId!");
        }

        //Update the event's data
        event.getEventTypeInstance().editEvent(pointInTime, data, event);

        //editing the event has been successful.
        return true;
    }

    /**
     * Reassign a supplied {@link Event} to another {@link Phase} inside the supplied {@link Scenario}.
     *
     * @param scenarioId the id of the scenario both phases are assigned to
     * @param oldPhaseId the id of the phase the event should be unassigned from
     * @param newPhaseId the id of the phase the event should be assigned to
     * @param eventId    the id of the event that should be reassigned
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     * @throws PhaseNotFoundException    if no phase with the supplied id can be found
     * @throws EventNotFoundException    if no event with the supplied id can be found
     */
    public void reassignEvent(final int scenarioId, final int oldPhaseId, final int newPhaseId, final long eventId)
            throws ScenarioNotFoundException, PhaseNotFoundException, EventNotFoundException {

        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("scenario with id " + scenarioId + " can not be found!");
        }
        Phase oldPhase = scenario.getPhaseById(oldPhaseId);
        if (oldPhase == null) {
            throw new PhaseNotFoundException("phase with id " + oldPhaseId + " can not be found in scenario with id "
                    + scenarioId);
        }
        Phase newPhase = scenario.getPhaseById(newPhaseId);
        if (newPhase == null) {
            throw new PhaseNotFoundException("phase with id " + newPhaseId + " can not be found in scenario with id "
                    + scenarioId);
        }
        Event event = oldPhase.removeEvent(eventId);
        if (event == null) {
            throw new EventNotFoundException("event with id " + eventId + " can not be found in phase with id "
                    + oldPhaseId + " in scenario with id " + scenarioId);
        }

        newPhase.addEvent(event);

    }

    /**
     * Delete an {@link Event}.
     *
     * @param scenarioId the scenario the phase the event should be deleted from is assigned to
     * @param phaseId    the phase the event is in
     * @param eventId    the event to delete
     * @throws PhaseNotFoundException if no phase with the supplied id can be found
     */
    public void deleteEvent(final int scenarioId, final int phaseId, final long eventId)
            throws PhaseNotFoundException, ScenarioNotFoundException {
        //get the phase
        Scenario scenario = this.scenarioRepository.getScenario(scenarioId);
        if (scenario == null) {
            throw new ScenarioNotFoundException("scenario with id " + scenarioId + " can not be found in repository");
        }
        Phase phase = scenario.getPhaseById(phaseId);
        if (phase == null) {
            throw new PhaseNotFoundException("Invalid phase ID!");
        }

        phase.removeEvent(eventId);
    }

    /**
     * Create a series of events.
     * <p>
     * If one of the events data fields contains mathematical functions, these are replaced. The function either has to
     * be alone or surrounded by '$' signs.
     *
     * @param numOfEvents         the number of events that should be generated
     * @param eventTypeName       the name of the event type the events should be of
     * @param pointInTimeFunction a mathematical function that describes the point in time of each event
     * @param data                the data of the event
     * @throws IllegalMathExpException   if one of the supplied mathematical expressions is invalid
     * @throws ScenarioNotFoundException if no scenario with the supplied id can be found
     * @throws PhaseNotFoundException    if no phase with the supplied id can be found
     * @throws IllegalEventTypeException if no {@link EventType} with the supplied typeName exists
     */
    public void createEventSeries(int scenarioId, int phaseId, int numOfEvents, String eventTypeName,
                                  String pointInTimeFunction, HashMap<String, Object> data)
            throws IllegalMathExpException, ScenarioNotFoundException, IllegalEventTypeException,
            PhaseNotFoundException {

        Expression pitExp = toValidatedExpression(pointInTimeFunction);

        // create events

        for (int eventCount = 0; eventCount < numOfEvents; eventCount++) {

            pitExp.setVariable("x", eventCount);
            long pointInTime = (long) pitExp.evaluate();
            HashMap<String, Object> eventData = toEvaluatedData(data, eventCount);

            Event event = this.createEvent(scenarioId, phaseId, eventTypeName, eventData, pointInTime);

        }

    }

    /**
     * Helper method that evaluates all mathematical expressions contained by the values in {@code data}.
     *
     * @param data      the hash map whose values should be evaluated
     * @param valueForX the x value
     * @return a {@link HashMap} containing the appropriate values
     * @throws IllegalMathExpException if a nested expression is invalid
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, Object> toEvaluatedData(HashMap<String, Object> data, double valueForX)
            throws IllegalMathExpException {

        HashMap<String, Object> newData = new HashMap<>();

        for (String key : data.keySet()) {

            if (key.equals("files")) {
                newData.put("files", data.get("files"));
                continue;
            } else if (key.equals("locations")) {
                HashMap<String, Object> locations = (HashMap<String, Object>) data.get("locations");
                newData.put("locations", parseLocations(locations, valueForX));
                continue;
            }

            String val = data.get(key).toString();

            // check if the value is a standalone expression
            try {

                Expression exp = toValidatedExpression(val);
                exp.setVariable("x", valueForX);
                newData.put(key, toNumber(exp.evaluate()));
                continue;

            } catch (IllegalMathExpException | UnknownFunctionOrVariableException ignored) {
            }

            // evaluate nested expressions
            String replacedExpressions = evaluateNestedExpressions(val, valueForX);
            newData.put(key, replacedExpressions);

        }

        return newData;
    }

    /**
     * Helper method that parses the locations.
     *
     * @param locations a {@link HashMap} containing the locations
     * @param valForX   the x value for evaluating the expression
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, Object> parseLocations(HashMap<String, Object> locations, double valForX)
            throws IllegalMathExpException {

        HashMap<String, Object> newLocations = new HashMap<>();

        for (String key : locations.keySet()) {

            HashMap<String, Object> oldLocation = (HashMap<String, Object>) locations.get(key);
            HashMap<String, Object> newLocation = new HashMap<>();

            ArrayList<Number> newCoordinates = new ArrayList<>();
            ArrayList<Object> oldCoordinates = (ArrayList<Object>) oldLocation.get("coordinates");

            for (Object oldCoordinate : oldCoordinates) {

                Expression e = toValidatedExpression(oldCoordinate.toString());
                e.setVariable("x", valForX);
                newCoordinates.add(toNumber(e.evaluate()));

            }

            newLocation.put("type", oldLocation.get("type"));
            newLocation.put("coordinates", newCoordinates);

            newLocations.put(key, newLocation);
        }

        return newLocations;
    }

    /**
     * Helper method that evaluates nested expressions in a {@link String}.
     * <p>
     * A nested expression must start and end with a '$' sign.
     *
     * @param origin    the original {@link String}
     * @param valueForX the x value
     * @return the {@link String} but with all nested expressions replaced
     * @throws IllegalMathExpException if a nested expression is invalid
     */
    private String evaluateNestedExpressions(String origin, double valueForX) throws IllegalMathExpException {

        char[] chars = origin.toCharArray();
        StringBuilder expressionRepresentation = new StringBuilder();
        StringBuilder evaluatedString = new StringBuilder();

        boolean isEscaped = false;
        boolean record = false;
        for (char c : chars) {

            if (isEscaped && c != '$') {
                evaluatedString.append('\\');
            }

            if (record && c != '$') {
                expressionRepresentation.append(c);
                continue;
            }

            if (c == '$' && !isEscaped) {
                if (!record) {
                    record = true;
                    continue;
                } else {
                    record = false;
                    Expression nestedExpression = toValidatedExpression(expressionRepresentation.reverse().toString());
                    nestedExpression.setVariable("x", valueForX);
                    evaluatedString.append(toNumber(nestedExpression.evaluate()));
                    continue;
                }
            }

            if (c == '\\') {
                isEscaped = true;
            } else {
                isEscaped = false;
                evaluatedString.append(c);
            }
        }

        return evaluatedString.toString();
    }

    /**
     * Helper method that converts a given mathematical expression to an {@link Expression} object.
     *
     * @param expression the {@link String} representation of the expression that should be converted
     * @return the expression
     * @throws IllegalMathExpException if the supplied expression is not valid
     */
    private Expression toValidatedExpression(String expression) throws IllegalMathExpException {

        Function randomInteger = new Function("randInt", 2) {
            @Override
            public double apply(double... doubles) {
                return ThreadLocalRandom.current().nextInt((int) doubles[0], (int) doubles[1]);
            }
        };
        Function randomDouble = new Function("rand", 2) {
            @Override
            public double apply(double... doubles) {
                return ThreadLocalRandom.current().nextDouble(doubles[0], doubles[1]);
            }
        };

        // validate pit expression

        Expression exp = new ExpressionBuilder(expression)
                .variable("x")
                .functions(randomDouble, randomInteger)
                .build();

        ValidationResult pitExpValidation = exp.validate(false);
        if (!pitExpValidation.isValid()) {
            throw new IllegalMathExpException("illegal math expression: " + expression,
                    pitExpValidation.getErrors());
        }

        return exp;
    }

    /**
     * Converts the supplied double value to an int, if possible.
     *
     * @param val the value
     * @return the supplied value as a double or an int, if possible
     */
    private Number toNumber(double val) {

        if (((int) val) == val) {
            return (int) val;
        }

        return val;
    }

}
