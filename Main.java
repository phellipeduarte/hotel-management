import java.io.*;
import java.util.*;

// ==================== Main ====================
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HotelService hotelService = new HotelService();

    public static void main(String[] args) {
        loadBackup();

        char wish;
        do {
            showMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> displayRoomDetails();
                case 2 -> displayRoomAvailability();
                case 3 -> bookRoom();
                case 4 -> orderFood();
                case 5 -> checkout();
                case 6 -> {
                    saveBackup();
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

            wish = readChar("\nContinue? (y/n): ");
        } while (wish == 'y' || wish == 'Y');
    }

    private static void showMenu() {
        System.out.println("""
                \n===== Hotel Management System =====
                1. Display room details
                2. Display room availability
                3. Book room
                4. Order food
                5. Checkout
                6. Exit
                """);
    }

    private static void displayRoomDetails() {
        int type = readRoomType();
        hotelService.showFeatures(type);
    }

    private static void displayRoomAvailability() {
        int type = readRoomType();
        hotelService.showAvailability(type);
    }

    private static void bookRoom() {
        int type = readRoomType();
        try {
            hotelService.bookRoom(type);
        } catch (NotAvailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void orderFood() {
        int roomNumber = readInt("Enter Room Number: ");
        if (!hotelService.isValidRoomNumber(roomNumber)) {
            System.out.println("Room doesn't exist.");
            return;
        }
        hotelService.orderFood(roomNumber);
    }

    private static void checkout() {
        int roomNumber = readInt("Enter Room Number: ");
        if (!hotelService.isValidRoomNumber(roomNumber)) {
            System.out.println("Room doesn't exist.");
            return;
        }
        hotelService.checkout(roomNumber);
    }

    private static int readRoomType() {
        System.out.println("""
                \nChoose room type:
                1. Luxury Double Room
                2. Deluxe Double Room
                3. Luxury Single Room
                4. Deluxe Single Room
                """);
        return readInt("Your choice: ");
    }

    private static int readInt(String message) {
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + message);
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static char readChar(String message) {
        System.out.print(message);
        return scanner.next().charAt(0);
    }

    private static void loadBackup() {
        File file = new File("backup");
        if (!file.exists()) return;

        try (FileInputStream fin = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            hotelService.setHolder((Holder) ois.readObject());
            System.out.println("Backup loaded successfully.");
        } catch (Exception e) {
            System.out.println("Failed to load backup: " + e.getMessage());
        }
    }

    private static void saveBackup() {
        Thread backupThread = new Thread(new WriteBackup(hotelService.getHolder()));
        backupThread.start();
    }
}

// ==================== HotelService ====================
class HotelService {
    private Holder holder = new Holder();

    public void showFeatures(int type) {
        Room[] rooms = getRoomArrayByType(type);
        for (int i = 0; i < rooms.length; i++) {
            System.out.print("\nRoom " + (getRoomNumber(type, i)) + ":\n");
            if (rooms[i] == null) {
                System.out.println("Not Booked");
            } else {
                rooms[i].displayFeatures();
            }
        }
    }

    public void showAvailability(int type) {
        Room[] rooms = getRoomArrayByType(type);
        int available = 0;
        for (Room room : rooms) {
            if (room == null) available++;
        }
        System.out.println(available + " rooms available");
    }

    public void bookRoom(int type) throws NotAvailableException {
        Room[] rooms = getRoomArrayByType(type);
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                rooms[i] = new Room(type);
                System.out.println("Room booked successfully. Room number: " + getRoomNumber(type, i));
                return;
            }
        }
        throw new NotAvailableException("No rooms available.");
    }

    public void orderFood(int roomNumber) {
        Room room = getRoomByNumber(roomNumber);
        if (room != null) {
            room.orderFood();
        }
    }

    public void checkout(int roomNumber) {
        int type = getRoomTypeByNumber(roomNumber);
        int index = getRoomIndexByNumber(roomNumber);
        Room[] rooms = getRoomArrayByType(type);

        if (rooms[index] != null) {
            rooms[index] = null;
            System.out.println("Checked out successfully.");
        } else {
            System.out.println("Room is already vacant.");
        }
    }

    public boolean isValidRoomNumber(int roomNumber) {
        return roomNumber > 0 && roomNumber <= 60;
    }

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    // ==================== Helpers ====================
    private Room[] getRoomArrayByType(int type) {
        return switch (type) {
            case 1 -> holder.luxuryDoubleRooms;
            case 2 -> holder.deluxeDoubleRooms;
            case 3 -> holder.luxurySingleRooms;
            case 4 -> holder.deluxeSingleRooms;
            default -> throw new IllegalArgumentException("Invalid room type.");
        };
    }

    private int getRoomNumber(int type, int index) {
        return switch (type) {
            case 1 -> index + 1;
            case 2 -> index + 11;
            case 3 -> index + 31;
            case 4 -> index + 41;
            default -> -1;
        };
    }

    private int getRoomTypeByNumber(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) return 1;
        if (roomNumber >= 11 && roomNumber <= 30) return 2;
        if (roomNumber >= 31 && roomNumber <= 40) return 3;
        if (roomNumber >= 41 && roomNumber <= 60) return 4;
        throw new IllegalArgumentException("Invalid room number.");
    }

    private int getRoomIndexByNumber(int roomNumber) {
        return switch (getRoomTypeByNumber(roomNumber)) {
            case 1 -> roomNumber - 1;
            case 2 -> roomNumber - 11;
            case 3 -> roomNumber - 31;
            case 4 -> roomNumber - 41;
            default -> -1;
        };
    }

    private Room getRoomByNumber(int roomNumber) {
        int type = getRoomTypeByNumber(roomNumber);
        int index = getRoomIndexByNumber(roomNumber);
        Room[] rooms = getRoomArrayByType(type);
        return rooms[index];
    }
}

