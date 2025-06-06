import java.io.*;
import java.util.*;

// Enum para tipos de quarto, com propriedades
enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room", 4000, 2, true),
    DELUXE_DOUBLE(2, "Deluxe Double Room", 3000, 2, false),
    LUXURY_SINGLE(3, "Luxury Single Room", 2200, 1, true),
    DELUXE_SINGLE(4, "Deluxe Single Room", 1200, 1, false);

    final int code;
    final String description;
    final double dailyCharge;
    final int beds;
    final boolean ac;

    RoomType(int code, String description, double dailyCharge, int beds, boolean ac) {
        this.code = code;
        this.description = description;
        this.dailyCharge = dailyCharge;
        this.beds = beds;
        this.ac = ac;
    }

    public static Optional<RoomType> fromCode(int code) {
        return Arrays.stream(values()).filter(r -> r.code == code).findFirst();
    }
}

// Enum para cardápio
enum MenuItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    final int code;
    final String name;
    final double price;

    MenuItem(int code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public static Optional<MenuItem> fromCode(int code) {
        return Arrays.stream(values()).filter(m -> m.code == code).findFirst();
    }
}

// Cliente
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

    // getters, setters e toString() se quiser
}

// Pedido de comida
class FoodOrder implements Serializable {
    private MenuItem item;
    private int quantity;
    private double price;

    public FoodOrder(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.price * quantity;
    }

    public double getPrice() {
        return price;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}

// Quarto abstrato
abstract class Room implements Serializable {
    protected RoomType type;
    protected List<Customer> customers = new ArrayList<>();
    protected List<FoodOrder> foodOrders = new ArrayList<>();

    public Room(RoomType type) {
        this.type = type;
    }

    public RoomType getType() {
        return type;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer c) {
        if (customers.size() < type.beds)
            customers.add(c);
        else
            throw new IllegalStateException("Room capacity exceeded");
    }

    public void addFoodOrder(FoodOrder order) {
        foodOrders.add(order);
    }

    public double getRoomCharge() {
        return type.dailyCharge;
    }

    public double getFoodCharge() {
        return foodOrders.stream().mapToDouble(FoodOrder::getPrice).sum();
    }

    public double getTotalBill() {
        return getRoomCharge() + getFoodCharge();
    }

    public void printBill() {
        System.out.println("\n****** Bill ******");
        System.out.println("Room Type: " + type.description);
        System.out.printf("Room Charge: Rs. %.2f%n", getRoomCharge());

        if (foodOrders.isEmpty()) {
            System.out.println("No food orders.");
        } else {
            System.out.println("\nFood Charges:");
            System.out.printf("%-15s%-10s%-10s%n", "Item", "Quantity", "Price");
            for (FoodOrder order : foodOrders) {
                System.out.printf("%-15s%-10d%-10.2f%n",
                        order.getItem().name,
                        order.getQuantity(),
                        order.getPrice());
            }
        }
        System.out.printf("\nTotal Amount: Rs. %.2f%n", getTotalBill());
    }

    public void printDetails() {
        System.out.println("Room Type: " + type.description);
        System.out.println("Beds: " + type.beds);
        System.out.println("AC: " + (type.ac ? "Yes" : "No"));
        System.out.println("Daily Charge: Rs. " + type.dailyCharge);
    }

    public boolean isAvailable() {
        return customers.isEmpty();
    }

    public void clearRoom() {
        customers.clear();
        foodOrders.clear();
    }
}

// Implementações específicas se necessário (não muito diferente nesse caso)
class SingleRoom extends Room {
    public SingleRoom(RoomType type) {
        super(type);
        if (type.beds != 1) throw new IllegalArgumentException("SingleRoom must have 1 bed");
    }
}

class DoubleRoom extends Room {
    public DoubleRoom(RoomType type) {
        super(type);
        if (type.beds != 2) throw new IllegalArgumentException("DoubleRoom must have 2 beds");
    }
}

// Gerenciador do hotel
class HotelManager {
    private Map<Integer, Room> rooms = new HashMap<>(); // roomNumber -> Room
    private Scanner sc = new Scanner(System.in);

    public HotelManager() {
        // Inicializa os quartos (roomNumber começa em 1, pode ser personalizado)
        // luxury double 1-10
        for (int i = 1; i <= 10; i++) {
            rooms.put(i, new DoubleRoom(RoomType.LUXURY_DOUBLE));
        }
        // deluxe double 11-30
        for (int i = 11; i <= 30; i++) {
            rooms.put(i, new DoubleRoom(RoomType.DELUXE_DOUBLE));
        }
        // luxury single 31-40
        for (int i = 31; i <= 40; i++) {
            rooms.put(i, new SingleRoom(RoomType.LUXURY_SINGLE));
        }
        // deluxe single 41-60
        for (int i = 41; i <= 60; i++) {
            rooms.put(i, new SingleRoom(RoomType.DELUXE_SINGLE));
        }
    }

