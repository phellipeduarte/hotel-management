import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum FoodItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    private final int code;
    private final String name;
    private final float unitPrice;

    FoodItem(int code, String name, float unitPrice) {
        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public static FoodItem fromCode(int code) {
        for (FoodItem item : values()) {
            if (item.code == code) return item;
        }
        throw new IllegalArgumentException("Invalid food item code");
    }

    public String getName() {
        return name;
    }

    public float getUnitPrice() {
        return unitPrice;
    }
}

class Food implements Serializable {
    private final FoodItem item;
    private final int quantity;

    public Food(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public float getPrice() {
        return item.getUnitPrice() * quantity;
    }

    public String getName() {
        return item.getName();
    }

    public int getQuantity() {
        return quantity;
    }
}

interface Room extends Serializable {
    void setGuests(List<Guest> guests);
    List<Guest> getGuests();
    List<Food> getOrders();
    void addFood(Food food);
    float getDailyRate();
    String getFeatures();
    String getPrimaryGuestName();
}

abstract class AbstractRoom implements Room {
    protected List<Guest> guests = new ArrayList<>();
    protected List<Food> orders = new ArrayList<>();

    @Override
    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    @Override
    public List<Guest> getGuests() {
        return guests;
    }

    @Override
    public List<Food> getOrders() {
        return orders;
    }

    @Override
    public void addFood(Food food) {
        orders.add(food);
    }

    @Override
    public String getPrimaryGuestName() {
        return guests.isEmpty() ? "N/A" : guests.get(0).getName();
    }
}

class LuxuryDoubleRoom extends AbstractRoom {
    public float getDailyRate() { return 4000; }
    public String getFeatures() {
        return "Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:4000";
    }
}

class DeluxeDoubleRoom extends AbstractRoom {
    public float getDailyRate() { return 3000; }
    public String getFeatures() {
        return "Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:3000";
    }
}

class LuxurySingleRoom extends AbstractRoom {
    public float getDailyRate() { return 2200; }
    public String getFeatures() {
        return "Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:2200";
    }
}

class DeluxeSingleRoom extends AbstractRoom {
    public float getDailyRate() { return 1200; }
    public String getFeatures() {
        return "Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:1200";
    }
}

class Guest implements Serializable {
    private final String name;
    private final String contact;
    private final String gender;

    public Guest(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public String getName() { return name; }
}

class RoomRegistry implements Serializable {
    private final Room[] luxuryDouble = new Room[10];
    private final Room[] deluxeDouble = new Room[20];
    private final Room[] luxurySingle = new Room[10];
    private final Room[] deluxeSingle = new Room[20];

    public Room[] getRooms(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> luxuryDouble;
            case DELUXE_DOUBLE -> deluxeDouble;
            case LUXURY_SINGLE -> luxurySingle;
            case DELUXE_SINGLE -> deluxeSingle;
        };
    }
}

enum RoomType {
    LUXURY_DOUBLE, DELUXE_DOUBLE, LUXURY_SINGLE, DELUXE_SINGLE
}

class BillService {
    public static void printBill(Room room) {
        float total = room.getDailyRate();
        System.out.printf("Room Charge - %.2f\n", total);
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item   Quantity    Price");
        System.out.println("-------------------------");

        for (Food food : room.getOrders()) {
            float price = food.getPrice();
            System.out.printf("%-10s%-10d%-10.2f\n", food.getName(), food.getQuantity(), price);
            total += price;
        }

        System.out.println("\nTotal Amount- " + total);
    }
}

class RoomSaver implements Runnable {
    private final RoomRegistry registry;

    public RoomSaver(RoomRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("backup"))) {
            out.writeObject(registry);
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }
}

class Hotel {
    private final RoomRegistry registry;

    public Hotel(RoomRegistry registry) {
        this.registry = registry;
    }

    public void showFeatures(RoomType type) {
        Room room = createRoomInstance(type);
        System.out.println(room.getFeatures());
    }

    public void showAvailability(RoomType type) {
        Room[] rooms = registry.getRooms(type);
        for (int i = 0; i < rooms.length; i++) {
            System.out.printf("Room %d: %s\n", getRoomNumber(type, i), rooms[i] == null ? "Available" : "Occupied");
        }
    }

