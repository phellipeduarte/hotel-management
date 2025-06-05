import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// --- Enums ---
enum RoomType {
    LUXURY_DOUBLE("Luxury Double", 4000),
    DELUXE_DOUBLE("Deluxe Double", 3000),
    LUXURY_SINGLE("Luxury Single", 2200),
    DELUXE_SINGLE("Deluxe Single", 1200);

    private final String name;
    private final double price;

    RoomType(String name, double price) {
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

// --- Exceptions ---
class RoomNotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Room Not Available!";
    }
}

class InvalidRoomNumberException extends Exception {
    @Override
    public String toString() {
        return "Invalid Room Number!";
    }
}

// --- Core Classes ---

class Customer implements Serializable {
    private String name;
    private String contact;
    private String gender;

    public Customer(String name, String contact, String gender) {
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
}

class FoodItem implements Serializable {
    private int itemNo;
    private int quantity;
    private double price;
    private static final Map<Integer, Double> ITEM_PRICES = new HashMap<>();
    private static final Map<Integer, String> ITEM_NAMES = new HashMap<>();

    static {
        ITEM_PRICES.put(1, 50.0); // Sandwich
        ITEM_PRICES.put(2, 60.0); // Pasta
        ITEM_PRICES.put(3, 70.0); // Noodles
        ITEM_PRICES.put(4, 30.0); // Coke

        ITEM_NAMES.put(1, "Sandwich");
        ITEM_NAMES.put(2, "Pasta");
        ITEM_NAMES.put(3, "Noodles");
        ITEM_NAMES.put(4, "Coke");
    }

    public FoodItem(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        if (ITEM_PRICES.containsKey(itemNo)) {
            this.price = quantity * ITEM_PRICES.get(itemNo);
        } else {
            this.price = 0; // Handle invalid item numbers
        }
    }

    public int getItemNo() {
        return itemNo;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return ITEM_NAMES.getOrDefault(itemNo, "Unknown Item");
    }
}

abstract class Room implements Serializable {
    private int roomNumber;
    private RoomType type;
    protected ArrayList<FoodItem> foodItems = new ArrayList<>();
    private boolean isBooked;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.isBooked = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void book() {
        this.isBooked = true;
    }

    public void deallocate() {
        this.isBooked = false;
        this.foodItems.clear(); // Clear food orders upon deallocation
    }

    public ArrayList<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void addFoodItem(FoodItem item) {
        this.foodItems.add(item);
    }

    public abstract double calculateRoomCharge();
    public abstract String getCustomerDetails();
    public abstract int getNumberOfOccupants();
}

class SingleRoom extends Room {
    private Customer primaryOccupant;

    public SingleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    public void assignOccupant(Customer occupant) {
        this.primaryOccupant = occupant;
        book();
    }

    public Customer getPrimaryOccupant() {
        return primaryOccupant;
    }

    @Override
    public double calculateRoomCharge() {
        return getType().getPrice();
    }

    @Override
    public String getCustomerDetails() {
        if (primaryOccupant != null) {
            return "Name: " + primaryOccupant.getName() + ", Contact: " + primaryOccupant.getContact() + ", Gender: " + primaryOccupant.getGender();
        }
        return "No occupant";
    }

    @Override
    public int getNumberOfOccupants() {
        return primaryOccupant != null ? 1 : 0;
    }
}

class DoubleRoom extends Room {
    private Customer primaryOccupant;
    private Customer secondOccupant;

    public DoubleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    public void assignOccupants(Customer primary, Customer second) {
        this.primaryOccupant = primary;
        this.secondOccupant = second;
        book();
    }

    public Customer getPrimaryOccupant() {
        return primaryOccupant;
    }

    public Customer getSecondOccupant() {
        return secondOccupant;
    }

    @Override
    public double calculateRoomCharge() {
        return getType().getPrice();
    }

    @Override
    public String getCustomerDetails() {
        StringBuilder details = new StringBuilder();
        if (primaryOccupant != null) {
            details.append("1. Name: ").append(primaryOccupant.getName())
                    .append(", Contact: ").append(primaryOccupant.getContact())
                    .append(", Gender: ").append(primaryOccupant.getGender());
        }
        if (secondOccupant != null) {
            details.append("\n2. Name: ").append(secondOccupant.getName())
                    .append(", Contact: ").append(secondOccupant.getContact())
                    .append(", Gender: ").append(secondOccupant.getGender());
        }
        return details.toString().isEmpty() ? "No occupants" : details.toString();
    }

