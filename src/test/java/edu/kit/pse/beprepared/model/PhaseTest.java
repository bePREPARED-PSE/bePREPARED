package edu.kit.pse.beprepared.model;

import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEvent;
import edu.kit.pse.beprepared.eventTypes.mockEventType.MockEventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class PhaseTest {

    @Test
    public void testShiftEventTimesValidInput() {

        // preset

        final int numberOfEvents = 42;
        final Phase phaseUnderTest = new Phase();
        final Collection<Event> events = new LinkedList<>();
        for (int i = 0; i < numberOfEvents; i++) {
            events.add(new MockEvent(new MockEventType(), 0L, "bouncyCats"));
        }
        phaseUnderTest.addEvents(events);

        // run method under test -> shift to the future

        phaseUnderTest.shiftEventTimes(1000L);

        // check result

        assertThat(phaseUnderTest.getAllEvents().stream().map(Event::getPointInTime).collect(Collectors.toList()),
                everyItem(is(1000L)));

        // run method under test -> shift to the past

        phaseUnderTest.shiftEventTimes(-500L);

        // check result

        assertThat(phaseUnderTest.getAllEvents().stream().map(Event::getPointInTime).collect(Collectors.toList()),
                everyItem(is(500L)));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftEventTimesInvalidInput() {

        // preset

        final int numberOfEvents = 42;
        final Phase phaseUnderTest = new Phase();
        final Collection<Event> events = new LinkedList<>();
        for (int i = 0; i < numberOfEvents; i++) {
            events.add(new MockEvent(new MockEventType(), 0L, "bouncyCats"));
        }
        phaseUnderTest.addEvents(events);

        // run method under test -> shift to the future

        phaseUnderTest.shiftEventTimes(-1000L);

    }

}
