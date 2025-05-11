import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenTest {

    @Test
    void shouldReturnMenu(){
        String expectedMenu = "1. Sandwich - R$50.00" + "\n" + "2. Pasta - R$60.00" + "\n" + "3. Noodles - R$70.00" + "\n" + "4. Coke - R$30.00" + "\n";
        assertEquals(expectedMenu, Kitchen.getInstance().getMenuOptions());
    }
}
