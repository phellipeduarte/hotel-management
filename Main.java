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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

// 1. Constantes bem definidas
final class HotelConstants {
    public static final String BACKUP_FILE_NAME = "backup";
    public static final int LUXURY_DOUBLE_ROOM_COUNT = 10;
    public static final int DELUXE_DOUBLE_ROOM_COUNT = 20;
    public static final int LUXURY_SINGLE_ROOM_COUNT = 10;
    public static final int DELUXE_SINGLE_ROOM_COUNT = 20;

    // Preços dos quartos
    public static final double PRICE_LUXURY_DOUBLE = 4000.0;
    public static final double PRICE_DELUXE_DOUBLE = 3000.0;
    public static final double PRICE_LUXURY_SINGLE = 2200.0;
    public static final double PRICE_DELUXE_SINGLE = 1200.0;

    private HotelConstants() {
        // impede instanciação
    }
}

// 2. Enum para tipos de quarto para evitar 'magic numbers'
enum RoomType {
    LUXURY_DOUBLE(1, HotelConstants.LUXURY_DOUBLE_ROOM_COUNT, HotelConstants.PRICE_LUXURY_DOUBLE, "Luxury Double Room"),
    DELUXE_DOUBLE(2, HotelConstants.DELUXE_DOUBLE_ROOM_COUNT, HotelConstants.PRICE_DELUXE_DOUBLE, "Deluxe Double Room"),
    LUXURY_SINGLE(3, HotelConstants.LUXURY_SINGLE_ROOM_COUNT, HotelConstants.PRICE_LUXURY_SINGLE, "Luxury Single Room"),
    DELUXE_SINGLE(4, HotelConstants.DELUXE_SINGLE_ROOM_COUNT, HotelConstants.PRICE_DELUXE_SINGLE, "Deluxe Single Room");

    private final int code;
    private final int capacity;
    private final double price;
    private final String description;

    RoomType(int code, int capacity, double price, String description) {
        this.code = code;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public static RoomType fromCode(int code) {
        for (RoomType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type code: " + code);
    }
}

// 3. Enum para itens de comida
enum FoodItem {
    SANDWICH(1, "Sandwich", 50.0f),
    PASTA(2, "Pasta", 60.0f),
    NOODLES(3, "Noodles", 70.0f),
    COKE(4, "Coke", 30.0f);

    private final int itemNo;
    private final String name;
    private final float price;

    FoodItem(int itemNo, String name, float price) {
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

    public float getPrice() {
        return price;
    }

    public static FoodItem fromItemNo(int itemNo) {
        for (FoodItem item : values()) {
            if (item.itemNo == itemNo) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid food item number: " + itemNo);
    }
}

// 4. Classe Food refatorada para usar Enum e ser mais coesa
class Food implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FoodItem item;
    private final int quantity;
    private final float totalPrice;

    public Food(FoodItem item, int quantity) {
        this.item = Objects.requireNonNull(item);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.quantity = quantity;
        this.totalPrice = item.getPrice() * quantity;
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
        return item.getName() + "\t" + quantity + "\t" + totalPrice;
    }
}

// 5. Cliente - Uma classe para representar uma pessoa
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

    @Override
    public String toString() {
        return "Name: " + name + ", Contact: " + contact + ", Gender: " + gender;
    }
}

// 6. Abstração para quarto (SRP, OCP, LSP)
abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    protected RoomType type;
    protected List<Customer> customers;
    protected List<Food> foodOrders;
    protected int roomNumber;

    public Room(RoomType type, int roomNumber) {
        this.type = type;
        this.roomNumber = roomNumber;
        this.customers = new ArrayList<>();
        this.foodOrders = new ArrayList<>();
    }

    public boolean isAvailable() {
        return customers.isEmpty();
    }

    public void addCustomer(Customer customer) {
        if (customers.size() < getMaxOccupancy()) {
            customers.add(customer);
        } else {
            throw new IllegalStateException("Room " + roomNumber + " is at maximum occupancy.");
        }
    }

    public abstract int getMaxOccupancy();

    public void addFood(Food food) {
        foodOrders.add(food);
    }

    public RoomType getType() {
        return type;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    public double calculateRoomCharge() {
        return type.getPrice();
    }

    public double calculateFoodCharge() {
        return foodOrders.stream().mapToDouble(Food::getTotalPrice).sum();
    }

    public double calculateTotalBill() {
        return calculateRoomCharge() + calculateFoodCharge();
    }

    // Método para exibir detalhes do quarto (para o usuário)
    public abstract String getFeatures();

    public void clearRoom() {
        customers.clear();
        foodOrders.clear();
    }
}

// 7. Implementações concretas de quartos (OCP)
class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public SingleRoom(RoomType type, int roomNumber) {
        super(type, roomNumber);
    }

    @Override
    public int getMaxOccupancy() {
        return 1;
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : " + (type == RoomType.LUXURY_SINGLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + type.getPrice();
    }
}

class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public DoubleRoom(RoomType type, int roomNumber) {
        super(type, roomNumber);
    }

