import org.junit.jupiter.api.Test;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    @Test
    void shouldReturnValueWithCurrency(){
        assertEquals("R$10.00", Utils.getValueLocalCurrency(10.0));
    }
}
