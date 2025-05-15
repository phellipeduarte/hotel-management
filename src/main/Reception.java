import java.util.ArrayList;
import java.util.Arrays;

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
        String roomCharge = "Room charge: " + Room.getValueLocalCurrency(room.getCharge()) + "\n";
        String foodCharge = "Order charge: " + Room.getValueLocalCurrency(room.getOrdersTotalValue()) + "\n====================\nQuantity x Item  ....  Price\n";

        bill.append(roomCharge);
        bill.append(foodCharge);

        ArrayList<Order> orders = room.getOrders();

        for(Order order : orders){
            String item = order.getQuantity() + "x " + order.getFood().getName() + ".......... " + Food.getValueLocalCurrency(order.getTotal());
            bill.append(item);
        }

        return bill.toString();
    }
}
