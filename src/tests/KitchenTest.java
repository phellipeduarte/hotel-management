import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenTest {

    @Test
    void shouldReturnMenu(){
        String expectedMenu = "\nMenu:\n1. Sandwich - R$50.00" + "\n" + "2. Pasta - R$60.00" + "\n" + "3. Noodles - R$70.00" + "\n" + "4. Coke - R$30.00" + "\n";
        assertEquals(expectedMenu, Kitchen.getInstance().getMenuOptions());
    }

    @Test
    void shouldAddOrderToRoom(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        Room room = new Singleroom(guest);
        Kitchen.getInstance().order(1, 1, room);
        assertEquals("Sandwich", room.getOrder(0).getFood().getName());
    }
}