    public void bookRoom(RoomType type, List<Guest> guests) {
        Room[] rooms = registry.getRooms(type);
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                Room room = createRoomInstance(type);
                room.setGuests(guests);
                rooms[i] = room;
                System.out.println("Room booked successfully. Room Number: " + getRoomNumber(type, i));
                return;
            }
        }
        System.out.println("Sorry, no available rooms of this type.");
    }

    public void orderFood(int roomNumber) {
        Room room = getRoomByNumber(roomNumber);
        if (room == null) {
            System.out.println("Room doesn't exist or is not occupied.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Menu:\n1.Sandwich - ₹50\n2.Pasta - ₹60\n3.Noodles - ₹70\n4.Coke - ₹30\nEnter your choice:");
        int choice = sc.nextInt();
        FoodItem item = FoodItem.fromCode(choice);

        System.out.print("Quantity: ");
        int qty = sc.nextInt();
        room.addFood(new Food(item, qty));

        System.out.println("Food ordered successfully.");
    }

    public void checkout(int roomNumber) {
        Room room = getRoomByNumber(roomNumber);
        if (room == null) {
            System.out.println("Room doesn't exist or is not occupied.");
            return;
        }

        BillService.printBill(room);
        clearRoom(roomNumber);
        System.out.println("Checkout successful.");
    }

    private Room createRoomInstance(RoomType type) {
        return switch (type) {
            case LUXURY_DOUBLE -> new LuxuryDoubleRoom();
            case DELUXE_DOUBLE -> new DeluxeDoubleRoom();
            case LUXURY_SINGLE -> new LuxurySingleRoom();
            case DELUXE_SINGLE -> new DeluxeSingleRoom();
        };
    }

    private int getRoomNumber(RoomType type, int index) {
        return switch (type) {
            case LUXURY_DOUBLE -> index + 1;
            case DELUXE_DOUBLE -> index + 11;
            case LUXURY_SINGLE -> index + 31;
            case DELUXE_SINGLE -> index + 41;
        };
    }

    private Room getRoomByNumber(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) {
            return registry.getRooms(RoomType.LUXURY_DOUBLE)[roomNumber - 1];
        } else if (roomNumber >= 11 && roomNumber <= 30) {
            return registry.getRooms(RoomType.DELUXE_DOUBLE)[roomNumber - 11];
        } else if (roomNumber >= 31 && roomNumber <= 40) {
            return registry.getRooms(RoomType.LUXURY_SINGLE)[roomNumber - 31];
        } else if (roomNumber >= 41 && roomNumber <= 60) {
            return registry.getRooms(RoomType.DELUXE_SINGLE)[roomNumber - 41];
        }
        return null;
    }

    private void clearRoom(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) {
            registry.getRooms(RoomType.LUXURY_DOUBLE)[roomNumber - 1] = null;
        } else if (roomNumber >= 11 && roomNumber <= 30) {
            registry.getRooms(RoomType.DELUXE_DOUBLE)[roomNumber - 11] = null;
        } else if (roomNumber >= 31 && roomNumber <= 40) {
            registry.getRooms(RoomType.LUXURY_SINGLE)[roomNumber - 31] = null;
        } else if (roomNumber >= 41 && roomNumber <= 60) {
            registry.getRooms(RoomType.DELUXE_SINGLE)[roomNumber - 41] = null;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            RoomRegistry registry;
            File f = new File("backup");
            if (f.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                    registry = (RoomRegistry) ois.readObject();
                }
            } else {
                registry = new RoomRegistry();
            }

            Hotel hotel = new Hotel(registry);
            Scanner sc = new Scanner(System.in);
            char wish;

            do {
                System.out.println("""
                    \nEnter your choice:
                    1. Display room details
                    2. Display room availability
                    3. Book
                    4. Order food
                    5. Checkout
                    6. Exit
                    """);

                int ch = sc.nextInt();
                switch (ch) {
                    case 1 -> hotel.showFeatures(chooseRoomType(sc));
                    case 2 -> hotel.showAvailability(chooseRoomType(sc));
                    case 3 -> {
                        RoomType type = chooseRoomType(sc);
                        List<Guest> guests = readGuests(sc, type);
                        hotel.bookRoom(type, guests);
                    }
                    case 4 -> {
                        System.out.print("Room Number: ");
                        int roomNumber = sc.nextInt();
                        hotel.orderFood(roomNumber);
                    }
                    case 5 -> {
                        System.out.print("Room Number: ");
                        int roomNumber = sc.nextInt();
                        hotel.checkout(roomNumber);
                    }
                    case 6 -> {
                        Thread t = new Thread(new RoomSaver(registry));
                        t.start();
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }

                System.out.print("\nContinue? (y/n): ");
                wish = sc.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static RoomType chooseRoomType(Scanner sc) {
        System.out.println("""
            Choose room type:
            1. Luxury Double Room
            2. Deluxe Double Room
            3. Luxury Single Room
            4. Deluxe Single Room
            """);
        int choice = sc.nextInt();
        return switch (choice) {
            case 1 -> RoomType.LUXURY_DOUBLE;
            case 2 -> RoomType.DELUXE_DOUBLE;
            case 3 -> RoomType.LUXURY_SINGLE;
            case 4 -> RoomType.DELUXE_SINGLE;
            default -> throw new IllegalArgumentException("Invalid room type.");
        };
    }

    private static List<Guest> readGuests(Scanner sc, RoomType type) {
        List<Guest> guests = new ArrayList<>();
        int guestCount = (type == RoomType.LUXURY_DOUBLE || type == RoomType.DELUXE_DOUBLE) ? 2 : 1;

        for (int i = 1; i <= guestCount; i++) {
            System.out.printf("Enter Guest %d Name: ", i);
            sc.nextLine(); // consume newline
            String name = sc.nextLine();
            System.out.printf("Enter Guest %d Contact: ", i);
            String contact = sc.nextLine();
            System.out.printf("Enter Guest %d Gender: ", i);
            String gender = sc.nextLine();
            guests.add(new Guest(name, contact, gender));
        }

        return guests;
    }
}
