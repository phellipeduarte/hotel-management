import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// --- Enums ---
enum RoomType {
    LUXURY_DOUBLE("Luxury Double Room", 4000.0, 10),
    DELUXE_DOUBLE("Deluxe Double Room", 3000.0, 20),
    LUXURY_SINGLE("Luxury Single Room", 2200.0, 10),
    DELUXE_SINGLE("Deluxe Single Room", 1200.0, 20);

    private final String name;
    private final double chargePerDay;
    private final int capacity;

    RoomType(String name, double chargePerDay, int capacity) {
        this.name = name;
        this.chargePerDay = chargePerDay;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public double getChargePerDay() {
        return chargePerDay;
    }

    public int getCapacity() {
        return capacity;
    }

    public static RoomType fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < RoomType.values().length) {
            return RoomType.values()[ordinal];
        }
        return null;
    }
}

enum FoodItem {
    SANDWICH("Sandwich", 50.0),
    PASTA("Pasta", 60.0),
    NOODLES("Noodles", 70.0),
    COKE("Coke", 30.0);

    private final String name;
    private final double price;

    FoodItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public static FoodItem fromItemNo(int itemNo) {
        if (itemNo > 0 && itemNo <= FoodItem.values().length) {
            return FoodItem.values()[itemNo - 1];
        }
        return null;
    }
}

// --- Exceptions ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room is not available!");
    }
}

class RoomNotFoundException extends Exception {
    public RoomNotFoundException() {
        super("Room does not exist!");
    }
}

// --- Model Classes ---
class Food implements Serializable {
    private FoodItem item;
    private int quantity;
    private double price;

    public Food(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice() * quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%-10s%-10d%-10.2f", item.getName(), quantity, price);
    }
}

abstract class Room implements Serializable {
    protected String guestName;
    protected String contact;
    protected String gender;
    protected List<Food> foodOrders;
    protected RoomType type;

    public Room(String guestName, String contact, String gender, RoomType type) {
        this.guestName = guestName;
        this.contact = contact;
        this.gender = gender;
        this.foodOrders = new ArrayList<>();
        this.type = type;
    }

    public Room() {
        this.guestName = "";
        this.foodOrders = new ArrayList<>();
    }

    public String getGuestName() {
        return guestName;
    }

    public String getContact() {
        return contact;
    }

    public String getGender() {
        return gender;
    }

    public RoomType getType() {
        return type;
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    public abstract int getNumberOfOccupants();
    public abstract String getRoomDetails();
}

class SingleRoom extends Room {
    public SingleRoom(String guestName, String contact, String gender, RoomType type) {
        super(guestName, contact, gender, type);
    }

    public SingleRoom() {
        super();
        this.type = RoomType.LUXURY_SINGLE; // Default or can be set upon creation
    }

    @Override
    public int getNumberOfOccupants() {
        return 1;
    }

    @Override
    public String getRoomDetails() {
        return "Number of single beds : 1\nAC : " + (type == RoomType.LUXURY_SINGLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + type.getChargePerDay();
    }
}

class DoubleRoom extends Room {
    private String guestName2;
    private String contact2;
    private String gender2;

    public DoubleRoom(String guestName, String contact, String gender, String guestName2, String contact2, String gender2, RoomType type) {
        super(guestName, contact, gender, type);
        this.guestName2 = guestName2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }

    public DoubleRoom() {
        super();
        this.guestName2 = "";
        this.type = RoomType.LUXURY_DOUBLE; // Default or can be set upon creation
    }

    public String getGuestName2() {
        return guestName2;
    }

    public String getContact2() {
        return contact2;
    }

    public String getGender2() {
        return gender2;
    }

    @Override
    public int getNumberOfOccupants() {
        return 2;
    }

