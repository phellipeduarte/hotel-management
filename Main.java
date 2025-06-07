import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// --- Modelo de Comida ---
class Food implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int itemNo;
    private final int quantity;
    private final float price;

    // Itens e seus preços fixos (constantes)
    public static final String[] MENU_ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};
    public static final int[] MENU_PRICES = {50, 60, 70, 30};

    public Food(int itemNo, int quantity) {
        if (itemNo < 1 || itemNo > MENU_ITEMS.length) {
            throw new IllegalArgumentException("Invalid item number");
        }
        this.itemNo = itemNo;
        this.quantity = quantity;
        this.price = quantity * MENU_PRICES[itemNo - 1];
    }

    public String getItemName() {
        return MENU_ITEMS[itemNo - 1];
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }
}

// --- Classe abstrata para Quarto ---
abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String[] customerNames;
    protected String[] contacts;
    protected String[] genders;

    protected final ArrayList<Food> foodOrders = new ArrayList<>();

    public Room(int capacity) {
        customerNames = new String[capacity];
        contacts = new String[capacity];
        genders = new String[capacity];
    }

    public void setCustomerDetails(int index, String name, String contact, String gender) {
        customerNames[index] = name;
        contacts[index] = contact;
        genders[index] = gender;
    }

    public ArrayList<Food> getFoodOrders() {
        return foodOrders;
    }

    public abstract double getRoomCharge();

    public abstract String getRoomType();

    public int getCapacity() {
        return customerNames.length;
    }

    public String getCustomerName(int index) {
        return customerNames[index];
    }
}

// --- Quarto Single ---
class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public SingleRoom() {
        super(1);
    }

    public SingleRoom(String name, String contact, String gender) {
        this();
        setCustomerDetails(0, name, contact, gender);
    }

    @Override
    public double getRoomCharge() {
        return 2200; // padrão para Luxury Single, será sobrescrito nas subclasses
    }

    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

// --- Quarto Double ---
class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public DoubleRoom() {
        super(2);
    }

    public DoubleRoom(String name1, String contact1, String gender1,
                      String name2, String contact2, String gender2) {
        this();
        setCustomerDetails(0, name1, contact1, gender1);
        setCustomerDetails(1, name2, contact2, gender2);
    }

    @Override
    public double getRoomCharge() {
        return 4000; // padrão para Luxury Double, será sobrescrito nas subclasses
    }

    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

// --- Quarto Luxury Double ---
class LuxuryDoubleRoom extends DoubleRoom {
    private static final long serialVersionUID = 1L;

    public LuxuryDoubleRoom(String name1, String contact1, String gender1,
                            String name2, String contact2, String gender2) {
        super(name1, contact1, gender1, name2, contact2, gender2);
    }

    @Override
    public double getRoomCharge() {
        return 4000;
    }

    @Override
    public String getRoomType() {
        return "Luxury Double Room";
    }
}

// --- Quarto Deluxe Double ---
class DeluxeDoubleRoom extends DoubleRoom {
    private static final long serialVersionUID = 1L;

    public DeluxeDoubleRoom(String name1, String contact1, String gender1,
                            String name2, String contact2, String gender2) {
        super(name1, contact1, gender1, name2, contact2, gender2);
    }

    @Override
    public double getRoomCharge() {
        return 3000;
    }

    @Override
    public String getRoomType() {
        return "Deluxe Double Room";
    }
}

// --- Quarto Luxury Single ---
class LuxurySingleRoom extends SingleRoom {
    private static final long serialVersionUID = 1L;

    public LuxurySingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getRoomCharge() {
        return 2200;
    }

    @Override
    public String getRoomType() {
        return "Luxury Single Room";
    }
}

// --- Quarto Deluxe Single ---
class DeluxeSingleRoom extends SingleRoom {
    private static final long serialVersionUID = 1L;

    public DeluxeSingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getRoomCharge() {
        return 1200;
    }

    @Override
    public String getRoomType() {
        return "Deluxe Single Room";
    }
}

// --- Exceção para quarto indisponível ---
class NotAvailableException extends Exception {
    public NotAvailableException() {
        super("Not Available!");
    }
}

// --- Classe Holder para quartos ---
class HotelRooms implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LUXURY_DOUBLE_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_CAPACITY = 20;
    public static final int LUXURY_SINGLE_CAPACITY = 10;
    public static final int DELUXE_SINGLE_CAPACITY = 20;

    private final Room[] luxuryDoubleRooms = new Room[LUXURY_DOUBLE_CAPACITY];
    private final Room[] deluxeDoubleRooms = new Room[DELUXE_DOUBLE_CAPACITY];
    private final Room[] luxurySingleRooms = new Room[LUXURY_SINGLE_CAPACITY];
    private final Room[] deluxeSingleRooms = new Room[DELUXE_SINGLE_CAPACITY];

    public Room[] getRooms(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> luxuryDoubleRooms;
            case DELUXE_DOUBLE -> deluxeDoubleRooms;
            case LUXURY_SINGLE -> luxurySingleRooms;
            case DELUXE_SINGLE -> deluxeSingleRooms;
        };
    }
}

