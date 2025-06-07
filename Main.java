import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// --- Constants ---
final class HotelConstants {
    // Food Prices
    public static final double SANDWICH_PRICE = 50.0;
    public static final double PASTA_PRICE = 60.0;
    public static final double NOODLES_PRICE = 70.0;
    public static final double COKE_PRICE = 30.0;

    // Room Charges
    public static final double LUXURY_DOUBLE_ROOM_CHARGE = 4000.0;
    public static final double DELUXE_DOUBLE_ROOM_CHARGE = 3000.0;
    public static final double LUXURY_SINGLE_ROOM_CHARGE = 2200.0;
    public static final double DELUXE_SINGLE_ROOM_CHARGE = 1200.0;

    // Room Capacities
    public static final int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    public static final int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    // Room Number Offsets (Illustrative, better handled by a RoomManager)
    public static final int LUXURY_DOUBLE_ROOM_START = 1;
    public static final int DELUXE_DOUBLE_ROOM_START = 11;
    public static final int LUXURY_SINGLE_ROOM_START = 31;
    public static final int DELUXE_SINGLE_ROOM_START = 41;

    // File Name
    public static final String BACKUP_FILE_NAME = "backup";

    // Menu Item Names
    public static final String[] MENU_ITEM_NAMES = {"Sandwich", "Pasta", "Noodles", "Coke"};
}

// --- Exceptions ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

class RoomNotFoundException extends Exception {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

// --- Enums ---
enum MenuItem {
    SANDWICH(1, "Sandwich", HotelConstants.SANDWICH_PRICE),
    PASTA(2, "Pasta", HotelConstants.PASTA_PRICE),
    NOODLES(3, "Noodles", HotelConstants.NOODLES_PRICE),
    COKE(4, "Coke", HotelConstants.COKE_PRICE);

    private final int itemNumber;
    private final String name;
    private final double price;

