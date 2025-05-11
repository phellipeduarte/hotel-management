import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleroomTest {

    Doubleroom doubleroom;

    @BeforeEach
    void setUp(){
        doubleroom = new Doubleroom("Phellipe", "Larissa", "3223", "3322", "M", "F");
    }

    @Test
    void shouldGetGuestName(){
        assertEquals("Phellipe - Larissa", doubleroom.getGuestsNames());
    }
}
