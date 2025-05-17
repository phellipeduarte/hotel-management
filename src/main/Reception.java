import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Reception {
    private static final Reception instance = new Reception();

    private Reception(){};

    public static Reception getInstance(){
        return instance;
    }

    public String roomFeatures(int option){
        String features;

        ArrayList<Room> roomOptions = new ArrayList<>(Arrays.asList(
                new Room(2, true, 4000.0),
                new Room(2, false, 3000.0),
                new Room(1, true, 2200.0),
                new Room(1, false, 1200.0)
        ));

        try{
            features = roomOptions.get(option - 1).getRoomDetails();
        } catch (IndexOutOfBoundsException exception){
            features = "Enter a valid option";
        }

        return features;
    }

    public String bill(Room room) {
        StringBuilder bill = new StringBuilder("\nBill:\n");
        String roomCharge = "Room charge: " + Utils.getValueLocalCurrency(room.getCharge()) + "\n";
        String foodCharge = "Order charge: " + Utils.getValueLocalCurrency(room.getOrdersTotalValue()) + "\n====================\nQuantity x Item  ....  Price\n";

        bill.append(roomCharge);
        bill.append(foodCharge);

        ArrayList<Order> orders = room.getOrders();

        for(Order order : orders){
            String item = order.getQuantity() + "x " + order.getFood().getName() + ".......... " + Utils.getValueLocalCurrency(order.getTotal());
            bill.append(item);
        }

        return bill.toString();
    }

    public void allocateRoom(Room room, int roomNumber) throws NotAvailable {
        Room[] rooms = Hotel.getInstance().getRooms();

        if(rooms[roomNumber - 1] == null){
            rooms[roomNumber - 1] = room;
        } else {
            throw new NotAvailable();
        }
    }

    public void doubleRoomCustDetails(int roomNumber) throws NotAvailable {
        Guest guest1, guest2;

        guest1 = new Guest();
        guest2 = new Guest();

        allocateRoom(new Room(guest1, guest2), roomNumber);
    }

    public void singleRoomCustDetails(int roomNumber) throws NotAvailable {
        Guest guest;
        guest = new Guest();

        allocateRoom(new Room(guest), roomNumber);
    }

    public void bookRoom(RoomTypeEnum roomType) throws NotAvailable {
        System.out.println("\nChoose room number from: ");

        List<Integer> roomInterval = Hotel.getInstance().getRoomIntervalByRoomType(roomType);

        int from = roomInterval.get(0);
        int to = roomInterval.get(1);

        System.out.println(Hotel.getInstance().getEmptyRooms(from, to));

        int roomNumber = InputOutputHandler.waitIntegerAnswer("");

        if(roomNumber > 30){
            singleRoomCustDetails(roomNumber);
        } else {
            doubleRoomCustDetails(roomNumber);
        }

        System.out.println("Room Booked");
    }

    public void checkout(int roomNumber){
        Room room = Hotel.getInstance().getRoom(roomNumber);

        if(room != null){
            String guestString = room.getGuestsNames();
            String question = "\nDo you want to checkout ?(y/n)\n";

            char wish = InputOutputHandler.waitStringAnswer(guestString + question).charAt(0);

            if(wish == 'y' || wish == 'Y'){
                deallocateRoom(roomNumber);
                System.out.println("Room deallocated succesfully");
            }
        } else {
            System.out.println("Room empty already.");
        }
    }

    public void deallocateRoom(int roomNumber){
        Hotel.getInstance().getRooms()[roomNumber] = null;
    }
}
