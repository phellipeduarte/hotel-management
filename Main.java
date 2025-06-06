import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

// --- Enums for better readability and type safety ---

/**
 * Represents the type of food available in the hotel.
 */
enum FoodType {
    SANDWICH(1, "Sandwich", 50.0f),
    PASTA(2, "Pasta", 60.0f),
    NOODLES(3, "Noodles", 70.0f),
    COKE(4, "Coke", 30.0f);

    private final int itemNumber;
    private final String name;
    private final float pricePerUnit;

    FoodType(int itemNumber, String name, float pricePerUnit) {
        this.itemNumber = itemNumber;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getName() {
        return name;
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    public static FoodType fromItemNumber(int itemNumber) {
        for (FoodType type : FoodType.values()) {
            if (type.getItemNumber() == itemNumber) {
                return type;
            }
        }
        return null; // Or throw an IllegalArgumentException
    }
}

/**
 * Represents the different types of rooms.
 */
enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room", 4000.0, 10),
    DELUXE_DOUBLE(2, "Deluxe Double Room", 3000.0, 20),
    LUXURY_SINGLE(3, "Luxury Single Room", 2200.0, 10),
    DELUXE_SINGLE(4, "Deluxe Single Room", 1200.0, 20);

    private final int typeNumber;
    private final String description;
    private final double chargePerDay;
    private final int capacity;

    RoomType(int typeNumber, String description, double chargePerDay, int capacity) {
        this.typeNumber = typeNumber;
        this.description = description;
        this.chargePerDay = chargePerDay;
        this.capacity = capacity;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getChargePerDay() {
        return chargePerDay;
    }

    public int getCapacity() {
        return capacity;
    }

    public static RoomType fromTypeNumber(int typeNumber) {
        for (RoomType type : RoomType.values()) {
            if (type.getTypeNumber() == typeNumber) {
                return type;
            }
        }
        return null; // Or throw an IllegalArgumentException
    }
}

// --- Exceptions ---

/**
 * Custom exception for when a room is not available.
 */
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room Not Available!");
    }
}

// --- Model Classes ---

/**
 * Represents a food item ordered by a customer.
 */
class Food implements Serializable {
    private FoodType type;
    private int quantity;
    private float price;

    public Food(FoodType type, int quantity) {
        this.type = type;
        this.quantity = quantity;
        this.price = type.getPricePerUnit() * quantity;
    }

    public FoodType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%-10s%-10d%-10.2f", type.getName(), quantity, price);
    }
}

/**
 * Abstract base class for all room types.
 * Encapsulates common room properties and customer details.
 */
abstract class Room implements Serializable {
    private String primaryGuestName;
    private String primaryGuestContact;
    private String primaryGuestGender;
    private List<Food> orderedFood;
    private RoomType roomType;

    public Room(String primaryGuestName, String primaryGuestContact, String primaryGuestGender, RoomType roomType) {
        this.primaryGuestName = primaryGuestName;
        this.primaryGuestContact = primaryGuestContact;
        this.primaryGuestGender = primaryGuestGender;
        this.orderedFood = new ArrayList<>();
        this.roomType = roomType;
    }

    // Default constructor for serialization
    public Room() {
        this.orderedFood = new ArrayList<>();
    }

    public String getPrimaryGuestName() {
        return primaryGuestName;
    }

    public String getPrimaryGuestContact() {
        return primaryGuestContact;
    }

    public String getPrimaryGuestGender() {
        return primaryGuestGender;
    }

    public List<Food> getOrderedFood() {
        return orderedFood;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void addFood(Food food) {
        this.orderedFood.add(food);
    }

    public abstract String getRoomDetails();
}

/**
 * Represents a single room.
 */
class SingleRoom extends Room implements Serializable {
    public SingleRoom(String primaryGuestName, String primaryGuestContact, String primaryGuestGender, RoomType roomType) {
        super(primaryGuestName, primaryGuestContact, primaryGuestGender, roomType);
    }

    public SingleRoom() {
        super();
    }

