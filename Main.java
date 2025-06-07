import java.io.*;
import java.util.*;

interface Room extends Serializable {
    void addFood(Food food);
    List<Food> getFoods();
    String getPrimaryGuestName();
}

class Constants {
    static final String[] FOOD_ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};
    static final int[] FOOD_PRICES = {50, 60, 70, 30};

    static final int ROOM_CHARGE_LUXURY_DOUBLE = 4000;
    static final int ROOM_CHARGE_DELUXE_DOUBLE = 3000;
    static final int ROOM_CHARGE_LUXURY_SINGLE = 2200;
    static final int ROOM_CHARGE_DELUXE_SINGLE = 1200;
}

class Food implements Serializable {
    int itemNo;
    int quantity;
    float price;

    Food(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        this.price = quantity * Constants.FOOD_PRICES[itemNo - 1];
    }
}

abstract class BaseRoom implements Room {
    String name;
    String contact;
    String gender;
    List<Food> foodList = new ArrayList<>();

    BaseRoom() {
        this.name = "";
    }

    BaseRoom(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public void addFood(Food food) {
        foodList.add(food);
    }

    public List<Food> getFoods() {
        return foodList;
    }

    public String getPrimaryGuestName() {
        return name;
    }
}

class SingleRoom extends BaseRoom {
    SingleRoom() {
        super();
    }

    SingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }
}

class DoubleRoom extends BaseRoom {
    String name2;
    String contact2;
    String gender2;

    DoubleRoom() {
        super();
        this.name2 = "";
    }

    DoubleRoom(String name, String contact, String gender, String name2, String contact2, String gender2) {
        super(name, contact, gender);
        this.name2 = name2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }
}

class NotAvailable extends Exception {
    @Override
    public String toString() {
        return "Not Available!";
    }
}

class RoomHolder implements Serializable {
    Room[] luxuryDoubleRooms = new Room[10];
    Room[] deluxeDoubleRooms = new Room[20];
    Room[] luxurySingleRooms = new Room[10];
    Room[] deluxeSingleRooms = new Room[20];
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Carrega dados do backup
        RoomHolder holder = DataReader.read();

        // Substitui o holder carregado no serviço
        try {
            java.lang.reflect.Field field = HotelService.class.getDeclaredField("holder");
            field.setAccessible(true);
            field.set(null, holder);
        } catch (Exception e) {
            System.out.println("Erro ao restaurar dados do backup.");
        }

        int choice;
        do {
            System.out.println("""
                -------------------------------------
                            Hotel Menu
                -------------------------------------
                1. Book Room
                2. View Room Features
                3. Check Room Availability
                4. Checkout
                5. Order Food
                0. Exit
                -------------------------------------
                Enter your choice: 
                """);
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> showRoomOptions("Book Room", HotelService::bookRoom);
                case 2 -> showRoomOptions("Room Features", HotelService::displayFeatures);
                case 3 -> showRoomOptions("Check Availability", HotelService::showAvailability);
                case 4 -> {
                    showRoomOptions("Checkout", code -> {
                        System.out.print("Enter room number: ");
                        int roomNumber = sc.nextInt();
                        HotelService.checkout(roomNumber, code);
                    });
                }
                case 5 -> {
                    showRoomOptions("Order Food", code -> {
                        System.out.print("Enter room number: ");
                        int roomNumber = sc.nextInt();
                        HotelService.placeOrder(roomNumber, code);
                    });
                }
                case 0 -> {
                    System.out.println("Saving data and exiting...");
                    new Thread(new DataWriter(HotelService.getRoomHolder())).start();
                }
                default -> System.out.println("Invalid option!");
            }

            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
            sc.nextLine();

        } while (choice != 0);
    }

    interface RoomAction {
        void execute(int roomCode);
    }

    private static void showRoomOptions(String title, RoomAction action) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nChoose Room Type for " + title + ":");
        System.out.println("1. Luxury Double Room");
        System.out.println("2. Deluxe Double Room");
        System.out.println("3. Luxury Single Room");
        System.out.println("4. Deluxe Single Room");
        System.out.print("Enter choice: ");
        int code = sc.nextInt();
        action.execute(code);
    }
}

class HotelService {
    private static final Scanner sc = new Scanner(System.in);
    private static final RoomHolder holder = new RoomHolder();

    private enum RoomType {
        LUXURY_DOUBLE(1, Constants.ROOM_CHARGE_LUXURY_DOUBLE, 1, 10, RoomCategory.DOUBLE, true),
        DELUXE_DOUBLE(2, Constants.ROOM_CHARGE_DELUXE_DOUBLE, 11, 20, RoomCategory.DOUBLE, false),
        LUXURY_SINGLE(3, Constants.ROOM_CHARGE_LUXURY_SINGLE, 31, 10, RoomCategory.SINGLE, true),
        DELUXE_SINGLE(4, Constants.ROOM_CHARGE_DELUXE_SINGLE, 41, 20, RoomCategory.SINGLE, false);

        final int code;
        final int charge;
        final int roomNumberStart;
        final int roomCount;
        final RoomCategory category;
        final boolean isLuxury;

        RoomType(int code, int charge, int roomNumberStart, int roomCount, RoomCategory category, boolean isLuxury) {
            this.code = code;
            this.charge = charge;
            this.roomNumberStart = roomNumberStart;
            this.roomCount = roomCount;
            this.category = category;
            this.isLuxury = isLuxury;
        }

        static RoomType fromCode(int code) {
            for (RoomType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid Room Type");
        }
    }

    private enum RoomCategory {
        SINGLE, DOUBLE
    }

