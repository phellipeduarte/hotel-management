import java.io.*;
import java.util.*;

// --- CONSTANTES ---
final class Constants {
    public static final int LUXURY_DOUBLE_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_CAPACITY = 20;
    public static final int LUXURY_SINGLE_CAPACITY = 10;
    public static final int DELUXE_SINGLE_CAPACITY = 20;

    public static final int ROOM_NUMBER_LUXURY_DOUBLE_START = 1;
    public static final int ROOM_NUMBER_DELUXE_DOUBLE_START = 11;
    public static final int ROOM_NUMBER_LUXURY_SINGLE_START = 31;
    public static final int ROOM_NUMBER_DELUXE_SINGLE_START = 41;
}

// --- EXCEÇÃO ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room Not Available!");
    }
}

// --- ENUMS ---
enum RoomType {
    LUXURY_DOUBLE(Constants.LUXURY_DOUBLE_CAPACITY, Constants.ROOM_NUMBER_LUXURY_DOUBLE_START, 4000, true, true, 1),
    DELUXE_DOUBLE(Constants.DELUXE_DOUBLE_CAPACITY, Constants.ROOM_NUMBER_DELUXE_DOUBLE_START, 3000, false, true, 1),
    LUXURY_SINGLE(Constants.LUXURY_SINGLE_CAPACITY, Constants.ROOM_NUMBER_LUXURY_SINGLE_START, 2200, true, true, 1),
    DELUXE_SINGLE(Constants.DELUXE_SINGLE_CAPACITY, Constants.ROOM_NUMBER_DELUXE_SINGLE_START, 1200, false, true, 1);

    private final int capacity;
    private final int roomNumberStart;
    private final int dailyCharge;
    private final boolean ac;
    private final boolean freeBreakfast;
    private final int beds;

    RoomType(int capacity, int roomNumberStart, int dailyCharge, boolean ac, boolean freeBreakfast, int beds) {
        this.capacity = capacity;
        this.roomNumberStart = roomNumberStart;
        this.dailyCharge = dailyCharge;
        this.ac = ac;
        this.freeBreakfast = freeBreakfast;
        this.beds = beds;
    }

    public int getCapacity() { return capacity; }
    public int getRoomNumberStart() { return roomNumberStart; }
    public int getDailyCharge() { return dailyCharge; }
    public boolean hasAC() { return ac; }
    public boolean hasFreeBreakfast() { return freeBreakfast; }
    public int getBeds() { return beds; }

    public String getDescription() {
        String bedType = (this.name().toLowerCase().contains("double")) ? "double bed" : "single bed";
        return String.format(
                "Number of %s(s): %d\nAC: %s\nFree breakfast: %s\nCharge per day: %d",
                bedType, beds, ac ? "Yes" : "No", freeBreakfast ? "Yes" : "No", dailyCharge
        );
    }
}

enum FoodItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    private final int itemNo;
    private final String name;
    private final int pricePerUnit;

    FoodItem(int itemNo, String name, int pricePerUnit) {
        this.itemNo = itemNo;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
    }

    public int getItemNo() { return itemNo; }
    public String getName() { return name; }
    public int getPricePerUnit() { return pricePerUnit; }

    public static Optional<FoodItem> fromItemNo(int itemNo) {
        return Arrays.stream(values()).filter(f -> f.itemNo == itemNo).findFirst();
    }
}

// --- MODELS ---
class Food implements Serializable {
    private final FoodItem foodItem;
    private final int quantity;
    private final float price;

    public Food(int itemNo, int quantity) {
        this.foodItem = FoodItem.fromItemNo(itemNo).orElseThrow(() -> new IllegalArgumentException("Invalid Food Item"));
        this.quantity = quantity;
        this.price = this.foodItem.getPricePerUnit() * quantity;
    }

    public String getName() { return foodItem.getName(); }
    public int getQuantity() { return quantity; }
    public float getPrice() { return price; }
}

interface Room extends Serializable {
    String getPrimaryGuestName();
    List<Food> getFoodOrders();
    void addFoodOrder(Food food);
}

