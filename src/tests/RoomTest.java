import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void shouldReturnSingleRoomDetails(){
        Singleroom singleroom = new Singleroom("Phellipe", "3223", "M");
        singleroom.setAc(true);
        singleroom.setCharge(4000.0);

        assertEquals("Bed capacity: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: R$4000.00\n", singleroom.getRoomDetails());
    }

    @Test
    void shouldReturnDoubleRoomDetails(){
        Room doubleroom = new Doubleroom("Phellipe", "3223", "M", "Larissa", "2332", "F");
        doubleroom.setAc(false);
        doubleroom.setCharge(6000.0);

        assertEquals("Bed capacity: 2\nAC: No\nFree breakfast: Yes\nCharge per day: R$6000.00\n", doubleroom.getRoomDetails());
    }
}