    @Override
    public int getMaxOccupancy() {
        return 2;
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : " + (type == RoomType.LUXURY_DOUBLE ? "Yes" : "No") +
                "\nFree breakfast : Yes\nCharge per day: " + type.getPrice();
    }
}

// 8. Fábrica para criar quartos (Factory Method)
class RoomFactory {
    public static Room createRoom(RoomType type, int roomNumber) {
        switch (type) {
            case LUXURY_DOUBLE:
            case DELUXE_DOUBLE:
                return new DoubleRoom(type, roomNumber);
            case LUXURY_SINGLE:
            case DELUXE_SINGLE:
                return new SingleRoom(type, roomNumber);
            default:
                throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}

// 9. Gerenciador de Quartos (SRP)
class RoomManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<RoomType, Room[]> roomsByType;
    private final Map<Integer, Room> allRoomsMap; // Mapeia número de quarto global para o objeto Room

    public RoomManager() {
        roomsByType = new HashMap<>();
        allRoomsMap = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        int roomNumberCounter = 1;

        // Luxury Double Rooms
        Room[] luxuryDoubleRooms = new Room[HotelConstants.LUXURY_DOUBLE_ROOM_COUNT];
        for (int i = 0; i < HotelConstants.LUXURY_DOUBLE_ROOM_COUNT; i++) {
            luxuryDoubleRooms[i] = RoomFactory.createRoom(RoomType.LUXURY_DOUBLE, roomNumberCounter);
            allRoomsMap.put(roomNumberCounter, luxuryDoubleRooms[i]);
            roomNumberCounter++;
        }
        roomsByType.put(RoomType.LUXURY_DOUBLE, luxuryDoubleRooms);

        // Deluxe Double Rooms
        Room[] deluxeDoubleRooms = new Room[HotelConstants.DELUXE_DOUBLE_ROOM_COUNT];
        for (int i = 0; i < HotelConstants.DELUXE_DOUBLE_ROOM_COUNT; i++) {
            deluxeDoubleRooms[i] = RoomFactory.createRoom(RoomType.DELUXE_DOUBLE, roomNumberCounter);
            allRoomsMap.put(roomNumberCounter, deluxeDoubleRooms[i]);
            roomNumberCounter++;
        }
        roomsByType.put(RoomType.DELUXE_DOUBLE, deluxeDoubleRooms);

        // Luxury Single Rooms
        Room[] luxurySingleRooms = new Room[HotelConstants.LUXURY_SINGLE_ROOM_COUNT];
        for (int i = 0; i < HotelConstants.LUXURY_SINGLE_ROOM_COUNT; i++) {
            luxurySingleRooms[i] = RoomFactory.createRoom(RoomType.LUXURY_SINGLE, roomNumberCounter);
            allRoomsMap.put(roomNumberCounter, luxurySingleRooms[i]);
            roomNumberCounter++;
        }
        roomsByType.put(RoomType.LUXURY_SINGLE, luxurySingleRooms);

        // Deluxe Single Rooms
        Room[] deluxeSingleRooms = new Room[HotelConstants.DELUXE_SINGLE_ROOM_COUNT];
        for (int i = 0; i < HotelConstants.DELUXE_SINGLE_ROOM_COUNT; i++) {
            deluxeSingleRooms[i] = RoomFactory.createRoom(RoomType.DELUXE_SINGLE, roomNumberCounter);
            allRoomsMap.put(roomNumberCounter, deluxeSingleRooms[i]);
            roomNumberCounter++;
        }
        roomsByType.put(RoomType.DELUXE_SINGLE, deluxeSingleRooms);
    }

    public Room getRoom(RoomType type, int index) {
        Room[] rooms = roomsByType.get(type);
        if (rooms != null && index >= 0 && index < rooms.length) {
            return rooms[index];
        }
        return null;
    }

    public Room getRoomByGlobalNumber(int globalRoomNumber) {
        return allRoomsMap.get(globalRoomNumber);
    }

    public int getAvailableRoomCount(RoomType type) {
        return (int) Arrays.stream(roomsByType.get(type))
                .filter(Room::isAvailable)
                .count();
    }

    public List<Integer> getAvailableRoomNumbers(RoomType type) {
        List<Integer> availableNumbers = new ArrayList<>();
        Room[] rooms = roomsByType.get(type);
        if (rooms != null) {
            for (Room room : rooms) {
                if (room.isAvailable()) {
                    availableNumbers.add(room.getRoomNumber());
                }
            }
        }
        return availableNumbers;
    }

    public Room[] getRoomsArray(RoomType type) {
        return roomsByType.get(type);
    }
}

// 10. Exceção customizada
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

// 11. Classe Hotel refatorada para ter responsabilidade única e usar o RoomManager
class HotelManagementSystem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RoomManager roomManager;
    private transient Scanner scanner; // transient para não serializar o Scanner

