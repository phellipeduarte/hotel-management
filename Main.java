import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

// --- Constants ---
final class HotelConstants {
    public static final String FILE_BACKUP = "backup";
    public static final int LUXURY_DOUBLE_ROOM_COUNT = 10;
    public static final int DELUXE_DOUBLE_ROOM_COUNT = 20;
    public static final int LUXURY_SINGLE_ROOM_COUNT = 10;
    public static final int DELUXE_SINGLE_ROOM_COUNT = 20;

    public static final double LUXURY_DOUBLE_ROOM_PRICE = 4000.0;
    public static final double DELUXE_DOUBLE_ROOM_PRICE = 3000.0;
    public static final double LUXURY_SINGLE_ROOM_PRICE = 2200.0;
    public static final double DELUXE_SINGLE_ROOM_PRICE = 1200.0;

    private static final Map<Integer, FoodItem> FOOD_MENU = new HashMap<>();

    static {
        FOOD_MENU.put(1, new FoodItem("Sandwich", 50.0));
        FOOD_MENU.put(2, new FoodItem("Pasta", 60.0));
        FOOD_MENU.put(3, new FoodItem("Noodles", 70.0));
        FOOD_MENU.put(4, new FoodItem("Coke", 30.0));
    }

    public static FoodItem getFoodItemById(int id) {
        return FOOD_MENU.get(id);
    }

    public static List<FoodItem> getAllFoodItems() {
        return new ArrayList<>(FOOD_MENU.values());
    }

    public static void displayFoodMenu() {
        System.out.println("\n==========");
        System.out.println("   Menu:   ");
        System.out.println("==========");
        FOOD_MENU.forEach((id, item) ->
                System.out.printf("%d.%-10sRs.%.2f%n", id, item.getName(), item.getPrice())
        );
        System.out.println();
    }
}

// --- Exceptions ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room Not Available!");
    }
}

class RoomNotFoundException extends Exception {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

// --- Value Objects / Entities ---

class FoodItem implements Serializable {
    private final String name;
    private final double price;

    public FoodItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

class OrderedFood implements Serializable {
    private final FoodItem foodItem;
    private final int quantity;
    private final double totalPrice;

