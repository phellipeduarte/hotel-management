import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

// --- Constantes ---
final class FoodConstants {
    public static final double SANDWICH_PRICE = 50.0;
    public static final double PASTA_PRICE = 60.0;
    public static final double NOODLES_PRICE = 70.0;
    public static final double COKE_PRICE = 30.0;

    public static final String[] FOOD_ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};

    public static double getPrice(int itemNo) {
        switch (itemNo) {
            case 1: return SANDWICH_PRICE;
            case 2: return PASTA_PRICE;
            case 3: return NOODLES_PRICE;
            case 4: return COKE_PRICE;
            default: return 0.0; // Ou lançar uma exceção IllegalArgumentException
        }
    }
}

final class RoomConstants {
    public static final int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    public static final int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    public static final double LUXURY_DOUBLE_ROOM_PRICE = 4000.0;
    public static final double DELUXE_DOUBLE_ROOM_PRICE = 3000.0;
    public static final double LUXURY_SINGLE_ROOM_PRICE = 2200.0;
    public static final double DELUXE_SINGLE_ROOM_PRICE = 1200.0;
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
        for (RoomType type : RoomType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type code: " + code);
    }
}

enum FoodItem {
    SANDWICH(1, "Sandwich", FoodConstants.SANDWICH_PRICE),
    PASTA(2, "Pasta", FoodConstants.PASTA_PRICE),
    NOODLES(3, "Noodles", FoodConstants.NOODLES_PRICE),
    COKE(4, "Coke", FoodConstants.COKE_PRICE);

    private final int itemNo;
    private final String name;
    private final double price;

    FoodItem(int itemNo, String name, double price) {
        this.itemNo = itemNo;
        this.name = name;
        this.price = price;
    }

    public int getItemNo() {
        return itemNo;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public static FoodItem fromItemNo(int itemNo) {
        for (FoodItem item : FoodItem.values()) {
            if (item.getItemNo() == itemNo) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid food item number: " + itemNo);
    }
}

// --- Exceções ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException() {
        super("Room is not available!");
    }
}

// --- Modelos (Entidades) ---
class Food implements Serializable {
    private static final long serialVersionUID = 1L;
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
}

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

abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int roomNumber;
    protected RoomType type;
    protected Customer primaryGuest;
    protected List<Food> foodOrders;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.foodOrders = new ArrayList<>();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public Customer getPrimaryGuest() {
        return primaryGuest;
    }

    public void setPrimaryGuest(Customer primaryGuest) {
        this.primaryGuest = primaryGuest;
    }

    public List<Food> getFoodOrders() {
        return Collections.unmodifiableList(foodOrders);
    }

    public void addFoodOrder(Food food) {
        this.foodOrders.add(food);
    }

    public boolean isOccupied() {
        return primaryGuest != null;
    }

    public void checkout() {
        this.primaryGuest = null;
        this.foodOrders.clear();
    }

    public abstract String getFeatures();
    public abstract double getPricePerDay();
}

class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public SingleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : " + (type == RoomType.LUXURY_SINGLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day:" + getPricePerDay();
    }

    @Override
    public double getPricePerDay() {
        return type == RoomType.LUXURY_SINGLE ? RoomConstants.LUXURY_SINGLE_ROOM_PRICE : RoomConstants.DELUXE_SINGLE_ROOM_PRICE;
    }
}

class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;
    private Customer secondGuest;

    public DoubleRoom(int roomNumber, RoomType type) {
        super(roomNumber, type);
    }

    public Customer getSecondGuest() {
        return secondGuest;
    }

    public void setSecondGuest(Customer secondGuest) {
        this.secondGuest = secondGuest;
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : " + (type == RoomType.LUXURY_DOUBLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day:" + getPricePerDay();
    }

    @Override
    public double getPricePerDay() {
        return type == RoomType.LUXURY_DOUBLE ? RoomConstants.LUXURY_DOUBLE_ROOM_PRICE : RoomConstants.DELUXE_DOUBLE_ROOM_PRICE;
    }

    @Override
    public void checkout() {
        super.checkout();
        this.secondGuest = null;
    }
}