// --- Enum para tipos de quarto ---
enum RoomType {
    LUXURY_DOUBLE(1, 1),
    DELUXE_DOUBLE(2, 11),
    LUXURY_SINGLE(3, 31),
    DELUXE_SINGLE(4, 41);

    private final int id;
    private final int roomNumberStart;

    RoomType(int id, int roomNumberStart) {
        this.id = id;
        this.roomNumberStart = roomNumberStart;
    }

    public int getId() {
        return id;
    }

    public int getRoomNumberStart() {
        return roomNumberStart;
    }

    public static RoomType fromId(int id) {
        for (RoomType type : values()) {
            if (type.id == id) return type;
        }
        return null;
    }

    public static RoomType fromRoomNumber(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) return LUXURY_DOUBLE;
        if (roomNumber >= 11 && roomNumber <= 30) return DELUXE_DOUBLE;
        if (roomNumber >= 31 && roomNumber <= 40) return LUXURY_SINGLE;
        if (roomNumber >= 41 && roomNumber <= 60) return DELUXE_SINGLE;
        return null;
    }
}

// --- Fábrica para criar quartos conforme tipo ---
class RoomFactory {
    public static Room createRoom(RoomType type, String[] customerNames, String[] contacts, String[] genders) {
        return switch (type) {
            case LUXURY_DOUBLE -> new LuxuryDoubleRoom(customerNames[0], contacts[0], genders[0],
                    customerNames[1], contacts[1], genders[1]);
            case DELUXE_DOUBLE -> new DeluxeDoubleRoom(customerNames[0], contacts[0], genders[0],
                    customerNames[1], contacts[1], genders[1]);
            case LUXURY_SINGLE -> new LuxurySingleRoom(customerNames[0], contacts[0], genders[0]);
            case DELUXE_SINGLE -> new DeluxeSingleRoom(customerNames[0], contacts[0], genders[0]);
        };
    }
}

// --- Serviço para interagir com usuário e gerenciar hotel ---
class HotelService {
    private final HotelRooms hotelRooms;
    private final Scanner scanner;

    public HotelService(HotelRooms hotelRooms) {
        this.hotelRooms = hotelRooms;
        this.scanner = new Scanner(System.in);
    }

    // Pede detalhes do cliente(s) e retorna arrays de dados
    private String[] inputCustomerData(int count) {
        String[] names = new String[count];
        String[] contacts = new String[count];
        String[] genders = new String[count];

        for (int i = 0; i < count; i++) {
            System.out.printf("Enter name of customer %d: ", i + 1);
            names[i] = scanner.nextLine().trim();

            System.out.printf("Enter contact of customer %d: ", i + 1);
            contacts[i] = scanner.nextLine().trim();

            System.out.printf("Enter gender of customer %d: ", i + 1);
            genders[i] = scanner.nextLine().trim();
        }
        return new String[]{String.join(",", names), String.join(",", contacts), String.join(",", genders)};
    }

