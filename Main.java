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
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// Custom exception for when a room is not available
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Not Available!";
    }
}

// Represents a person (customer)
class Person implements Serializable {
    private String name;
    private String contact;
    private String gender;

    public Person(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    // Getters
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

// Represents a food item
class FoodItem implements Serializable {
    private int itemNo;
    private String name;
    private double pricePerUnit;

    public FoodItem(int itemNo, String name, double pricePerUnit) {
        this.itemNo = itemNo;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters
    public int getItemNo() {
        return itemNo;
    }

    public String getName() {
        return name;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    // Static method to get all available food items
    public static List<FoodItem> getMenu() {
        return Arrays.asList(
                new FoodItem(1, "Sandwich", 50.0),
                new FoodItem(2, "Pasta", 60.0),
                new FoodItem(3, "Noodles", 70.0),
                new FoodItem(4, "Coke", 30.0)
        );
    }
}

// Represents an ordered food item with quantity
class FoodOrder implements Serializable {
    private FoodItem item;
    private int quantity;
    private double totalPrice;

    public FoodOrder(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.totalPrice = item.getPricePerUnit() * quantity;
    }

    // Getters
    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

// Abstract base class for all room types
abstract class Room implements Serializable {
    private int roomNumber;
    private double price;
    private List<Person> occupants;
    private List<FoodOrder> foodOrders;
    private boolean isAvailable;

    public Room(int roomNumber, double price) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.occupants = new ArrayList<>();
        this.foodOrders = new ArrayList<>();
        this.isAvailable = true;
    }

    // Getters
    public int getRoomNumber() {
        return roomNumber;
    }

    public double getPrice() {
        return price;
    }

    public List<Person> getOccupants() {
        return occupants;
    }

    public List<FoodOrder> getFoodOrders() {
        return foodOrders;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // Methods to manage room status and occupants
    public void bookRoom(List<Person> newOccupants) throws RoomNotAvailableException {
        if (!isAvailable) {
            throw new RoomNotAvailableException("Room " + roomNumber + " is already booked.");
        }
        this.occupants.addAll(newOccupants);
        this.isAvailable = false;
        System.out.println("Room " + roomNumber + " booked successfully.");
    }

    public void checkout() {
        this.occupants.clear();
        this.foodOrders.clear();
        this.isAvailable = true;
        System.out.println("Room " + roomNumber + " deallocated successfully.");
    }

    public void addFoodOrder(FoodOrder order) {
        this.foodOrders.add(order);
    }

    // Abstract method for displaying room features
    public abstract void displayFeatures();

    // Calculate total bill for the room
    public double calculateBill() {
        double total = price;
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + price);
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item        Quantity    Price");
        System.out.println("-------------------------");

        for (FoodOrder order : foodOrders) {
            total += order.getTotalPrice();
            String format = "%-10s%-12s%-10s%n";
            System.out.printf(format, order.getItem().getName(), order.getQuantity(), order.getTotalPrice());
        }
        return total;
    }
}

// Concrete class for Single Luxury Room
class LuxurySingleRoom extends Room {
    public LuxurySingleRoom(int roomNumber) {
        super(roomNumber, 2200.0);
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of single beds: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: " + getPrice());
    }
}

// Concrete class for Deluxe Single Room
class DeluxeSingleRoom extends Room {
    public DeluxeSingleRoom(int roomNumber) {
        super(roomNumber, 1200.0);
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of single beds: 1\nAC: No\nFree breakfast: Yes\nCharge per day: " + getPrice());
    }
}

// Concrete class for Luxury Double Room
class LuxuryDoubleRoom extends Room {
    public LuxuryDoubleRoom(int roomNumber) {
        super(roomNumber, 4000.0);
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of double beds: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: " + getPrice());
    }
}

// Concrete class for Deluxe Double Room
class DeluxeDoubleRoom extends Room {
    public DeluxeDoubleRoom(int roomNumber) {
        super(roomNumber, 3000.0);
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of double beds: 1\nAC: No\nFree breakfast: Yes\nCharge per day: " + getPrice());
    }
}

// Manages all rooms in the hotel
class HotelManager implements Serializable {
    private static final int LUXURY_DOUBLE_ROOMS = 10;
    private static final int DELUXE_DOUBLE_ROOMS = 20;
    private static final int LUXURY_SINGLE_ROOMS = 10;
    private static final int DELUXE_SINGLE_ROOMS = 20;

    private Room[] luxuryDoubleRooms;
    private Room[] deluxeDoubleRooms;
    private Room[] luxurySingleRooms;
    private Room[] deluxeSingleRooms;

    public HotelManager() {
        luxuryDoubleRooms = new LuxuryDoubleRoom[LUXURY_DOUBLE_ROOMS];
        deluxeDoubleRooms = new DeluxeDoubleRoom[DELUXE_DOUBLE_ROOMS];
        luxurySingleRooms = new LuxurySingleRoom[LUXURY_SINGLE_ROOMS];
        deluxeSingleRooms = new DeluxeSingleRoom[DELUXE_SINGLE_ROOMS];

        // Initialize rooms with their numbers
        for (int i = 0; i < LUXURY_DOUBLE_ROOMS; i++) {
            luxuryDoubleRooms[i] = new LuxuryDoubleRoom(i + 1);
        }
        for (int i = 0; i < DELUXE_DOUBLE_ROOMS; i++) {
            deluxeDoubleRooms[i] = new DeluxeDoubleRoom(i + 11);
        }
        for (int i = 0; i < LUXURY_SINGLE_ROOMS; i++) {
            luxurySingleRooms[i] = new LuxurySingleRoom(i + 31);
        }
        for (int i = 0; i < DELUXE_SINGLE_ROOMS; i++) {
            deluxeSingleRooms[i] = new DeluxeSingleRoom(i + 41);
        }
    }

    // Helper to get room array based on type
    private Room[] getRoomArray(int roomType) {
        switch (roomType) {
            case 1: return luxuryDoubleRooms;
            case 2: return deluxeDoubleRooms;
            case 3: return luxurySingleRooms;
            case 4: return deluxeSingleRooms;
            default: return null;
        }
    }

    // Helper to get room index based on room number and type
    private int getRoomIndex(int roomNumber, int roomType) {
        switch (roomType) {
            case 1: return roomNumber - 1;
            case 2: return roomNumber - 11;
            case 3: return roomNumber - 31;
            case 4: return roomNumber - 41;
            default: return -1;
        }
    }

    // Displays features of a selected room type
    public void displayRoomFeatures(int roomTypeChoice) {
        Room[] rooms = getRoomArray(roomTypeChoice);
        if (rooms != null && rooms.length > 0) {
            // Display features of the first room of that type (assuming all rooms of a type have same features)
            rooms[0].displayFeatures();
        } else {
            System.out.println("Invalid room type selected.");
        }
    }

    // Displays available rooms for a selected room type
    public void displayRoomAvailability(int roomTypeChoice) {
        Room[] rooms = getRoomArray(roomTypeChoice);
        if (rooms == null) {
            System.out.println("Invalid room type selected.");
            return;
        }

        int count = 0;
        System.out.print("Available room numbers: ");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.print(room.getRoomNumber() + ", ");
                count++;
            }
        }
        System.out.println("\nNumber of rooms available: " + count);
    }

    // Books a room
    public void bookRoom(int roomTypeChoice, Scanner scanner) {
        Room[] rooms = getRoomArray(roomTypeChoice);
        if (rooms == null) {
            System.out.println("Invalid room type selected.");
            return;
        }

        displayRoomAvailability(roomTypeChoice);
        System.out.print("\nEnter room number to book: ");
        try {
            int roomNumber = scanner.nextInt();
            int roomIndex = getRoomIndex(roomNumber, roomTypeChoice);

            if (roomIndex < 0 || roomIndex >= rooms.length) {
                throw new RoomNotAvailableException("Room " + roomNumber + " does not exist for this type.");
            }

            Room room = rooms[roomIndex];
            if (!room.isAvailable()) {
                throw new RoomNotAvailableException("Room " + roomNumber + " is already booked.");
            }

            List<Person> occupants = new ArrayList<>();
            System.out.print("Enter customer name: ");
            String name = scanner.next();
            System.out.print("Enter contact number: ");
            String contact = scanner.next();
            System.out.print("Enter gender: ");
            String gender = scanner.next();
            occupants.add(new Person(name, contact, gender));

            // For double rooms, ask for second person's details
            if (room instanceof LuxuryDoubleRoom || room instanceof DeluxeDoubleRoom) {
                System.out.print("Enter second customer name: ");
                String name2 = scanner.next();
                System.out.print("Enter contact number: ");
                String contact2 = scanner.next();
                System.out.print("Enter gender: ");
                String gender2 = scanner.next();
                occupants.add(new Person(name2, contact2, gender2));
            }

            room.bookRoom(occupants);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear invalid input
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred during booking: " + e.getMessage());
        }
    }

    // Orders food for a room
    public void orderFood(int roomNumber, int roomTypeChoice, Scanner scanner) {
        try {
            int roomIndex = getRoomIndex(roomNumber, roomTypeChoice);
            Room[] rooms = getRoomArray(roomTypeChoice);

            if (rooms == null || roomIndex < 0 || roomIndex >= rooms.length) {
                System.out.println("Room " + roomNumber + " does not exist or invalid room type.");
                return;
            }

            Room room = rooms[roomIndex];
            if (room.isAvailable()) {
                System.out.println("Room " + roomNumber + " is not booked. Please book it first.");
                return;
            }

            System.out.println("\n==========\n    Menu: \n==========\n");
            List<FoodItem> menu = FoodItem.getMenu();
            for (FoodItem item : menu) {
                System.out.printf("%d. %-10sRs.%.2f%n", item.getItemNo(), item.getName(), item.getPricePerUnit());
            }

            char wish;
            do {
                System.out.print("Enter item number: ");
                int itemChoice = scanner.nextInt();
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();

                FoodItem selectedItem = menu.stream()
                        .filter(item -> item.getItemNo() == itemChoice)
                        .findFirst()
                        .orElse(null);

                if (selectedItem != null) {
                    room.addFoodOrder(new FoodOrder(selectedItem, quantity));
                    System.out.println("Item added to order.");
                } else {
                    System.out.println("Invalid item number.");
                }

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear invalid input
        } catch (Exception e) {
            System.out.println("Cannot place order: " + e.getMessage());
        }
    }

    // Deallocates a room and generates bill
    public void checkoutRoom(int roomNumber, int roomTypeChoice, Scanner scanner) {
        try {
            int roomIndex = getRoomIndex(roomNumber, roomTypeChoice);
            Room[] rooms = getRoomArray(roomTypeChoice);

            if (rooms == null || roomIndex < 0 || roomIndex >= rooms.length) {
                System.out.println("Room " + roomNumber + " does not exist or invalid room type.");
                return;
            }

            Room room = rooms[roomIndex];
            if (room.isAvailable()) {
                System.out.println("Room is already empty.");
                return;
            }

            System.out.println("Room " + room.getRoomNumber() + " is used by: " + room.getOccupants().get(0).getName());
            if (room.getOccupants().size() > 1) {
                System.out.println("And: " + room.getOccupants().get(1).getName());
            }

            System.out.println("Do you want to checkout? (y/n)");
            char wish = scanner.next().charAt(0);
            if (wish == 'y' || wish == 'Y') {
                double totalBill = room.calculateBill();
                System.out.println("\nTotal Amount: " + totalBill);
                room.checkout();
            } else {
                System.out.println("Checkout cancelled.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Clear invalid input
        } catch (Exception e) {
            System.out.println("An error occurred during checkout: " + e.getMessage());
        }
    }

    // Gets a specific room by its number
    public Room getRoomByNumber(int roomNumber) {
        if (roomNumber > 0 && roomNumber <= LUXURY_DOUBLE_ROOMS) {
            return luxuryDoubleRooms[roomNumber - 1];
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS) {
            return deluxeDoubleRooms[roomNumber - 11];
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS) {
            return luxurySingleRooms[roomNumber - 31];
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS + DELUXE_SINGLE_ROOMS) {
            return deluxeSingleRooms[roomNumber - 41];
        }
        return null; // Room not found
    }

    // Determines room type based on room number
    public int getRoomTypeByNumber(int roomNumber) {
        if (roomNumber > 0 && roomNumber <= LUXURY_DOUBLE_ROOMS) {
            return 1; // Luxury Double
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS) {
            return 2; // Deluxe Double
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS) {
            return 3; // Luxury Single
        } else if (roomNumber > LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS && roomNumber <= LUXURY_DOUBLE_ROOMS + DELUXE_DOUBLE_ROOMS + LUXURY_SINGLE_ROOMS + DELUXE_SINGLE_ROOMS) {
            return 4; // Deluxe Single
        }
        return -1; // Invalid room number
    }
}

// Runnable for saving hotel data
class DataWriter implements Runnable {
    private HotelManager hotelManager;
    private static final String FILENAME = "hotel_backup.ser";

    public DataWriter(HotelManager hotelManager) {
        this.hotelManager = hotelManager;
    }

    @Override
    public void run() {
        try (FileOutputStream fileOut = new FileOutputStream(FILENAME);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(hotelManager);
            System.out.println("\nHotel data saved successfully to " + FILENAME);
        } catch (Exception e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    // Static method to load hotel data
    public static HotelManager loadHotelData() {
        File file = new File(FILENAME);
        if (file.exists()) {
            try (FileInputStream fileIn = new FileInputStream(file);
                 ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
                System.out.println("Loading hotel data from " + FILENAME + "...");
                return (HotelManager) objectIn.readObject();
            } catch (Exception e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return null;
            }
        }
        System.out.println("No existing hotel data found. Starting fresh.");
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelManager hotelManager = DataWriter.loadHotelData();

        if (hotelManager == null) {
            hotelManager = new HotelManager();
        }

        int choice;
        do {
            System.out.println("\n--- Hotel Management System ---");
            System.out.println("1. Display room details");
            System.out.println("2. Display room availability");
            System.out.println("3. Book a room");
            System.out.println("4. Order food");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\n--- Choose Room Type ---");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type: ");
                        int roomTypeFeatures = scanner.nextInt();
                        hotelManager.displayRoomFeatures(roomTypeFeatures);
                        break;
                    case 2:
                        System.out.println("\n--- Choose Room Type ---");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type: ");
                        int roomTypeAvailability = scanner.nextInt();
                        hotelManager.displayRoomAvailability(roomTypeAvailability);
                        break;
                    case 3:
                        System.out.println("\n--- Choose Room Type to Book ---");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type: ");
                        int roomTypeBook = scanner.nextInt();
                        hotelManager.bookRoom(roomTypeBook, scanner);
                        break;
                    case 4:
                        System.out.print("Enter room number to order food: ");
                        int roomNumOrder = scanner.nextInt();
                        int roomTypeOrder = hotelManager.getRoomTypeByNumber(roomNumOrder);
                        if (roomTypeOrder != -1) {
                            hotelManager.orderFood(roomNumOrder, roomTypeOrder, scanner);
                        } else {
                            System.out.println("Invalid room number.");
                        }
                        break;
                    case 5:
                        System.out.print("Enter room number to checkout: ");
                        int roomNumCheckout = scanner.nextInt();
                        int roomTypeCheckout = hotelManager.getRoomTypeByNumber(roomNumCheckout);
                        if (roomTypeCheckout != -1) {
                            hotelManager.checkoutRoom(roomNumCheckout, roomTypeCheckout, scanner);
                        } else {
                            System.out.println("Invalid room number.");
                        }
                        break;
                    case 6:
                        // Save data before exiting
                        Thread t = new Thread(new DataWriter(hotelManager));
                        t.start();
                        // Wait for the saving thread to finish before exiting
                        try {
                            t.join();
                            System.out.println("Exiting application. Goodbye!");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Error during data saving: " + e.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
                choice = 0; // Set choice to 0 to continue the loop
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                choice = 0; // Set choice to 0 to continue the loop
            }
            System.out.println("Press Enter to continue...");
            scanner.nextLine(); // Consume the newline character left by nextInt()
            scanner.nextLine(); // Wait for user to press Enter
        } while (choice != 6);

        scanner.close();
    }
}