    public OrderedFood(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.totalPrice = foodItem.getPrice() * quantity;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

class Guest implements Serializable {
    private String name;
    private String contact;
    private String gender;

    public Guest(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getGender() { return gender; }
}

abstract class Room implements Serializable {
    private List<Guest> guests;
    protected List<OrderedFood> orderedFood;
    protected double basePrice;
    protected boolean isOccupied;

    public Room(double basePrice) {
        this.basePrice = basePrice;
        this.guests = new ArrayList<>();
        this.orderedFood = new ArrayList<>();
        this.isOccupied = false;
    }

    public void addGuest(Guest guest) {
        this.guests.add(guest);
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void addFood(OrderedFood food) {
        this.orderedFood.add(food);
    }

    public List<OrderedFood> getOrderedFood() {
        return orderedFood;
    }

    public double calculateFoodBill() {
        return orderedFood.stream().mapToDouble(OrderedFood::getTotalPrice).sum();
    }

    public double getTotalBill() {
        return basePrice + calculateFoodBill();
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public abstract String getFeatures();
    public abstract String getType();

    // Method to reset room for new occupancy
    public void checkout() {
        this.guests.clear();
        this.orderedFood.clear();
        this.isOccupied = false;
    }
}

class SingleRoom extends Room {
    public SingleRoom(double basePrice) {
        super(basePrice);
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : " + (basePrice == HotelConstants.LUXURY_SINGLE_ROOM_PRICE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + basePrice;
    }

    @Override
    public String getType() {
        return (basePrice == HotelConstants.LUXURY_SINGLE_ROOM_PRICE ? "Luxury" : "Deluxe") + " Single Room";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom(double basePrice) {
        super(basePrice);
    }

    @Override
    public void addGuest(Guest guest) {
        if (this.getGuests().size() < 2) { // Allow up to 2 guests for a double room
            super.addGuest(guest);
        } else {
            System.out.println("Double room can only accommodate two guests.");
        }
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : " + (basePrice == HotelConstants.LUXURY_DOUBLE_ROOM_PRICE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + basePrice;
    }

    @Override
    public String getType() {
        return (basePrice == HotelConstants.LUXURY_DOUBLE_ROOM_PRICE ? "Luxury" : "Deluxe") + " Double Room";
    }
}

// --- Repository/Data Holder ---
class HotelRoomRegistry implements Serializable {
    private final Room[] luxuryDoubleRooms = new DoubleRoom[HotelConstants.LUXURY_DOUBLE_ROOM_COUNT];
    private final Room[] deluxeDoubleRooms = new DoubleRoom[HotelConstants.DELUXE_DOUBLE_ROOM_COUNT];
    private final Room[] luxurySingleRooms = new SingleRoom[HotelConstants.LUXURY_SINGLE_ROOM_COUNT];
    private final Room[] deluxeSingleRooms = new SingleRoom[HotelConstants.DELUXE_SINGLE_ROOM_COUNT];

    public HotelRoomRegistry() {
        // Initialize rooms
        for (int i = 0; i < HotelConstants.LUXURY_DOUBLE_ROOM_COUNT; i++) {
            luxuryDoubleRooms[i] = new DoubleRoom(HotelConstants.LUXURY_DOUBLE_ROOM_PRICE);
        }
        for (int i = 0; i < HotelConstants.DELUXE_DOUBLE_ROOM_COUNT; i++) {
            deluxeDoubleRooms[i] = new DoubleRoom(HotelConstants.DELUXE_DOUBLE_ROOM_PRICE);
        }
        for (int i = 0; i < HotelConstants.LUXURY_SINGLE_ROOM_COUNT; i++) {
            luxurySingleRooms[i] = new SingleRoom(HotelConstants.LUXURY_SINGLE_ROOM_PRICE);
        }
        for (int i = 0; i < HotelConstants.DELUXE_SINGLE_ROOM_COUNT; i++) {
            deluxeSingleRooms[i] = new SingleRoom(HotelConstants.DELUXE_SINGLE_ROOM_PRICE);
        }
    }

    public Room[] getRooms(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> luxuryDoubleRooms;
            case DELUXE_DOUBLE -> deluxeDoubleRooms;
            case LUXURY_SINGLE -> luxurySingleRooms;
            case DELUXE_SINGLE -> deluxeSingleRooms;
        };
    }

    public Room getRoom(RoomType type, int index) throws RoomNotFoundException {
        Room[] rooms = getRooms(type);
        if (index < 0 || index >= rooms.length) {
            throw new RoomNotFoundException("Room index out of bounds for " + type);
        }
        return rooms[index];
    }
}

// --- Enums for clarity and type safety ---
enum RoomType {
    LUXURY_DOUBLE(1, HotelConstants.LUXURY_DOUBLE_ROOM_COUNT, 0),
    DELUXE_DOUBLE(2, HotelConstants.DELUXE_DOUBLE_ROOM_COUNT, HotelConstants.LUXURY_DOUBLE_ROOM_COUNT),
    LUXURY_SINGLE(3, HotelConstants.LUXURY_SINGLE_ROOM_COUNT, HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT),
    DELUXE_SINGLE(4, HotelConstants.DELUXE_SINGLE_ROOM_COUNT, HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT + HotelConstants.LUXURY_SINGLE_ROOM_COUNT);

    private final int option;
    private final int count;
    private final int startIndex; // Used for global room numbering

    RoomType(int option, int count, int startIndex) {
        this.option = option;
        this.count = count;
        this.startIndex = startIndex;
    }

    public int getOption() {
        return option;
    }

    public int getCount() {
        return count;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public static RoomType fromOption(int option) {
        for (RoomType type : RoomType.values()) {
            if (type.getOption() == option) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type option: " + option);
    }

    public int getGlobalRoomNumber(int localIndex) {
        return startIndex + localIndex + 1;
    }

    public static RoomType fromGlobalRoomNumber(int globalNumber) {
        if (globalNumber > 0 && globalNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_COUNT) {
            return LUXURY_DOUBLE;
        } else if (globalNumber > HotelConstants.LUXURY_DOUBLE_ROOM_COUNT && globalNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT) {
            return DELUXE_DOUBLE;
        } else if (globalNumber > HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT && globalNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT + HotelConstants.LUXURY_SINGLE_ROOM_COUNT) {
            return LUXURY_SINGLE;
        } else if (globalNumber > HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT + HotelConstants.LUXURY_SINGLE_ROOM_COUNT && globalNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_COUNT + HotelConstants.DELUXE_DOUBLE_ROOM_COUNT + HotelConstants.LUXURY_SINGLE_ROOM_COUNT + HotelConstants.DELUXE_SINGLE_ROOM_COUNT) {
            return DELUXE_SINGLE;
        }
        throw new IllegalArgumentException("Invalid global room number: " + globalNumber);
    }

    public static int getLocalRoomIndex(int globalNumber, RoomType type) {
        return globalNumber - type.getStartIndex() - 1;
    }
}

// --- Service Layer ---
class HotelManagement {
    private HotelRoomRegistry roomRegistry;
    private Scanner scanner;

    public HotelManagement(HotelRoomRegistry roomRegistry, Scanner scanner) {
        this.roomRegistry = roomRegistry;
        this.scanner = scanner;
    }

    public void displayRoomDetails(RoomType type) {
        System.out.println("\n--- " + type.name().replace("_", " ") + " Features ---");
        // Get any room of this type to display features, as features are common for a type
        Room room = roomRegistry.getRooms(type)[0];
        System.out.println(room.getFeatures());
    }

    public void displayRoomAvailability(RoomType type) {
        Room[] rooms = roomRegistry.getRooms(type);
        long availableCount = Arrays.stream(rooms).filter(room -> !room.isOccupied()).count();
        System.out.println("Number of " + type.name().replace("_", " ") + " rooms available: " + availableCount);
        if (availableCount > 0) {
            System.out.print("Available room numbers: ");
            for (int i = 0; i < rooms.length; i++) {
                if (!rooms[i].isOccupied()) {
                    System.out.print(type.getGlobalRoomNumber(i) + ", ");
                }
            }
            System.out.println();
        }
    }

    public void bookRoom(RoomType type) {
        Room[] rooms = roomRegistry.getRooms(type);
        List<Integer> availableRoomNumbers = new ArrayList<>();
        for (int i = 0; i < rooms.length; i++) {
            if (!rooms[i].isOccupied()) {
                availableRoomNumbers.add(type.getGlobalRoomNumber(i));
            }
        }

        if (availableRoomNumbers.isEmpty()) {
            System.out.println("Sorry, no " + type.name().replace("_", " ") + " rooms are available.");
            return;
        }

        System.out.println("\nChoose room number from: " + availableRoomNumbers.stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.print("Enter room number: ");
        try {
            int globalRoomNumber = scanner.nextInt();
            if (!availableRoomNumbers.contains(globalRoomNumber)) {
                throw new RoomNotAvailableException();
            }

            int localIndex = RoomType.getLocalRoomIndex(globalRoomNumber, type);
            Room selectedRoom = roomRegistry.getRoom(type, localIndex);

            if (selectedRoom.isOccupied()) {
                throw new RoomNotAvailableException();
            }

            System.out.print("Enter first customer name: ");
            String name = scanner.next();
            System.out.print("Enter contact number: ");
            String contact = scanner.next();
            System.out.print("Enter gender: ");
            String gender = scanner.next();
            selectedRoom.addGuest(new Guest(name, contact, gender));

            if (selectedRoom instanceof DoubleRoom) {
                System.out.print("Enter second customer name: ");
                String name2 = scanner.next();
                System.out.print("Enter contact number: ");
                String contact2 = scanner.next();
                System.out.print("Enter gender: ");
                String gender2 = scanner.next();
                selectedRoom.addGuest(new Guest(name2, contact2, gender2));
            }

            selectedRoom.setOccupied(true);
            System.out.println("Room booked successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Consume the invalid input
        } catch (RoomNotAvailableException e) {
            System.out.println("Room is already occupied or selected room is not available.");
        } catch (RoomNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void orderFood(int globalRoomNumber) {
        try {
            RoomType type = RoomType.fromGlobalRoomNumber(globalRoomNumber);
            int localIndex = RoomType.getLocalRoomIndex(globalRoomNumber, type);
            Room room = roomRegistry.getRoom(type, localIndex);

            if (!room.isOccupied()) {
                System.out.println("Room is not booked. Cannot order food.");
                return;
            }

            HotelConstants.displayFoodMenu();
            char wish;
            do {
                System.out.print("Enter food item number: ");
                int itemNo = scanner.nextInt();
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();

                FoodItem foodItem = HotelConstants.getFoodItemById(itemNo);
                if (foodItem != null) {
                    room.addFood(new OrderedFood(foodItem, quantity));
                    System.out.println("Item added to order.");
                } else {
                    System.out.println("Invalid food item number.");
                }

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Consume the invalid input
        } catch (RoomNotFoundException e) {
            System.out.println("Room " + globalRoomNumber + " does not exist.");
        } catch (Exception e) {
            System.out.println("An error occurred while ordering food: " + e.getMessage());
        }
    }

    public void checkoutRoom(int globalRoomNumber) {
        try {
            RoomType type = RoomType.fromGlobalRoomNumber(globalRoomNumber);
            int localIndex = RoomType.getLocalRoomIndex(globalRoomNumber, type);
            Room room = roomRegistry.getRoom(type, localIndex);

            if (!room.isOccupied()) {
                System.out.println("Room " + globalRoomNumber + " is already empty.");
                return;
            }

            System.out.println("Room " + globalRoomNumber + " is currently occupied by: " +
                    room.getGuests().stream().map(Guest::getName).collect(Collectors.joining(" and ")));

            System.out.println("Do you want to checkout? (y/n)");
            char wish = scanner.next().charAt(0);

            if (wish == 'y' || wish == 'Y') {
                generateBill(room);
                room.checkout();
                System.out.println("Room " + globalRoomNumber + " deallocated successfully.");
            } else {
                System.out.println("Checkout cancelled.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Consume the invalid input
        } catch (RoomNotFoundException e) {
            System.out.println("Room " + globalRoomNumber + " does not exist.");
        } catch (Exception e) {
            System.out.println("An error occurred during checkout: " + e.getMessage());
        }
    }

    private void generateBill(Room room) {
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.printf("\nRoom Charge - %.2f%n", room.basePrice);

        if (!room.getOrderedFood().isEmpty()) {
            System.out.println("\n===============");
            System.out.println("Food Charges:- ");
            System.out.println("===============");
            System.out.println("Item        Quantity   Price");
            System.out.println("-------------------------");
            for (OrderedFood orderedFood : room.getOrderedFood()) {
                System.out.printf("%-10s%-10d%-10.2f%n", orderedFood.getFoodItem().getName(), orderedFood.getQuantity(), orderedFood.getTotalPrice());
            }
        }
        System.out.printf("\nTotal Amount - %.2f%n", room.getTotalBill());
    }
}

// --- Persistence Layer ---
class HotelDataStore {
    public void save(HotelRoomRegistry registry) {
        try (FileOutputStream fout = new FileOutputStream(HotelConstants.FILE_BACKUP);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(registry);
            System.out.println("Hotel data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public HotelRoomRegistry load() {
        File f = new File(HotelConstants.FILE_BACKUP);
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                System.out.println("Loading hotel data from backup...");
                return (HotelRoomRegistry) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error loading hotel data: " + e.getMessage());
                // Fallback to new registry if loading fails
                return new HotelRoomRegistry();
            }
        }
        System.out.println("No existing hotel data found. Starting with a new registry.");
        return new HotelRoomRegistry();
    }
}

// --- Main Application ---
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelDataStore dataStore = new HotelDataStore();
        HotelRoomRegistry hotelRegistry = dataStore.load();
        HotelManagement hotelManagement = new HotelManagement(hotelRegistry, sc);

        try {
            int choice, roomTypeOption;
            char continueProgram = 'y';

            do {
                System.out.println("\nEnter your choice :");
                System.out.println("1. Display room details");
                System.out.println("2. Display room availability");
                System.out.println("3. Book a room");
                System.out.println("4. Order food");
                System.out.println("5. Checkout room");
                System.out.println("6. Exit");
                System.out.print("Choice: ");

                try {
                    choice = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 6.");
                    sc.next(); // Consume the invalid input
                    continue;
                }

                switch (choice) {
                    case 1:
                        System.out.println("\nChoose room type :");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Room Type: ");
                        try {
                            roomTypeOption = sc.nextInt();
                            hotelManagement.displayRoomDetails(RoomType.fromOption(roomTypeOption));
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number between 1 and 4.");
                            sc.next();
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 2:
                        System.out.println("\nChoose room type :");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Room Type: ");
                        try {
                            roomTypeOption = sc.nextInt();
                            hotelManagement.displayRoomAvailability(RoomType.fromOption(roomTypeOption));
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number between 1 and 4.");
                            sc.next();
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("\nChoose room type :");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Room Type: ");
                        try {
                            roomTypeOption = sc.nextInt();
                            hotelManagement.bookRoom(RoomType.fromOption(roomTypeOption));
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number between 1 and 4.");
                            sc.next();
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.print("Enter Room Number to order food: ");
                        try {
                            int roomNumber = sc.nextInt();
                            hotelManagement.orderFood(roomNumber);
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid room number.");
                            sc.next();
                        }
                        break;
                    case 5:
                        System.out.print("Enter Room Number to checkout: ");
                        try {
                            int roomNumber = sc.nextInt();
                            hotelManagement.checkoutRoom(roomNumber);
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid room number.");
                            sc.next();
                        }
                        break;
                    case 6:
                        System.out.println("Exiting program. Saving data...");
                        // Save data on a separate thread (as in original)
                        Thread t = new Thread(() -> dataStore.save(hotelRegistry));
                        t.start();
                        try {
                            t.join(); // Wait for the save thread to complete before exiting
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println("Program interrupted while saving data.");
                        }
                        return; // Exit the program
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

                System.out.println("\nContinue program? (y/n)");
                continueProgram = sc.next().charAt(0);
                if (!(continueProgram == 'y' || continueProgram == 'Y' || continueProgram == 'n' || continueProgram == 'N')) {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                }

            } while (continueProgram == 'y' || continueProgram == 'Y');

        } finally {
            sc.close();
            // Ensure data is saved even if an unexpected error occurs before the loop exits
            Thread t = new Thread(() -> dataStore.save(hotelRegistry));
            t.start();
        }
    }
}