import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

// --- Constantes e Enums ---
final class Constants {
    public static final String BACKUP_FILE_NAME = "backup";
    public static final int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    public static final int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    // Prefixes for room numbers to avoid overlapping and make them distinct
    public static final int LUXURY_DOUBLE_ROOM_PREFIX = 100;
    public static final int DELUXE_DOUBLE_ROOM_PREFIX = 200;
    public static final int LUXURY_SINGLE_ROOM_PREFIX = 300;
    public static final int DELUXE_SINGLE_ROOM_PREFIX = 400;

    private Constants() {
        // Private constructor to prevent instantiation
    }
}

enum RoomType {
    LUXURY_DOUBLE(Constants.LUXURY_DOUBLE_ROOM_CAPACITY, 4000.0, "Luxury Double Room", Constants.LUXURY_DOUBLE_ROOM_PREFIX),
    DELUXE_DOUBLE(Constants.DELUXE_DOUBLE_ROOM_CAPACITY, 3000.0, "Deluxe Double Room", Constants.DELUXE_DOUBLE_ROOM_PREFIX),
    LUXURY_SINGLE(Constants.LUXURY_SINGLE_ROOM_CAPACITY, 2200.0, "Luxury Single Room", Constants.LUXURY_SINGLE_ROOM_PREFIX),
    DELUXE_SINGLE(Constants.DELUXE_SINGLE_ROOM_CAPACITY, 1200.0, "Deluxe Single Room", Constants.DELUXE_SINGLE_ROOM_PREFIX);

    private final int capacity;
    private final double dailyCharge;
    private final String description;
    private final int roomNumberPrefix;

    RoomType(int capacity, double dailyCharge, String description, int roomNumberPrefix) {
        this.capacity = capacity;
        this.dailyCharge = dailyCharge;
        this.description = description;
        this.roomNumberPrefix = roomNumberPrefix;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getDailyCharge() {
        return dailyCharge;
    }

    public String getDescription() {
        return description;
    }

    public int getRoomNumberPrefix() {
        return roomNumberPrefix;
    }

    public static RoomType fromOption(int option) {
        return switch (option) {
            case 1 -> LUXURY_DOUBLE;
            case 2 -> DELUXE_DOUBLE;
            case 3 -> LUXURY_SINGLE;
            case 4 -> DELUXE_SINGLE;
            default -> throw new IllegalArgumentException("Invalid room type option: " + option);
        };
    }
}

enum MenuItem {
    SANDWICH("Sandwich", 50.0),
    PASTA("Pasta", 60.0),
    NOODLES("Noodles", 70.0),
    COKE("Coke", 30.0);

    private final String name;
    private final double price;

    MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public static MenuItem fromItemNumber(int itemNo) {
        return switch (itemNo) {
            case 1 -> SANDWICH;
            case 2 -> PASTA;
            case 3 -> NOODLES;
            case 4 -> COKE;
            default -> throw new IllegalArgumentException("Invalid menu item number: " + itemNo);
        };
    }
}

// --- Exceções Personalizadas ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "RoomNotAvailableException: " + getMessage();
    }
}

class RoomNotFoundException extends Exception {
    public RoomNotFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "RoomNotFoundException: " + getMessage();
    }
}

