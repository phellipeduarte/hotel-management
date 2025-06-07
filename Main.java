import java.io.*;
import java.util.*;

// ====== Constantes comuns =======
final class HotelConstants {
    private HotelConstants() {}

    // Room types
    public static final int LUXURY_DOUBLE = 1;
    public static final int DELUXE_DOUBLE = 2;
    public static final int LUXURY_SINGLE = 3;
    public static final int DELUXE_SINGLE = 4;

    // Room counts and number offsets
    public static final int LUXURY_DOUBLE_COUNT = 10;
    public static final int DELUXE_DOUBLE_COUNT = 20;
    public static final int LUXURY_SINGLE_COUNT = 10;
    public static final int DELUXE_SINGLE_COUNT = 20;

    public static final int LUXURY_DOUBLE_OFFSET = 1;
    public static final int DELUXE_DOUBLE_OFFSET = 11;
    public static final int LUXURY_SINGLE_OFFSET = 31;
    public static final int DELUXE_SINGLE_OFFSET = 41;

    // Room charges
    public static final double LUXURY_DOUBLE_CHARGE = 4000;
    public static final double DELUXE_DOUBLE_CHARGE = 3000;
    public static final double LUXURY_SINGLE_CHARGE = 2200;
    public static final double DELUXE_SINGLE_CHARGE = 1200;

    // Food items and prices
    public static final String[] FOOD_NAMES = {"Sandwich", "Pasta", "Noodles", "Coke"};
    public static final int[] FOOD_PRICES = {50, 60, 70, 30};
}

// ====== Food Class =======
class Food implements Serializable {
    private final int itemNo;
    private final int quantity;
    private final int price;

    public Food(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        this.price = HotelConstants.FOOD_PRICES[itemNo - 1] * quantity;
    }

    public int getItemNo() { return itemNo; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
}

// ====== Abstract Room =======
abstract class Room implements Serializable {
    protected String name;
    protected String contact;
    protected String gender;
    protected final List<Food> foodOrders = new ArrayList<>();

    public Room() {
        this("", "", "");
    }

    public Room(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public void addFoodOrder(Food food) {
        foodOrders.add(food);
    }

    public List<Food> getFoodOrders() {
        return Collections.unmodifiableList(foodOrders);
    }

    public String getName() {
        return name;
    }

    public abstract double getDailyCharge();

    public abstract String getRoomFeatures();

    public abstract boolean isDoubleRoom();
}

// ====== SingleRoom Implementation =======
class SingleRoom extends Room {
    public SingleRoom() { super(); }

    public SingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getDailyCharge() {
        // Should be overridden by subtype (Luxury or Deluxe)
        return 0;
    }

    @Override
    public String getRoomFeatures() {
        return "Number of single beds: 1\nFree breakfast: Yes";
    }

    @Override
    public boolean isDoubleRoom() {
        return false;
    }
}

// ====== DoubleRoom Implementation =======
class DoubleRoom extends Room {
    private String name2;
    private String contact2;
    private String gender2;

    public DoubleRoom() {
        this("", "", "", "", "", "");
    }

    public DoubleRoom(String name, String contact, String gender,
                      String name2, String contact2, String gender2) {
        super(name, contact, gender);
        this.name2 = name2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }

    @Override
    public double getDailyCharge() {
        // Should be overridden by subtype (Luxury or Deluxe)
        return 0;
    }

    @Override
    public String getRoomFeatures() {
        return "Number of double beds: 1\nFree breakfast: Yes";
    }

    @Override
    public boolean isDoubleRoom() {
        return true;
    }

    public String getName2() {
        return name2;
    }
}

// ====== Specific Room Types =======

class LuxuryDoubleRoom extends DoubleRoom {
    public LuxuryDoubleRoom(String name, String contact, String gender,
                            String name2, String contact2, String gender2) {
        super(name, contact, gender, name2, contact2, gender2);
    }

    @Override
    public double getDailyCharge() {
        return HotelConstants.LUXURY_DOUBLE_CHARGE;
    }

    @Override
    public String getRoomFeatures() {
        return super.getRoomFeatures() + "\nAC: Yes\nCharge per day: " + getDailyCharge();
    }
}

class DeluxeDoubleRoom extends DoubleRoom {
    public DeluxeDoubleRoom(String name, String contact, String gender,
                            String name2, String contact2, String gender2) {
        super(name, contact, gender, name2, contact2, gender2);
    }

    @Override
    public double getDailyCharge() {
        return HotelConstants.DELUXE_DOUBLE_CHARGE;
    }

    @Override
    public String getRoomFeatures() {
        return super.getRoomFeatures() + "\nAC: No\nCharge per day: " + getDailyCharge();
    }
}

class LuxurySingleRoom extends SingleRoom {
    public LuxurySingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getDailyCharge() {
        return HotelConstants.LUXURY_SINGLE_CHARGE;
    }

