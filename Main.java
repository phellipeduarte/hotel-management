import java.io.*;
import java.util.Scanner;

import java.util.*;

enum FoodItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    private final int id;
    private final String name;
    private final float price;

    FoodItem(int id, String name, float price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public float getPrice() { return price; }
    public String getName() { return name; }

    public static FoodItem fromId(int id) {
        return Arrays.stream(values())
                .filter(item -> item.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Food ID"));
    }
}

class Food implements Serializable {
    private final FoodItem item;
    private final int quantity;

    public Food(int itemId, int quantity) {
        this.item = FoodItem.fromId(itemId);
        this.quantity = quantity;
    }

    public float getPrice() {
        return quantity * item.getPrice();
    }

    public String getItemName() {
        return item.getName();
    }

    public int getQuantity() {
        return quantity;
    }
}

abstract class Room implements Serializable {
    protected final List<Customer> customers = new ArrayList<>();
    protected final List<Food> foodOrders = new ArrayList<>();

    public abstract float getDailyRate();

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void orderFood(Food food) {
        foodOrders.add(food);
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public float getTotalFoodCharges() {
        return (float) foodOrders.stream().mapToDouble(Food::getPrice).sum();
    }

    public float getTotalCharge() {
        return getDailyRate() + getTotalFoodCharges();
    }
}

class SingleRoom extends Room {
    private static final float RATE = 2200;

    @Override
    public float getDailyRate() {
        return RATE;
    }
}

class DoubleRoom extends Room {
    private static final float RATE = 4000;

    @Override
    public float getDailyRate() {
        return RATE;
    }
}

class DeluxeSingleRoom extends SingleRoom {
    private static final float RATE = 1200;

    @Override
    public float getDailyRate() {
        return RATE;
    }
}

class DeluxeDoubleRoom extends DoubleRoom {
    private static final float RATE = 3000;

    @Override
    public float getDailyRate() {
        return RATE;
    }
}

class Customer implements Serializable {
    private final String name;
    private final String contact;
    private final String gender;

    public Customer(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }
}

class NotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Not Available!";
    }
}

enum RoomType {
    LUXURY_DOUBLE, DELUXE_DOUBLE, LUXURY_SINGLE, DELUXE_SINGLE
}

class HotelManager implements Serializable {
    private final Map<RoomType, Room[]> rooms = new EnumMap<>(RoomType.class);
    private static final Scanner sc = new Scanner(System.in);

    public HotelManager() {
        rooms.put(RoomType.LUXURY_DOUBLE, new DoubleRoom[10]);
        rooms.put(RoomType.DELUXE_DOUBLE, new DeluxeDoubleRoom[20]);
        rooms.put(RoomType.LUXURY_SINGLE, new SingleRoom[10]);
        rooms.put(RoomType.DELUXE_SINGLE, new DeluxeSingleRoom[20]);
    }

    public void bookRoom(RoomType type) {
        Room[] roomArray = rooms.get(type);
        int offset = getRoomOffset(type);
        showAvailableRooms(roomArray, offset);
        try {
            int roomNumber = sc.nextInt() - offset;
            if (roomArray[roomNumber] != null) throw new NotAvailableException();

            Room room = createRoomInstance(type);
            int occupants = (type.name().contains("DOUBLE")) ? 2 : 1;
            for (int i = 0; i < occupants; i++) {
                System.out.printf("Enter details for guest %d:\n", i + 1);
                room.addCustomer(collectCustomerInfo());
            }
            roomArray[roomNumber] = room;
            System.out.println("Room booked successfully.");
        } catch (Exception e) {
            System.out.println("Invalid option or booking failed.");
        }
    }

    public Room createRoomInstance(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> new DoubleRoom();
            case DELUXE_DOUBLE -> new DeluxeDoubleRoom();
            case LUXURY_SINGLE -> new SingleRoom();
            case DELUXE_SINGLE -> new DeluxeSingleRoom();
        };
    }

    public Room[] getRoomArray(RoomType type) {
        return rooms.getOrDefault(type, new Room[0]);
    }

    private Customer collectCustomerInfo() {
        System.out.print("Name: ");
        String name = sc.next();
        System.out.print("Contact: ");
        String contact = sc.next();
        System.out.print("Gender: ");
        String gender = sc.next();
        return new Customer(name, contact, gender);
    }

    private void showAvailableRooms(Room[] rooms, int offset) {
        System.out.print("Available rooms: ");
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) System.out.print((i + offset) + " ");
        }
        System.out.println("\nChoose a room number:");
    }

    private int getRoomOffset(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> 1;
            case DELUXE_DOUBLE -> 11;
            case LUXURY_SINGLE -> 31;
            case DELUXE_SINGLE -> 41;
        };
    }

    // Demais métodos: orderFood(), deallocateRoom(), showBill(), showFeatures(), showAvailability()
    // Podem ser implementados em estilo semelhante e reutilizável.
}

public class Main {

    private static final String BACKUP_FILE = "backup";