    MenuItem(int itemNumber, String name, double price) {
        this.itemNumber = itemNumber;
        this.name = name;
        this.price = price;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public static MenuItem fromItemNumber(int itemNumber) {
        for (MenuItem item : MenuItem.values()) {
            if (item.getItemNumber() == itemNumber) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid menu item number: " + itemNumber);
    }
}

enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room", HotelConstants.LUXURY_DOUBLE_ROOM_CHARGE, HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY),
    DELUXE_DOUBLE(2, "Deluxe Double Room", HotelConstants.DELUXE_DOUBLE_ROOM_CHARGE, HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY),
    LUXURY_SINGLE(3, "Luxury Single Room", HotelConstants.LUXURY_SINGLE_ROOM_CHARGE, HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY),
    DELUXE_SINGLE(4, "Deluxe Single Room", HotelConstants.DELUXE_SINGLE_ROOM_CHARGE, HotelConstants.DELUXE_SINGLE_ROOM_CAPACITY);

    private final int typeCode;
    private final String description;
    private final double baseCharge;
    private final int capacity;

    RoomType(int typeCode, String description, double baseCharge, int capacity) {
        this.typeCode = typeCode;
        this.description = description;
        this.baseCharge = baseCharge;
        this.capacity = capacity;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getDescription() {
        return description;
    }

    public double getBaseCharge() {
        return baseCharge;
    }

    public int getCapacity() {
        return capacity;
    }

    public static RoomType fromTypeCode(int typeCode) {
        for (RoomType type : RoomType.values()) {
            if (type.getTypeCode() == typeCode) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type code: " + typeCode);
    }
}

// --- Domain Model Classes ---
class FoodOrder implements Serializable {
    private MenuItem item;
    private int quantity;

    public FoodOrder(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
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

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Contact: " + contact + ", Gender: " + gender;
    }
}

abstract class Room implements Serializable {
    private int roomNumber;
    private RoomType type;
    private List<Guest> guests;
    private List<FoodOrder> foodOrders;
    private boolean isBooked;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.guests = new ArrayList<>();
        this.foodOrders = new ArrayList<>();
        this.isBooked = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public List<FoodOrder> getFoodOrders() {
        return foodOrders;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public void addGuest(Guest guest) {
        if (guests.size() < type.getCapacity()) {
            guests.add(guest);
        } else {
            System.out.println("Room is full. Cannot add more guests.");
        }
    }

    public void addFoodOrder(FoodOrder foodOrder) {
        foodOrders.add(foodOrder);
    }

    public void clearFoodOrders() {
        foodOrders.clear();
    }

    public double calculateFoodBill() {
        return foodOrders.stream().mapToDouble(FoodOrder::getTotalPrice).sum();
    }

    public double getTotalBill() {
        return type.getBaseCharge() + calculateFoodBill();
    }

    public abstract String getFeatures();
}

class SingleRoom extends Room {
    public SingleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : " + (getType() == RoomType.LUXURY_SINGLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + getType().getBaseCharge();
    }
}

class DoubleRoom extends Room {
    public DoubleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : " + (getType() == RoomType.LUXURY_DOUBLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + getType().getBaseCharge();
    }
}

// --- Data Store / Repository ---
class HotelData implements Serializable {
    private Map<Integer, Room> rooms;

    public HotelData() {
        this.rooms = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        // Luxury Double Rooms (1-10)
        for (int i = 0; i < HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY; i++) {
            rooms.put(HotelConstants.LUXURY_DOUBLE_ROOM_START + i, new DoubleRoom(HotelConstants.LUXURY_DOUBLE_ROOM_START + i, RoomType.LUXURY_DOUBLE));
        }
        // Deluxe Double Rooms (11-30)
        for (int i = 0; i < HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY; i++) {
            rooms.put(HotelConstants.DELUXE_DOUBLE_ROOM_START + i, new DoubleRoom(HotelConstants.DELUXE_DOUBLE_ROOM_START + i, RoomType.DELUXE_DOUBLE));
        }
        // Luxury Single Rooms (31-40)
        for (int i = 0; i < HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY; i++) {
            rooms.put(HotelConstants.LUXURY_SINGLE_ROOM_START + i, new SingleRoom(HotelConstants.LUXURY_SINGLE_ROOM_START + i, RoomType.LUXURY_SINGLE));
        }
        // Deluxe Single Rooms (41-60)
        for (int i = 0; i < HotelConstants.DELUXE_SINGLE_ROOM_START; i++) {
            rooms.put(HotelConstants.DELUXE_SINGLE_ROOM_START + i, new SingleRoom(HotelConstants.DELUXE_SINGLE_ROOM_START + i, RoomType.DELUXE_SINGLE));
        }
    }

    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public List<Room> getRoomsByType(RoomType type) {
        List<Room> roomsOfType = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getType() == type) {
                roomsOfType.add(room);
            }
        }
        return roomsOfType;
    }

    public Map<Integer, Room> getAllRooms() {
        return rooms;
    }
}

// --- Persistence Layer ---
class SerializationManager {
    public static void saveHotelData(HotelData data, String filename) {
        try (FileOutputStream fout = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(data);
            System.out.println("Hotel data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static HotelData loadHotelData(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try (FileInputStream fin = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                System.out.println("Loading hotel data from backup...");
                return (HotelData) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return new HotelData(); // Return new data if loading fails
            }
        }
        System.out.println("No backup file found. Starting with new hotel data.");
        return new HotelData(); // If no file exists, create new data
    }
}

// --- Service Layer / Business Logic ---
class HotelService {
    HotelData hotelData;
    private InputHandler inputHandler;

    public HotelService(HotelData hotelData, InputHandler inputHandler) {
        this.hotelData = hotelData;
        this.inputHandler = inputHandler;
    }

    public void displayRoomDetails(RoomType type) {
        System.out.println("\n--- " + type.getDescription() + " Features ---");
        // Get any room of this type to display features
        Room room = hotelData.getRoomsByType(type).stream().findFirst().orElse(null);
        if (room != null) {
            System.out.println(room.getFeatures());
        } else {
            System.out.println("No rooms of this type exist.");
        }
    }

    public void displayRoomAvailability(RoomType type) {
        long availableRooms = hotelData.getRoomsByType(type).stream()
                .filter(room -> !room.isBooked())
                .count();
        System.out.println("Number of " + type.getDescription() + " rooms available: " + availableRooms);
    }