    @Override
    public String getRoomFeatures() {
        return super.getRoomFeatures() + "\nAC: Yes\nCharge per day: " + getDailyCharge();
    }
}

class DeluxeSingleRoom extends SingleRoom {
    public DeluxeSingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getDailyCharge() {
        return HotelConstants.DELUXE_SINGLE_CHARGE;
    }

    @Override
    public String getRoomFeatures() {
        return super.getRoomFeatures() + "\nAC: No\nCharge per day: " + getDailyCharge();
    }
}

// ====== Exception =======
class NotAvailableException extends Exception {
    public NotAvailableException() {
        super("Room Not Available!");
    }
}

// ====== Holder Class =======
class HotelHolder implements Serializable {
    private final Room[] luxuryDoubleRooms = new Room[HotelConstants.LUXURY_DOUBLE_COUNT];
    private final Room[] deluxeDoubleRooms = new Room[HotelConstants.DELUXE_DOUBLE_COUNT];
    private final Room[] luxurySingleRooms = new Room[HotelConstants.LUXURY_SINGLE_COUNT];
    private final Room[] deluxeSingleRooms = new Room[HotelConstants.DELUXE_SINGLE_COUNT];

    public Room[] getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public Room[] getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public Room[] getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public Room[] getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }
}

// ====== Hotel Manager =======
class HotelManager {
    private final HotelHolder hotelHolder;
    private final Scanner scanner;

    public HotelManager(HotelHolder hotelHolder) {
        this.hotelHolder = hotelHolder;
        this.scanner = new Scanner(System.in);
    }

    // Mapeia o tipo de quarto para seus arrays e offset para número da sala
    private Room[] getRoomArray(int roomType) {
        return switch (roomType) {
            case HotelConstants.LUXURY_DOUBLE -> hotelHolder.getLuxuryDoubleRooms();
            case HotelConstants.DELUXE_DOUBLE -> hotelHolder.getDeluxeDoubleRooms();
            case HotelConstants.LUXURY_SINGLE -> hotelHolder.getLuxurySingleRooms();
            case HotelConstants.DELUXE_SINGLE -> hotelHolder.getDeluxeSingleRooms();
            default -> null;
        };
    }

    private int getRoomNumberOffset(int roomType) {
        return switch (roomType) {
            case HotelConstants.LUXURY_DOUBLE -> HotelConstants.LUXURY_DOUBLE_OFFSET;
            case HotelConstants.DELUXE_DOUBLE -> HotelConstants.DELUXE_DOUBLE_OFFSET;
            case HotelConstants.LUXURY_SINGLE -> HotelConstants.LUXURY_SINGLE_OFFSET;
            case HotelConstants.DELUXE_SINGLE -> HotelConstants.DELUXE_SINGLE_OFFSET;
            default -> 0;
        };
    }

    private int getRoomArrayLength(int roomType) {
        Room[] rooms = getRoomArray(roomType);
        return rooms != null ? rooms.length : 0;
    }

    // Entrada dos detalhes do cliente
    private void inputCustomerDetails(int roomType, int roomIndex) {
        System.out.print("Enter Name: ");
        String name = scanner.next();
        System.out.print("Enter Contact Number: ");
        String contact = scanner.next();
        System.out.print("Enter Gender: ");
        String gender = scanner.next();

        Room[] rooms = getRoomArray(roomType);

        switch (roomType) {
            case HotelConstants.LUXURY_DOUBLE -> {
                System.out.print("Enter second occupant's Name: ");
                String name2 = scanner.next();
                System.out.print("Enter second occupant's Contact Number: ");
                String contact2 = scanner.next();
                System.out.print("Enter second occupant's Gender: ");
                String gender2 = scanner.next();
                rooms[roomIndex] = new LuxuryDoubleRoom(name, contact, gender, name2, contact2, gender2);
            }
            case HotelConstants.DELUXE_DOUBLE -> {
                System.out.print("Enter second occupant's Name: ");
                String name2 = scanner.next();
                System.out.print("Enter second occupant's Contact Number: ");
                String contact2 = scanner.next();
                System.out.print("Enter second occupant's Gender: ");
                String gender2 = scanner.next();
                rooms[roomIndex] = new DeluxeDoubleRoom(name, contact, gender, name2, contact2, gender2);
            }
            case HotelConstants.LUXURY_SINGLE -> rooms[roomIndex] = new LuxurySingleRoom(name, contact, gender);
            case HotelConstants.DELUXE_SINGLE -> rooms[roomIndex] = new DeluxeSingleRoom(name, contact, gender);
        }
    }

