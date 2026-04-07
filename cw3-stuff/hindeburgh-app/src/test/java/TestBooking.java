import com.fortytwogroup.model.Booking;
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

    }

    @Test
    void getStudentDetails() {

    }

    @Test
    void generateBookingRecord() {

    }
}