    public void bookRoom(RoomType type) throws NotAvailableException {
        Room[] rooms = hotelRooms.getRooms(type);
        int capacity = switch (type) {
            case LUXURY_DOUBLE, DELUXE_DOUBLE -> 2;
            case LUXURY_SINGLE, DELUXE_SINGLE -> 1;
        };

        int availableIndex = -1;
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                availableIndex = i;
                break;
            }
        }
        if (availableIndex == -1) throw new NotAvailableException();

        String[] customerNames = new String[capacity];
        String[] contacts = new String[capacity];
        String[] genders = new String[capacity];

        for (int i = 0; i < capacity; i++) {
            System.out.printf("Enter name of customer %d: ", i + 1);
            customerNames[i] = scanner.nextLine().trim();

            System.out.printf("Enter contact of customer %d: ", i + 1);
            contacts[i] = scanner.nextLine().trim();

            System.out.printf("Enter gender of customer %d: ", i + 1);
            genders[i] = scanner.nextLine().trim();
        }

        rooms[availableIndex] = RoomFactory.createRoom(type, customerNames, contacts, genders);

        System.out.printf("%s successfully booked! Your room number is %d.\n",
                type.name().replace("_", " "), availableIndex + type.getRoomNumberStart());
    }

    public void orderFood(int roomNumber) throws IllegalArgumentException {
        RoomType type = RoomType.fromRoomNumber(roomNumber);
        if (type == null) {
            throw new IllegalArgumentException("Invalid room number.");
        }
        Room[] rooms = hotelRooms.getRooms(type);
        int index = roomNumber - type.getRoomNumberStart();

        Room room = rooms[index];
        if (room == null) {
            System.out.println("Room not booked.");
            return;
        }

        boolean ordering = true;
        while (ordering) {
            System.out.println("Food Menu:");
            for (int i = 0; i < Food.MENU_ITEMS.length; i++) {
                System.out.printf("%d. %s - Rs %d\n", i + 1, Food.MENU_ITEMS[i], Food.MENU_PRICES[i]);
            }
            System.out.print("Enter food item number to order or 0 to finish: ");

            int itemNo;
            try {
                itemNo = scanner.nextInt();
                scanner.nextLine(); // limpar buffer
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input.");
                continue;
            }

            if (itemNo == 0) {
                ordering = false;
                break;
            }

            if (itemNo < 1 || itemNo > Food.MENU_ITEMS.length) {
                System.out.println("Invalid item number.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int qty;
            try {
                qty = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid quantity.");
                continue;
            }

            room.getFoodOrders().add(new Food(itemNo, qty));
            System.out.println("Item added to order.");
        }
    }

    public void displayBill(int roomNumber) {
        RoomType type = RoomType.fromRoomNumber(roomNumber);
        if (type == null) {
            System.out.println("Invalid room number.");
            return;
        }
        Room[] rooms = hotelRooms.getRooms(type);
        int index = roomNumber - type.getRoomNumberStart();

        Room room = rooms[index];
        if (room == null) {
            System.out.println("Room not booked.");
            return;
        }

        System.out.println("----- Bill -----");
        System.out.println("Room Type: " + room.getRoomType());
        System.out.printf("Room Charge: Rs %.2f\n", room.getRoomCharge());

        float foodTotal = 0;
        if (!room.getFoodOrders().isEmpty()) {
            System.out.println("Food Orders:");
            for (Food food : room.getFoodOrders()) {
                System.out.printf("%s x %d = Rs %.2f\n", food.getItemName(), food.getQuantity(), food.getPrice());
                foodTotal += food.getPrice();
            }
        }

        System.out.printf("Food Total: Rs %.2f\n", foodTotal);
        System.out.printf("Total Amount Payable: Rs %.2f\n", room.getRoomCharge() + foodTotal);
        System.out.println("----------------");
    }

    public void displayAvailableRooms() {
        System.out.println("Available Rooms:");

        for (RoomType type : RoomType.values()) {
            Room[] rooms = hotelRooms.getRooms(type);
            System.out.printf("%s available rooms: ", type.name().replace("_", " "));
            boolean anyAvailable = false;
            for (int i = 0; i < rooms.length; i++) {
                if (rooms[i] == null) {
                    System.out.print((i + type.getRoomNumberStart()) + " ");
                    anyAvailable = true;
                }
            }
            if (!anyAvailable) {
                System.out.print("None");
            }
            System.out.println();
        }
    }

    // Métodos adicionais para cancelar reserva, listar hóspedes, etc., podem ser adicionados aqui
}

// --- Persistência ---
class HotelPersistence {
    private static final String FILE_PATH = "backup";

    public static void saveHotelRooms(HotelRooms hotelRooms) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(hotelRooms);
        } catch (IOException e) {
            System.out.println("Error saving hotel data: " + e.getMessage());
        }
    }

    public static HotelRooms loadHotelRooms() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (HotelRooms) in.readObject();
        } catch (FileNotFoundException e) {
            return new HotelRooms(); // se não existir, retorna vazio
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading hotel data: " + e.getMessage());
            return new HotelRooms();
        }
    }
}

// --- Classe Main ---
public class Main {
    public static void main(String[] args) {
        HotelRooms hotelRooms = HotelPersistence.loadHotelRooms();
        HotelService hotelService = new HotelService(hotelRooms);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.println("\n1. Book Room\n2. Order Food\n3. Display Bill\n4. Show Available Rooms\n5. Exit");
            System.out.print("Choose an option: ");
            int option;
            try {
                option = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Invalid input.");
                continue;
            }

            try {
                switch (option) {
                    case 1 -> {
                        System.out.println("Room Types:\n1. Luxury Double\n2. Deluxe Double\n3. Luxury Single\n4. Deluxe Single");
                        System.out.print("Select room type: ");
                        int roomTypeId = scanner.nextInt();
                        scanner.nextLine();

                        RoomType type = RoomType.fromId(roomTypeId);
                        if (type == null) {
                            System.out.println("Invalid room type.");
                            continue;
                        }

                        hotelService.bookRoom(type);
                        HotelPersistence.saveHotelRooms(hotelRooms);
                    }
                    case 2 -> {
                        System.out.print("Enter room number: ");
                        int roomNo = scanner.nextInt();
                        scanner.nextLine();
                        hotelService.orderFood(roomNo);
                        HotelPersistence.saveHotelRooms(hotelRooms);
                    }
                    case 3 -> {
                        System.out.print("Enter room number: ");
                        int roomNo = scanner.nextInt();
                        scanner.nextLine();
                        hotelService.displayBill(roomNo);
                    }
                    case 4 -> hotelService.displayAvailableRooms();
                    case 5 -> {
                        running = false;
                        System.out.println("Exiting...");
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (NotAvailableException e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }
}