    public void bookRoom(RoomType type) {
        List<Room> availableRooms = hotelData.getRoomsByType(type).stream()
                .filter(room -> !room.isBooked())
                .toList();

        if (availableRooms.isEmpty()) {
            System.out.println("Sorry, no " + type.getDescription() + " rooms are available.");
            return;
        }

        System.out.println("\nAvailable " + type.getDescription() + " room numbers:");
        availableRooms.forEach(room -> System.out.print(room.getRoomNumber() + ", "));
        System.out.print("\nEnter room number to book: ");

        try {
            int roomNumber = inputHandler.readInt();
            Room roomToBook = hotelData.getRoom(roomNumber);

            if (roomToBook == null || roomToBook.getType() != type) {
                throw new RoomNotFoundException("Room number " + roomNumber + " does not exist or is not a " + type.getDescription() + ".");
            }
            if (roomToBook.isBooked()) {
                throw new RoomNotAvailableException("Room " + roomNumber + " is already booked.");
            }

            // Collect guest details
            System.out.println("\nEnter details for Guest 1:");
            roomToBook.addGuest(inputHandler.readGuestDetails());

            if (type.getCapacity() > 1) {
                System.out.println("Enter details for Guest 2:");
                roomToBook.addGuest(inputHandler.readGuestDetails());
            }

            roomToBook.setBooked(true);
            System.out.println("Room " + roomNumber + " (" + type.getDescription() + ") booked successfully!");

        } catch (RoomNotFoundException | RoomNotAvailableException e) {
            System.err.println("Booking failed: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a valid room number.");
            inputHandler.clearScannerBuffer(); // Clear invalid input
        }
    }

    public void orderFood(int roomNumber) {
        try {
            Room room = hotelData.getRoom(roomNumber);
            if (room == null || !room.isBooked()) {
                throw new RoomNotFoundException("Room " + roomNumber + " is not booked or does not exist.");
            }

            System.out.println("\n==========\n   Menu:  \n==========\n");
            for (MenuItem item : MenuItem.values()) {
                System.out.printf("%d.%-10sRs.%.2f%n", item.getItemNumber(), item.getName(), item.getPrice());
            }

            char wish;
            do {
                System.out.print("\nEnter menu item number: ");
                int itemNumber = inputHandler.readInt();
                System.out.print("Enter quantity: ");
                int quantity = inputHandler.readInt();

                MenuItem selectedItem = MenuItem.fromItemNumber(itemNumber);
                room.addFoodOrder(new FoodOrder(selectedItem, quantity));
                System.out.println("Food item added to order.");

                System.out.print("Do you want to order anything else? (y/n): ");
                wish = inputHandler.readChar();
            } while (Character.toLowerCase(wish) == 'y');

        } catch (RoomNotFoundException e) {
            System.err.println("Order failed: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a valid number.");
            inputHandler.clearScannerBuffer();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid menu item: " + e.getMessage());
        }
    }

    public void checkoutRoom(int roomNumber) {
        try {
            Room room = hotelData.getRoom(roomNumber);

            if (room == null) {
                throw new RoomNotFoundException("Room " + roomNumber + " does not exist.");
            }
            if (!room.isBooked()) {
                System.out.println("Room " + roomNumber + " is already empty.");
                return;
            }

            System.out.println("Room " + roomNumber + " is used by: " + room.getGuests().get(0).getName());
            System.out.print("Do you want to check out? (y/n): ");
            char confirm = inputHandler.readChar();

            if (Character.toLowerCase(confirm) == 'y') {
                generateBill(room);
                room.setBooked(false);
                room.getGuests().clear();
                room.clearFoodOrders();
                System.out.println("Room " + roomNumber + " deallocated successfully.");
            } else {
                System.out.println("Checkout cancelled.");
            }
        } catch (RoomNotFoundException e) {
            System.err.println("Checkout failed: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter 'y' or 'n'.");
            inputHandler.clearScannerBuffer();
        }
    }

    private void generateBill(Room room) {
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + room.getType().getBaseCharge());

        if (!room.getFoodOrders().isEmpty()) {
            System.out.println("\n===============");
            System.out.println("Food Charges:- ");
            System.out.println("===============");
            System.out.println("Item       Quantity   Price");
            System.out.println("----------------------------");
            for (FoodOrder order : room.getFoodOrders()) {
                System.out.printf("%-10s%-10s%-10.2f%n", order.getItem().getName(), order.getQuantity(), order.getTotalPrice());
            }
        }
        System.out.println("\nTotal Amount - " + room.getTotalBill());
    }
}

