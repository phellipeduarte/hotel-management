import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void shouldReturnSingleRoomDetails(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        Singleroom singleroom = new Singleroom(guest);
        singleroom.setAc(true);
        singleroom.setCharge(4000.0);

        assertEquals("Bed capacity: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: R$4000.00\n", singleroom.getRoomDetails());
    }

    @Test
    void shouldReturnDoubleRoomDetails(){
        Guest guest1 = new Guest("Phellipe", "3223", "M");
        Guest guest2 = new Guest("Larissa", "2332", "F");
        Room doubleroom = new Doubleroom(guest1, guest2);
        doubleroom.setAc(false);
        doubleroom.setCharge(6000.0);

        assertEquals("Bed capacity: 2\nAC: No\nFree breakfast: Yes\nCharge per day: R$6000.00\n", doubleroom.getRoomDetails());
    }
}