class SingleRoom implements Room {
    private String name;
    private String contact;
    private String gender;
    private final List<Food> foodOrders = new ArrayList<>();

    public SingleRoom() {
        this("", "", "");
    }

    public SingleRoom(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    @Override
    public String getPrimaryGuestName() {
        return name;
    }

    @Override
    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    @Override
    public void addFoodOrder(Food food) {
        foodOrders.add(food);
    }
}

class DoubleRoom implements Room {
    private String name1;
    private String contact1;
    private String gender1;
    private String name2;
    private String contact2;
    private String gender2;
    private final List<Food> foodOrders = new ArrayList<>();

    public DoubleRoom() {
        this("", "", "", "", "", "");
    }

    public DoubleRoom(String name1, String contact1, String gender1, String name2, String contact2, String gender2) {
        this.name1 = name1;
        this.contact1 = contact1;
        this.gender1 = gender1;
        this.name2 = name2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }

    @Override
    public String getPrimaryGuestName() {
        return name1;
    }

    @Override
    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    @Override
    public void addFoodOrder(Food food) {
        foodOrders.add(food);
    }
}

// --- HOLDER ---
class HotelInventory implements Serializable {
    public final Map<RoomType, Room[]> rooms;

    public HotelInventory() {
        rooms = new EnumMap<>(RoomType.class);
        rooms.put(RoomType.LUXURY_DOUBLE, new DoubleRoom[RoomType.LUXURY_DOUBLE.getCapacity()]);
        rooms.put(RoomType.DELUXE_DOUBLE, new DoubleRoom[RoomType.DELUXE_DOUBLE.getCapacity()]);
        rooms.put(RoomType.LUXURY_SINGLE, new SingleRoom[RoomType.LUXURY_SINGLE.getCapacity()]);
        rooms.put(RoomType.DELUXE_SINGLE, new SingleRoom[RoomType.DELUXE_SINGLE.getCapacity()]);
    }

    @SuppressWarnings("unchecked")
    public <T extends Room> T getRoom(RoomType roomType, int index) {
        Room[] roomArr = rooms.get(roomType);
        if (roomArr == null || index < 0 || index >= roomArr.length) {
            throw new IndexOutOfBoundsException("Invalid room index");
        }
        return (T) roomArr[index];
    }

    public void setRoom(RoomType roomType, int index, Room room) {
        Room[] roomArr = rooms.get(roomType);
        if (roomArr == null || index < 0 || index >= roomArr.length) {
            throw new IndexOutOfBoundsException("Invalid room index");
        }
        roomArr[index] = room;
    }

    public int getAvailableRoomsCount(RoomType roomType) {
        Room[] roomArr = rooms.get(roomType);
        int count = 0;
        for (Room r : roomArr) {
            if (r == null) count++;
        }
        return count;
    }

    public List<Integer> getAvailableRoomNumbers(RoomType roomType) {
        Room[] roomArr = rooms.get(roomType);
        List<Integer> available = new ArrayList<>();
        int start = roomType.getRoomNumberStart();
        for (int i = 0; i < roomArr.length; i++) {
            if (roomArr[i] == null) {
                available.add(start + i);
            }
        }
        return available;
    }
}

// --- GESTOR DE HOTEL ---
class HotelManager {
    public final HotelInventory inventory;
    private final Scanner scanner;

    public HotelManager(HotelInventory inventory, Scanner scanner) {
        this.inventory = inventory;
        this.scanner = scanner;
    }

    public void displayFeatures(RoomType roomType) {
        System.out.println(roomType.getDescription());
    }

    public void displayAvailability(RoomType roomType) {
        int available = inventory.getAvailableRoomsCount(roomType);
        System.out.printf("Number of rooms available: %d%n", available);
    }