    @Override
    public int getNumberOfOccupants() {
        int count = 0;
        if (primaryOccupant != null) count++;
        if (secondOccupant != null) count++;
        return count;
    }
}

// --- Hotel Management ---

class HotelRoomsHolder implements Serializable {
    private ArrayList<Room> rooms;

    public HotelRoomsHolder() {
        rooms = new ArrayList<>();
        // Initialize rooms
        for (int i = 0; i < 10; i++) rooms.add(new DoubleRoom(i + 1, RoomType.LUXURY_DOUBLE)); // Rooms 1-10
        for (int i = 0; i < 20; i++) rooms.add(new DoubleRoom(i + 11, RoomType.DELUXE_DOUBLE)); // Rooms 11-30
        for (int i = 0; i < 10; i++) rooms.add(new SingleRoom(i + 31, RoomType.LUXURY_SINGLE)); // Rooms 31-40
        for (int i = 0; i < 20; i++) rooms.add(new SingleRoom(i + 41, RoomType.DELUXE_SINGLE)); // Rooms 41-60
    }

    public Room getRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public ArrayList<Room> getAvailableRooms(RoomType type) {
        ArrayList<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getType() == type && !room.isBooked()) {
                available.add(room);
            }
        }
        return available;
    }

    public int getAvailableRoomCount(RoomType type) {
        int count = 0;
        for (Room room : rooms) {
            if (room.getType() == type && !room.isBooked()) {
                count++;
            }
        }
        return count;
    }
}

class Hotel {
    private static HotelRoomsHolder hotelRooms = new HotelRoomsHolder();
    private static Scanner sc = new Scanner(System.in);

    // Static setter and getter for HotelRoomsHolder to allow backup loading
    public static void setHotelRooms(HotelRoomsHolder loadedRooms) {
        Hotel.hotelRooms = loadedRooms;
    }

    public static HotelRoomsHolder getHotelRooms() {
        return hotelRooms;
    }