    // Faz reserva para o tipo de quarto
    public void reserveRoom(int roomType) throws NotAvailableException {
        Room[] rooms = getRoomArray(roomType);
        int length = rooms.length;
        boolean reserved = false;

        for (int i = 0; i < length; i++) {
            if (rooms[i] == null) {
                inputCustomerDetails(roomType, i);
                int roomNumber = getRoomNumberOffset(roomType) + i;
                System.out.println("Room successfully booked. Your room number is: " + roomNumber);
                reserved = true;
                break;
            }
        }

        if (!reserved) throw new NotAvailableException();
    }

    // Cadastrar pedido de comida para um quarto
    public void orderFood(int roomType, int roomNumber) throws NotAvailableException {
        Room[] rooms = getRoomArray(roomType);
        int index = roomNumber - getRoomNumberOffset(roomType);

        if (index < 0 || index >= rooms.length || rooms[index] == null) {
            throw new NotAvailableException();
        }

        System.out.println("Enter number of items to order:");
        int n = scanner.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("Menu:");
            for (int j = 0; j < HotelConstants.FOOD_NAMES.length; j++) {
                System.out.printf("%d. %s (%d Rs.)\n", j + 1, HotelConstants.FOOD_NAMES[j], HotelConstants.FOOD_PRICES[j]);
            }
            System.out.print("Enter item number: ");
            int itemNo = scanner.nextInt();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();

            if (itemNo < 1 || itemNo > HotelConstants.FOOD_NAMES.length) {
                System.out.println("Invalid item number!");
                i--;
                continue;
            }

            Food food = new Food(itemNo, quantity);
            rooms[index].addFoodOrder(food);
            System.out.println("Item added.");
        }
    }

    // Mostrar detalhes do quarto e conta
    public void showDetails(int roomType, int roomNumber) throws NotAvailableException {
        Room[] rooms = getRoomArray(roomType);
        int index = roomNumber - getRoomNumberOffset(roomType);

        if (index < 0 || index >= rooms.length || rooms[index] == null) {
            throw new NotAvailableException();
        }

        Room room = rooms[index];

        System.out.println("Customer Name: " + room.getName());
        System.out.println(room.getRoomFeatures());

        System.out.println("Food Ordered:");
        double foodTotal = 0;
        for (Food food : room.getFoodOrders()) {
            String foodName = HotelConstants.FOOD_NAMES[food.getItemNo() - 1];
            int quantity = food.getQuantity();
            int price = food.getPrice();
            System.out.printf("%s x%d = %d Rs.\n", foodName, quantity, price);
            foodTotal += price;
        }

        double roomCharge = room.getDailyCharge();
        System.out.println("Room Charges: " + roomCharge + " Rs.");
        System.out.println("Food Charges: " + foodTotal + " Rs.");
        System.out.println("Total Bill: " + (roomCharge + foodTotal) + " Rs.");
    }

    // Cancelar reserva
    public void cancelReservation(int roomType, int roomNumber) throws NotAvailableException {
        Room[] rooms = getRoomArray(roomType);
        int index = roomNumber - getRoomNumberOffset(roomType);

        if (index < 0 || index >= rooms.length || rooms[index] == null) {
            throw new NotAvailableException();
        }

        rooms[index] = null;
        System.out.println("Reservation cancelled successfully.");
    }
}

// ====== Persistência =======
class HotelPersistence {
    private static final String FILE_NAME = "backup";

    public static void save(HotelHolder holder) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(holder);
        }
    }

    public static HotelHolder load() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new HotelHolder();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (HotelHolder) ois.readObject();
        }
    }
}

// ====== Main =======
public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        HotelHolder holder;
        try {
            holder = HotelPersistence.load();
        } catch (Exception e) {
            holder = new HotelHolder();
        }

        HotelManager manager = new HotelManager(holder);

        while (true) {
            System.out.println("1. Book Room");
            System.out.println("2. Order Food");
            System.out.println("3. Show Details");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();

            try {
                if (choice == 5) {
                    HotelPersistence.save(holder);
                    System.out.println("Data saved. Exiting.");
                    break;
                }

                System.out.println("Select room type:");
                System.out.println("1. Luxury Double");
                System.out.println("2. Deluxe Double");
                System.out.println("3. Luxury Single");
                System.out.println("4. Deluxe Single");
                int roomType = scanner.nextInt();

                switch (choice) {
                    case 1 -> manager.reserveRoom(roomType);
                    case 2 -> {
                        System.out.print("Enter room number: ");
                        int roomNo = scanner.nextInt();
                        manager.orderFood(roomType, roomNo);
                    }
                    case 3 -> {
                        System.out.print("Enter room number: ");
                        int roomNo = scanner.nextInt();
                        manager.showDetails(roomType, roomNo);
                    }
                    case 4 -> {
                        System.out.print("Enter room number: ");
                        int roomNo = scanner.nextInt();
                        manager.cancelReservation(roomType, roomNo);
                    }
                    default -> System.out.println("Invalid choice");
                }

            } catch (NotAvailableException e) {
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
    }
}