    public void bookRoom(RoomType roomType) {
        List<Integer> availableRooms = inventory.getAvailableRoomNumbers(roomType);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for this type.");
            return;
        }
        System.out.printf("Available room numbers: %s%n", availableRooms);
        System.out.print("Enter room number to book: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();  // consume newline

        if (!availableRooms.contains(roomNum)) {
            System.out.println("Selected room not available.");
            return;
        }
        int index = roomNum - roomType.getRoomNumberStart();

        switch (roomType) {
            case LUXURY_DOUBLE, DELUXE_DOUBLE -> {
                DoubleRoom dr = createDoubleRoomDetails();
                inventory.setRoom(roomType, index, dr);
            }
            case LUXURY_SINGLE, DELUXE_SINGLE -> {
                SingleRoom sr = createSingleRoomDetails();
                inventory.setRoom(roomType, index, sr);
            }
        }
        System.out.println("Room booked successfully!");
    }

    private SingleRoom createSingleRoomDetails() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter contact: ");
        String contact = scanner.nextLine();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();
        return new SingleRoom(name, contact, gender);
    }

    private DoubleRoom createDoubleRoomDetails() {
        System.out.println("Enter details for first person:");
        String name1 = prompt("Name");
        String contact1 = prompt("Contact");
        String gender1 = prompt("Gender");
        System.out.println("Enter details for second person:");
        String name2 = prompt("Name");
        String contact2 = prompt("Contact");
        String gender2 = prompt("Gender");
        return new DoubleRoom(name1, contact1, gender1, name2, contact2, gender2);
    }

    private String prompt(String field) {
        System.out.printf("Enter %s: ", field);
        return scanner.nextLine();
    }

    public void orderFood(RoomType roomType) {
        List<Integer> availableRooms = inventory.getAvailableRoomNumbers(roomType);
        List<Integer> occupiedRooms = new ArrayList<>();
        Room[] roomsArray = null;
        switch (roomType) {
            case LUXURY_DOUBLE, DELUXE_DOUBLE -> roomsArray = inventory.rooms.get(roomType);
            case LUXURY_SINGLE, DELUXE_SINGLE -> roomsArray = inventory.rooms.get(roomType);
        }

        // Build list of occupied rooms
        if (roomsArray != null) {
            for (int i = 0; i < roomsArray.length; i++) {
                if (roomsArray[i] != null) {
                    occupiedRooms.add(roomType.getRoomNumberStart() + i);
                }
            }
        }

        if (occupiedRooms.isEmpty()) {
            System.out.println("No rooms booked in this category.");
            return;
        }
        System.out.printf("Occupied room numbers: %s%n", occupiedRooms);
        System.out.print("Enter room number to order food: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();  // consume newline

        if (!occupiedRooms.contains(roomNum)) {
            System.out.println("Invalid room number.");
            return;
        }

        int index = roomNum - roomType.getRoomNumberStart();
        Room room = inventory.getRoom(roomType, index);

        System.out.println("Menu:");
        for (FoodItem food : FoodItem.values()) {
            System.out.printf("%d. %s (Rs. %d)%n", food.getItemNo(), food.getName(), food.getPricePerUnit());
        }

        System.out.print("Enter item number: ");
        int itemNo = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline

        try {
            Food foodOrder = new Food(itemNo, quantity);
            room.addFoodOrder(foodOrder);
            System.out.println("Food ordered successfully!");
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid food item.");
        }
    }

    public void generateBill(RoomType roomType) {
        List<Integer> occupiedRooms = new ArrayList<>();
        Room[] roomsArray = inventory.rooms.get(roomType);
        if (roomsArray == null) {
            System.out.println("Invalid room type.");
            return;
        }
        for (int i = 0; i < roomsArray.length; i++) {
            if (roomsArray[i] != null) {
                occupiedRooms.add(roomType.getRoomNumberStart() + i);
            }
        }

        if (occupiedRooms.isEmpty()) {
            System.out.println("No bookings in this category.");
            return;
        }

        System.out.printf("Occupied room numbers: %s%n", occupiedRooms);
        System.out.print("Enter room number to generate bill: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();

        if (!occupiedRooms.contains(roomNum)) {
            System.out.println("Invalid room number.");
            return;
        }

        int index = roomNum - roomType.getRoomNumberStart();
        Room room = inventory.getRoom(roomType, index);
        List<Food> foodOrders = room.getFoodOrders();

        int days = 0;
        while (true) {
            System.out.print("Enter number of days stayed (1-10): ");
            days = scanner.nextInt();
            scanner.nextLine();
            if (days >= 1 && days <= 10) break;
            System.out.println("Days must be between 1 and 10.");
        }

        int roomCharge = roomType.getDailyCharge() * days;
        float foodCharge = 0f;
        System.out.println("------- BILL -------");
        System.out.printf("Room charges for %d day(s): Rs %d%n", days, roomCharge);
        if (!foodOrders.isEmpty()) {
            System.out.println("Food charges:");
            for (Food f : foodOrders) {
                System.out.printf("%s x %d = Rs %.2f%n", f.getName(), f.getQuantity(), f.getPrice());
                foodCharge += f.getPrice();
            }
        }
        System.out.printf("Total food charges: Rs %.2f%n", foodCharge);
        System.out.printf("Total bill: Rs %.2f%n", roomCharge + foodCharge);
        System.out.println("--------------------");
    }

    public void checkout(RoomType roomType) {
        List<Integer> occupiedRooms = new ArrayList<>();
        Room[] roomsArray = inventory.rooms.get(roomType);
        if (roomsArray == null) {
            System.out.println("Invalid room type.");
            return;
        }
        for (int i = 0; i < roomsArray.length; i++) {
            if (roomsArray[i] != null) {
                occupiedRooms.add(roomType.getRoomNumberStart() + i);
            }
        }
        if (occupiedRooms.isEmpty()) {
            System.out.println("No bookings in this category.");
            return;
        }
        System.out.printf("Occupied room numbers: %s%n", occupiedRooms);
        System.out.print("Enter room number to checkout: ");
        int roomNum = scanner.nextInt();
        scanner.nextLine();

        if (!occupiedRooms.contains(roomNum)) {
            System.out.println("Invalid room number.");
            return;
        }
        int index = roomNum - roomType.getRoomNumberStart();
        inventory.setRoom(roomType, index, null);
        System.out.println("Room checked out successfully!");
    }
}

// --- PERSISTÊNCIA ---
class HotelPersistence {
    private static final String FILENAME = "backup";

    public static void save(HotelInventory inventory) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(inventory);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static HotelInventory load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            return (HotelInventory) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found, starting fresh.");
            return new HotelInventory();
        }
    }
}