// --- Classes de Entidade ---
class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String contact;
    private String gender;

    public Customer(String name, String contact, String gender) {
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

class FoodOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private MenuItem item;
    private int quantity;
    private double price;

    public FoodOrder(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice() * quantity;
    }

    // Getters
    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private int roomNumber;
    private RoomType type;
    private List<FoodOrder> foodOrders = new ArrayList<>();
    private boolean isBooked = false;

    protected Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public List<FoodOrder> getFoodOrders() {
        return foodOrders;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void book() {
        this.isBooked = true;
    }

    public void deallocate() {
        this.isBooked = false;
        this.foodOrders.clear(); // Clear food orders on deallocation
    }

    public void addFoodOrder(FoodOrder order) {
        foodOrders.add(order);
    }

    public abstract String getRoomDetails();
    public abstract List<Customer> getCustomers();
}

class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;
    private Customer customer;

    public SingleRoom(int roomNumber, RoomType type, Customer customer) {
        super(roomNumber, type);
        this.customer = customer;
        book();
    }

    // Constructor for empty room initialization
    public SingleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String getRoomDetails() {
        String details = "Room Number: " + getRoomNumber() + "\n" +
                "Type: " + getType().getDescription() + "\n" +
                "AC: " + (getType() == RoomType.LUXURY_SINGLE ? "Yes" : "No") + "\n" +
                "Free breakfast: Yes\n" +
                "Charge per day: " + getType().getDailyCharge();
        if (isBooked() && customer != null) {
            details += "\nBooked by: " + customer.getName();
        }
        return details;
    }

    @Override
    public List<Customer> getCustomers() {
        return customer != null ? List.of(customer) : new ArrayList<>();
    }

    @Override
    public void deallocate() {
        super.deallocate();
        this.customer = null;
    }
}

class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;
    private Customer customer1;
    private Customer customer2;

    public DoubleRoom(int roomNumber, RoomType type, Customer customer1, Customer customer2) {
        super(roomNumber, type);
        this.customer1 = customer1;
        this.customer2 = customer2;
        book();
    }

    // Constructor for empty room initialization
    public DoubleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    public Customer getCustomer1() {
        return customer1;
    }

    public Customer getCustomer2() {
        return customer2;
    }

    @Override
    public String getRoomDetails() {
        String details = "Room Number: " + getRoomNumber() + "\n" +
                "Type: " + getType().getDescription() + "\n" +
                "Number of double beds : 1\n" +
                "AC: " + (getType() == RoomType.LUXURY_DOUBLE ? "Yes" : "No") + "\n" +
                "Free breakfast: Yes\n" +
                "Charge per day: " + getType().getDailyCharge();
        if (isBooked()) {
            details += "\nBooked by: " + (customer1 != null ? customer1.getName() : "N/A") +
                    (customer2 != null ? " and " + customer2.getName() : "");
        }
        return details;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        if (customer1 != null) customers.add(customer1);
        if (customer2 != null) customers.add(customer2);
        return customers;
    }

    @Override
    public void deallocate() {
        super.deallocate();
        this.customer1 = null;
        this.customer2 = null;
    }
}

// --- Serviços ---

// Singleton para o Menu (se os itens são fixos)
class Menu {
    private static Menu instance;
    private final List<MenuItem> menuItems;

    private Menu() {
        menuItems = Arrays.asList(MenuItem.values());
    }

    public static synchronized Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public MenuItem getMenuItem(int itemNumber) throws IllegalArgumentException {
        return MenuItem.fromItemNumber(itemNumber);
    }

    public void displayMenu(OutputHandler outputHandler) {
        outputHandler.displayMessage("\n==========\n   Menu:  \n==========\n");
        for (MenuItem item : menuItems) {
            outputHandler.displayMessage(String.format("%d.%s\t\tRs.%.2f", item.ordinal() + 1, item.getName(), item.getPrice()));
        }
        outputHandler.displayMessage(""); // New line for better formatting
    }
}

class RoomManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<RoomType, List<Room>> rooms;

    public RoomManager() {
        rooms = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        for (RoomType type : RoomType.values()) {
            rooms.put(type, new ArrayList<>(type.getCapacity()));
            for (int i = 0; i < type.getCapacity(); i++) {
                int roomNumber = type.getRoomNumberPrefix() + i + 1;
                if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
                    rooms.get(type).add(new DoubleRoom(roomNumber, type));
                } else {
                    rooms.get(type).add(new SingleRoom(roomNumber, type));
                }
            }
        }
    }

    public List<Room> getRoomsByType(RoomType type) {
        return rooms.getOrDefault(type, new ArrayList<>());
    }

    public Room findRoom(int roomNumber) {
        for (List<Room> roomList : rooms.values()) {
            for (Room room : roomList) {
                if (room.getRoomNumber() == roomNumber) {
                    return room;
                }
            }
        }
        return null; // Room not found
    }

    public List<Room> getAvailableRooms(RoomType type) {
        return rooms.getOrDefault(type, new ArrayList<>())
                .stream()
                .filter(room -> !room.isBooked())
                .collect(Collectors.toList());
    }

    public int getAvailableRoomCount(RoomType type) {
        return (int) rooms.getOrDefault(type, new ArrayList<>())
                .stream()
                .filter(room -> !room.isBooked())
                .count();
    }

    public void bookRoom(int roomNumber, Customer customer1, Customer customer2) throws RoomNotAvailableException, RoomNotFoundException {
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room number " + roomNumber + " does not exist.");
        }
        if (room.isBooked()) {
            throw new RoomNotAvailableException("Room " + roomNumber + " is already booked.");
        }

        if (room instanceof DoubleRoom doubleRoom) {
            if (customer1 == null || customer2 == null) {
                throw new IllegalArgumentException("Double rooms require two customers.");
            }
            doubleRoom.deallocate(); // Clear existing data if any
            DoubleRoom newDoubleRoom = new DoubleRoom(roomNumber, room.getType(), customer1, customer2);
            replaceRoomInManager(newDoubleRoom);
        } else if (room instanceof SingleRoom singleRoom) {
            if (customer1 == null) {
                throw new IllegalArgumentException("Single rooms require one customer.");
            }
            singleRoom.deallocate(); // Clear existing data if any
            SingleRoom newSingleRoom = new SingleRoom(roomNumber, room.getType(), customer1);
            replaceRoomInManager(newSingleRoom);
        }
    }

    private void replaceRoomInManager(Room newRoom) {
        List<Room> roomList = rooms.get(newRoom.getType());
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomNumber() == newRoom.getRoomNumber()) {
                roomList.set(i, newRoom);
                return;
            }
        }
    }


    public void deallocateRoom(int roomNumber) throws RoomNotFoundException {
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room number " + roomNumber + " does not exist.");
        }
        if (!room.isBooked()) {
            System.out.println("Room " + roomNumber + " is already empty."); // This should be handled by UI layer
            return;
        }
        room.deallocate();
    }

    public void addFoodToRoom(int roomNumber, FoodOrder order) throws RoomNotFoundException, RoomNotAvailableException {
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room number " + roomNumber + " does not exist.");
        }
        if (!room.isBooked()) {
            throw new RoomNotAvailableException("Cannot order food for an unbooked room.");
        }
        room.addFoodOrder(order);
    }
}

class BillingService {
    public void generateBill(Room room, OutputHandler outputHandler) {
        if (room == null) {
            outputHandler.displayMessage("Error: Room not found for billing.");
            return;
        }

        double totalAmount = room.getType().getDailyCharge();

        outputHandler.displayMessage("\n*******");
        outputHandler.displayMessage(" Bill:-");
        outputHandler.displayMessage("*******");
        outputHandler.displayMessage("\nRoom Charge - " + room.getType().getDailyCharge());

        if (!room.getFoodOrders().isEmpty()) {
            outputHandler.displayMessage("\n===============");
            outputHandler.displayMessage("Food Charges:- ");
            outputHandler.displayMessage("===============");
            outputHandler.displayMessage("Item        Quantity    Price");
            outputHandler.displayMessage("-------------------------");
            for (FoodOrder order : room.getFoodOrders()) {
                totalAmount += order.getPrice();
                String format = "%-12s%-12s%-10s%n";
                outputHandler.displayMessage(String.format(format, order.getItem().getName(), order.getQuantity(), order.getPrice()));
            }
        }
        outputHandler.displayMessage("\nTotal Amount- " + totalAmount);
    }
}

// Uma interface para abstrair a persistência
interface PersistenceStrategy {
    RoomManager loadData() throws Exception;
    void saveData(RoomManager roomManager) throws Exception;
}

