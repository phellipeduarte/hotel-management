import java.io.*;
import java.util.*;

// Interface para persistência
interface DataPersistence {
    void save(Object data) throws IOException;
    Object load() throws IOException, ClassNotFoundException;
}

// Implementação da persistência em arquivo
class FilePersistence implements DataPersistence {
    private final File file;

    public FilePersistence(String filename) {
        this.file = new File(filename);
    }

    @Override
    public void save(Object data) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(data);
        }
    }

    @Override
    public Object load() throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fin = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            return ois.readObject();
        }
    }
}

// Classe para os dados do hotel
class HotelData implements Serializable {
    Room[][] rooms;

    public HotelData() {
        rooms = new Room[4][];
        rooms[0] = new Room[10]; // Luxury Double Room
        rooms[1] = new Room[20]; // Deluxe Double Room
        rooms[2] = new Room[10]; // Luxury Single Room
        rooms[3] = new Room[20]; // Deluxe Single Room
    }
}

// Classe base de quarto
abstract class Room implements Serializable {
    String roomType;
    boolean isBooked;
    List<String> foodOrders;

    public Room(String roomType) {
        this.roomType = roomType;
        this.isBooked = false;
        this.foodOrders = new ArrayList<>();
    }

    public void book() {
        if (!isBooked) {
            isBooked = true;
            System.out.println(roomType + " booked successfully.");
        } else {
            System.out.println(roomType + " is already booked.");
        }
    }

    public void orderFood(String item) {
        if (isBooked) {
            foodOrders.add(item);
            System.out.println(item + " ordered successfully.");
        } else {
            System.out.println("Room is not booked. Please book the room first.");
        }
    }

    public void checkout() {
        if (isBooked) {
            isBooked = false;
            foodOrders.clear();
            System.out.println(roomType + " checkout completed.");
        } else {
            System.out.println("Room is not booked.");
        }
    }

    public void showFeatures() {
        System.out.println("Room Type: " + roomType);
    }

    public boolean isAvailable() {
        return !isBooked;
    }
}

// Tipos específicos de quartos
class LuxuryDoubleRoom extends Room {
    public LuxuryDoubleRoom() {
        super("Luxury Double Room");
    }
}

class DeluxeDoubleRoom extends Room {
    public DeluxeDoubleRoom() {
        super("Deluxe Double Room");
    }
}

class LuxurySingleRoom extends Room {
    public LuxurySingleRoom() {
        super("Luxury Single Room");
    }
}

class DeluxeSingleRoom extends Room {
    public DeluxeSingleRoom() {
        super("Deluxe Single Room");
    }
}

// Classe que gerencia o hotel
class Hotel {
    private HotelData hotelData;

    public Hotel(HotelData data) {
        this.hotelData = data;
        initializeRooms();
    }

    private void initializeRooms() {
        if (hotelData.rooms[0][0] == null) {
            for (int i = 0; i < 10; i++) hotelData.rooms[0][i] = new LuxuryDoubleRoom();
            for (int i = 0; i < 20; i++) hotelData.rooms[1][i] = new DeluxeDoubleRoom();
            for (int i = 0; i < 10; i++) hotelData.rooms[2][i] = new LuxurySingleRoom();
            for (int i = 0; i < 20; i++) hotelData.rooms[3][i] = new DeluxeSingleRoom();
        }
    }

    public void showFeatures(int roomType) {
        for (Room room : hotelData.rooms[roomType - 1]) {
            room.showFeatures();
        }
    }

    public void showAvailability(int roomType) {
        long available = Arrays.stream(hotelData.rooms[roomType - 1])
                .filter(Room::isAvailable)
                .count();
        System.out.println("Available " + getRoomTypeName(roomType) + ": " + available);
    }

    public void bookRoom(int roomType) {
        for (Room room : hotelData.rooms[roomType - 1]) {
            if (room.isAvailable()) {
                room.book();
                return;
            }
        }
        System.out.println("No available rooms of this type.");
    }

    public void orderFood(int roomNumber) {
        Room room = getRoomByNumber(roomNumber);
        if (room != null) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter food item:");
            String item = sc.nextLine();
            room.orderFood(item);
        }
    }

    public void checkout(int roomNumber) {
        Room room = getRoomByNumber(roomNumber);
        if (room != null) {
            room.checkout();
        }
    }

    private Room getRoomByNumber(int roomNumber) {
        if (roomNumber <= 0 || roomNumber > 60) {
            System.out.println("Room doesn't exist.");
            return null;
        }
        int type, index;
        if (roomNumber <= 10) {
            type = 0;
            index = roomNumber - 1;
        } else if (roomNumber <= 30) {
            type = 1;
            index = roomNumber - 11;
        } else if (roomNumber <= 40) {
            type = 2;
            index = roomNumber - 31;
        } else {
            type = 3;
            index = roomNumber - 41;
        }
        return hotelData.rooms[type][index];
    }

    private String getRoomTypeName(int roomType) {
        return switch (roomType) {
            case 1 -> "Luxury Double Room";
            case 2 -> "Deluxe Double Room";
            case 3 -> "Luxury Single Room";
            case 4 -> "Deluxe Single Room";
            default -> "Unknown";
        };
    }
}

// Classe principal (Main)
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DataPersistence persistence = new FilePersistence("backup");
        HotelData data;
        try {
            data = (HotelData) persistence.load();
            if (data == null) data = new HotelData();
        } catch (Exception e) {
            System.out.println("Failed to load data. Starting fresh.");
            data = new HotelData();
        }

        Hotel hotel = new Hotel(data);

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
                case 1 -> {
                    System.out.println("""
                            \nChoose room type:
                            1. Luxury Double Room
                            2. Deluxe Double Room
                            3. Luxury Single Room
                            4. Deluxe Single Room
                            """);
                    hotel.showFeatures(sc.nextInt());
                }
                case 2 -> {
                    System.out.println("""
                            \nChoose room type:
                            1. Luxury Double Room
                            2. Deluxe Double Room
                            3. Luxury Single Room
                            4. Deluxe Single Room
                            """);
                    hotel.showAvailability(sc.nextInt());
                }
                case 3 -> {
                    System.out.println("""
                            \nChoose room type:
                            1. Luxury Double Room
                            2. Deluxe Double Room
                            3. Luxury Single Room
                            4. Deluxe Single Room
                            """);
                    hotel.bookRoom(sc.nextInt());
                }
                case 4 -> {
                    System.out.print("Room Number: ");
                    hotel.orderFood(sc.nextInt());
                }
                case 5 -> {
                    System.out.print("Room Number: ");
                    hotel.checkout(sc.nextInt());
                }
                case 6 -> {
                    System.out.println("Exiting...");
                    break;
                }
                default -> System.out.println("Invalid option.");
            }

            System.out.println("\nContinue? (y/n)");
            wish = sc.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');

        try {
            persistence.save(data);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save data.");
        }
    }
}