    public HotelManagementSystem() {
        this.roomManager = new RoomManager();
        this.scanner = new Scanner(System.in);
    }

    // Construtor para desserialização
    public HotelManagementSystem(RoomManager roomManager) {
        this.roomManager = roomManager;
        this.scanner = new Scanner(System.in);
    }

    // Configura o scanner após a desserialização
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public void displayRoomDetails(RoomType type) {
        System.out.println("\n--- " + type.getDescription() + " Features ---");
        System.out.println(RoomFactory.createRoom(type, -1).getFeatures()); // -1 é um placeholder, não será usado
        System.out.println("Charge per day: " + type.getPrice());
        System.out.println("-------------------------");
    }

    public void displayRoomAvailability(RoomType type) {
        int availableCount = roomManager.getAvailableRoomCount(type);
        System.out.println("Number of " + type.getDescription() + " rooms available: " + availableCount);
        List<Integer> availableRooms = roomManager.getAvailableRoomNumbers(type);
        if (!availableRooms.isEmpty()) {
            System.out.print("Available room numbers: ");
            availableRooms.forEach(roomNum -> System.out.print(roomNum + " "));
            System.out.println();
        }
    }

    public void bookRoom(RoomType type) {
        List<Integer> availableRooms = roomManager.getAvailableRoomNumbers(type);
        if (availableRooms.isEmpty()) {
            System.out.println("Sorry, no " + type.getDescription() + " rooms are available.");
            return;
        }

        System.out.print("\nEnter desired room number from " + availableRooms + ": ");
        int roomNumber;
        try {
            roomNumber = scanner.nextInt();
            if (!availableRooms.contains(roomNumber)) {
                throw new RoomNotAvailableException("Room " + roomNumber + " is not available or does not exist.");
            }
        } catch (RoomNotAvailableException e) {
            System.out.println("Booking failed: " + e.getMessage());
            return;
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }

        Room roomToBook = roomManager.getRoomByGlobalNumber(roomNumber);
        if (roomToBook == null || !roomToBook.isAvailable()) {
            System.out.println("Booking failed: Room " + roomNumber + " is already occupied or does not exist.");
            return;
        }

        System.out.println("\nEnter customer details for room " + roomNumber + ":");
        Customer customer1 = getCustomerDetails(scanner);
        roomToBook.addCustomer(customer1);

        if (roomToBook instanceof DoubleRoom) {
            System.out.println("Enter details for second customer:");
            Customer customer2 = getCustomerDetails(scanner);
            roomToBook.addCustomer(customer2);
        }

        System.out.println("Room " + roomNumber + " booked successfully!");
    }

    private Customer getCustomerDetails(Scanner sc) {
        System.out.print("Enter customer name: ");
        String name = sc.next();
        System.out.print("Enter contact number: ");
        String contact = sc.next();
        System.out.print("Enter gender: ");
        String gender = sc.next();
        return new Customer(name, contact, gender);
    }

