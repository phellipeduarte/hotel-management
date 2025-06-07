import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.stream.Collectors;

// --- Constants ---
final class HotelConstants {
    // Room Capacities
    public static final int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    public static final int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    // Room Charges
    public static final double LUXURY_DOUBLE_ROOM_CHARGE = 4000.0;
    public static final double DELUXE_DOUBLE_ROOM_CHARGE = 3000.0;
    public static final double LUXURY_SINGLE_ROOM_CHARGE = 2200.0;
    public static final double DELUXE_SINGLE_ROOM_CHARGE = 1200.0;

    // Food Prices
    public static final double SANDWICH_PRICE = 50.0;
    public static final double PASTA_PRICE = 60.0;
    public static final double NOODLES_PRICE = 70.0;
    public static final double COKE_PRICE = 30.0;

    // File Paths
    public static final String BACKUP_FILE_NAME = "backup";

    // Menu Options
    public static final int DISPLAY_ROOM_DETAILS = 1;
    public static final int DISPLAY_ROOM_AVAILABILITY = 2;
    public static final int BOOK_ROOM = 3;
    public static final int ORDER_FOOD = 4;
    public static final int CHECKOUT_ROOM = 5;
    public static final int EXIT_APPLICATION = 6;

    private HotelConstants() {
        // Private constructor to prevent instantiation
    }
}

// --- Exceptions ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room is not available!");
    }
}

class RoomNotFoundException extends Exception {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

// --- Enums ---
enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room"),
    DELUXE_DOUBLE(2, "Deluxe Double Room"),
    LUXURY_SINGLE(3, "Luxury Single Room"),
    DELUXE_SINGLE(4, "Deluxe Single Room");

    private final int code;
    private final String description;

    RoomType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RoomType fromCode(int code) {
        for (RoomType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type code: " + code);
    }
}

enum FoodItem {
    SANDWICH(1, "Sandwich", HotelConstants.SANDWICH_PRICE),
    PASTA(2, "Pasta", HotelConstants.PASTA_PRICE),
    NOODLES(3, "Noodles", HotelConstants.NOODLES_PRICE),
    COKE(4, "Coke", HotelConstants.COKE_PRICE);

    private final int itemNumber;
    private final String name;
    private final double pricePerUnit;