    public static void displayMenu() {
        System.out.println("\nWelcome to the Hotel Management System!");
        System.out.println("1. Book Room");
        System.out.println("2. Features of Room");
        System.out.println("3. Check Availability");
        System.out.println("4. Order Food");
        System.out.println("5. Checkout Room");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void chooseRoomTypeAndPerformAction(int actionChoice) {
        System.out.println("\nSelect Room Type:");
        System.out.println("1. Luxury Double Room (" + RoomType.LUXURY_DOUBLE.getPrice() + ")");
        System.out.println("2. Deluxe Double Room (" + RoomType.DELUXE_DOUBLE.getPrice() + ")");
        System.out.println("3. Luxury Single Room (" + RoomType.LUXURY_SINGLE.getPrice() + ")");
        System.out.println("4. Deluxe Single Room (" + RoomType.DELUXE_SINGLE.getPrice() + ")");
        System.out.print("Enter your choice: ");
        int roomTypeChoice = sc.nextInt();

        RoomType selectedRoomType = null;
        switch (roomTypeChoice) {
            case 1: selectedRoomType = RoomType.LUXURY_DOUBLE; break;
            case 2: selectedRoomType = RoomType.DELUXE_DOUBLE; break;
            case 3: selectedRoomType = RoomType.LUXURY_SINGLE; break;
            case 4: selectedRoomType = RoomType.DELUXE_SINGLE; break;
            default: System.out.println("Invalid room type choice."); return;
        }

        switch (actionChoice) {
            case 1: bookRoom(selectedRoomType); break;
            case 2: displayRoomFeatures(selectedRoomType); break;
            case 3: checkAvailability(selectedRoomType); break;
            // Cases 4 and 5 (order and checkout) are handled separately as they need room number
            // which can be any type, so passing null here to avoid redundant type selection
            // The promptForRoomNumberAnd... methods will ask for room number explicitly
        }
    }

    private static Customer getCustomerDetails(String prompt) {
        System.out.print(prompt + " customer name: ");
        String name = sc.next();
        System.out.print(prompt + " contact number: ");
        String contact = sc.next();
        System.out.print(prompt + " gender: ");
        String gender = sc.next();
        return new Customer(name, contact, gender);
    }

    public static void bookRoom(RoomType type) {
        ArrayList<Room> availableRooms = hotelRooms.getAvailableRooms(type);
        if (availableRooms.isEmpty()) {
            System.out.println("Sorry, no " + type.getName() + " rooms available.");
            return;
        }

        System.out.print("\nAvailable " + type.getName() + " room numbers: ");
        for (Room room : availableRooms) {
            System.out.print(room.getRoomNumber() + " ");
        }
        System.out.print("\nEnter desired room number: ");
        int roomNumber = sc.nextInt();

        try {
            Room roomToBook = hotelRooms.getRoomByNumber(roomNumber);
            if (roomToBook == null || roomToBook.getType() != type) {
                throw new InvalidRoomNumberException();
            }
            if (roomToBook.isBooked()) {
                throw new RoomNotAvailableException();
            }

            if (roomToBook instanceof SingleRoom) {
                Customer primary = getCustomerDetails("Enter");
                ((SingleRoom) roomToBook).assignOccupant(primary);
            } else if (roomToBook instanceof DoubleRoom) {
                Customer primary = getCustomerDetails("Enter first");
                Customer second = getCustomerDetails("Enter second");
                ((DoubleRoom) roomToBook).assignOccupants(primary, second);
            }
            System.out.println("Room " + roomNumber + " booked successfully!");
        } catch (InvalidRoomNumberException e) {
            System.out.println("Error: " + e.toString() + " Please choose a valid room number for the selected type.");
        } catch (RoomNotAvailableException e) {
            System.out.println("Error: " + e.toString() + " This room is already occupied.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during booking: " + e.getMessage());
        }
    }

    public static void displayRoomFeatures(RoomType type) {
        System.out.println("\n--- " + type.getName() + " Features ---");
        switch (type) {
            case LUXURY_DOUBLE:
                System.out.println("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:" + type.getPrice());
                break;
            case DELUXE_DOUBLE:
                System.out.println("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:" + type.getPrice());
                break;
            case LUXURY_SINGLE:
                System.out.println("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:" + type.getPrice());
                break;
            case DELUXE_SINGLE:
                System.out.println("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:" + type.getPrice());
                break;
        }
    }

    public static void checkAvailability(RoomType type) {
        int count = hotelRooms.getAvailableRoomCount(type);
        System.out.println("Number of " + type.getName() + " rooms available: " + count);
    }

    // roomType parameter is unused here, as the method will prompt for room number
    public static void promptForRoomNumberAndOrderFood(RoomType roomType) {
        System.out.print("Enter room number to order food for: ");
        int roomNumber = sc.nextInt();
        Room room = hotelRooms.getRoomByNumber(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }
        if (!room.isBooked()) {
            System.out.println("Room " + roomNumber + " is not booked. Cannot order food.");
            return;
        }
        orderFood(room);
    }

    public static void orderFood(Room room) {
        try {
            System.out.println("\n==========\n   Menu:  \n==========\n");
            System.out.println("1. Sandwich\tRs.50\n2. Pasta\t\tRs.60\n3. Noodles\tRs.70\n4. Coke\t\tRs.30\n");
            char wish;
            do {
                System.out.print("Enter item number: ");
                int itemNo = sc.nextInt();
                System.out.print("Enter quantity: ");
                int quantity = sc.nextInt();

                FoodItem foodItem = new FoodItem(itemNo, quantity);
                if (foodItem.getPrice() > 0) { // Check if it's a valid item
                    room.addFoodItem(foodItem);
                    System.out.println("Item added to order.");
                } else {
                    System.out.println("Invalid food item number. Please choose from the menu.");
                }

                System.out.print("Do you want to order anything else? (y/n): ");
                wish = sc.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');
        } catch (Exception e) {
            System.out.println("An error occurred while ordering food: " + e.getMessage());
        }
    }

    // roomType parameter is unused here, as the method will prompt for room number
    public static void promptForRoomNumberAndCheckout(RoomType roomType) {
        System.out.print("Enter room number to checkout: ");
        int roomNumber = sc.nextInt();
        Room room = hotelRooms.getRoomByNumber(roomNumber);

        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist.");
            return;
        }
        if (!room.isBooked()) {
            System.out.println("Room " + roomNumber + " is already empty.");
            return;
        }
        checkoutRoom(room);
    }

    public static void checkoutRoom(Room room) {
        System.out.println("Room used by: " + room.getCustomerDetails());
        System.out.print("Do you want to checkout? (y/n): ");
        char confirmation = sc.next().charAt(0);

        if (confirmation == 'y' || confirmation == 'Y') {
            generateBill(room);
            room.deallocate();
            System.out.println("Room " + room.getRoomNumber() + " deallocated successfully.");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    public static void generateBill(Room room) {
        double totalAmount = room.calculateRoomCharge();
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + String.format("%.2f", room.calculateRoomCharge()));
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item   Quantity    Price");
        System.out.println("-------------------------");

        for (FoodItem item : room.getFoodItems()) {
            totalAmount += item.getPrice();
            String format = "%-10s%-10s%-10.2f%n";
            System.out.printf(format, item.getName(), item.getQuantity(), item.getPrice());
        }
        System.out.println("\nTotal Amount: " + String.format("%.2f", totalAmount));
    }
}

// Assuming 'write' class exists and handles saving HotelRoomsHolder
class write implements Runnable {
    HotelRoomsHolder hotelRooms;

    write(HotelRoomsHolder hotelRooms) {
        this.hotelRooms = hotelRooms;
    }

    @Override
    public void run() {
        try {
            FileOutputStream fout = new FileOutputStream("backup");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(hotelRooms); // Save the HotelRoomsHolder object
            oos.close();
        } catch (Exception e) {
            System.out.println("Error saving backup: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Initialize HotelRoomsHolder before potentially loading from backup
        HotelRoomsHolder initialHotelRooms = new HotelRoomsHolder();

        // Attempt to load backup
        try {
            File f = new File("backup");
            if (f.exists()) {
                FileInputStream fin = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fin);
                // Assign the loaded object directly to the static hotelRooms in Hotel class
                Hotel.setHotelRooms((HotelRoomsHolder) ois.readObject());
                ois.close();
                System.out.println("Hotel data loaded from backup successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error loading backup (or backup not found/corrupted). Starting with fresh data: " + e.getMessage());
            // If backup fails, ensure Hotel still has a valid HotelRoomsHolder
            Hotel.setHotelRooms(initialHotelRooms);
        }

        Scanner sc = new Scanner(System.in); // Still needed for the main menu choice

        int choice;
        char wish;

        do {
            Hotel.displayMenu(); // Displays the main menu options
            choice = sc.nextInt();

            switch (choice) {
                case 1: // Book Room
                case 2: // Features of Room
                case 3: // Check Availability
                    // These actions now directly prompt for room type selection within Hotel.chooseRoomTypeAndPerformAction
                    Hotel.chooseRoomTypeAndPerformAction(choice);
                    break;
                case 4: // Order food
                    // For ordering food, we still need to ask for a room number
                    Hotel.promptForRoomNumberAndOrderFood(null); // Pass null as type will be derived from room number
                    break;
                case 5: // Checkout
                    // For checkout, we still need to ask for a room number
                    Hotel.promptForRoomNumberAndCheckout(null); // Pass null as type will be derived from room number
                    break;
                case 6:
                    System.out.println("Exiting Hotel Management System...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 6) { // Don't ask to continue if exiting
                System.out.print("\nContinue operation? (y/n): ");
                wish = sc.next().charAt(0);
                while (!(wish == 'y' || wish == 'Y' || wish == 'n' || wish == 'N')) {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    System.out.print("Continue operation? (y/n): ");
                    wish = sc.next().charAt(0);
                }
            } else {
                wish = 'n'; // Set wish to 'n' to exit the loop
            }

        } while (wish == 'y' || wish == 'Y');

        // Save data before exiting
        Thread t = new Thread(new write(Hotel.getHotelRooms()));
        t.start();

        // Wait for the save thread to finish to ensure data is written
        try {
            t.join();
        } catch (InterruptedException e) {
            System.out.println("Backup thread interrupted.");
        }

        System.out.println("Data saved. Goodbye!");
        sc.close();
    }
}