class FilePersistenceStrategy implements PersistenceStrategy, Runnable {
    private RoomManager roomManager;
    private final String fileName;

    public FilePersistenceStrategy(String fileName) {
        this.fileName = fileName;
    }

    // Constructor used for saving data via Thread
    public FilePersistenceStrategy(RoomManager roomManager, String fileName) {
        this.roomManager = roomManager;
        this.fileName = fileName;
    }

    @Override
    public RoomManager loadData() throws Exception {
        File f = new File(fileName);
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                return (RoomManager) ois.readObject();
            }
        }
        return new RoomManager(); // Return a new manager if no backup exists
    }

    @Override
    public void saveData(RoomManager roomManager) throws Exception {
        this.roomManager = roomManager; // Set the manager for the Runnable to use
        Thread t = new Thread(this);
        t.start();
        t.join(); // Wait for the thread to finish saving
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(roomManager);
        } catch (Exception e) {
            System.err.println("Error in writing: " + e.getMessage());
        }
    }
}

class HotelService {
    private RoomManager roomManager;
    private Menu menu;
    private BillingService billingService;
    private PersistenceStrategy persistenceStrategy;
    private InputHandler inputHandler;
    private OutputHandler outputHandler;

    public HotelService(PersistenceStrategy persistenceStrategy, InputHandler inputHandler, OutputHandler outputHandler) {
        this.persistenceStrategy = persistenceStrategy;
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
        this.menu = Menu.getInstance();
        this.billingService = new BillingService();
        loadHotelData();
    }

    private void loadHotelData() {
        try {
            roomManager = persistenceStrategy.loadData();
            outputHandler.displayMessage("Hotel data loaded successfully.");
        } catch (Exception e) {
            outputHandler.displayMessage("Error loading hotel data. Starting with fresh data: " + e.getMessage());
            roomManager = new RoomManager();
        }
    }

    public void saveHotelData() {
        try {
            persistenceStrategy.saveData(roomManager);
            outputHandler.displayMessage("Hotel data saved successfully.");
        } catch (Exception e) {
            outputHandler.displayMessage("Error saving hotel data: " + e.getMessage());
        }
    }

    public void displayRoomDetails(RoomType type) {
        outputHandler.displayMessage(type.getDescription() + " Features:");
        switch (type) {
            case LUXURY_DOUBLE:
                outputHandler.displayMessage("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: " + type.getDailyCharge());
                break;
            case DELUXE_DOUBLE:
                outputHandler.displayMessage("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: " + type.getDailyCharge());
                break;
            case LUXURY_SINGLE:
                outputHandler.displayMessage("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day: " + type.getDailyCharge());
                break;
            case DELUXE_SINGLE:
                outputHandler.displayMessage("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day: " + type.getDailyCharge());
                break;
        }
    }

    public void displayRoomAvailability(RoomType type) {
        int count = roomManager.getAvailableRoomCount(type);
        outputHandler.displayMessage("Number of " + type.getDescription() + " rooms available: " + count);
        if (count > 0) {
            List<Room> availableRooms = roomManager.getAvailableRooms(type);
            outputHandler.displayMessage("Available room numbers for " + type.getDescription() + ": " +
                    availableRooms.stream()
                            .map(room -> String.valueOf(room.getRoomNumber()))
                            .collect(Collectors.joining(", ")));
        }
    }