    @Override
    public String getRoomDetails() {
        return "Number of double beds : 1\nAC : " + (type == RoomType.LUXURY_DOUBLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + type.getChargePerDay();
    }
}

class HotelData implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    public DoubleRoom[] luxuryDoubleRooms = new DoubleRoom[RoomType.LUXURY_DOUBLE.getCapacity()];
    public DoubleRoom[] deluxeDoubleRooms = new DoubleRoom[RoomType.DELUXE_DOUBLE.getCapacity()];
    public SingleRoom[] luxurySingleRooms = new SingleRoom[RoomType.LUXURY_SINGLE.getCapacity()];
    public SingleRoom[] deluxeSingleRooms = new SingleRoom[RoomType.DELUXE_SINGLE.getCapacity()];

    // A map to easily get room arrays by type
    @SuppressWarnings("unchecked")
    private transient Map<RoomType, Room[]> roomMap = new EnumMap<>(RoomType.class);

    public HotelData() {
        initializeRoomMap();
    }

    // Re-initialize transient map after deserialization
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, java.io.IOException {
        ois.defaultReadObject();
        initializeRoomMap();
    }

    private void initializeRoomMap() {
        roomMap.put(RoomType.LUXURY_DOUBLE, luxuryDoubleRooms);
        roomMap.put(RoomType.DELUXE_DOUBLE, deluxeDoubleRooms);
        roomMap.put(RoomType.LUXURY_SINGLE, luxurySingleRooms);
        roomMap.put(RoomType.DELUXE_SINGLE, deluxeSingleRooms);
    }

    public Room[] getRooms(RoomType type) {
        return roomMap.get(type);
    }
}

// --- Services/Managers ---
class HotelManagement {
    private static HotelData hotelData = new HotelData();
    private static final Scanner scanner = new Scanner(System.in);

    public static HotelData getHotelData() {
        return hotelData;
    }

    public static void loadHotelData(HotelData data) {
        hotelData = data;
    }

    public static void displayRoomDetails(RoomType type) {
        if (type == null) {
            System.out.println("Invalid room type selected.");
            return;
        }
        System.out.println("\n--- " + type.getName() + " Details ---");
        System.out.println(type.getName() + ":");
        if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
            System.out.println(new DoubleRoom().getRoomDetails()); // Using a temporary instance for details
        } else {
            System.out.println(new SingleRoom().getRoomDetails()); // Using a temporary instance for details
        }
        System.out.println("Charge per day: " + type.getChargePerDay());
    }

    public static void displayRoomAvailability(RoomType type) {
        if (type == null) {
            System.out.println("Invalid room type selected.");
            return;
        }
        int count = 0;
        Room[] rooms = hotelData.getRooms(type);
        if (rooms != null) {
            for (Room room : rooms) {
                if (room == null || room.getGuestName().isEmpty()) { // Check if room is null or not booked (empty name)
                    count++;
                }
            }
        }
        System.out.println("Number of " + type.getName() + " rooms available: " + count);
    }

    public static void bookRoom(RoomType type) {
        if (type == null) {
            System.out.println("Invalid room type selected.");
            return;
        }

        Room[] rooms = hotelData.getRooms(type);
        if (rooms == null) {
            System.out.println("Error: Room type not configured.");
            return;
        }

        System.out.print("Available " + type.getName() + " room numbers: ");
        List<Integer> availableRoomNumbers = new ArrayList<>();
        for (int j = 0; j < rooms.length; j++) {
            if (rooms[j] == null || rooms[j].getGuestName().isEmpty()) {
                // Adjust room number display based on original logic (e.g., +1, +11, +31, +41)
                int displayRoomNumber = getDisplayRoomNumber(type, j);
                System.out.print(displayRoomNumber + ", ");
                availableRoomNumbers.add(displayRoomNumber);
            }
        }
        System.out.println(); // New line for better formatting

        if (availableRoomNumbers.isEmpty()) {
            System.out.println("No " + type.getName() + " rooms available.");
            return;
        }

        try {
            System.out.print("Enter room number to book: ");
            int roomNumberToBook = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            int arrayIndex = getArrayIndex(type, roomNumberToBook);

            if (arrayIndex == -1 || arrayIndex >= rooms.length || rooms[arrayIndex] != null && !rooms[arrayIndex].getGuestName().isEmpty()) {
                throw new RoomNotAvailableException();
            }

            System.out.print("Enter customer name: ");
            String name = scanner.nextLine();
            System.out.print("Enter contact number: ");
            String contact = scanner.nextLine();
            System.out.print("Enter gender: ");
            String gender = scanner.nextLine();

            if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
                System.out.print("Enter second customer name: ");
                String name2 = scanner.nextLine();
                System.out.print("Enter second contact number: ");
                String contact2 = scanner.nextLine();
                System.out.print("Enter second gender: ");
                String gender2 = scanner.nextLine();
                hotelData.getRooms(type)[arrayIndex] = new DoubleRoom(name, contact, gender, name2, contact2, gender2, type);
            } else {
                hotelData.getRooms(type)[arrayIndex] = new SingleRoom(name, contact, gender, type);
            }
            System.out.println(type.getName() + " Room " + roomNumberToBook + " booked successfully!");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid room number.");
            scanner.nextLine(); // Consume invalid input
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred during booking: " + e.getMessage());
        }
    }

