import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// Enum to represent the food items available in the hotel
enum FoodItem {
    SANDWICH("Sandwich", 50.0f),
    PASTA("Pasta", 60.0f),
    NOODLES("Noodles", 70.0f),
    COKE("Coke", 30.0f);

    private final String name;
    private final float pricePerUnit;

    FoodItem(String name, float pricePerUnit) {
        this.name = name;
        this.pricePerUnit = pricePerUnit;
    }

    public String getName() {
        return name;
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    // Helper method to get a FoodItem by its 1-based item number
    public static FoodItem getByItemNumber(int itemNumber) {
        if (itemNumber > 0 && itemNumber <= values().length) {
            return values()[itemNumber - 1];
        }
        return null; // Returns null if the item number is invalid
    }
}

// Class representing a food item in an order
class Food implements Serializable {
    private final FoodItem item; // Uses the FoodItem enum for the item type
    private final int quantity;
    private final float totalPrice; // Total price for this item and quantity

    public Food(FoodItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Food item cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.item = item;
        this.quantity = quantity;
        this.totalPrice = item.getPricePerUnit() * quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return item.getName() + " (x" + quantity + ") - R$" + String.format("%.2f", totalPrice);
    }
}

// Class representing a hotel guest
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Contact: " + contact + ", Gender: " + gender;
    }
}

// Abstract base class for all room types
abstract class Room implements Serializable {
    protected List<Food> foodOrders = new ArrayList<>();
    protected float basePrice; // Base price of the room per night

    public Room(float basePrice) {
        this.basePrice = basePrice;
    }

    public void addFoodOrder(Food food) {
        foodOrders.add(food);
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    public float getBasePrice() {
        return basePrice;
    }

    // Abstract methods that subclasses must implement
    public abstract String getRoomDetails();
    public abstract String getOccupantNames(); // To facilitate displaying who is in the room
    public abstract int getCapacity(); // Guest capacity
}

// Class representing a single room
class SingleRoom extends Room implements Serializable {
    private Guest guest;

    public SingleRoom(Guest guest, float basePrice) {
        super(basePrice);
        this.guest = guest;
    }

    // Empty constructor for deserialization
    public SingleRoom() {
        super(0); // Base price will be set by the room type in Holder/Hotel
        this.guest = null;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @Override
    public String getRoomDetails() {
        return "Single Room: " + (guest != null ? guest.getName() : "Vacant");
    }

    @Override
    public String getOccupantNames() {
        return (guest != null) ? guest.getName() : "N/A";
    }

    @Override
    public int getCapacity() {
        return 1;
    }
}

// Class representing a double room
class DoubleRoom extends Room implements Serializable {
    private Guest guest1;
    private Guest guest2;

    public DoubleRoom(Guest guest1, Guest guest2, float basePrice) {
        super(basePrice);
        this.guest1 = guest1;
        this.guest2 = guest2;
    }

    // Empty constructor for deserialization
    public DoubleRoom() {
        super(0); // Base price will be set by the room type in Holder/Hotel
        this.guest1 = null;
        this.guest2 = null;
    }

    public Guest getGuest1() {
        return guest1;
    }

    public void setGuest1(Guest guest1) {
        this.guest1 = guest1;
    }

    public Guest getGuest2() {
        return guest2;
    }

    public void setGuest2(Guest guest2) {
        this.guest2 = guest2;
    }

    @Override
    public String getRoomDetails() {
        return "Double Room: " + (guest1 != null ? guest1.getName() : "Vacant")
                + (guest2 != null ? " and " + guest2.getName() : "");
    }

    @Override
    public String getOccupantNames() {
        String names = (guest1 != null) ? guest1.getName() : "";
        if (guest2 != null) {
            names += (names.isEmpty() ? "" : " and ") + guest2.getName();
        }
        return names.isEmpty() ? "N/A" : names;
    }

    @Override
    public int getCapacity() {
        return 2;
    }
}

// Custom exception for when a room is not available
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room not available!");
    }

    public RoomNotAvailableException(String message) {
        super(message);
    }
}