    @Override
    public String getRoomDetails() {
        return "Number of single beds : 1\nAC : " + (getRoomType() == RoomType.LUXURY_SINGLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + getRoomType().getChargePerDay();
    }
}

/**
 * Represents a double room, extending SingleRoom to include a second guest.
 * A better design might be to have a Guest class and a list of Guests in the Room class,
 * but for refactoring the existing structure, this extends the original logic.
 */
class DoubleRoom extends Room implements Serializable {
    private String secondaryGuestName;
    private String secondaryGuestContact;
    private String secondaryGuestGender;

    public DoubleRoom(String primaryGuestName, String primaryGuestContact, String primaryGuestGender,
                      String secondaryGuestName, String secondaryGuestContact, String secondaryGuestGender,
                      RoomType roomType) {
        super(primaryGuestName, primaryGuestContact, primaryGuestGender, roomType);
        this.secondaryGuestName = secondaryGuestName;
        this.secondaryGuestContact = secondaryGuestContact;
        this.secondaryGuestGender = secondaryGuestGender;
    }

    public DoubleRoom() {
        super();
    }

    public String getSecondaryGuestName() {
        return secondaryGuestName;
    }

    public String getSecondaryGuestContact() {
        return secondaryGuestContact;
    }

    public String getSecondaryGuestGender() {
        return secondaryGuestGender;
    }

    @Override
    public String getRoomDetails() {
        return "Number of double beds : 1\nAC : " + (getRoomType() == RoomType.LUXURY_DOUBLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + getRoomType().getChargePerDay();
    }
}

/**
 * A holder class to manage all types of rooms.
 * Uses Lists instead of raw arrays for better flexibility.
 */
class HotelRoomRegistry implements Serializable {
    private List<DoubleRoom> luxuryDoubleRooms;
    private List<DoubleRoom> deluxeDoubleRooms;
    private List<SingleRoom> luxurySingleRooms;
    private List<SingleRoom> deluxeSingleRooms;

    public HotelRoomRegistry() {
        luxuryDoubleRooms = new ArrayList<>(RoomType.LUXURY_DOUBLE.getCapacity());
        deluxeDoubleRooms = new ArrayList<>(RoomType.DELUXE_DOUBLE.getCapacity());
        luxurySingleRooms = new ArrayList<>(RoomType.LUXURY_SINGLE.getCapacity());
        deluxeSingleRooms = new ArrayList<>(RoomType.DELUXE_SINGLE.getCapacity());

        // Initialize with nulls to represent empty rooms
        for (int i = 0; i < RoomType.LUXURY_DOUBLE.getCapacity(); i++) luxuryDoubleRooms.add(null);
        for (int i = 0; i < RoomType.DELUXE_DOUBLE.getCapacity(); i++) deluxeDoubleRooms.add(null);
        for (int i = 0; i < RoomType.LUXURY_SINGLE.getCapacity(); i++) luxurySingleRooms.add(null);
        for (int i = 0; i < RoomType.DELUXE_SINGLE.getCapacity(); i++) deluxeSingleRooms.add(null);
    }

    public List<DoubleRoom> getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public List<DoubleRoom> getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public List<SingleRoom> getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public List<SingleRoom> getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }

    // Helper method to get a room by its type and index
    public Room getRoom(RoomType type, int index) {
        switch (type) {
            case LUXURY_DOUBLE:
                return luxuryDoubleRooms.get(index);
            case DELUXE_DOUBLE:
                return deluxeDoubleRooms.get(index);
            case LUXURY_SINGLE:
                return luxurySingleRooms.get(index);
            case DELUXE_SINGLE:
                return deluxeSingleRooms.get(index);
            default:
                return null;
        }
    }

    // Helper method to set a room by its type and index
    public void setRoom(RoomType type, int index, Room room) {
        switch (type) {
            case LUXURY_DOUBLE:
                luxuryDoubleRooms.set(index, (DoubleRoom) room);
                break;
            case DELUXE_DOUBLE:
                deluxeDoubleRooms.set(index, (DoubleRoom) room);
                break;
            case LUXURY_SINGLE:
                luxurySingleRooms.set(index, (SingleRoom) room);
                break;
            case DELUXE_SINGLE:
                deluxeSingleRooms.set(index, (SingleRoom) room);
                break;
        }
    }

