import domain.guest.Guest;
import domain.hotel.Kitchen;
import domain.hotel.Reception;
import domain.hotel.Room;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        List<Guest> guestList = List.of(guest);

        Room room = new Room(guestList, Reception.getInstance().getRoomOptions().get(2));
        Kitchen.getInstance().order(1, 1, room);

        assertEquals("Sandwich", room.getOrder(0).getFood().getName());
    }
}
