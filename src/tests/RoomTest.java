import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void shouldReturnGuestName(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        List<Guest> guestList = List.of(guest);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(2));

        assertEquals(room.getGuestsNames(), "Phellipe");
    }


    @Test
    void shouldReturnGuestsNames(){
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "2332", "F");
        List<Guest> guestList = Arrays.asList(guest1, guest2);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(0));

        assertEquals(room.getGuestsNames(), "Phellipe, Larissa.");
    }


    @Test
    void shouldReturnOneGuestRoomDetails(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        List<Guest> guestList = List.of(guest);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(2));
        room.setLuxury(true);
        room.setCharge(4000.0);

        assertEquals("Room capacity: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: R$4000.00\n", room.getRoomDetails());
    }

    @Test
    void shouldReturnTwoGuestsRoomDetails(){
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "2332", "F");
        List<Guest> guestList = List.of(guest1, guest2);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(0));
        room.setLuxury(false);
        room.setCharge(6000.0);

        assertEquals("Room capacity: 2\nAC: No\nFree breakfast: Yes\nCharge per day: R$6000.00\n", room.getRoomDetails());
    }

    @Test
    void shouldReturnTotalValueOrders(){
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "2332", "F");
        List<Guest> guestList = List.of(guest1, guest2);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(0));
        room.addOrder(new Food("Pasta", 50.0), 2);
        room.addOrder(new Food("Coke", 15.0), 2);

        assertEquals(130.0, room.getOrdersTotalValue());
    }
}
