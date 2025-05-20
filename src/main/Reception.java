import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Reception {
    private static final Reception instance = new Reception();

    private final ArrayList<Room> roomOptions;

    private Reception(){
        roomOptions = new ArrayList<>(Arrays.asList(
                new Room(2, true, 4000.0),
                new Room(2, false, 3000.0),
                new Room(1, true, 2200.0),
                new Room(1, false, 1200.0)
        ));
    };

    public List<Room> getRoomOptions() {
        return roomOptions;
    }

    public static Reception getInstance(){
        return instance;
    }

    public String roomFeatures(int option){
        String features;

        try{
            features = roomOptions.get(option - 1).getRoomDetails();
        } catch (IndexOutOfBoundsException exception){
            features = "Enter a valid option";
        }

        return features;
    }

    public String bill(Room room) {
        StringBuilder bill = new StringBuilder("\nBill:\n");
        String totalCharge = "Total charge: " + Utils.getValueLocalCurrency(room.getCharge() + room.getOrdersTotalValue()) + "\n";
        String roomCharge = "Room charge: " + Utils.getValueLocalCurrency(room.getCharge()) + "\n";
        String foodCharge = "Order charge: " + Utils.getValueLocalCurrency(room.getOrdersTotalValue()) + "\n====================\nQuantity x Item  ....  Price\n";

        bill.append(totalCharge).append(roomCharge).append(foodCharge);

        List<Order> orders = room.getOrders();

        for(Order order : orders){
            String item = order.getQuantity() + "x " + order.getFood().getName() + ".......... " + Utils.getValueLocalCurrency(order.getTotal()) + "\n";
            bill.append(item);
        }

        return bill.toString();
    }

    public void checkin(RoomTypeEnum roomType) throws NotAvailable {
        System.out.println("\nChoose room number from: ");

        Room roomFrame = roomOptions.get(roomType.ordinal());

        List<Integer> roomInterval = Hotel.getInstance().getRoomIntervalByRoomType(roomType);

        int from = roomInterval.get(0);
        int to = roomInterval.get(1);

        if(from == -1 || to == -1){
            System.out.println("Invalid option.");
            return;
        }

        System.out.println(Hotel.getInstance().getEmptyRooms(from, to));

        int roomNumber = InputOutputHandler.waitIntegerAnswer("");

        if(roomNumber < from || roomNumber  > to){
            System.out.println("Invalid option.");
        } else {
            roomCustDetails(roomNumber, roomFrame);
            System.out.println("Room Booked");
        }
    }

    public void roomCustDetails(int roomNumber, Room roomFrame) throws NotAvailable {
        List<Guest> guestList = new ArrayList<>();

        for(int index = 0; index < roomFrame.getCapacity(); index++){
            Guest guest = new Guest();
            guestList.add(guest);
        }

        Hotel.getInstance().allocateRoom(new Room(guestList, roomFrame), roomNumber);
    }

    public void checkout(int roomNumber){
        Room room = Hotel.getInstance().getRoom(roomNumber);

        if(room != null){
            String guestString = room.getGuestsNames();
            String question = "\nDo you want to checkout ?(y/n)\n";

            char wish = InputOutputHandler.waitStringAnswer(guestString + question).charAt(0);

            if(wish == 'y' || wish == 'Y'){
                System.out.println(bill(room));
                Hotel.getInstance().deallocateRoom(roomNumber);
                System.out.println("Room deallocated succesfully");
            }
        } else {
            System.out.println("Room empty already.");
        }
    }
}