    public void displayFeatures(int roomTypeCode) {
        RoomType.fromCode(roomTypeCode).ifPresentOrElse(
                rt -> {
                    System.out.println("Room Features:");
                    System.out.println("Description: " + rt.description);
                    System.out.println("Beds: " + rt.beds);
                    System.out.println("AC: " + (rt.ac ? "Yes" : "No"));
                    System.out.println("Daily Charge: Rs. " + rt.dailyCharge);
                },
                () -> System.out.println("Invalid room type")
        );
    }

    public void listAvailableRooms(int roomTypeCode) {
        RoomType type = RoomType.fromCode(roomTypeCode).orElse(null);
        if (type == null) {
            System.out.println("Invalid room type");
            return;
        }
        System.out.println("Available rooms of type " + type.description + ":");
        rooms.entrySet().stream()
                .filter(e -> e.getValue().getType() == type && e.getValue().isAvailable())
                .map(Map.Entry::getKey)
                .sorted()
                .forEach(rn -> System.out.print(rn + " "));
        System.out.println();
    }

    public void bookRoom(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Room does not exist");
            return;
        }
        if (!room.isAvailable()) {
            System.out.println("Room not available");
            return;
        }
        int customersNeeded = room.getType().beds;
        System.out.println("Enter details for " + customersNeeded + " customer(s):");
        for (int i = 0; i < customersNeeded; i++) {
            System.out.print("Name: ");
            String name = sc.next();
            System.out.print("Contact: ");
            String contact = sc.next();
            System.out.print("Gender: ");
            String gender = sc.next();
            room.addCustomer(new Customer(name, contact, gender));
        }
        System.out.println("Room " + roomNumber + " booked successfully.");
    }

    public void orderFood(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Room does not exist");
            return;
        }
        if (room.isAvailable()) {
            System.out.println("Room not booked yet.");
            return;
        }

        char choice;
        do {
            System.out.println("\nMenu:");
            for (MenuItem item : MenuItem.values()) {
                System.out.printf("%d. %s - Rs. %.2f%n", item.code, item.name, item.price);
            }
            System.out.print("Enter item number: ");
            int itemCode = sc.nextInt();
            MenuItem.fromCode(itemCode).ifPresentOrElse(item -> {
                System.out.print("Enter quantity: ");
                int qty = sc.nextInt();
                room.addFoodOrder(new FoodOrder(item, qty));
                System.out.println(qty + " " + item.name + "(s) added.");
            }, () -> System.out.println("Invalid menu item."));

            System.out.print("Order more? (y/n): ");
            choice = sc.next().toLowerCase().charAt(0);
        } while (choice == 'y');
    }

    public void checkout(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Room does not exist");
            return;
        }
        if (room.isAvailable()) {
            System.out.println("Room not booked.");
            return;
        }
        room.printBill();
        room.clearRoom();
        System.out.println("Checkout complete. Room is now available.");
    }

    // Salvar e carregar dados - você pode adaptar pra salvar a coleção rooms toda
    public void saveData(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(rooms);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            rooms = (Map<Integer, Room>) ois.readObject();
            System.out.println("Data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found, starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        HotelManager manager = new HotelManager();
        Scanner sc = new Scanner(System.in);
        manager.loadData("hotel_data.ser");

        while (true) {
            System.out.println("\nHotel Management System");
            System.out.println("1. Display Room Features");
            System.out.println("2. Book Room");
            System.out.println("3. Order Food");
            System.out.println("4. Checkout");
            System.out.println("5. List Available Rooms");
            System.out.println("6. Save and Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter room type code (1-4): ");
                    int code = sc.nextInt();
                    RoomType.fromCode(code).ifPresentOrElse(
                            rt -> {
                                System.out.println("Room Features:");
                                System.out.println("Description: " + rt.description);
                                System.out.println("Beds: " + rt.beds);
                                System.out.println("AC: " + (rt.ac ? "Yes" : "No"));
                                System.out.println("Daily Charge: Rs. " + rt.dailyCharge);
                            },
                            () -> System.out.println("Invalid room type")
                    );
                }
                case 2 -> {
                    System.out.print("Enter room number to book: ");
                    int roomNum = sc.nextInt();
                    try {
                        manager.bookRoom(roomNum);
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.print("Enter room number to order food: ");
                    int roomNum = sc.nextInt();
                    manager.orderFood(roomNum);
                }
                case 4 -> {
                    System.out.print("Enter room number to checkout: ");
                    int roomNum = sc.nextInt();
                    manager.checkout(roomNum);
                }
                case 5 -> {
                    System.out.print("Enter room type code to list available rooms: ");
                    int code = sc.nextInt();
                    manager.listAvailableRooms(code);
                }
                case 6 -> {
                    manager.saveData("hotel_data.ser");
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}