    public void bookRoomProcess(RoomType type) {
        List<Room> availableRooms = roomManager.getAvailableRooms(type);
        if (availableRooms.isEmpty()) {
            outputHandler.displayMessage("Sorry, no " + type.getDescription() + " rooms are available.");
            return;
        }

        outputHandler.displayMessage("\nChoose room number from available rooms for " + type.getDescription() + ":");
        outputHandler.displayMessage(availableRooms.stream()
                .map(room -> String.valueOf(room.getRoomNumber()))
                .collect(Collectors.joining(", ")));

        int roomNumber = inputHandler.readInt("Enter desired room number: ");

        try {
            Room roomToBook = roomManager.findRoom(roomNumber);
            if (roomToBook == null || roomToBook.getType() != type) {
                throw new RoomNotFoundException("Room number " + roomNumber + " is not a valid " + type.getDescription() + " room or does not exist.");
            }
            if (roomToBook.isBooked()) {
                throw new RoomNotAvailableException("Room " + roomNumber + " is already booked.");
            }

            Customer customer1 = getCustomerDetails(false);
            Customer customer2 = null;
            if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
                customer2 = getCustomerDetails(true);
            }

            roomManager.bookRoom(roomNumber, customer1, customer2);
            outputHandler.displayMessage("Room " + roomNumber + " booked successfully!");
        } catch (RoomNotAvailableException | RoomNotFoundException e) {
            outputHandler.displayMessage("Booking failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            outputHandler.displayMessage("Booking failed: " + e.getMessage());
        } catch (Exception e) {
            outputHandler.displayMessage("An unexpected error occurred during booking: " + e.getMessage());
        }
    }

    private Customer getCustomerDetails(boolean isSecondCustomer) {
        outputHandler.displayMessage("\nEnter " + (isSecondCustomer ? "second " : "") + "customer details:");
        String name = inputHandler.readString("Enter name: ");
        String contact = inputHandler.readString("Enter contact number: ");
        String gender = inputHandler.readString("Enter gender: ");
        return new Customer(name, contact, gender);
    }

    public void orderFoodProcess(int roomNumber) {
        try {
            Room room = roomManager.findRoom(roomNumber);
            if (room == null || !room.isBooked()) {
                outputHandler.displayMessage("Room " + roomNumber + " is not booked or does not exist. Cannot order food.");
                return;
            }

            menu.displayMenu(outputHandler);
            char wish;
            do {
                int itemChoice = inputHandler.readInt("Enter item number to order: ");
                int quantity = inputHandler.readInt("Enter quantity: ");

                try {
                    MenuItem selectedItem = menu.getMenuItem(itemChoice);
                    FoodOrder order = new FoodOrder(selectedItem, quantity);
                    roomManager.addFoodToRoom(roomNumber, order);
                    outputHandler.displayMessage("Food item added to room " + roomNumber + ".");
                } catch (IllegalArgumentException e) {
                    outputHandler.displayMessage("Invalid menu item number: " + e.getMessage());
                }

                wish = inputHandler.readChar("Do you want to order anything else? (y/n): ");
            } while (wish == 'y' || wish == 'Y');

        } catch (RoomNotFoundException | RoomNotAvailableException e) {
            outputHandler.displayMessage("Order failed: " + e.getMessage());
        } catch (Exception e) {
            outputHandler.displayMessage("An unexpected error occurred during food order: " + e.getMessage());
        }
    }

    public void checkoutRoom(int roomNumber) {
        try {
            Room room = roomManager.findRoom(roomNumber);
            if (room == null) {
                outputHandler.displayMessage("Room " + roomNumber + " does not exist.");
                return;
            }
            if (!room.isBooked()) {
                outputHandler.displayMessage("Room " + roomNumber + " is already empty.");
                return;
            }

            outputHandler.displayMessage("Room " + roomNumber + " is currently used by: " +
                    room.getCustomers().stream().map(Customer::getName).collect(Collectors.joining(" and ")));

            char confirm = inputHandler.readChar("Do you want to checkout? (y/n): ");
            if (confirm == 'y' || confirm == 'Y') {
                billingService.generateBill(room, outputHandler);
                roomManager.deallocateRoom(roomNumber);
                outputHandler.displayMessage("Room " + roomNumber + " deallocated successfully!");
            } else {
                outputHandler.displayMessage("Checkout cancelled for room " + roomNumber + ".");
            }
        } catch (RoomNotFoundException e) {
            outputHandler.displayMessage("Checkout failed: " + e.getMessage());
        } catch (Exception e) {
            outputHandler.displayMessage("An unexpected error occurred during checkout: " + e.getMessage());
        }
    }
}