// --- Repositório ---
class RoomRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<RoomType, Room[]> rooms;

    public RoomRepository() {
        rooms = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        rooms.put(RoomType.LUXURY_DOUBLE, new DoubleRoom[RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY]);
        rooms.put(RoomType.DELUXE_DOUBLE, new DoubleRoom[RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY]);
        rooms.put(RoomType.LUXURY_SINGLE, new SingleRoom[RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY]);
        rooms.put(RoomType.DELUXE_SINGLE, new SingleRoom[RoomConstants.DELUXE_SINGLE_ROOM_CAPACITY]);
    }

    public Room[] getRoomsByType(RoomType type) {
        return rooms.get(type);
    }

    public List<Room> getAvailableRooms(RoomType type) {
        return Arrays.stream(rooms.get(type))
                .filter(room -> room == null || !room.isOccupied())
                .collect(Collectors.toList());
    }

    public Room getRoom(RoomType type, int index) {
        Room[] roomArray = rooms.get(type);
        if (roomArray != null && index >= 0 && index < roomArray.length) {
            return roomArray[index];
        }
        return null;
    }

    public void assignRoom(RoomType type, int index, Room room) {
        Room[] roomArray = rooms.get(type);
        if (roomArray != null && index >= 0 && index < roomArray.length) {
            roomArray[index] = room;
        } else {
            throw new IllegalArgumentException("Invalid room index for type " + type);
        }
    }

    public int getRoomCount(RoomType type) {
        Room[] roomArray = rooms.get(type);
        return roomArray != null ? roomArray.length : 0;
    }

    public int getAvailableRoomCount(RoomType type) {
        return (int) Arrays.stream(rooms.get(type)).filter(room -> room == null || !room.isOccupied()).count();
    }
}

// --- Gerenciador de Dados (Persistência) ---
class DataManager {
    private static final String BACKUP_FILE = "backup";

    public static void saveHotelData(RoomRepository roomRepository) {
        try (FileOutputStream fout = new FileOutputStream(BACKUP_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(roomRepository);
            System.out.println("Hotel data saved successfully.");
        } catch (Exception e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static RoomRepository loadHotelData() {
        File f = new File(BACKUP_FILE);
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                System.out.println("Loading hotel data from backup...");
                return (RoomRepository) ois.readObject();
            } catch (Exception e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return new RoomRepository(); // Return a new one if loading fails
            }
        }
        return new RoomRepository(); // Return a new one if no backup file exists
    }
}

// --- Serviço de Negócio ---
class HotelService {
    RoomRepository roomRepository;

    public HotelService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public String getRoomFeatures(RoomType type) {
        // Criamos uma instância temporária para obter as características,
        // já que as características são dependentes apenas do tipo.
        switch (type) {
            case LUXURY_DOUBLE:
                return new DoubleRoom(0, RoomType.LUXURY_DOUBLE).getFeatures();
            case DELUXE_DOUBLE:
                return new DoubleRoom(0, RoomType.DELUXE_DOUBLE).getFeatures();
            case LUXURY_SINGLE:
                return new SingleRoom(0, RoomType.LUXURY_SINGLE).getFeatures();
            case DELUXE_SINGLE:
                return new SingleRoom(0, RoomType.DELUXE_SINGLE).getFeatures();
            default:
                throw new IllegalArgumentException("Invalid Room Type.");
        }
    }

    public int getAvailableRoomCount(RoomType type) {
        return roomRepository.getAvailableRoomCount(type);
    }

    public List<Room> getAvailableRooms(RoomType type) {
        return roomRepository.getAvailableRooms(type);
    }

    public Room bookRoom(RoomType type, int roomIndex, Customer primaryGuest, Customer secondGuest) throws RoomNotAvailableException {
        Room[] rooms = roomRepository.getRoomsByType(type);

        if (roomIndex < 0 || roomIndex >= rooms.length) {
            throw new IllegalArgumentException("Invalid room index.");
        }

        if (rooms[roomIndex] != null && rooms[roomIndex].isOccupied()) {
            throw new RoomNotAvailableException();
        }

        Room newRoom;
        if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
            DoubleRoom doubleRoom = new DoubleRoom(roomIndex + 1, type); // Room numbers typically start from 1
            doubleRoom.setPrimaryGuest(primaryGuest);
            doubleRoom.setSecondGuest(secondGuest);
            newRoom = doubleRoom;
        } else {
            SingleRoom singleRoom = new SingleRoom(roomIndex + 1, type);
            singleRoom.setPrimaryGuest(primaryGuest);
            newRoom = singleRoom;
        }
        roomRepository.assignRoom(type, roomIndex, newRoom);
        return newRoom;
    }

    public void addFoodToRoom(RoomType type, int roomIndex, FoodItem foodItem, int quantity) {
        Room room = roomRepository.getRoom(type, roomIndex);
        if (room == null || !room.isOccupied()) {
            throw new IllegalStateException("Room not booked or does not exist for food order.");
        }
        room.addFoodOrder(new Food(foodItem, quantity));
    }

    public double calculateBill(RoomType type, int roomIndex) {
        Room room = roomRepository.getRoom(type, roomIndex);
        if (room == null || !room.isOccupied()) {
            System.out.println("Room is not occupied. Cannot generate bill.");
            return 0.0;
        }

        double totalAmount = room.getPricePerDay();
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + String.format("%.2f", room.getPricePerDay()));
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item        Quantity    Price");
        System.out.println("-------------------------");