    public static void orderFood(int roomNumber) {
        Room room = findRoomByDisplayNumber(roomNumber);
        if (room == null) {
            System.out.println("Room " + roomNumber + " does not exist or is not booked.");
            return;
        }

        try {
            System.out.println("\n==========\n    Menu:   \n==========\n");
            for (FoodItem item : FoodItem.values()) {
                System.out.println((item.ordinal() + 1) + "." + item.getName() + "\t\tRs." + item.getPrice());
            }

            char wish = 'y';
            do {
                System.out.print("Enter item number: ");
                int itemChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                FoodItem selectedItem = FoodItem.fromItemNo(itemChoice);
                if (selectedItem == null) {
                    System.out.println("Invalid food item number.");
                    continue;
                }

                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                room.getFoodOrders().add(new Food(selectedItem, quantity));
                System.out.println("Food added to order.");

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.nextLine().charAt(0);
            } while (wish == 'y' || wish == 'Y');
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
        } catch (Exception e) {
            System.out.println("An error occurred while ordering food: " + e.getMessage());
        }
    }

    public static void checkoutRoom(int roomNumber) {
        Room room = findRoomByDisplayNumber(roomNumber);
        if (room == null || room.getGuestName().isEmpty()) {
            System.out.println("Room " + roomNumber + " is empty or does not exist.");
            return;
        }

        System.out.println("Room " + roomNumber + " is currently occupied by " + room.getGuestName() + ".");
        System.out.print("Do you want to checkout? (y/n): ");
        char wish = scanner.nextLine().charAt(0);

        if (wish == 'y' || wish == 'Y') {
            generateBill(room, roomNumber);
            // Deallocate the room by setting it to null or a new empty room object
            RoomType type = room.getType();
            int arrayIndex = getArrayIndex(type, roomNumber);
            if (arrayIndex != -1) {
                if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
                    hotelData.getRooms(type)[arrayIndex] = null; // Or new DoubleRoom()
                } else {
                    hotelData.getRooms(type)[arrayIndex] = null; // Or new SingleRoom()
                }
            }
            System.out.println("Room " + roomNumber + " deallocated successfully!");
        }
    }

    private static void generateBill(Room room, int roomNumber) {
        System.out.println("\n*******");
        System.out.println(" Bill for Room " + roomNumber + ":");
        System.out.println("*******");

        double totalAmount = 0;

        // Room Charge
        double roomCharge = room.getType().getChargePerDay();
        totalAmount += roomCharge;
        System.out.println("Room Charge - " + String.format("%.2f", roomCharge));

        // Food Charges
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item        Quantity   Price");
        System.out.println("-------------------------");
        if (room.getFoodOrders().isEmpty()) {
            System.out.println("No food ordered.");
        } else {
            for (Food food : room.getFoodOrders()) {
                totalAmount += food.getPrice();
                System.out.println(food);
            }
        }
        System.out.println("\nTotal Amount: " + String.format("%.2f", totalAmount));
    }

    // Helper method to find a room by its display number
    private static Room findRoomByDisplayNumber(int displayRoomNumber) {
        for (RoomType type : RoomType.values()) {
            Room[] rooms = hotelData.getRooms(type);
            if (rooms != null) {
                for (int i = 0; i < rooms.length; i++) {
                    int currentDisplayNumber = getDisplayRoomNumber(type, i);
                    if (currentDisplayNumber == displayRoomNumber) {
                        return rooms[i];
                    }
                }
            }
        }
        return null;
    }

    // Helper to get the display room number from array index and room type
    private static int getDisplayRoomNumber(RoomType type, int arrayIndex) {
        return switch (type) {
            case LUXURY_DOUBLE -> arrayIndex + 1;
            case DELUXE_DOUBLE -> arrayIndex + 11;
            case LUXURY_SINGLE -> arrayIndex + 31;
            case DELUXE_SINGLE -> arrayIndex + 41;
        };
    }

    // Helper to get the array index from display room number and room type
    private static int getArrayIndex(RoomType type, int displayRoomNumber) {
        return switch (type) {
            case LUXURY_DOUBLE -> displayRoomNumber - 1;
            case DELUXE_DOUBLE -> displayRoomNumber - 11;
            case LUXURY_SINGLE -> displayRoomNumber - 31;
            case DELUXE_SINGLE -> displayRoomNumber - 41;
        };
    }
}

