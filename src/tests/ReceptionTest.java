import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceptionTest {

    @Test
    void shouldReturnRoomOption(){
        final String expectedOption = "Room capacity: 2\nAC: Yes\nFree breakfast: Yes\nCharge per day: R$4000.00\n";
        assertEquals(expectedOption, Reception.getInstance().roomFeatures(1));
    }

    @Test
    void shouldReturnValidation(){
        final String expectedOption = "Enter a valid option";
        assertEquals(expectedOption, Reception.getInstance().roomFeatures(-1));
    }

    @Test
    void shouldReturnBill(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        Room room = new Room(guest);
        room.setCharge(3000.0);
        room.addOrder(new Food("Pasta", 50.0), 1);

        String expectedResult = "\nBill:\nRoom charge: R$3000.00\nOrder charge: R$50.00\n====================\nQuantity x Item  ....  Price\n1x Pasta.......... R$50.00";

        assertEquals(expectedResult, Reception.getInstance().bill(room));
    }
}