        for (Food food : room.getFoodOrders()) {
            totalAmount += food.getPrice();
            String format = "%-12s%-12s%-12.2f%n";
            System.out.printf(format, food.getItem().getName(), food.getQuantity(), food.getPrice());
        }
        System.out.println("\nTotal Amount- " + String.format("%.2f", totalAmount));
        return totalAmount;
    }

    public boolean checkoutRoom(RoomType type, int roomIndex) {
        Room room = roomRepository.getRoom(type, roomIndex);
        if (room == null || !room.isOccupied()) {
            System.out.println("Room is empty already.");
            return false;
        }

        System.out.println("Room used by " + room.getPrimaryGuest().getName());
        return true; // Indicate that checkout is possible, UI handles confirmation
    }

    public void confirmCheckout(RoomType type, int roomIndex) {
        Room room = roomRepository.getRoom(type, roomIndex);
        if (room != null && room.isOccupied()) {
            calculateBill(type, roomIndex);
            room.checkout();
            roomRepository.assignRoom(type, roomIndex, null); // Deallocate the room
            System.out.println("Deallocated successfully.");
        }
    }
}

// --- Aplicativo Principal (Interface do Usuário) ---
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static HotelService hotelService;

    public static void main(String[] args) {
        RoomRepository roomRepository = DataManager.loadHotelData();
        hotelService = new HotelService(roomRepository);

        int choice;
        do {
            displayMainMenu();
            choice = getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        displayRoomDetailsMenu();
                        break;
                    case 2:
                        displayRoomAvailabilityMenu();
                        break;
                    case 3:
                        bookRoomMenu();
                        break;
                    case 4:
                        orderFoodMenu();
                        break;
                    case 5:
                        checkoutRoomMenu();
                        break;
                    case 6:
                        System.out.println("Exiting Hotel Management System. Saving data...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }

            if (choice != 6) {
                System.out.println("\nContinue? (y/n)");
                char wish = scanner.next().charAt(0);
                if (wish == 'n' || wish == 'N') {
                    choice = 6;
                } else if (wish != 'y' && wish != 'Y') {
                    System.out.println("Invalid option. Continuing by default.");
                }
            }

        } while (choice != 6);

        DataManager.saveHotelData(roomRepository);
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n-------------------------------------");
        System.out.println("      HOTEL MANAGEMENT SYSTEM      ");
        System.out.println("-------------------------------------");
        System.out.println("1. Display Room Details");
        System.out.println("2. Display Room Availability");
        System.out.println("3. Book Room");
        System.out.println("4. Order Food");
        System.out.println("5. Checkout");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Consume the invalid input
            System.out.print("Enter your choice: ");
        }
        return scanner.nextInt();
    }

    private static RoomType getRoomTypeChoice() {
        System.out.println("\nChoose room type:");
        for (RoomType type : RoomType.values()) {
            System.out.println(type.getCode() + ". " + type.getDescription());
        }
        System.out.print("Enter room type: ");
        int typeCode = getUserChoice();
        return RoomType.fromCode(typeCode);
    }

    private static void displayRoomDetailsMenu() {
        try {
            RoomType type = getRoomTypeChoice();
            System.out.println("\n" + type.getDescription() + " Features:");
            System.out.println(hotelService.getRoomFeatures(type));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void displayRoomAvailabilityMenu() {
        try {
            RoomType type = getRoomTypeChoice();
            int availableCount = hotelService.getAvailableRoomCount(type);
            System.out.println("Number of " + type.getDescription() + " available: " + availableCount);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void bookRoomMenu() {
        try {
            RoomType type = getRoomTypeChoice();
            List<Room> availableRooms = hotelService.getAvailableRooms(type);

            if (availableRooms.isEmpty()) {
                System.out.println("No " + type.getDescription() + " available at the moment.");
                return;
            }

            System.out.println("\nAvailable " + type.getDescription() + " numbers: ");
            // Print room numbers that are null or not occupied
            for (int i = 0; i < hotelService.roomRepository.getRoomCount(type); i++) {
                Room room = hotelService.roomRepository.getRoom(type, i);
                if (room == null || !room.isOccupied()) {
                    System.out.print((i + getRoomNumberOffset(type)) + ", ");
                }
            }
            System.out.println("\nEnter desired room number:");
            int roomNumberInput = getUserChoice();
            int roomIndex = getRoomIndex(roomNumberInput, type); // Adjust to 0-based index for the specific room type array

            if (roomIndex == -1 || roomIndex >= hotelService.roomRepository.getRoomCount(type)) {
                System.out.println("Invalid room number for this type.");
                return;
            }

            System.out.print("Enter primary customer name: ");
            String name = scanner.next();
            System.out.print("Enter contact number: ");
            String contact = scanner.next();
            System.out.print("Enter gender: ");
            String gender = scanner.next();
            Customer primaryGuest = new Customer(name, contact, gender);

            Customer secondGuest = null;
            if (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) {
                System.out.print("Enter second customer name: ");
                String name2 = scanner.next();
                System.out.print("Enter contact number: ");
                String contact2 = scanner.next();
                System.out.print("Enter gender: ");
                String gender2 = scanner.next();
                secondGuest = new Customer(name2, contact2, gender2);
            }

            hotelService.bookRoom(type, roomIndex, primaryGuest, secondGuest);
            System.out.println("Room " + roomNumberInput + " booked successfully for " + type.getDescription() + "!");

        } catch (IllegalArgumentException | RoomNotAvailableException e) {
            System.out.println("Error booking room: " + e.getMessage());
        }
    }

    private static void orderFoodMenu() {
        try {
            System.out.print("Enter room number: ");
            int roomNumberInput = getUserChoice();
            RoomType type = determineRoomType(roomNumberInput);
            int roomIndex = getRoomIndex(roomNumberInput, type);

            if (type == null) {
                System.out.println("Room " + roomNumberInput + " does not exist or is an invalid room number.");
                return;
            }
            // Check if the room is actually occupied
            Room room = hotelService.roomRepository.getRoom(type, roomIndex);
            if (room == null || !room.isOccupied()) {
                System.out.println("Room " + roomNumberInput + " is not booked. Cannot order food.");
                return;
            }

            System.out.println("\n==========\n   Menu:  \n==========\n");
            for (FoodItem item : FoodItem.values()) {
                System.out.printf("%d. %-10sRs.%.2f%n", item.getItemNo(), item.getName(), item.getPrice());
            }

            char wish;
            do {
                System.out.print("Enter food item number: ");
                int itemNo = getUserChoice();
                FoodItem foodItem = FoodItem.fromItemNo(itemNo);

                System.out.print("Enter quantity: ");
                int quantity = getUserChoice();

                hotelService.addFoodToRoom(type, roomIndex, foodItem, quantity);
                System.out.println("Food added to room " + roomNumberInput + ".");

                System.out.println("Do you want to order anything else? (y/n)");
                wish = scanner.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error ordering food: " + e.getMessage());
        }
    }

    private static void checkoutRoomMenu() {
        try {
            System.out.print("Enter room number: ");
            int roomNumberInput = getUserChoice();
            RoomType type = determineRoomType(roomNumberInput);
            int roomIndex = getRoomIndex(roomNumberInput, type);

            if (type == null) {
                System.out.println("Room " + roomNumberInput + " does not exist or is an invalid room number.");
                return;
            }

            if (hotelService.checkoutRoom(type, roomIndex)) {
                System.out.println("Do you want to checkout? (y/n)");
                char confirm = scanner.next().charAt(0);
                if (confirm == 'y' || confirm == 'Y') {
                    hotelService.confirmCheckout(type, roomIndex);
                } else {
                    System.out.println("Checkout cancelled.");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error during checkout: " + e.getMessage());
        }
    }

    // --- Métodos Auxiliares para Mapeamento de Números de Quartos ---
    private static RoomType determineRoomType(int roomNumber) {
        if (roomNumber > 0 && roomNumber <= RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY) return RoomType.LUXURY_DOUBLE;
        if (roomNumber > RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY && roomNumber <= (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY)) return RoomType.DELUXE_DOUBLE;
        if (roomNumber > (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY) && roomNumber <= (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY)) return RoomType.LUXURY_SINGLE;
        if (roomNumber > (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY) && roomNumber <= (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY + RoomConstants.DELUXE_SINGLE_ROOM_CAPACITY)) return RoomType.DELUXE_SINGLE;
        return null; // Invalid room number
    }

    private static int getRoomIndex(int roomNumber, RoomType type) {
        if (type == null) return -1;
        switch (type) {
            case LUXURY_DOUBLE:
                return roomNumber - 1;
            case DELUXE_DOUBLE:
                return roomNumber - (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + 1);
            case LUXURY_SINGLE:
                return roomNumber - (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + 1);
            case DELUXE_SINGLE:
                return roomNumber - (RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY + 1);
            default:
                return -1;
        }
    }

    private static int getRoomNumberOffset(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE:
                return 1;
            case DELUXE_DOUBLE:
                return RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + 1;
            case LUXURY_SINGLE:
                return RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + 1;
            case DELUXE_SINGLE:
                return RoomConstants.LUXURY_DOUBLE_ROOM_CAPACITY + RoomConstants.DELUXE_DOUBLE_ROOM_CAPACITY + RoomConstants.LUXURY_SINGLE_ROOM_CAPACITY + 1;
            default:
                return 0;
        }
    }
}