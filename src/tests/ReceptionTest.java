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
}