    FoodItem(int itemNumber, String name, double pricePerUnit) {
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

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public static FoodItem fromItemNumber(int itemNumber) {
        for (FoodItem item : values()) {
            if (item.getItemNumber() == itemNumber) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid food item number: " + itemNumber);
    }
}

// --- Domain Models ---

class FoodOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private FoodItem item;
    private int quantity;
    private double totalPrice;

    public FoodOrder(FoodItem item, int quantity) {
        this.item = Objects.requireNonNull(item, "Food item cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        this.totalPrice = item.getPricePerUnit() * quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return String.format("%-10s%-10s%-10.2f", item.getName(), quantity, totalPrice);
    }
}

class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
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

interface IRoom extends Serializable {
    boolean isOccupied();
    void assignGuests(List<Guest> guests);
    List<Guest> getGuests();
    void addFoodOrder(FoodOrder order);
    List<FoodOrder> getFoodOrders();
    void clearRoom();
    double getRoomCharge();
    String getFeatures();
    RoomType getType();
    int getRoomNumber();
    void setRoomNumber(int roomNumber);
}

abstract class AbstractRoom implements IRoom {
    private static final long serialVersionUID = 1L;
    protected List<Guest> guests;
    protected ArrayList<FoodOrder> foodOrders;
    protected int roomNumber;

    public AbstractRoom() {
        this.guests = new ArrayList<>();
        this.foodOrders = new ArrayList<>();
    }

    @Override
    public boolean isOccupied() {
        return !guests.isEmpty();
    }

    @Override
    public void assignGuests(List<Guest> guests) {
        this.guests = new ArrayList<>(guests);
    }

    @Override
    public List<Guest> getGuests() {
        return new ArrayList<>(guests);
    }

    @Override
    public void addFoodOrder(FoodOrder order) {
        this.foodOrders.add(order);
    }

    @Override
    public List<FoodOrder> getFoodOrders() {
        return new ArrayList<>(foodOrders);
    }

    @Override
    public void clearRoom() {
        this.guests.clear();
        this.foodOrders.clear();
    }

    @Override
    public int getRoomNumber() {
        return roomNumber;
    }

    @Override
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}

class LuxuryDoubleRoom extends AbstractRoom {
    private static final long serialVersionUID = 1L;

    @Override
    public double getRoomCharge() {
        return HotelConstants.LUXURY_DOUBLE_ROOM_CHARGE;
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: " + getRoomCharge();
    }

    @Override
    public RoomType getType() {
        return RoomType.LUXURY_DOUBLE;
    }
}

class DeluxeDoubleRoom extends AbstractRoom {
    private static final long serialVersionUID = 1L;

    @Override
    public double getRoomCharge() {
        return HotelConstants.DELUXE_DOUBLE_ROOM_CHARGE;
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: " + getRoomCharge();
    }

    @Override
    public RoomType getType() {
        return RoomType.DELUXE_DOUBLE;
    }
}

class LuxurySingleRoom extends AbstractRoom {
    private static final long serialVersionUID = 1L;

    @Override
    public double getRoomCharge() {
        return HotelConstants.LUXURY_SINGLE_ROOM_CHARGE;
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: " + getRoomCharge();
    }

    @Override
    public RoomType getType() {
        return RoomType.LUXURY_SINGLE;
    }
}

class DeluxeSingleRoom extends AbstractRoom {
    private static final long serialVersionUID = 1L;

    @Override
    public double getRoomCharge() {
        return HotelConstants.DELUXE_SINGLE_ROOM_CHARGE;
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: " + getRoomCharge();
    }

    @Override
    public RoomType getType() {
        return RoomType.DELUXE_SINGLE;
    }
}

// --- Data Layer (Persistence & Repository) ---

class HotelData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<RoomType, List<IRoom>> roomsByType;

    public HotelData() {
        roomsByType = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        roomsByType.put(RoomType.LUXURY_DOUBLE, createRooms(HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY, LuxuryDoubleRoom::new));
        roomsByType.put(RoomType.DELUXE_DOUBLE, createRooms(HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY, DeluxeDoubleRoom::new));
        roomsByType.put(RoomType.LUXURY_SINGLE, createRooms(HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY, LuxurySingleRoom::new));
        roomsByType.put(RoomType.DELUXE_SINGLE, createRooms(HotelConstants.DELUXE_SINGLE_ROOM_CAPACITY, DeluxeSingleRoom::new));
    }

    private List<IRoom> createRooms(int capacity, java.util.function.Supplier<IRoom> roomSupplier) {
        List<IRoom> rooms = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            rooms.add(null); // Initialize with nulls to represent empty slots
        }
        return rooms;
    }

    public List<IRoom> getRooms(RoomType type) {
        return roomsByType.get(type);
    }
}

interface IRoomRepository {
    List<IRoom> getAllRooms(RoomType type);
    IRoom getRoom(RoomType type, int index);
    void updateRoom(RoomType type, int index, IRoom room);
    int getAvailableRoomCount(RoomType type);
}

class RoomRepository implements IRoomRepository {
    private HotelData hotelData;

    public RoomRepository(HotelData hotelData) {
        this.hotelData = hotelData;
    }

    @Override
    public List<IRoom> getAllRooms(RoomType type) {
        return hotelData.getRooms(type);
    }

    @Override
    public IRoom getRoom(RoomType type, int index) {
        List<IRoom> rooms = hotelData.getRooms(type);
        if (index < 0 || index >= rooms.size()) {
            return null; // Or throw an IndexOutOfBoundsException
        }
        return rooms.get(index);
    }

    @Override
    public void updateRoom(RoomType type, int index, IRoom room) {
        List<IRoom> rooms = hotelData.getRooms(type);
        if (index >= 0 && index < rooms.size()) {
            rooms.set(index, room);
        }
    }

