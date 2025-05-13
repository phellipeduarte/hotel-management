import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HotelTest {

    @Test
    void shouldReturnAvailabilty(){
        assertEquals("Number of rooms available: 10", Hotel.availability(1));
    }

    @Test
    void shouldCountAvailableRooms(){
        Room[] roomArray = new Room[10];
        assertEquals(10, Hotel.countAvailableRooms(roomArray));
    }

    @Test
    void shouldCountAvailableRoomsExceptAssignedRoom(){
        Room[] roomArray = new Room[10];
        Singleroom singleroom = new Singleroom(false, 3000.0);
        roomArray[5] = singleroom;
        assertEquals(9, Hotel.countAvailableRooms(roomArray));
    }
}
