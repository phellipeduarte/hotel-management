import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        Room room = new Room(1, false, 3000.0);
        roomArray[5] = room;
        assertEquals(9, Hotel.countAvailableRooms(roomArray));
    }

    @Test
    void shouldReturnRoomByNumber(){
        Room expectedRoom = new Room(2, false, 3000.0);
        Hotel.hotelOb.deluxeDoubleroom[19] = expectedRoom;
        Room room = Hotel.getRoom(30);

        assertEquals(expectedRoom.getRoomDetails(), room.getRoomDetails());
        Hotel.hotelOb.deluxeDoubleroom[19] = null;
    }

    @Test
    void shouldReturnInvalidRoomNumberSuperiorLimit(){
        try {
            Hotel.getRoom(61);
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("Enter a valid option", exception.getMessage());
        }
    }

    @Test
    void shouldReturnInvalidRoomNumberInferiorLimit(){
        try {
            Hotel.getRoom(0);
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("Enter a valid option", exception.getMessage());
        }
    }
}