    @Override
    public int getAvailableRoomCount(RoomType type) {
        return (int) hotelData.getRooms(type).stream()
                .filter(Objects::isNull)
                .count();
    }
}

class HotelDataStore {
    public static HotelData loadHotelData() {
        File file = new File(HotelConstants.BACKUP_FILE_NAME);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (HotelData) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return new HotelData(); // Return new data if loading fails
            }
        }
        return new HotelData(); // New data if file doesn't exist
    }

    public static void saveHotelData(HotelData hotelData) {
        try (FileOutputStream fos = new FileOutputStream(HotelConstants.BACKUP_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(hotelData);
        } catch (IOException e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }
}

class AutoSaveTask implements Runnable {
    private final HotelData hotelData;

    public AutoSaveTask(HotelData hotelData) {
        this.hotelData = hotelData;
    }

    @Override
    public void run() {
        HotelDataStore.saveHotelData(hotelData);
    }
}

// --- Services / Business Logic ---

class RoomFactory {
    public static IRoom createRoom(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE:
                return new LuxuryDoubleRoom();
            case DELUXE_DOUBLE:
                return new DeluxeDoubleRoom();
            case LUXURY_SINGLE:
                return new LuxurySingleRoom();
            case DELUXE_SINGLE:
                return new DeluxeSingleRoom();
            default:
                throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}

interface IBookingService {
    void bookRoom(RoomType roomType, Scanner scanner, IRoomRepository roomRepository) throws RoomNotAvailableException, InputMismatchException;
}

class RoomBookingService implements IBookingService {
    @Override
    public void bookRoom(RoomType roomType, Scanner scanner, IRoomRepository roomRepository) throws RoomNotAvailableException, InputMismatchException {
        System.out.println("\nChoose room number from: ");
        List<IRoom> rooms = roomRepository.getAllRooms(roomType);
        List<Integer> availableRoomNumbers = new ArrayList<>();
        int roomNumberOffset = getRoomNumberOffset(roomType);

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i) == null || !rooms.get(i).isOccupied()) {
                availableRoomNumbers.add(i + 1 + roomNumberOffset);
            }
        }

        if (availableRoomNumbers.isEmpty()) {
            throw new RoomNotAvailableException();
        }

        System.out.println(availableRoomNumbers.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.print("\nEnter room number: ");
        int selectedRoomNumber = scanner.nextInt();
        int roomIndex = selectedRoomNumber - 1 - roomNumberOffset;

        if (roomIndex < 0 || roomIndex >= rooms.size() || (rooms.get(roomIndex) != null && rooms.get(roomIndex).isOccupied())) {
            throw new RoomNotAvailableException();
        }

        IRoom newRoom = RoomFactory.createRoom(roomType);
        newRoom.setRoomNumber(selectedRoomNumber);

        List<Guest> guests = new ArrayList<>();
        System.out.print("\nEnter first customer name: ");
        String name = scanner.next();
        System.out.print("Enter contact number: ");
        String contact = scanner.next();
        System.out.print("Enter gender: ");
        String gender = scanner.next();
        guests.add(new Guest(name, contact, gender));

        if (roomType == RoomType.LUXURY_DOUBLE || roomType == RoomType.DELUXE_DOUBLE) {
            System.out.print("Enter second customer name: ");
            String name2 = scanner.next();
            System.out.print("Enter contact number: ");
            String contact2 = scanner.next();
            System.out.print("Enter gender: ");
            String gender2 = scanner.next();
            guests.add(new Guest(name2, contact2, gender2));
        }

        newRoom.assignGuests(guests);
        roomRepository.updateRoom(roomType, roomIndex, newRoom);
        System.out.println("Room Booked Successfully!");
    }

    private int getRoomNumberOffset(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE: return 0;
            case DELUXE_DOUBLE: return HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY;
            case LUXURY_SINGLE: return HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY;
            case DELUXE_SINGLE: return HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY + HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY;
            default: return 0;
        }
    }
}

interface IBillCalculator {
    void printBill(IRoom room);
}

class BillCalculator implements IBillCalculator {
    @Override
    public void printBill(IRoom room) {
        if (room == null) {
            System.out.println("Room not found or not occupied.");
            return;
        }

        double totalAmount = room.getRoomCharge();

        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + room.getRoomCharge());

        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item        Quantity   Price");
        System.out.println("-------------------------");