// --- MAIN ---
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelInventory inventory = HotelPersistence.load();
        HotelManager manager = new HotelManager(inventory, scanner);

        boolean running = true;
        while (running) {
            System.out.println("\nHotel Management System");
            System.out.println("1. Display features of rooms");
            System.out.println("2. Display number of rooms available");
            System.out.println("3. Book a room");
            System.out.println("4. Order food");
            System.out.println("5. Generate bill");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 7) {
                running = false;
                HotelPersistence.save(inventory);
                System.out.println("Exiting and saving data...");
                break;
            }

            RoomType selectedRoomType = null;
            if (option >= 1 && option <= 6) {
                selectedRoomType = promptRoomType(scanner);
                if (selectedRoomType == null) {
                    System.out.println("Invalid room type selection.");
                    continue;
                }
            }

            switch (option) {
                case 1 -> manager.displayFeatures(selectedRoomType);
                case 2 -> manager.displayAvailability(selectedRoomType);
                case 3 -> manager.bookRoom(selectedRoomType);
                case 4 -> manager.orderFood(selectedRoomType);
                case 5 -> manager.generateBill(selectedRoomType);
                case 6 -> manager.checkout(selectedRoomType);
                default -> System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }

    private static RoomType promptRoomType(Scanner scanner) {
        System.out.println("Select room type:");
        for (RoomType rt : RoomType.values()) {
            System.out.printf("%d. %s%n", rt.ordinal() + 1, rt.name().replace('_', ' '));
        }
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice < 1 || choice > RoomType.values().length) {
            return null;
        }
        return RoomType.values()[choice - 1];
    }
}