// Constants for room prices and array sizes
final class RoomConstants {
    public static final int LUXURY_DOUBLE_ROOM_COUNT = 10;
    public static final int DELUXE_DOUBLE_ROOM_COUNT = 20;
    public static final int LUXURY_SINGLE_ROOM_COUNT = 10;
    public static final int DELUXE_SINGLE_ROOM_COUNT = 20;

    public static final float LUXURY_DOUBLE_PRICE = 4000.0f;
    public static final float DELUXE_DOUBLE_PRICE = 3000.0f;
    public static final float LUXURY_SINGLE_PRICE = 2200.0f;
    public static final float DELUXE_SINGLE_PRICE = 1200.0f;

    // Prefix for room numbers for better identification (e.g., Luxury Double starts at 101)
    public static final int LUXURY_DOUBLE_ROOM_PREFIX = 1; // e.g., 101-110
    public static final int DELUXE_DOUBLE_ROOM_PREFIX = 2; // e.g., 201-220
    public static final int LUXURY_SINGLE_ROOM_PREFIX = 3; // e.g., 301-310
    public static final int DELUXE_SINGLE_ROOM_PREFIX = 4; // e.g., 401-420
}

// Class that holds all hotel room data
class HotelData implements Serializable {
    // Using the base Room class to allow polymorphism
    private final DoubleRoom[] luxuryDoubleRooms;
    private final DoubleRoom[] deluxeDoubleRooms;
    private final SingleRoom[] luxurySingleRooms;
    private final SingleRoom[] deluxeSingleRooms;

    public HotelData() {
        luxuryDoubleRooms = new DoubleRoom[RoomConstants.LUXURY_DOUBLE_ROOM_COUNT];
        deluxeDoubleRooms = new DoubleRoom[RoomConstants.DELUXE_DOUBLE_ROOM_COUNT];
        luxurySingleRooms = new SingleRoom[RoomConstants.LUXURY_SINGLE_ROOM_COUNT];
        deluxeSingleRooms = new SingleRoom[RoomConstants.DELUXE_SINGLE_ROOM_COUNT];
    }

    // Getters for room arrays
    public DoubleRoom[] getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public DoubleRoom[] getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public SingleRoom[] getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public SingleRoom[] getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }

    /**
     * Gets a room by its type and internal array index.
     * @param roomType The type of room (1-4 as per menu).
     * @param index The 0-based index within its specific room array.
     * @return The Room object or null if not found.
     */
    public Room getRoom(int roomType, int index) {
        switch (roomType) {
            case 1: return luxuryDoubleRooms[index];
            case 2: return deluxeDoubleRooms[index];
            case 3: return luxurySingleRooms[index];
            case 4: return deluxeSingleRooms[index];
            default: return null;
        }
    }

    /**
     * Sets a room object at a specific type and internal array index.
     * @param roomType The type of room.
     * @param index The 0-based index.
     * @param room The Room object to set (can be null for deallocation).
     */
    public void setRoom(int roomType, int index, Room room) {
        switch (roomType) {
            case 1: luxuryDoubleRooms[index] = (DoubleRoom) room; break;
            case 2: deluxeDoubleRooms[index] = (DoubleRoom) room; break;
            case 3: luxurySingleRooms[index] = (SingleRoom) room; break;
            case 4: deluxeSingleRooms[index] = (SingleRoom) room; break;
            default:
                // Handle invalid roomType, e.g., throw IllegalArgumentException
                System.err.println("Error: Invalid room type for setting room.");
                break;
        }
    }

    /**
     * Determines the room type (1-4) based on the user-facing room number.
     * @param userRoomNumber The room number provided by the user (e.g., 101, 215).
     * @return The room type (1-4) or -1 if the number does not match any known type range.
     */
    public int getRoomTypeByUserNumber(int userRoomNumber) {
        if (userRoomNumber >= (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + 1) &&
                userRoomNumber <= (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + RoomConstants.LUXURY_DOUBLE_ROOM_COUNT)) {
            return 1;
        } else if (userRoomNumber >= (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + 1) &&
                userRoomNumber <= (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + RoomConstants.DELUXE_DOUBLE_ROOM_COUNT)) {
            return 2;
        } else if (userRoomNumber >= (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + 1) &&
                userRoomNumber <= (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + RoomConstants.LUXURY_SINGLE_ROOM_COUNT)) {
            return 3;
        } else if (userRoomNumber >= (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + 1) &&
                userRoomNumber <= (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + RoomConstants.DELUXE_SINGLE_ROOM_COUNT)) {
            return 4;
        }
        return -1; // Room number does not exist or invalid
    }

    /**
     * Determines the internal array index based on the user-facing room number and room type.
     * @param roomType The determined room type (1-4).
     * @param userRoomNumber The user-facing room number.
     * @return The 0-based array index, or -1 if invalid.
     */
    public int getRoomIndexByUserNumber(int roomType, int userRoomNumber) {
        switch (roomType) {
            case 1: return userRoomNumber - (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + 1);
            case 2: return userRoomNumber - (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + 1);
            case 3: return userRoomNumber - (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + 1);
            case 4: return userRoomNumber - (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + 1);
            default: return -1;
        }
    }
}