    public static void bookRoom(int code) {
        try {
            RoomType roomType = RoomType.fromCode(code);
            Room[] rooms = getRoomsArray(roomType);
            System.out.print("Available rooms: ");
            for (int i = 0; i < rooms.length; i++) {
                if (rooms[i] == null) System.out.print((i + roomType.roomNumberStart) + " ");
            }

            System.out.print("\nEnter room number: ");
            int input = sc.nextInt() - roomType.roomNumberStart;

            if (input < 0 || input >= rooms.length || rooms[input] != null) throw new NotAvailable();

            rooms[input] = createRoom(roomType);
            System.out.println("Room Booked");
        } catch (Exception e) {
            System.out.println("Invalid Option");
        }
    }

    private static Room createRoom(RoomType roomType) {
        System.out.print("\nEnter customer name: ");
        String name = sc.next();
        System.out.print("Enter contact number: ");
        String contact = sc.next();
        System.out.print("Enter gender: ");
        String gender = sc.next();

        if (roomType.category == RoomCategory.DOUBLE) {
            System.out.print("Enter second customer name: ");
            String name2 = sc.next();
            System.out.print("Enter contact number: ");
            String contact2 = sc.next();
            System.out.print("Enter gender: ");
            String gender2 = sc.next();
            return new DoubleRoom(name, contact, gender, name2, contact2, gender2);
        }
        return new SingleRoom(name, contact, gender);
    }

    public static void displayFeatures(int code) {
        try {
            RoomType roomType = RoomType.fromCode(code);
            System.out.printf("""
                Beds       : 1 %s
                AC         : %s
                Breakfast  : Free
                Daily Rate : %d
                """,
                    roomType.category == RoomCategory.DOUBLE ? "double bed" : "single bed",
                    roomType.isLuxury ? "Yes" : "No",
                    roomType.charge
            );
        } catch (Exception e) {
            System.out.println("Invalid Option");
        }
    }

    public static void showAvailability(int code) {
        try {
            RoomType roomType = RoomType.fromCode(code);
            Room[] rooms = getRoomsArray(roomType);
            long count = Arrays.stream(rooms).filter(Objects::isNull).count();
            System.out.println("Available rooms: " + count);
        } catch (Exception e) {
            System.out.println("Invalid Option");
        }
    }

    public static void checkout(int roomNumber, int code) {
        try {
            RoomType roomType = RoomType.fromCode(code);
            int index = roomNumber - roomType.roomNumberStart;
            Room[] rooms = getRoomsArray(roomType);

            if (rooms[index] == null) {
                System.out.println("Already Empty");
                return;
            }

            System.out.println("Room used by: " + rooms[index].getPrimaryGuestName());
            System.out.print("Do you want to checkout? (y/n): ");
            char confirm = sc.next().charAt(0);

            if (confirm == 'y' || confirm == 'Y') {
                printBill(rooms[index], roomType);
                rooms[index] = null;
                System.out.println("Deallocated successfully");
            }
        } catch (Exception e) {
            System.out.println("Invalid Option");
        }
    }

    public static void placeOrder(int roomNumber, int code) {
        try {
            RoomType roomType = RoomType.fromCode(code);
            int index = roomNumber - roomType.roomNumberStart;
            Room[] rooms = getRoomsArray(roomType);

            if (rooms[index] == null) throw new NullPointerException();

            System.out.println("""
            ==========
               Menu   
            ==========
            1. Sandwich  Rs.50
            2. Pasta     Rs.60
            3. Noodles   Rs.70
            4. Coke      Rs.30
            """);

            char more;
            do {
                int item = sc.nextInt();
                System.out.print("Quantity: ");
                int qty = sc.nextInt();
                rooms[index].addFood(new Food(item, qty));
                System.out.print("Order more? (y/n): ");
                more = sc.next().charAt(0);
            } while (more == 'y' || more == 'Y');

        } catch (NullPointerException e) {
            System.out.println("Room not booked");
        } catch (Exception e) {
            System.out.println("Cannot process order");
        }
    }

    private static void printBill(Room room, RoomType roomType) {
        double total = roomType.charge;
        System.out.println("""
        *******
         Bill
        *******
        Room Charge - """ + roomType.charge);

        System.out.println("===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.printf("%-10s%-10s%-10s%n", "Item", "Quantity", "Price");
        System.out.println("-------------------------");

        for (Food f : room.getFoods()) {
            total += f.price;
            System.out.printf("%-10s%-10d%-10.2f%n", Constants.FOOD_ITEMS[f.itemNo - 1], f.quantity, f.price);
        }

        System.out.println("\nTotal Amount: " + total);
    }

    private static Room[] getRoomsArray(RoomType roomType) {
        return switch (roomType) {
            case LUXURY_DOUBLE -> holder.luxuryDoubleRooms;
            case DELUXE_DOUBLE -> holder.deluxeDoubleRooms;
            case LUXURY_SINGLE -> holder.luxurySingleRooms;
            case DELUXE_SINGLE -> holder.deluxeSingleRooms;
        };
    }

    public static RoomHolder getRoomHolder() {
        return holder;
    }
}

class DataWriter implements Runnable {
    private final RoomHolder holder;

    DataWriter(RoomHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("backup"))) {
            oos.writeObject(holder);
        } catch (IOException e) {
            System.out.println("Failed to backup data");
        }
    }
}

class DataReader {
    public static RoomHolder read() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("backup"))) {
            return (RoomHolder) ois.readObject();
        } catch (Exception e) {
            return new RoomHolder();
        }
    }
}
