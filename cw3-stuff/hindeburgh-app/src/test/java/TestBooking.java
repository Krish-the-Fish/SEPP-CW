import com.fortytwogroup.model.Booking;
import com.fortytwogroup.model.Event;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.enums.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class TestBooking {
    private Booking booking;
    private Student mockStudent;
    private Performance mockPerformance;

    @BeforeEach
    void setUp() {
        mockStudent = mock(Student.class);
        mockPerformance = mock(Performance.class);

        booking = new Booking(344, 10,
                100, mockStudent, mockPerformance);
    }

    @Test
    void cancelByStudent_success() {
        booking.cancelByStudent();

        assertEquals(BookingStatus.CANCELLED_BY_STUDENT, booking.getStatus(),
                "Booking should be cancelled by student");
    }

    @Test
    void cancelPaymentFailed() {
        booking.cancelPaymentFailed();

        assertEquals(BookingStatus.PAYMENT_FAILED, booking.getStatus(),
                "Booking should be cancelled by payment failure");
    }

    @Test
    void cancelByProvider() {
        booking.cancelByProvider();

        assertEquals(BookingStatus.CANCELLED_BY_PROVIDER, booking.getStatus(),
                "Booking should be cancelled by provider");
    }

    @Test
    void cancelByStudent_no_overwrites() {
        booking.cancelByStudent();

        assertEquals(BookingStatus.CANCELLED_BY_STUDENT, booking.getStatus(),
                "Booking should be cancelled by student");

        booking.cancelByProvider();
        assertNotEquals(BookingStatus.CANCELLED_BY_PROVIDER, booking.getStatus(),
                "Booking status should not be overwritten");
        booking.cancelPaymentFailed();
        assertNotEquals(BookingStatus.PAYMENT_FAILED, booking.getStatus(),
                "Booking status should not be overwritten");
    }

    @Test
    void cancelPaymentFailed_no_overwrites() {
        booking.cancelPaymentFailed();

        assertEquals(BookingStatus.PAYMENT_FAILED, booking.getStatus(),
                "Booking should be cancelled by payment failure");

        booking.cancelByProvider();
        assertNotEquals(BookingStatus.CANCELLED_BY_PROVIDER, booking.getStatus(),
                "Booking status should not be overwritten");
        booking.cancelByStudent();
        assertNotEquals(BookingStatus.CANCELLED_BY_STUDENT, booking.getStatus(),
                "Booking status should not be overwritten");
    }

    @Test
    void cancelByProvider_no_overwrites() {
        booking.cancelByProvider();

        assertEquals(BookingStatus.CANCELLED_BY_PROVIDER, booking.getStatus(),
                "Booking should be cancelled by provider");

        booking.cancelPaymentFailed();
        assertNotEquals(BookingStatus.PAYMENT_FAILED, booking.getStatus(),
                "Booking status should not be overwritten");
        booking.cancelByStudent();
        assertNotEquals(BookingStatus.CANCELLED_BY_STUDENT, booking.getStatus(),
                "Booking status should not be overwritten");
    }

    @Test
    void checkBookedByStudent() {
        Student student = mock(Student.class);
        when(student.getEmail()).thenReturn("student@school.com");
        when(student.getPhoneNumber()).thenReturn(123456789);
        Performance performance = mock(Performance.class);
        Booking booking = new Booking(1, 2, 20.0, student, performance);

        assertTrue(booking.checkBookedByStudent("student@school.com"));
        assertFalse(booking.checkBookedByStudent("other@school.com"));
    }

    @Test
    void getStudentDetails() {
        Student student = mock(Student.class);
        when(student.getEmail()).thenReturn("student@school.com");
        when(student.getPhoneNumber()).thenReturn(123456789);
        Performance performance = mock(Performance.class);
        Booking booking = new Booking(1, 2, 20.0, student, performance);

        String expected = "Student email: student@school.com\nStudent phone: 123456789";

        assertEquals(expected, booking.getStudentDetails());
    }


    @Test
    void generateBookingRecord() {
        Student student = mock(Student.class);
        when(student.getName()).thenReturn("John");
        when(student.getEmail()).thenReturn("student@school.com");
        when(student.getPhoneNumber()).thenReturn(123456789);

        Event event = mock(Event.class);
        when(event.toString()).thenReturn("EventData");

        Performance performance = mock(Performance.class);
        when(performance.toStringSensitive()).thenReturn("PerformanceData");
        when(performance.getEvent()).thenReturn(event);

        Booking booking = new Booking(1, 2, 20.0, student, performance);

        String expected = "Student name: John\n" +
            "Student email: student@school.com\n" +
            "Student phone number123456789\n" +
            "PerformanceData\n" +
            "EventData\n";

        assertEquals(expected, booking.generateBookingRecord());
    }
}