// Class that manages hotel operations
class HotelManager {
    // Single instance of HotelData to maintain hotel state
    private static HotelData hotelData = new HotelData();
    private static final Scanner scanner = new Scanner(System.in); // Shared Scanner

    private static final String DATA_FILE = "hotel_data.ser";

    // Static block to load hotel data when the class is initialized
    static {
        loadHotelData();
    }

    // Private constructor to prevent instantiation (if the class is purely static utility)
    private HotelManager() {}

    // --- Helper Methods for User Input ---
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.next(); // Use next() for single-word tokens
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next(); // Consume the invalid input to prevent infinite loop
            }
        }
    }

    // --- Business Logic Methods ---

    /**
     * Collects guest(s) details and creates Guest instances.
     * @param isDoubleRoom true if it's a double room, false if single.
     * @return an array of Guest objects.
     */
    private static Guest[] getGuestDetails(boolean isDoubleRoom) {
        String name = readString("Enter customer name: ");
        String contact = readString("Enter contact number: ");
        String gender = readString("Enter gender: ");
        Guest guest1 = new Guest(name, contact, gender);

        if (isDoubleRoom) {
            System.out.println("\n--- Second Guest Details ---");
            String name2 = readString("Enter second customer name: ");
            String contact2 = readString("Enter second contact number: ");
            String gender2 = readString("Enter second gender: ");
            Guest guest2 = new Guest(name2, contact2, gender2);
            return new Guest[]{guest1, guest2};
        } else {
            return new Guest[]{guest1};
        }
    }

    /**
     * Displays the features of a specific room type.
     * @param roomType The type of room (1-Luxury Double, 2-Deluxe Double, 3-Luxury Single, 4-Deluxe Single).
     */
    public static void displayRoomFeatures(int roomType) {
        System.out.println("\n--- Room Features ---");
        switch (roomType) {
            case 1: // Luxury Double
                System.out.println("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: R$" + RoomConstants.LUXURY_DOUBLE_PRICE);
                break;
            case 2: // Deluxe Double
                System.out.println("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: R$" + RoomConstants.DELUXE_DOUBLE_PRICE);
                break;
            case 3: // Luxury Single
                System.out.println("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: R$" + RoomConstants.LUXURY_SINGLE_PRICE);
                break;
            case 4: // Deluxe Single
                System.out.println("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: R$" + RoomConstants.DELUXE_SINGLE_PRICE);
                break;
            default:
                System.out.println("Invalid room option.");
                break;
        }
    }

    /**
     * Shows available rooms for a given type.
     * @param roomType The type of room.
     */
    public static void displayRoomAvailability(int roomType) {
        int count = 0;
        int startIndex = 0;
        Room[] rooms = null; // Use Room[] for polymorphism

        System.out.print("\nAvailable room numbers: ");

        switch (roomType) {
            case 1:
                rooms = hotelData.getLuxuryDoubleRooms();
                startIndex = RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100;
                break;
            case 2:
                rooms = hotelData.getDeluxeDoubleRooms();
                startIndex = RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100;
                break;
            case 3:
                rooms = hotelData.getLuxurySingleRooms();
                startIndex = RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100;
                break;
            case 4:
                rooms = hotelData.getDeluxeSingleRooms();
                startIndex = RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100;
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        if (rooms != null) {
            for (int j = 0; j < rooms.length; j++) {
                if (rooms[j] == null) {
                    System.out.print((startIndex + j + 1) + ", ");
                    count++;
                }
            }
        }
        System.out.println("\nTotal number of rooms available: " + count);
    }

    /**
     * Books a room for a customer.
     * @param roomType The type of room to be booked.
     */
    public static void bookRoom(int roomType) {
        displayRoomAvailability(roomType); // Show available first
        int userRoomNumber = readInt("Enter the room number you wish to book: ");

        int index = -1; // 0-based index in the array
        Room[] roomsArray = null;
        float roomPrice = 0.0f;
        boolean isDoubleRoom = false;

        // Determine the correct array, index, price, and room type details
        switch (roomType) {
            case 1:
                if (userRoomNumber >= (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + 1) &&
                        userRoomNumber <= (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + RoomConstants.LUXURY_DOUBLE_ROOM_COUNT)) {
                    index = userRoomNumber - (RoomConstants.LUXURY_DOUBLE_ROOM_PREFIX * 100 + 1);
                    roomsArray = hotelData.getLuxuryDoubleRooms();
                    roomPrice = RoomConstants.LUXURY_DOUBLE_PRICE;
                    isDoubleRoom = true;
                }
                break;
            case 2:
                if (userRoomNumber >= (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + 1) &&
                        userRoomNumber <= (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + RoomConstants.DELUXE_DOUBLE_ROOM_COUNT)) {
                    index = userRoomNumber - (RoomConstants.DELUXE_DOUBLE_ROOM_PREFIX * 100 + 1);
                    roomsArray = hotelData.getDeluxeDoubleRooms();
                    roomPrice = RoomConstants.DELUXE_DOUBLE_PRICE;
                    isDoubleRoom = true;
                }
                break;
            case 3:
                if (userRoomNumber >= (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + 1) &&
                        userRoomNumber <= (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + RoomConstants.LUXURY_SINGLE_ROOM_COUNT)) {
                    index = userRoomNumber - (RoomConstants.LUXURY_SINGLE_ROOM_PREFIX * 100 + 1);
                    roomsArray = hotelData.getLuxurySingleRooms();
                    roomPrice = RoomConstants.LUXURY_SINGLE_PRICE;
                    isDoubleRoom = false;
                }
                break;
            case 4:
                if (userRoomNumber >= (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + 1) &&
                        userRoomNumber <= (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + RoomConstants.DELUXE_SINGLE_ROOM_COUNT)) {
                    index = userRoomNumber - (RoomConstants.DELUXE_SINGLE_ROOM_PREFIX * 100 + 1);
                    roomsArray = hotelData.getDeluxeSingleRooms();
                    roomPrice = RoomConstants.DELUXE_SINGLE_PRICE;
                    isDoubleRoom = false;
                }
                break;
            default:
                System.out.println("Invalid room type selected.");
                return;
        }

        // Validate index and availability
        if (roomsArray == null || index == -1 || index >= roomsArray.length || roomsArray[index] != null) {
            System.out.println("Invalid room number or room is already occupied.");
            return;
        }

        try {
            if (roomsArray[index] != null) { // Double check, though should be caught by previous if
                throw new RoomNotAvailableException("Room " + userRoomNumber + " is already occupied.");
            }

            Guest[] guests = getGuestDetails(isDoubleRoom);

            if (isDoubleRoom) {
                DoubleRoom newRoom = new DoubleRoom(guests[0], guests[1], roomPrice);
                hotelData.setRoom(roomType, index, newRoom);
            } else {
                SingleRoom newRoom = new SingleRoom(guests[0], roomPrice);
                hotelData.setRoom(roomType, index, newRoom);
            }

            System.out.println("Room " + userRoomNumber + " booked successfully!");
            saveHotelData(); // Save data after booking
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred while booking the room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Allows ordering food for a specific room.
     * @param userRoomNumber The user-facing room number.
     */
    public static void orderFood(int userRoomNumber) {
        int roomType = hotelData.getRoomTypeByUserNumber(userRoomNumber);
        int index = hotelData.getRoomIndexByUserNumber(roomType, userRoomNumber);

        Room targetRoom = null;
        if (roomType != -1 && index != -1) {
            targetRoom = hotelData.getRoom(roomType, index);
        }

        if (targetRoom == null) {
            System.out.println("Room " + userRoomNumber + " not found or not booked.");
            return;
        }

        System.out.println("\n==========\n   Menu:  \n==========\n");
        for (int i = 0; i < FoodItem.values().length; i++) {
            FoodItem item = FoodItem.values()[i];
            System.out.printf("%d. %-15s Rs.%.2f%n", (i + 1), item.getName(), item.getPricePerUnit());
        }

        char wish;
        do {
            int itemNumber = readInt("Select item number: ");
            int quantity = readInt("Quantity: ");

            FoodItem selectedFoodItem = FoodItem.getByItemNumber(itemNumber);

            if (selectedFoodItem != null) {
                targetRoom.addFoodOrder(new Food(selectedFoodItem, quantity));
                System.out.println("Item added to order.");
            } else {
                System.out.println("Invalid item number.");
            }

            System.out.print("Do you want to order anything else? (y/n): ");
            wish = scanner.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');
        saveHotelData(); // Save data after ordering
    }

    /**
     * Generates the bill for a room and deallocates it if the user confirms.
     * @param userRoomNumber The user-facing room number.
     */
    public static void checkoutRoom(int userRoomNumber) {
        int roomType = hotelData.getRoomTypeByUserNumber(userRoomNumber);
        int index = hotelData.getRoomIndexByUserNumber(roomType, userRoomNumber);

        Room targetRoom = null;
        if (roomType != -1 && index != -1) {
            targetRoom = hotelData.getRoom(roomType, index);
        }

        if (targetRoom == null) {
            System.out.println("Room " + userRoomNumber + " is empty or does not exist.");
            return;
        }

        System.out.println("Room " + userRoomNumber + " used by: " + targetRoom.getOccupantNames());
        System.out.print("Do you want to checkout? (y/n): ");
        char confirm = scanner.next().charAt(0);

        if (confirm == 'y' || confirm == 'Y') {
            generateBill(targetRoom); // Generate the bill
            hotelData.setRoom(roomType, index, null); // Deallocate the room
            System.out.println("Room " + userRoomNumber + " deallocated successfully.");
            saveHotelData(); // Save data after checkout
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    /**
     * Generates the bill for a specific room.
     * @param room The Room object for which the bill will be generated.
     */
    private static void generateBill(Room room) {
        if (room == null) {
            System.out.println("Cannot generate bill for a null room.");
            return;
        }

        double totalAmount = 0;
        System.out.println("\n*******");
        System.out.println(" Bill:");
        System.out.println("*******");

        // Room details
        System.out.println("\nRoom Charges:");
        System.out.println("Type: " + room.getClass().getSimpleName()); // SingleRoom or DoubleRoom
        System.out.println("Occupant(s): " + room.getOccupantNames());
        System.out.printf("Daily Rate: R$%.2f%n", room.getBasePrice());
        totalAmount += room.getBasePrice();

        // Food details
        System.out.println("\n===============");
        System.out.println("Food Charges:");
        System.out.println("===============");
        if (room.getFoodOrders().isEmpty()) {
            System.out.println("No food orders.");
        } else {
            System.out.println("Item        Quantity    Price");
            System.out.println("-----------------------------");
            for (Food foodOrder : room.getFoodOrders()) {
                totalAmount += foodOrder.getTotalPrice();
                System.out.printf("%-12s%-12d%-10.2f%n",
                        foodOrder.getItem().getName(),
                        foodOrder.getQuantity(),
                        foodOrder.getTotalPrice());
            }
        }
        System.out.println("-----------------------------");
        System.out.printf("Total Amount: R$%.2f%n", totalAmount);
        System.out.println("*******");
    }

    // --- Persistence Methods ---
    private static void saveHotelData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(hotelData);
            System.out.println("Hotel data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
            // e.printStackTrace(); // For debugging
        }
    }

    private static void loadHotelData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            hotelData = (HotelData) in.readObject();
            System.out.println("Hotel data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Hotel data file not found. Starting with empty data.");
            hotelData = new HotelData(); // Initialize if file does not exist
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading hotel data: " + e.getMessage());
            // e.printStackTrace(); // For debugging
            hotelData = new HotelData(); // Initialize in case of loading error
        }
    }
}


public class Main {
    public static void main(String[] args) {
        // Initial hotel data loading is now handled statically within HotelManager.
        // HotelManager.loadHotelData() is called in HotelManager's static block.

        Scanner sc = new Scanner(System.in);
        int choice;
        int roomType;
        int roomNumber;

        do {
            System.out.println("\n====================================");
            System.out.println("       HOTEL MANAGEMENT SYSTEM      ");
            System.out.println("====================================");
            System.out.println("1. Display room details");
            System.out.println("2. Display room availability");
            System.out.println("3. Book room");
            System.out.println("4. Order food");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // Consume the invalid input to prevent infinite loop
                choice = 0; // Set an invalid choice to repeat the loop
                continue;
            }

            switch (choice) {
                case 1: // Display Room Details
                    System.out.println("\n--- Display Room Details ---");
                    System.out.println("Select room type :");
                    System.out.println("1. Luxury Double Room (Rate: R$" + RoomConstants.LUXURY_DOUBLE_PRICE + ")");
                    System.out.println("2. Deluxe Double Room (Rate: R$" + RoomConstants.DELUXE_DOUBLE_PRICE + ")");
                    System.out.println("3. Luxury Single Room (Rate: R$" + RoomConstants.LUXURY_SINGLE_PRICE + ")");
                    System.out.println("4. Deluxe Single Room (Rate: R$" + RoomConstants.DELUXE_SINGLE_PRICE + ")");
                    System.out.print("Enter your choice: ");
                    try {
                        roomType = sc.nextInt();
                        HotelManager.displayRoomFeatures(roomType);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input for room type.");
                        sc.next();
                    }
                    break;

                case 2: // Display Room Availability
                    System.out.println("\n--- Display Room Availability ---");
                    System.out.println("Select room type :");
                    System.out.println("1. Luxury Double Room");
                    System.out.println("2. Deluxe Double Room");
                    System.out.println("3. Luxury Single Room");
                    System.out.println("4. Deluxe Single Room");
                    System.out.print("Enter your choice: ");
                    try {
                        roomType = sc.nextInt();
                        HotelManager.displayRoomAvailability(roomType);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input for room type.");
                        sc.next();
                    }
                    break;

                case 3: // Book Room
                    System.out.println("\n--- Book Room ---");
                    System.out.println("Select room type to book:");
                    System.out.println("1. Luxury Double Room");
                    System.out.println("2. Deluxe Double Room");
                    System.out.println("3. Luxury Single Room");
                    System.out.println("4. Deluxe Single Room");
                    System.out.print("Enter your choice: ");
                    try {
                        roomType = sc.nextInt();
                        HotelManager.bookRoom(roomType);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input for room type.");
                        sc.next();
                    }
                    break;

                case 4: // Order Food
                    System.out.println("\n--- Order Food ---");
                    System.out.print("Enter Room Number: ");
                    try {
                        roomNumber = sc.nextInt();
                        // The room type is inferred by HotelManager.orderFood based on the room number
                        HotelManager.orderFood(roomNumber);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input for room number.");
                        sc.next();
                    }
                    break;

                case 5: // Checkout
                    System.out.println("\n--- Checkout ---");
                    System.out.print("Enter Room Number: ");
                    try {
                        roomNumber = sc.nextInt();
                        // The room type is inferred by HotelManager.checkoutRoom based on the room number
                        HotelManager.checkoutRoom(roomNumber);
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input for room number.");
                        sc.next();
                    }
                    break;

                case 6: // Exit
                    System.out.println("Exiting the system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 6.");
                    break;
            }

        } while (choice != 6);

        sc.close(); // Close the scanner when exiting the program.
    }
}