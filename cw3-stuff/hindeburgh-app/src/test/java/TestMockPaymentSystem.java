import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortytwogroup.external.MockPaymentSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TestMockPaymentSystem {

    private MockPaymentSystem mockPaymentSystem;

    @BeforeEach
    void setUp() {
        mockPaymentSystem = new MockPaymentSystem();
    }

    @Test
    void processPayment_Success() {
        boolean result = mockPaymentSystem.processPayment(10, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", 100);

        assertTrue(result);
    }

    @Test
    void processPayment_empty_student_email() {
        boolean result = mockPaymentSystem.processPayment(10, "Event1",
                null, 293846752,
                "epemail@email.com", 100);

        assertFalse(result);
    }

    @Test
    void processPayment_empty_ep_email() {
        boolean result = mockPaymentSystem.processPayment(10, "Event1",
                "email@email.com", 293846752,
                null, 100);

        assertFalse(result);
    }

    @Test
    void processPayment_empty_event_title() {
        boolean result = mockPaymentSystem.processPayment(10, null,
                "email@email.com", 293846752,
                "epemail@email.com", 100);

        assertFalse(result);
    }

    @Test
    void processPayment_invalid_numTickets() {
        boolean result = mockPaymentSystem.processPayment(-999, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", 100);

        assertFalse(result);
    }

    @Test
    void processPayment_invalid_transaction_amount() {
        boolean result = mockPaymentSystem.processPayment(10, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", -999);

        assertFalse(result);
    }

    @Test
    void processRefund_Success() {
        boolean result = mockPaymentSystem.processRefund(10, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", 100, "msg");

        assertTrue(result);
    }

    @Test
    void processRefund_empty_student_email() {
        boolean result = mockPaymentSystem.processRefund(10, "Event1",
                null, 293846752,
                "epemail@email.com", 100, "msg");

        assertFalse(result);
    }

    @Test
    void processRefund_empty_ep_email() {
        boolean result = mockPaymentSystem.processRefund(10, "Event1",
                "email@email.com", 293846752,
                null, 100, "msg");

        assertFalse(result);
    }

    @Test
    void processRefund_empty_event_title() {
        boolean result = mockPaymentSystem.processRefund(10, null,
                "email@email.com", 293846752,
                "epemail@email.com", 100, "msg");

        assertFalse(result);
    }

    @Test
    void processRefund_invalid_numTickets() {
        boolean result = mockPaymentSystem.processRefund(-999, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", 100, "msg");

        assertFalse(result);
    }

    @Test
    void processRefund_invalid_transaction_amount() {
        boolean result = mockPaymentSystem.processRefund(10, "Event1",
                "email@email.com", 293846752,
                "epemail@email.com", -999, "msg");

        assertFalse(result);
    }
}