// ==================== Holder ====================
class Holder implements Serializable {
    public Room[] luxuryDoubleRooms = new Room[10];
    public Room[] deluxeDoubleRooms = new Room[20];
    public Room[] luxurySingleRooms = new Room[10];
    public Room[] deluxeSingleRooms = new Room[20];
}

// ==================== Room ====================
class Room implements Serializable {
    private String customerName;
    private String phoneNumber;
    private final Food[] foodOrders = new Food[10];
    private int foodCount = 0;
    private final int type;

    public Room(int type) {
        this.type = type;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter customer name: ");
        this.customerName = sc.nextLine();
        System.out.print("Enter phone number: ");
        this.phoneNumber = sc.nextLine();
        System.out.println("Room booked successfully.");
    }

    public void displayFeatures() {
        System.out.println("Customer Name: " + customerName);
        System.out.println("Phone Number: " + phoneNumber);
        switch (type) {
            case 1 -> System.out.println("Luxury Double Room -- Double Bed, AC, 4 Breakfasts, Free WiFi, 5000/night");
            case 2 -> System.out.println("Deluxe Double Room -- Double Bed, AC, 2 Breakfasts, Free WiFi, 3500/night");
            case 3 -> System.out.println("Luxury Single Room -- Single Bed, AC, 2 Breakfasts, Free WiFi, 2200/night");
            case 4 -> System.out.println("Deluxe Single Room -- Single Bed, AC, 1 Breakfast, Free WiFi, 1200/night");
        }
    }

    public void orderFood() {
        if (foodCount >= foodOrders.length) {
            System.out.println("Maximum food orders reached.");
            return;
        }

        System.out.println("""
                \n===== Menu =====
                1. Sandwich - Rs.50
                2. Pasta - Rs.60
                3. Noodles - Rs.70
                4. Coke - Rs.30
                """);
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        Food food = switch (choice) {
            case 1 -> new Food("Sandwich", 50);
            case 2 -> new Food("Pasta", 60);
            case 3 -> new Food("Noodles", 70);
            case 4 -> new Food("Coke", 30);
            default -> {
                System.out.println("Invalid choice.");
                yield null;
            }
        };

        if (food != null) {
            foodOrders[foodCount++] = food;
            System.out.println("Food ordered successfully.");
        }
    }
}

// ==================== Food ====================
class Food implements Serializable {
    String item;
    int price;

    public Food(String item, int price) {
        this.item = item;
        this.price = price;
    }
}

// ==================== NotAvailableException ====================
class NotAvailableException extends Exception {
    public NotAvailableException(String message) {
        super(message);
    }
}

// ==================== WriteBackup ====================
class WriteBackup implements Runnable {
    private final Holder holder;

    public WriteBackup(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(holder);
            System.out.println("Backup saved.");
        } catch (Exception e) {
            System.out.println("Failed to save backup: " + e.getMessage());
        }
    }
}
