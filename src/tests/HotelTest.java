import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HotelTest {

    Hotel hotel;

    @BeforeEach
    void setUp(){
        hotel = new Hotel();
    }

    @Test
    void shouldReturnAvailabilty(){
        assertEquals("Number of rooms available: 10", hotel.availability(RoomTypeEnum.LUXURY_DOUBLEROOM));
    }

    @Test
    void shouldCountAvailableRooms(){
        assertEquals(10, hotel.countAvailableRooms(RoomTypeEnum.LUXURY_DOUBLEROOM));
    }

    @Test
    void shouldCountAvailableRoomsExceptAssignedRoom() throws NotAvailable {
        Room expectedRoom = new Room(2, false, 3000.0);
        hotel.allocateRoom(expectedRoom, 9);

        assertEquals(9, hotel.countAvailableRooms(RoomTypeEnum.LUXURY_DOUBLEROOM));
    }

    @Test
    void shouldReturnRoomByNumber() throws NotAvailable {
        Room expectedRoom = new Room(2, false, 3000.0);
        hotel.allocateRoom(expectedRoom, 19);

        Room room = hotel.getRoom(19);

        assertEquals(expectedRoom.getRoomDetails(), room.getRoomDetails());
        hotel.getRooms()[19] = null;
    }

    @Test
    void shouldReturnInvalidRoomNumberSuperiorLimit(){
        try {
            hotel.getRoom(61);
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("Enter a valid option", exception.getMessage());
        }
    }

    @Test
    void shouldReturnInvalidRoomNumberInferiorLimit(){
        try {
            hotel.getRoom(0);
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("Enter a valid option", exception.getMessage());
        }
    }

    @Test
    void shouldReturnIntervalForEveryRoomType(){
        List<Integer> interval = Hotel.getRoomIntervalByRoomType(RoomTypeEnum.LUXURY_DOUBLEROOM);
        assertEquals("[1, 10]", interval.toString());

        interval = Hotel.getRoomIntervalByRoomType(RoomTypeEnum.DELUXE_DOUBLEROOM);
        assertEquals("[11, 30]", interval.toString());

        interval = Hotel.getRoomIntervalByRoomType(RoomTypeEnum.LUXURY_SINGLEROOM);
        assertEquals("[31, 40]", interval.toString());

        interval = Hotel.getRoomIntervalByRoomType(RoomTypeEnum.DELUXE_SINGLEROOM);
        assertEquals("[41, 60]", interval.toString());
    }

    @Test
    void shouldReturnAvailableRooms() throws NotAvailable {
        Guest guest = new Guest("Phellipe", "3223", "M");
        Room room = new Room(guest);

        hotel.allocateRoom(room, 2);

        assertEquals("1, 3, 4, 5, 6, 7, 8, 9, 10.", hotel.getEmptyRooms(1, 10));
    }
}