        for (FoodOrder order : room.getFoodOrders()) {
            totalAmount += order.getTotalPrice();
            System.out.println(order);
        }
        System.out.println("\nTotal Amount - " + totalAmount);
    }
}

class HotelManagementSystem {
    private static HotelData hotelData = HotelDataStore.loadHotelData();
    private static IRoomRepository roomRepository = new RoomRepository(hotelData);
    private static IBookingService bookingService = new RoomBookingService();
    private static IBillCalculator billCalculator = new BillCalculator();
    private static Scanner scanner = new Scanner(System.in); // Singleton Scanner

    // Private constructor to prevent instantiation
    private HotelManagementSystem() {
    }

    public static HotelData getHotelData() {
        return hotelData;
    }


    public static void displayRoomDetails(RoomType type) {
        IRoom dummyRoom = RoomFactory.createRoom(type);
        System.out.println(dummyRoom.getFeatures());
    }

    public static void displayRoomAvailability(RoomType type) {
        int count = roomRepository.getAvailableRoomCount(type);
        System.out.println("Number of rooms available : " + count);
    }

    public static void bookRoom(RoomType type) {
        try {
            bookingService.bookRoom(type, scanner, roomRepository);
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid room number.");
            scanner.next(); // Consume the invalid input
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void orderFood(int roomNumber) {
        try {
            RoomType roomType = getRoomTypeFromRoomNumber(roomNumber);
            int roomIndex = getRoomIndexFromRoomNumber(roomNumber, roomType);
            IRoom room = roomRepository.getRoom(roomType, roomIndex);

            if (room == null || !room.isOccupied()) {
                throw new RoomNotFoundException("\nRoom not booked or doesn't exist.");
            }

            System.out.println("\n==========\n    Menu:   \n==========\n\n");
            for (FoodItem item : FoodItem.values()) {
                System.out.printf("%d.%-10sRs.%.0f%n", item.getItemNumber(), item.getName(), item.getPricePerUnit());
            }

            char wish;
            do {
                System.out.print("Enter item number: ");
                int itemChoice = scanner.nextInt();
                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();

                FoodItem selectedItem = FoodItem.fromItemNumber(itemChoice);
                room.addFoodOrder(new FoodOrder(selectedItem, quantity));

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.next().charAt(0);
            } while (Character.toLowerCase(wish) == 'y');

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Consume the invalid input
        } catch (IllegalArgumentException | RoomNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.out.println("An error occurred during food ordering: " + e.getMessage());
        }
    }

    public static void checkOutRoom(int roomNumber) {
        try {
            RoomType roomType = getRoomTypeFromRoomNumber(roomNumber);
            int roomIndex = getRoomIndexFromRoomNumber(roomNumber, roomType);
            IRoom room = roomRepository.getRoom(roomType, roomIndex);

            if (room == null || !room.isOccupied()) {
                System.out.println("Room is already empty or does not exist.");
                return;
            }

            System.out.println("Room currently used by " + room.getGuests().stream().map(Guest::getName).collect(Collectors.joining(" and ")));
            System.out.println("Do you want to checkout? (y/n)");
            char wish = scanner.next().charAt(0);

            if (Character.toLowerCase(wish) == 'y') {
                billCalculator.printBill(room);
                room.clearRoom(); // Clear guest and food data
                roomRepository.updateRoom(roomType, roomIndex, null); // Set room slot to null
                System.out.println("Deallocated successfully!");
            }
        } catch (RoomNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid room number.");
            scanner.next(); // Consume the invalid input
        } catch (Exception e) {
            System.out.println("An error occurred during checkout: " + e.getMessage());
        }
    }

    // Helper method to determine room type from room number
    private static RoomType getRoomTypeFromRoomNumber(int roomNumber) throws RoomNotFoundException {
        if (roomNumber > 0 && roomNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY) {
            return RoomType.LUXURY_DOUBLE;
        } else if (roomNumber > HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY && roomNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY) {
            return RoomType.DELUXE_DOUBLE;
        } else if (roomNumber > HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY && roomNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY + HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY) {
            return RoomType.LUXURY_SINGLE;
        } else if (roomNumber > HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY + HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY && roomNumber <= HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY + HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY + HotelConstants.DELUXE_SINGLE_ROOM_CAPACITY) {
            return RoomType.DELUXE_SINGLE;
        } else {
            throw new RoomNotFoundException("Room number " + roomNumber + " does not exist.");
        }
    }

    // Helper method to calculate array index from room number
    private static int getRoomIndexFromRoomNumber(int roomNumber, RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE:
                return roomNumber - 1;
            case DELUXE_DOUBLE:
                return roomNumber - 1 - HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY;
            case LUXURY_SINGLE:
                return roomNumber - 1 - (HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY);
            case DELUXE_SINGLE:
                return roomNumber - 1 - (HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY + HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY + HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY);
            default:
                return -1; // Should not happen with prior validation
        }
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) { // Use try-with-resources for Scanner
            int choice, subChoice;
            char continueChoice;

            do {
                System.out.println("\n--- Hotel Management System ---");
                System.out.println("1. Display Room Details");
                System.out.println("2. Display Room Availability");
                System.out.println("3. Book Room");
                System.out.println("4. Order Food");
                System.out.println("5. Checkout Room");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                try {
                    choice = sc.nextInt();

                    switch (choice) {
                        case HotelConstants.DISPLAY_ROOM_DETAILS:
                            System.out.println("\nChoose room type:");
                            Arrays.stream(RoomType.values()).forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                            System.out.print("Enter room type choice: ");
                            subChoice = sc.nextInt();
                            HotelManagementSystem.displayRoomDetails(RoomType.fromCode(subChoice));
                            break;
                        case HotelConstants.DISPLAY_ROOM_AVAILABILITY:
                            System.out.println("\nChoose room type:");
                            Arrays.stream(RoomType.values()).forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                            System.out.print("Enter room type choice: ");
                            subChoice = sc.nextInt();
                            HotelManagementSystem.displayRoomAvailability(RoomType.fromCode(subChoice));
                            break;
                        case HotelConstants.BOOK_ROOM:
                            System.out.println("\nChoose room type:");
                            Arrays.stream(RoomType.values()).forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                            System.out.print("Enter room type choice: ");
                            subChoice = sc.nextInt();
                            HotelManagementSystem.bookRoom(RoomType.fromCode(subChoice));
                            break;
                        case HotelConstants.ORDER_FOOD:
                            System.out.print("Enter Room Number: ");
                            subChoice = sc.nextInt();
                            HotelManagementSystem.orderFood(subChoice);
                            break;
                        case HotelConstants.CHECKOUT_ROOM:
                            System.out.print("Enter Room Number: ");
                            subChoice = sc.nextInt();
                            HotelManagementSystem.checkOutRoom(subChoice);
                            break;
                        case HotelConstants.EXIT_APPLICATION:
                            System.out.println("Exiting application. Saving data...");
                            Thread t = new Thread(new AutoSaveTask(HotelManagementSystem.getHotelData())); // Access static hotelData
                            t.start();
                            try {
                                t.join(); // Wait for the save thread to complete
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.err.println("Save operation interrupted.");
                            }
                            return; // Exit main method
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next(); // Consume the invalid input
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }

                System.out.print("\nContinue? (y/n): ");
                continueChoice = sc.next().charAt(0);
                while (Character.toLowerCase(continueChoice) != 'y' && Character.toLowerCase(continueChoice) != 'n') {
                    System.out.println("Invalid option. Please enter 'y' or 'n'.");
                    System.out.print("\nContinue? (y/n): ");
                    continueChoice = sc.next().charAt(0);
                }
            } while (Character.toLowerCase(continueChoice) == 'y');

            System.out.println("Exiting application. Saving data...");
            Thread t = new Thread(new AutoSaveTask(HotelManagementSystem.getHotelData())); // Access static hotelData
            t.start();
            try {
                t.join(); // Wait for the save thread to complete
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Save operation interrupted.");
            }

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            HotelManagementSystem.closeScanner(); // Ensure scanner is closed
        }
    }
}