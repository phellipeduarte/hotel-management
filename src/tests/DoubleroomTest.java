import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleroomTest {

    Doubleroom doubleroom;

    @BeforeEach
    void setUp(){
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "2332", "F");
        doubleroom = new Doubleroom(guest1, guest2);
    }

    @Test
    void shouldGetGuestName(){
        assertEquals("Phellipe - Larissa", doubleroom.getGuestsNames());
    }
}