// --- Utility Class for Input Handling ---
class InputHandler {
    Scanner scanner;

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }

    public String readString() {
        return scanner.next();
    }

    public char readChar() {
        return scanner.next().charAt(0);
    }

    public Guest readGuestDetails() {
        System.out.print("Enter customer name: ");
        String name = readString();
        System.out.print("Enter contact number: ");
        String contact = readString();
        System.out.print("Enter gender: ");
        String gender = readString();
        return new Guest(name, contact, gender);
    }

    public void clearScannerBuffer() {
        scanner.nextLine(); // Consume the rest of the line after an int or char to prevent issues
    }
}

// --- Main Application Class ---
class HotelApplication {
    public HotelService hotelService;
    private InputHandler inputHandler;

    public HotelApplication() {
        Scanner scanner = new Scanner(System.in);
        this.inputHandler = new InputHandler(scanner);
        HotelData hotelData = SerializationManager.loadHotelData(HotelConstants.BACKUP_FILE_NAME);
        this.hotelService = new HotelService(hotelData, inputHandler);
    }

    public void run() {
        int choice;
        char continueChoice;

        do {
            displayMainMenu();
            choice = inputHandler.readInt();

            switch (choice) {
                case 1: // Display room details
                    displayRoomTypeMenu();
                    int roomDetailType = inputHandler.readInt();
                    try {
                        hotelService.displayRoomDetails(RoomType.fromTypeCode(roomDetailType));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid room type: " + e.getMessage());
                    }
                    break;
                case 2: // Display room availability
                    displayRoomTypeMenu();
                    int roomAvailabilityType = inputHandler.readInt();
                    try {
                        hotelService.displayRoomAvailability(RoomType.fromTypeCode(roomAvailabilityType));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid room type: " + e.getMessage());
                    }
                    break;
                case 3: // Book room
                    displayRoomTypeMenu();
                    int roomBookType = inputHandler.readInt();
                    try {
                        hotelService.bookRoom(RoomType.fromTypeCode(roomBookType));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid room type: " + e.getMessage());
                    }
                    break;
                case 4: // Order food
                    System.out.print("Enter room number to order food: ");
                    int foodRoomNumber = inputHandler.readInt();
                    hotelService.orderFood(foodRoomNumber);
                    break;
                case 5: // Checkout
                    System.out.print("Enter room number to checkout: ");
                    int checkoutRoomNumber = inputHandler.readInt();
                    hotelService.checkoutRoom(checkoutRoomNumber);
                    break;
                case 6: // Exit
                    System.out.println("Exiting hotel management system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 6) {
                System.out.print("\nContinue? (y/n): ");
                continueChoice = inputHandler.readChar();
                while (Character.toLowerCase(continueChoice) != 'y' && Character.toLowerCase(continueChoice) != 'n') {
                    System.out.println("Invalid option. Please enter 'y' or 'n'.");
                    System.out.print("\nContinue? (y/n): ");
                    continueChoice = inputHandler.readChar();
                }
            } else {
                continueChoice = 'n'; // Exit loop if user chose '6'
            }

        } while (Character.toLowerCase(continueChoice) == 'y');

        SerializationManager.saveHotelData(hotelService.hotelData, HotelConstants.BACKUP_FILE_NAME);
        inputHandler.scanner.close(); // Close the scanner when done
    }

    private void displayMainMenu() {
        System.out.println("\n--- Hotel Management System ---");
        System.out.println("1. Display room details");
        System.out.println("2. Display room availability");
        System.out.println("3. Book room");
        System.out.println("4. Order food");
        System.out.println("5. Checkout");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private void displayRoomTypeMenu() {
        System.out.println("\nChoose room type:");
        for (RoomType type : RoomType.values()) {
            System.out.println(type.getTypeCode() + ". " + type.getDescription());
        }
        System.out.print("Enter room type: ");
    }
}

public class Main {
    public static void main(String[] args) {
        HotelApplication app = new HotelApplication();
        app.run();
    }
}