    public static void main(String[] args) {
        HotelManager hotelManager = loadHotelManager();
        Scanner sc = new Scanner(System.in);
        char wish;

        do {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1. Mostrar detalhes do quarto");
            System.out.println("2. Verificar disponibilidade");
            System.out.println("3. Reservar quarto");
            System.out.println("4. Pedir comida");
            System.out.println("5. Checkout");
            System.out.println("6. Sair");

            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> showRoomDetails(hotelManager);
                case 2 -> showAvailability(hotelManager);
                case 3 -> bookRoom(hotelManager);
                case 4 -> orderFood(hotelManager);
                case 5 -> checkout(hotelManager);
                case 6 -> {
                    saveHotelManager(hotelManager);
                    System.out.println("Saindo... dados salvos.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }

            System.out.print("\nDeseja continuar? (y/n): ");
            wish = sc.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');

        saveHotelManager(hotelManager);
        System.out.println("Encerrado. Backup salvo.");
    }

    private static void showRoomDetails(HotelManager hotelManager) {
        RoomType type = selectRoomType();
        System.out.println("Características de quartos do tipo " + type.name());
        System.out.println("Tarifa diária: R$" + hotelManager.createRoomInstance(type).getDailyRate());
    }

    private static void showAvailability(HotelManager hotelManager) {
        RoomType type = selectRoomType();
        int offset = getOffset(type);
        Room[] rooms = hotelManager.getRoomArray(type);
        System.out.println("Quartos disponíveis: ");
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print((i + offset) + " ");
            }
        }
        System.out.println();
    }

    private static void bookRoom(HotelManager hotelManager) {
        RoomType type = selectRoomType();
        hotelManager.bookRoom(type);
    }

    private static void orderFood(HotelManager hotelManager) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Número do quarto: ");
        int roomNumber = sc.nextInt();
        RoomType type = getRoomTypeByNumber(roomNumber);
        if (type == null) {
            System.out.println("Quarto inválido.");
            return;
        }
        int index = roomNumber - getOffset(type);
        Room[] rooms = hotelManager.getRoomArray(type);
        if (index >= 0 && index < rooms.length && rooms[index] != null) {
            while (true) {
                System.out.println("Menu:");
                for (FoodItem item : FoodItem.values()) {
                    System.out.printf("%d. %s - R$%.2f\n", item.ordinal() + 1, item.getName(), item.getPrice());
                }
                System.out.print("Escolha o item (0 para sair): ");
                int itemId = sc.nextInt();
                if (itemId == 0) break;

                try {
                    FoodItem foodItem = FoodItem.fromId(itemId);
                    System.out.print("Quantidade: ");
                    int quantity = sc.nextInt();
                    rooms[index].orderFood(new Food(itemId, quantity));
                    System.out.println("Pedido adicionado.");
                } catch (Exception e) {
                    System.out.println("Item inválido.");
                }
            }
        } else {
            System.out.println("Quarto não disponível.");
        }
    }

    private static void checkout(HotelManager hotelManager) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Número do quarto: ");
        int roomNumber = sc.nextInt();
        RoomType type = getRoomTypeByNumber(roomNumber);
        if (type == null) {
            System.out.println("Quarto inválido.");
            return;
        }

        int index = roomNumber - getOffset(type);
        Room[] rooms = hotelManager.getRoomArray(type);
        if (index >= 0 && index < rooms.length && rooms[index] != null) {
            Room room = rooms[index];
            System.out.println("Fatura do quarto:");
            System.out.printf("Tarifa do quarto: R$%.2f\n", room.getDailyRate());
            System.out.printf("Comidas: R$%.2f\n", room.getTotalFoodCharges());
            System.out.printf("Total: R$%.2f\n", room.getTotalCharge());
            rooms[index] = null;
            System.out.println("Checkout realizado.");
        } else {
            System.out.println("Quarto não disponível.");
        }
    }

    private static RoomType selectRoomType() {
        Scanner sc = new Scanner(System.in);
        System.out.println("""
            Escolha o tipo de quarto:
            1. Luxury Double Room
            2. Deluxe Double Room
            3. Luxury Single Room
            4. Deluxe Single Room
            """);
        int option = sc.nextInt();
        return switch (option) {
            case 1 -> RoomType.LUXURY_DOUBLE;
            case 2 -> RoomType.DELUXE_DOUBLE;
            case 3 -> RoomType.LUXURY_SINGLE;
            case 4 -> RoomType.DELUXE_SINGLE;
            default -> throw new IllegalArgumentException("Tipo inválido.");
        };
    }

    private static int getOffset(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> 1;
            case DELUXE_DOUBLE -> 11;
            case LUXURY_SINGLE -> 31;
            case DELUXE_SINGLE -> 41;
        };
    }

    private static RoomType getRoomTypeByNumber(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) return RoomType.LUXURY_DOUBLE;
        if (roomNumber >= 11 && roomNumber <= 30) return RoomType.DELUXE_DOUBLE;
        if (roomNumber >= 31 && roomNumber <= 40) return RoomType.LUXURY_SINGLE;
        if (roomNumber >= 41 && roomNumber <= 60) return RoomType.DELUXE_SINGLE;
        return null;
    }

    private static void saveHotelManager(HotelManager manager) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BACKUP_FILE))) {
            oos.writeObject(manager);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private static HotelManager loadHotelManager() {
        File f = new File(BACKUP_FILE);
        if (!f.exists()) return new HotelManager();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (HotelManager) ois.readObject();
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados anteriores, iniciando novo sistema.");
            return new HotelManager();
        }
    }
}