    public int getAvailableRoomsCount(RoomType type) {
        List<? extends Room> rooms;
        switch (type) {
            case LUXURY_DOUBLE:
                rooms = luxuryDoubleRooms;
                break;
            case DELUXE_DOUBLE:
                rooms = deluxeDoubleRooms;
                break;
            case LUXURY_SINGLE:
                rooms = luxurySingleRooms;
                break;
            case DELUXE_SINGLE:
                rooms = deluxeSingleRooms;
                break;
            default:
                return 0;
        }
        return (int) rooms.stream().filter(Objects::isNull).count();
    }
}

// --- Service Class for Hotel Operations ---

class  HotelService {
    private static HotelRoomRegistry hotelRoomRegistry = new HotelRoomRegistry();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String BACKUP_FILE = "backup";

    // Static initializer to load data on startup
    static {
        loadHotelData();
    }

    private static void loadHotelData() {
        File file = new File(BACKUP_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                hotelRoomRegistry = (HotelRoomRegistry) ois.readObject();
                System.out.println("Hotel data loaded from backup.");
            } catch (Exception e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
            }
        }
    }

    public static void saveHotelData() {
        try (FileOutputStream fos = new FileOutputStream(BACKUP_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(hotelRoomRegistry);
            System.out.println("Hotel data saved successfully.");
        } catch (Exception e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static void displayRoomFeatures(RoomType roomType) {
        if (roomType == null) {
            System.out.println("Invalid room type selected.");
            return;
        }
        System.out.println("\n--- " + roomType.getDescription() + " Features ---");
        System.out.println(roomType.getDescription().contains("Double") ? "Number of double beds : 1" : "Number of single beds : 1");
        System.out.println("AC : " + (roomType == RoomType.LUXURY_DOUBLE || roomType == RoomType.LUXURY_SINGLE ? "Yes" : "No"));
        System.out.println("Free breakfast : Yes");
        System.out.println("Charge per day: " + roomType.getChargePerDay());
    }

    public static void displayRoomAvailability(RoomType roomType) {
        if (roomType == null) {
            System.out.println("Invalid room type selected.");
            return;
        }
        int availableRooms = hotelRoomRegistry.getAvailableRoomsCount(roomType);
        System.out.println("Number of " + roomType.getDescription() + " rooms available: " + availableRooms);
    }

    public static void bookRoom(RoomType roomType) {
        if (roomType == null) {
            System.out.println("Invalid room type selected.");
            return;
        }

        List<? extends Room> rooms;
        int startIndex;

        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = hotelRoomRegistry.getLuxuryDoubleRooms();
                startIndex = 1;
                break;
            case DELUXE_DOUBLE:
                rooms = hotelRoomRegistry.getDeluxeDoubleRooms();
                startIndex = 11;
                break;
            case LUXURY_SINGLE:
                rooms = hotelRoomRegistry.getLuxurySingleRooms();
                startIndex = 31;
                break;
            case DELUXE_SINGLE:
                rooms = hotelRoomRegistry.getDeluxeSingleRooms();
                startIndex = 41;
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        System.out.println("\nChoose room number from available " + roomType.getDescription() + " rooms:");
        String availableRoomNumbers = rooms.stream()
                .filter(Objects::isNull)
                .map(r -> rooms.indexOf(r) + startIndex)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println(availableRoomNumbers.isEmpty() ? "No rooms available." : availableRoomNumbers);

        if (availableRoomNumbers.isEmpty()) {
            return;
        }

        System.out.print("Enter room number: ");
        try {
            int roomNumber = scanner.nextInt();
            int roomIndex = roomNumber - startIndex;

            if (roomIndex < 0 || roomIndex >= rooms.size() || rooms.get(roomIndex) != null) {
                throw new RoomNotAvailableException();
            }

            scanner.nextLine(); // Consume newline

            System.out.print("Enter primary customer name: ");
            String name = scanner.nextLine();
            System.out.print("Enter contact number: ");
            String contact = scanner.nextLine();
            System.out.print("Enter gender: ");
            String gender = scanner.nextLine();

            Room newRoom;
            if (roomType.getDescription().contains("Double")) {
                System.out.print("Enter secondary customer name: ");
                String name2 = scanner.nextLine();
                System.out.print("Enter second contact number: ");
                String contact2 = scanner.nextLine();
                System.out.print("Enter second gender: ");
                String gender2 = scanner.nextLine();
                newRoom = new DoubleRoom(name, contact, gender, name2, contact2, gender2, roomType);
            } else {
                newRoom = new SingleRoom(name, contact, gender, roomType);
            }
            hotelRoomRegistry.setRoom(roomType, roomIndex, newRoom);
            System.out.println("Room Booked Successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Consume the invalid input
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage() + " The room might be occupied or the room number is invalid.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void orderFood(int roomNumber) {
        Room room = findRoomByGlobalNumber(roomNumber);
        if (room == null) {
            System.out.println("Room doesn't exist or is not booked.");
            return;
        }

        System.out.println("\n==========\n   Menu:   \n==========\n");
        Arrays.stream(FoodType.values())
                .forEach(food -> System.out.printf("%d.%s\t\tRs.%.0f\n", food.getItemNumber(), food.getName(), food.getPricePerUnit()));

        char wish;
        do {
            try {
                System.out.print("Enter item number: ");
                int itemChoice = scanner.nextInt();
                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();

                FoodType foodType = FoodType.fromItemNumber(itemChoice);
                if (foodType != null) {
                    room.addFood(new Food(foodType, quantity));
                    System.out.println("Item added to order.");
                } else {
                    System.out.println("Invalid food item number.");
                }

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.next().charAt(0);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
                wish = 'y'; // Continue loop to allow re-entry
            } catch (Exception e) {
                System.out.println("An error occurred during ordering: " + e.getMessage());
                wish = 'n'; // Exit loop on unexpected error
            }
        } while (wish == 'y' || wish == 'Y');
    }

    public static void checkoutRoom(int roomNumber) {
        Room room = findRoomByGlobalNumber(roomNumber);
        if (room == null) {
            System.out.println("Room doesn't exist or is already empty.");
            return;
        }

        System.out.println("Room " + roomNumber + " is used by " + room.getPrimaryGuestName());
        System.out.println("Do you want to checkout? (y/n)");
        char wish = scanner.next().charAt(0);

        if (wish == 'y' || wish == 'Y') {
            generateBill(room, roomNumber);
            // Deallocate the room by setting it to null in the registry
            int roomIndex = getRoomIndex(roomNumber, room.getRoomType());
            if (roomIndex != -1) {
                hotelRoomRegistry.setRoom(room.getRoomType(), roomIndex, null);
                System.out.println("Room " + roomNumber + " deallocated successfully.");
            }
        }
    }

    private static void generateBill(Room room, int roomNumber) {
        double totalAmount = room.getRoomType().getChargePerDay();

        System.out.println("\n*******");
        System.out.println(" Bill for Room " + roomNumber + ":-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + room.getRoomType().getChargePerDay());

        if (!room.getOrderedFood().isEmpty()) {
            System.out.println("\n===============");
            System.out.println("Food Charges:- ");
            System.out.println("===============");
            System.out.println("Item        Quantity   Price");
            System.out.println("-------------------------");
            for (Food food : room.getOrderedFood()) {
                System.out.println(food);
                totalAmount += food.getPrice();
            }
        } else {
            System.out.println("\nNo food ordered.");
        }

        System.out.println("\nTotal Amount - " + String.format("%.2f", totalAmount));
    }

    /**
     * Finds a Room object by its global room number.
     * This handles the mapping from a user-facing room number (e.g., 1, 11, 31, 41)
     * to the internal array index and room type.
     * @param globalRoomNumber The room number as presented to the user.
     * @return The Room object if found and booked, otherwise null.
     */
    private static Room findRoomByGlobalNumber(int globalRoomNumber) {
        if (globalRoomNumber > 60 || globalRoomNumber <= 0) {
            return null;
        }

        if (globalRoomNumber <= 10) { // Luxury Double (1-10)
            return hotelRoomRegistry.getLuxuryDoubleRooms().get(globalRoomNumber - 1);
        } else if (globalRoomNumber <= 30) { // Deluxe Double (11-30)
            return hotelRoomRegistry.getDeluxeDoubleRooms().get(globalRoomNumber - 11);
        } else if (globalRoomNumber <= 40) { // Luxury Single (31-40)
            return hotelRoomRegistry.getLuxurySingleRooms().get(globalRoomNumber - 31);
        } else { // Deluxe Single (41-60)
            return hotelRoomRegistry.getDeluxeSingleRooms().get(globalRoomNumber - 41);
        }
    }

    /**
     * Gets the internal index for a given global room number and room type.
     * @param globalRoomNumber The global room number.
     * @param roomType The type of the room.
     * @return The internal index, or -1 if not found/invalid.
     */
    private static int getRoomIndex(int globalRoomNumber, RoomType roomType) {
        switch (roomType) {
            case LUXURY_DOUBLE: return globalRoomNumber - 1;
            case DELUXE_DOUBLE: return globalRoomNumber - 11;
            case LUXURY_SINGLE: return globalRoomNumber - 31;
            case DELUXE_SINGLE: return globalRoomNumber - 41;
            default: return -1;
        }
    }

    public static void closeScanner() {
        scanner.close();
    }
}

// --- Main Application Class ---

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) { // Use try-with-resources for Scanner
            int choice;
            char continueChoice;

            do {
                System.out.println("\n--- Hotel Management System ---");
                System.out.println("1. Display room details");
                System.out.println("2. Display room availability");
                System.out.println("3. Book a room");
                System.out.println("4. Order food");
                System.out.println("5. Checkout room");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                try {
                    choice = sc.nextInt();
                    int roomTypeChoice;
                    int roomNumber;

                    switch (choice) {
                        case 1:
                            System.out.println("\nChoose room type:");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            roomTypeChoice = sc.nextInt();
                            HotelService.displayRoomFeatures(RoomType.fromTypeNumber(roomTypeChoice));
                            break;
                        case 2:
                            System.out.println("\nChoose room type:");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            roomTypeChoice = sc.nextInt();
                            HotelService.displayRoomAvailability(RoomType.fromTypeNumber(roomTypeChoice));
                            break;
                        case 3:
                            System.out.println("\nChoose room type:");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            roomTypeChoice = sc.nextInt();
                            HotelService.bookRoom(RoomType.fromTypeNumber(roomTypeChoice));
                            break;
                        case 4:
                            System.out.print("Enter room number to order food: ");
                            roomNumber = sc.nextInt();
                            HotelService.orderFood(roomNumber);
                            break;
                        case 5:
                            System.out.print("Enter room number to checkout: ");
                            roomNumber = sc.nextInt();
                            HotelService.checkoutRoom(roomNumber);
                            break;
                        case 6:
                            System.out.println("Exiting application. Saving data...");
                            HotelService.closeScanner(); // Close scanner before exiting
                            return; // Exit the program
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.nextLine(); // Consume the invalid input
                    choice = 0; // Set to 0 to re-enter the loop
                }

                System.out.print("\nDo you want to continue? (y/n): ");
                continueChoice = sc.next().charAt(0);
                sc.nextLine(); // Consume newline
                while (!(continueChoice == 'y' || continueChoice == 'Y' || continueChoice == 'n' || continueChoice == 'N')) {
                    System.out.println("Invalid option. Please enter 'y' or 'n'.");
                    System.out.print("Do you want to continue? (y/n): ");
                    continueChoice = sc.next().charAt(0);
                    sc.nextLine(); // Consume newline
                }

            } while (continueChoice == 'y' || continueChoice == 'Y');

        } finally {
            // This ensures data is saved even if an unexpected exception occurs
            // or if the application exits in a way that doesn't explicitly call saveHotelData.
            // However, for a clean exit, it's better to call it before 'return'.
            Thread t = new Thread(HotelService::saveHotelData);
            t.start();
        }
    }
}

// --- Write class (for serialization, kept similar for functionality but improved) ---
class WriteTask implements Runnable {
    private HotelRoomRegistry hotelRoomRegistry;
    private String filename;

    public WriteTask(HotelRoomRegistry hotelRoomRegistry, String filename) {
        this.hotelRoomRegistry = hotelRoomRegistry;
        this.filename = filename;
    }

    @Override
    public void run() {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(hotelRoomRegistry);
        } catch (Exception e) {
            System.err.println("Error in writing backup: " + e.getMessage());
        }
    }
}