import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleroomTest {

    Singleroom singleroom;

    @BeforeEach
    void setUp(){
        singleroom = new Singleroom("Phellipe", "3223", "M");
    }

    @Test
    void shouldGetGuestName(){
        assertEquals("Phellipe", singleroom.getGuestName());
    }
}