// --- Adapters para Entrada/Saída ---
interface InputHandler {
    String readString(String prompt);
    int readInt(String prompt);
    char readChar(String prompt);
}

class ConsoleInputHandler implements InputHandler {
    private final Scanner scanner;

    public ConsoleInputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }

    @Override
    public int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // consume the invalid input
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over
        return value;
    }

    @Override
    public char readChar(String prompt) {
        System.out.print(prompt);
        return scanner.next().charAt(0);
    }
}

interface OutputHandler {
    void displayMessage(String message);
    void displayError(String message);
}

class ConsoleOutputHandler implements OutputHandler {
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.err.println("Error: " + message);
    }
}

// --- Main Application ---
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new ConsoleInputHandler(scanner);
        OutputHandler outputHandler = new ConsoleOutputHandler();
        PersistenceStrategy persistenceStrategy = new FilePersistenceStrategy(Constants.BACKUP_FILE_NAME);
        HotelService hotelService = new HotelService(persistenceStrategy, inputHandler, outputHandler);

        try {
            int choice;
            int roomTypeOption;
            char continueChoice;

            do {
                outputHandler.displayMessage("\nEnter your choice :\n1.Display room details\n2.Display room availability \n3.Book Room\n4.Order food\n5.Checkout\n6.Exit\n");
                choice = inputHandler.readInt("Enter option: ");

                switch (choice) {
                    case 1:
                        roomTypeOption = inputHandler.readInt("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room \n4.Deluxe Single Room\nEnter choice: ");
                        try {
                            RoomType type = RoomType.fromOption(roomTypeOption);
                            hotelService.displayRoomDetails(type);
                        } catch (IllegalArgumentException e) {
                            outputHandler.displayError("Invalid room type option.");
                        }
                        break;
                    case 2:
                        roomTypeOption = inputHandler.readInt("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room\n4.Deluxe Single Room\nEnter choice: ");
                        try {
                            RoomType type = RoomType.fromOption(roomTypeOption);
                            hotelService.displayRoomAvailability(type);
                        } catch (IllegalArgumentException e) {
                            outputHandler.displayError("Invalid room type option.");
                        }
                        break;
                    case 3:
                        roomTypeOption = inputHandler.readInt("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room\n4.Deluxe Single Room\nEnter choice: ");
                        try {
                            RoomType type = RoomType.fromOption(roomTypeOption);
                            hotelService.bookRoomProcess(type);
                        } catch (IllegalArgumentException e) {
                            outputHandler.displayError("Invalid room type option.");
                        }
                        break;
                    case 4:
                        int roomNumberForFood = inputHandler.readInt("Enter Room Number for food order: ");
                        hotelService.orderFoodProcess(roomNumberForFood);
                        break;
                    case 5:
                        int roomNumberForCheckout = inputHandler.readInt("Enter Room Number to checkout: ");
                        hotelService.checkoutRoom(roomNumberForCheckout);
                        break;
                    case 6:
                        outputHandler.displayMessage("Exiting Hotel Management System. Goodbye!");
                        break;
                    default:
                        outputHandler.displayMessage("Invalid option. Please try again.");
                }

                if (choice != 6) {
                    continueChoice = inputHandler.readChar("\nContinue: (y/n): ");
                    while (!isValidYesNo(continueChoice)) {
                        outputHandler.displayMessage("Invalid input. Please enter 'y' or 'n'.");
                        continueChoice = inputHandler.readChar("\nContinue: (y/n): ");
                    }
                } else {
                    continueChoice = 'n'; // Exit loop if user chose to exit
                }
            } while (continueChoice == 'y' || continueChoice == 'Y');

        } catch (Exception e) {
            outputHandler.displayError("An unexpected error occurred in the main application loop: " + e.getMessage());
            e.printStackTrace();
        } finally {
            hotelService.saveHotelData(); // Ensure data is saved on exit
            scanner.close();
        }
    }

    private static boolean isValidYesNo(char c) {
        return c == 'y' || c == 'Y' || c == 'n' || c == 'N';
    }
}