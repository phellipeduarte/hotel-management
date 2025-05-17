import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceptionTest {
    Hotel hotel = Hotel.getInstance();

    @BeforeEach
    void deallocateAllRooms(){
        Room[] rooms = hotel.getRooms();

        for(int index = 1; index <= rooms.length; index++){
            hotel.deallocateRoom(index);
        }
    }

    @Test
    void shouldReturnRoomOption(){
        final String expectedOption = "Room capacity: 2\nAC: Yes\nFree breakfast: Yes\nCharge per day: R$4000.00\n";
        assertEquals(expectedOption, Reception.getInstance().roomFeatures(1));
    }

    @Test
    void shouldCountAvailableRoomsExceptAssignedRoom() throws NotAvailable {
        Room expectedRoom = new Room(2, false, 3000.0);
        hotel.allocateRoom(expectedRoom, 9);

        assertEquals(9, hotel.countAvailableRooms(RoomTypeEnum.LUXURY_DOUBLEROOM));
    }

    @Test
    void shouldReturnRoomByNumber() throws NotAvailable {
        Room expectedRoom = new Room(2, false, 3000.0);
        hotel.allocateRoom(expectedRoom, 19);

        Room room = hotel.getRoom(19);

        assertEquals(expectedRoom.getRoomDetails(), room.getRoomDetails());
    }


    @Test
    void shouldReturnValidation(){
        final String expectedOption = "Enter a valid option";
        assertEquals(expectedOption, Reception.getInstance().roomFeatures(-1));
    }

    @Test
    void shouldReturnBill(){
        Guest guest = new Guest("Phellipe", "3223", "M");
        Room room = new Room(guest, Reception.getInstance().getRoomOptions().get(3));
        room.setCharge(3000.0);
        room.addOrder(new Food("Pasta", 50.0), 1);

        String expectedResult = "\nBill:\nRoom charge: R$3000.00\nOrder charge: R$50.00\n====================\nQuantity x Item  ....  Price\n1x Pasta.......... R$50.00";

        assertEquals(expectedResult, Reception.getInstance().bill(room));
    }
}
