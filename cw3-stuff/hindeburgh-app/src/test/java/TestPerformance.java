import com.fortytwogroup.model.Booking;
import com.fortytwogroup.model.Event;
import com.fortytwogroup.model.Performance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestPerformance {

    private Performance performance;
    private Event mockEvent;
    private LocalDateTime mockDateTime;

    @BeforeEach
    void setUp() {
        mockEvent = mock(Event.class);
        mockDateTime = mock(LocalDateTime.class);

        ArrayList<String> performers = new ArrayList<>();
        performers.add("Performer1");
        performers.add("Performer2");
        performers.add("Performer3");

        performance = new Performance(
                1234,
                LocalDateTime.of(2026, 2, 2, 14, 0),
                LocalDateTime.of(2026, 2, 8, 14, 0),
                performers,
                "123 Event Street",
                600,
                true,
                false,
                600,
                10.0,
                mockEvent
        );
    }

    @Test
    void checkIfEventIsTicketed_ReturnTrue() {
        when(mockEvent.getIsTicketed()).thenReturn(true);

        boolean result = performance.checkIfEventIsTicketed();

        assertTrue(result, "A valid, ticketed event should return true");
    }

    @Test
    void checkIfEventIsTicketed_ReturnFale() {
        when(mockEvent.getIsTicketed()).thenReturn(false);

        boolean result = performance.checkIfEventIsTicketed();

        assertFalse(result, "A valid, non ticketed event should return false");
    }

    @Test
    void checkIfEventIsTicketed_NullEvent() {
        boolean result = performance.checkIfEventIsTicketed();

        assertFalse(result, "An invalid event should return false");
    }


    @Test
    void checkIfTicketsLeft_returnTrue() {
        boolean result = performance.checkIfTicketsLeft(10);

        assertTrue(result, "A valid tickets left should return true");
    }

    @Test
    void checkIfTicketsLeft_returnFalse() {
        boolean result = performance.checkIfTicketsLeft(9999);

        assertFalse(result, "More tickets wanted that there are available should return false");
    }

    @Test
    void getFinalTicketPrice_zeroSold() {
        performance.setSponsorshipAmountRemaining(3000);

        double finalTicketPrice = performance.getFinalTicketPrice();

        assertEquals(5, finalTicketPrice, "Final ticket price should be 5");
    }

    @Test
    void getFinalTicketPrice_halfSold() {
        performance.setSponsorshipAmountRemaining(3000);

        Booking booking = mock(Booking.class);

        performance.addBooking(booking);
        when(booking.getNumTickets()).thenReturn(300);

        double finalTicketPrice = performance.getFinalTicketPrice();

        assertEquals(0, finalTicketPrice, "Final ticket price should be 5");
    }

    @Test
    void checkHasNotHappenedYet_ReturnTrue() {
        LocalDateTime testDateTime = LocalDateTime.of(2026, 2, 1, 14, 0);

        try (MockedStatic<LocalDateTime> mockedTime = Mockito.mockStatic(LocalDateTime.class)) {

            mockedTime.when(LocalDateTime::now).thenReturn(testDateTime);

            boolean result = performance.checkHasNotHappenedYet();

            assertTrue(result, "If the event has not happened yet, it should return true");
        }
    }

    @Test
    void checkHasNotHappenedYet_ReturnFalse() {
        LocalDateTime testDateTime = LocalDateTime.of(2026, 4, 2, 14, 0);

        try (MockedStatic<LocalDateTime> mockedTime = Mockito.mockStatic(LocalDateTime.class)) {

            mockedTime.when(LocalDateTime::now).thenReturn(testDateTime);

            boolean result = performance.checkHasNotHappenedYet();

            assertFalse(result, "If the event has already happened, it should return false");
        }
    }

    @Test
    void checkCreatedByEP_returnTrue() {
        when(mockEvent.getOrganiserEmail()).thenReturn("ep@ep.com");

        boolean result = performance.checkCreatedByEP("ep@ep.com");

        assertTrue(result,  "Matching emails should return true");
    }

    @Test
    void checkCreatedByEP_returnFalse() {
        when(mockEvent.getOrganiserEmail()).thenReturn("ep@ep.com");

        boolean result = performance.checkCreatedByEP("notep@notep.com");

        assertFalse(result,  "Mismatching emails should return false");
    }

    @Test
    void hasActiveBookings_returnTrue() {
        Booking booking = mock(Booking.class);

        performance.addBooking(booking);
        when(booking.getNumTickets()).thenReturn(1);

        boolean result = performance.hasActiveBookings();

        assertTrue(result, "A valid event with active bookings should return true");
    }

    @Test
    void hasActiveBookings_NoBookings_returnFalse() {
        boolean result = performance.hasActiveBookings();

        assertFalse(result, "If there have been no bookings, it should return false");
    }

    @Test
    void hasActiveBookings_performanceCancelled_returnFalse() {
        performance.cancel();
        boolean result = performance.hasActiveBookings();

        assertFalse(result, "If there have been no bookings, it should return false");
    }


}