    public void orderFood(int globalRoomNumber) {
        Room room = roomManager.getRoomByGlobalNumber(globalRoomNumber);
        if (room == null || room.isAvailable()) { // If null or available (not booked)
            System.out.println("\nRoom " + globalRoomNumber + " not booked or doesn't exist.");
            return;
        }

        System.out.println("\n==========\n    Menu:  \n==========\n");
        Arrays.stream(FoodItem.values())
                .forEach(item -> System.out.printf("%d.%s\tRs.%.2f%n", item.getItemNo(), item.getName(), item.getPrice()));

        char wish;
        do {
            try {
                System.out.print("Enter item number: ");
                int itemNo = scanner.nextInt();
                System.out.print("Quantity: ");
                int quantity = scanner.nextInt();

                FoodItem foodItem = FoodItem.fromItemNo(itemNo);
                Food food = new Food(foodItem, quantity);
                room.addFood(food);
                System.out.println("Food added to order.");

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid food item: " + e.getMessage());
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
            System.out.println("Do you want to order anything else? (y/n)");
            wish = scanner.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');
    }

    public void checkoutRoom(int globalRoomNumber) {
        Room room = roomManager.getRoomByGlobalNumber(globalRoomNumber);
        if (room == null) {
            System.out.println("Room " + globalRoomNumber + " doesn't exist.");
            return;
        }

        if (room.isAvailable()) {
            System.out.println("Room " + globalRoomNumber + " is already empty.");
            return;
        }

        System.out.println("Room " + globalRoomNumber + " is used by: " + room.getCustomers().get(0).getName() +
                (room.getCustomers().size() > 1 ? " and " + room.getCustomers().get(1).getName() : ""));

        System.out.println("Do you want to checkout? (y/n)");
        char choice = scanner.next().charAt(0);
        if (choice == 'y' || choice == 'Y') {
            displayBill(room);
            room.clearRoom();
            System.out.println("Room " + globalRoomNumber + " deallocated successfully.");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    private void displayBill(Room room) {
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("Room Charge - " + room.calculateRoomCharge());

        if (!room.getFoodOrders().isEmpty()) {
            System.out.println("\nFood Charges:- ");
            System.out.println("===============");
            System.out.println("Item        Quantity  Price");
            System.out.println("-------------------------");
            for (Food food : room.getFoodOrders()) {
                System.out.println(food);
            }
        }
        System.out.println("\nTotal Amount: " + room.calculateTotalBill());
    }
}

// 12. Classe de persistência (SRP)
class HotelDataStore {
    public static void saveHotelData(HotelManagementSystem hotelSystem) {
        try (FileOutputStream fout = new FileOutputStream(HotelConstants.BACKUP_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(hotelSystem.getRoomManager()); // Salva apenas o RoomManager
            System.out.println("Hotel data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static RoomManager loadHotelData() {
        File f = new File(HotelConstants.BACKUP_FILE_NAME);
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                RoomManager roomManager = (RoomManager) ois.readObject();
                System.out.println("Hotel data loaded from backup.");
                return roomManager;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading hotel data: " + e.getMessage());
                return new RoomManager(); // Retorna um novo RoomManager se houver erro
            }
        }
        return new RoomManager(); // Cria um novo se o arquivo não existir
    }
}

// 13. Classe principal para execução
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelManagementSystem hotelSystem;

        // Carrega os dados ou inicializa um novo sistema
        RoomManager loadedRoomManager = HotelDataStore.loadHotelData();
        hotelSystem = new HotelManagementSystem(loadedRoomManager);
        hotelSystem.setScanner(sc); // Injeta o scanner após a criação/carregamento

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
                choice = sc.nextInt();
                switch (choice) {
                    case 1: // Display room details
                        System.out.println("\nChoose room type:");
                        Arrays.stream(RoomType.values())
                                .forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                        System.out.print("Enter room type code: ");
                        int roomTypeChoice1 = sc.nextInt();
                        hotelSystem.displayRoomDetails(RoomType.fromCode(roomTypeChoice1));
                        break;
                    case 2: // Display room availability
                        System.out.println("\nChoose room type:");
                        Arrays.stream(RoomType.values())
                                .forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                        System.out.print("Enter room type code: ");
                        int roomTypeChoice2 = sc.nextInt();
                        hotelSystem.displayRoomAvailability(RoomType.fromCode(roomTypeChoice2));
                        break;
                    case 3: // Book a room
                        System.out.println("\nChoose room type:");
                        Arrays.stream(RoomType.values())
                                .forEach(type -> System.out.println(type.getCode() + ". " + type.getDescription()));
                        System.out.print("Enter room type code: ");
                        int roomTypeChoice3 = sc.nextInt();
                        hotelSystem.bookRoom(RoomType.fromCode(roomTypeChoice3));
                        break;
                    case 4: // Order food
                        System.out.print("Enter room number to order food: ");
                        int roomNumOrder = sc.nextInt();
                        hotelSystem.orderFood(roomNumOrder);
                        break;
                    case 5: // Checkout room
                        System.out.print("Enter room number to checkout: ");
                        int roomNumCheckout = sc.nextInt();
                        hotelSystem.checkoutRoom(roomNumCheckout);
                        break;
                    case 6: // Exit
                        System.out.println("Exiting application. Saving data...");
                        HotelDataStore.saveHotelData(hotelSystem);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Consome a entrada inválida
                choice = 0; // Para garantir que o loop continue
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                choice = 0;
            }
            System.out.println("\nPress Enter to continue...");
            sc.nextLine(); // Consome o newline pendente após nextInt()
            sc.nextLine(); // Espera pelo Enter
        } while (choice != 6);

        sc.close();
    }
}