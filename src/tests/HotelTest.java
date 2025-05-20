import domain.guest.Guest;
import domain.hotel.Hotel;
import domain.hotel.Reception;
import domain.hotel.Room;
import enums.RoomTypeEnum;
import exceptions.NotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HotelTest {

    Hotel hotel = Hotel.getInstance();

    @BeforeEach
    void deallocateAllRooms(){
        Room[] rooms = hotel.getRooms();

        for(int index = 1; index <= rooms.length; index++){
            hotel.deallocateRoom(index);
        }
    }

    @Test
    void shouldGetRoom() throws NotAvailable {
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "3113", "F");
        List<Guest> guestList = List.of(guest1, guest2);
        Room expectedRoom = new Room(guestList, Reception.getInstance().getRoomOptions().get(0));

        hotel.allocateRoom(expectedRoom, 5);

        Room room = hotel.getRoom(5);

        assertEquals(expectedRoom.getRoomDetails(), room.getRoomDetails());
        assertEquals(expectedRoom.getGuestsNames(), room.getGuestsNames());
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
        List<Integer> interval = hotel.getRoomIntervalByRoomType(RoomTypeEnum.LUXURY_DOUBLEROOM);
        assertEquals("[1, 10]", interval.toString());

        interval = hotel.getRoomIntervalByRoomType(RoomTypeEnum.DELUXE_DOUBLEROOM);
        assertEquals("[11, 30]", interval.toString());

        interval = hotel.getRoomIntervalByRoomType(RoomTypeEnum.LUXURY_SINGLEROOM);
        assertEquals("[31, 40]", interval.toString());

        interval = hotel.getRoomIntervalByRoomType(RoomTypeEnum.DELUXE_SINGLEROOM);
        assertEquals("[41, 60]", interval.toString());
    }

    @Test
    void shouldReturnAvailableRooms() throws NotAvailable {
        Guest guest = new Guest("Phellipe", "3223", "M");
        List<Guest> guestList = List.of(guest);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(3));
        hotel.allocateRoom(room, 2);

        assertEquals("1, 3, 4, 5, 6, 7, 8, 9, 10.", hotel.getEmptyRooms(1, 10));
    }
}