class HotelDataSaver implements Runnable {
    private HotelData hotelData;
    private static final String FILENAME = "backup";

    public HotelDataSaver(HotelData hotelData) {
        this.hotelData = hotelData;
    }

    @Override
    public void run() {
        try (FileOutputStream fileOut = new FileOutputStream(FILENAME);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            objOut.writeObject(hotelData);
            System.out.println("Hotel data saved to " + FILENAME);
        } catch (Exception e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static HotelData loadData() {
        File f = new File(FILENAME);
        if (f.exists()) {
            try (FileInputStream fileIn = new FileInputStream(f);
                 ObjectInputStream objIn = new ObjectInputStream(fileIn)) {
                return (HotelData) objIn.readObject();
            } catch (Exception e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return new HotelData(); // Return a new instance if loading fails
            }
        }
        return new HotelData(); // Return a new instance if file doesn't exist
    }
}

public class Main {
    public static void main(String[] args) {
        HotelManagement.loadHotelData(HotelDataSaver.loadData());
        Scanner scanner = new Scanner(System.in);
        int choice;

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
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                    case 2:
                    case 3:
                        System.out.println("\nChoose room type:");
                        for (RoomType type : RoomType.values()) {
                            System.out.println((type.ordinal() + 1) + ". " + type.getName());
                        }
                        System.out.print("Enter room type choice: ");
                        int roomTypeChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        RoomType selectedRoomType = RoomType.fromOrdinal(roomTypeChoice - 1);

                        if (selectedRoomType == null) {
                            System.out.println("Invalid room type choice.");
                            break;
                        }

                        if (choice == 1) {
                            HotelManagement.displayRoomDetails(selectedRoomType);
                        } else if (choice == 2) {
                            HotelManagement.displayRoomAvailability(selectedRoomType);
                        } else { // choice == 3
                            HotelManagement.bookRoom(selectedRoomType);
                        }
                        break;
                    case 4:
                        System.out.print("Enter room number for food order: ");
                        int foodRoomNumber = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        HotelManagement.orderFood(foodRoomNumber);
                        break;
                    case 5:
                        System.out.print("Enter room number to checkout: ");
                        int checkoutRoomNumber = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        HotelManagement.checkoutRoom(checkoutRoomNumber);
                        break;
                    case 6:
                        System.out.println("Exiting application. Saving data...");
                        Thread t = new Thread(new HotelDataSaver(HotelManagement.getHotelData()));
                        t.start();
                        t.join(); // Wait for the save thread to finish
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                choice = 0; // Set to a value that will continue the loop
            } catch (InterruptedException e) {
                System.err.println("Error while saving data: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restore the interrupted status
                choice = 6; // Exit
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                choice = 0; // Set to a value that will continue the loop
            }

        } while (choice != 6);

        scanner.close();
        System.out.println("Application gracefully exited.");
    }
}