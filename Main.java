import java.io.*;
import java.util.*;

enum FoodItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    public final int code;
    public final String name;
    public final float price;

    FoodItem(int code, String name, float price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public static FoodItem fromCode(int code) {
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Food Code"));
    }
}

class Food implements Serializable {
    private final FoodItem item;
    private final int quantity;
    private final float price;

    public Food(int itemCode, int quantity) {
        this.item = FoodItem.fromCode(itemCode);
        this.quantity = quantity;
        this.price = this.item.price * quantity;
    }

    public String getDescription() {
        return String.format("%-10s%-10d%-10.2f", item.name, quantity, price);
    }

    public float getPrice() {
        return price;
    }
}

abstract class Room implements Serializable {
    protected String customer1Name;
    protected String customer1Contact;
    protected String customer1Gender;
    protected List<Food> foodOrders = new ArrayList<>();

    public Room(String name, String contact, String gender) {
        this.customer1Name = name;
        this.customer1Contact = contact;
        this.customer1Gender = gender;
    }

    public abstract double getRoomCharge();

    public String getCustomerName() {
        return customer1Name;
    }

    public void addFood(Food food) {
        foodOrders.add(food);
    }

    public List<Food> getFoodOrders() {
        return foodOrders;
    }
}

class SingleRoom extends Room {
    public SingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getRoomCharge() {
        return 2200;
    }
}

class DoubleRoom extends Room {
    private String customer2Name;
    private String customer2Contact;
    private String customer2Gender;

    public DoubleRoom(String name1, String contact1, String gender1,
                      String name2, String contact2, String gender2) {
        super(name1, contact1, gender1);
        this.customer2Name = name2;
        this.customer2Contact = contact2;
        this.customer2Gender = gender2;
    }

    @Override
    public double getRoomCharge() {
        return 4000;
    }
}

class NotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Room Not Available!";
    }
}

class HotelData implements Serializable {
    public DoubleRoom[] luxuryDouble = new DoubleRoom[10];
    public DoubleRoom[] deluxeDouble = new DoubleRoom[20];
    public SingleRoom[] luxurySingle = new SingleRoom[10];
    public SingleRoom[] deluxeSingle = new SingleRoom[20];
}

class Hotel {
    private HotelData hotelData;

    public Hotel() {
        loadHotelData();
    }

    private void loadHotelData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("backup"))) {
            hotelData = (HotelData) in.readObject();
        } catch (Exception e) {
            hotelData = new HotelData();
        }
    }

    private void saveHotelData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("backup"))) {
            out.writeObject(hotelData);
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void checkIn(int roomType, int roomNumber, Scanner sc) throws NotAvailableException {
        switch (roomType) {
            case 1 -> {
                if (hotelData.luxuryDouble[roomNumber] != null) throw new NotAvailableException();
                System.out.print("Enter Customer 1 Name: ");
                String name1 = sc.next();
                System.out.print("Enter Customer 1 Contact: ");
                String contact1 = sc.next();
                System.out.print("Enter Customer 1 Gender: ");
                String gender1 = sc.next();
                System.out.print("Enter Customer 2 Name: ");
                String name2 = sc.next();
                System.out.print("Enter Customer 2 Contact: ");
                String contact2 = sc.next();
                System.out.print("Enter Customer 2 Gender: ");
                String gender2 = sc.next();
                hotelData.luxuryDouble[roomNumber] = new DoubleRoom(name1, contact1, gender1, name2, contact2, gender2);
            }
            case 2 -> {
                if (hotelData.deluxeDouble[roomNumber] != null) throw new NotAvailableException();
                System.out.print("Enter Customer 1 Name: ");
                String name1 = sc.next();
                System.out.print("Enter Customer 1 Contact: ");
                String contact1 = sc.next();
                System.out.print("Enter Customer 1 Gender: ");
                String gender1 = sc.next();
                System.out.print("Enter Customer 2 Name: ");
                String name2 = sc.next();
                System.out.print("Enter Customer 2 Contact: ");
                String contact2 = sc.next();
                System.out.print("Enter Customer 2 Gender: ");
                String gender2 = sc.next();
                hotelData.deluxeDouble[roomNumber] = new DoubleRoom(name1, contact1, gender1, name2, contact2, gender2);
            }
            case 3 -> {
                if (hotelData.luxurySingle[roomNumber] != null) throw new NotAvailableException();
                System.out.print("Enter Customer Name: ");
                String name = sc.next();
                System.out.print("Enter Customer Contact: ");
                String contact = sc.next();
                System.out.print("Enter Customer Gender: ");
                String gender = sc.next();
                hotelData.luxurySingle[roomNumber] = new SingleRoom(name, contact, gender);
            }
            case 4 -> {
                if (hotelData.deluxeSingle[roomNumber] != null) throw new NotAvailableException();
                System.out.print("Enter Customer Name: ");
                String name = sc.next();
                System.out.print("Enter Customer Contact: ");
                String contact = sc.next();
                System.out.print("Enter Customer Gender: ");
                String gender = sc.next();
                hotelData.deluxeSingle[roomNumber] = new SingleRoom(name, contact, gender);
            }
        }
        saveHotelData();
    }

    public void orderFood(int roomType, int roomNumber, Scanner sc) {
        Room room = getRoom(roomType, roomNumber);
        if (room == null) {
            System.out.println("Room not occupied.");
            return;
        }

        while (true) {
            System.out.println("\n=== Menu ===");
            for (FoodItem item : FoodItem.values()) {
                System.out.printf("%d. %s - %.2f\n", item.code, item.name, item.price);
            }
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            if (choice == 5) break;
            System.out.print("Enter quantity: ");
            int quantity = sc.nextInt();
            try {
                Food food = new Food(choice, quantity);
                room.addFood(food);
                System.out.println("Order added.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice.");
            }
        }
        saveHotelData();
    }

    public void checkout(int roomType, int roomNumber) {
        Room room = getRoom(roomType, roomNumber);
        if (room == null) {
            System.out.println("Room not occupied.");
            return;
        }

        double roomCharge = room.getRoomCharge();
        float foodCharge = 0;
        System.out.println("\n=== Food Charges ===");
        System.out.printf("%-10s%-10s%-10s\n", "Item", "Qty", "Price");
        for (Food f : room.getFoodOrders()) {
            System.out.println(f.getDescription());
            foodCharge += f.getPrice();
        }
        double total = roomCharge + foodCharge;
        System.out.println("\nRoom Charge: " + roomCharge);
        System.out.println("Food Charge: " + foodCharge);
        System.out.println("Total Charge: " + total);

        removeRoom(roomType, roomNumber);
        saveHotelData();
    }

    public void viewRoom(int roomType, int roomNumber) {
        Room room = getRoom(roomType, roomNumber);
        if (room == null) {
            System.out.println("Room is empty.");
        } else {
            System.out.println("Customer Name: " + room.getCustomerName());
            System.out.println("Room Charge: " + room.getRoomCharge());
            System.out.println("Food Orders:");
            for (Food f : room.getFoodOrders()) {
                System.out.println(f.getDescription());
            }
        }
    }

    private Room getRoom(int roomType, int roomNumber) {
        return switch (roomType) {
            case 1 -> hotelData.luxuryDouble[roomNumber];
            case 2 -> hotelData.deluxeDouble[roomNumber];
            case 3 -> hotelData.luxurySingle[roomNumber];
            case 4 -> hotelData.deluxeSingle[roomNumber];
            default -> null;
        };
    }

    private void removeRoom(int roomType, int roomNumber) {
        switch (roomType) {
            case 1 -> hotelData.luxuryDouble[roomNumber] = null;
            case 2 -> hotelData.deluxeDouble[roomNumber] = null;
            case 3 -> hotelData.luxurySingle[roomNumber] = null;
            case 4 -> hotelData.deluxeSingle[roomNumber] = null;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();
        int choice;

        do {
            System.out.println("\n=== Hotel Menu ===");
            System.out.println("1. Check-In");
            System.out.println("2. Order Food");
            System.out.println("3. Check-Out");
            System.out.println("4. View Room Details");
            System.out.println("5. Exit");
            System.out.print("Enter Choice: ");
            choice = sc.nextInt();

            if (choice >= 1 && choice <= 4) {
                System.out.println("Select Room Type:");
                System.out.println("1. Luxury Double Room");
                System.out.println("2. Deluxe Double Room");
                System.out.println("3. Luxury Single Room");
                System.out.println("4. Deluxe Single Room");
                System.out.print("Enter Room Type: ");
                int roomType = sc.nextInt();

                int maxRooms = switch (roomType) {
                    case 1, 3 -> 10;
                    case 2, 4 -> 20;
                    default -> 0;
                };

                if (maxRooms == 0) {
                    System.out.println("Invalid Room Type.");
                    continue;
                }

                System.out.printf("Enter Room Number (0-%d): ", maxRooms - 1);
                int roomNumber = sc.nextInt();
                if (roomNumber < 0 || roomNumber >= maxRooms) {
                    System.out.println("Invalid Room Number.");
                    continue;
                }

                try {
                    switch (choice) {
                        case 1 -> hotel.checkIn(roomType, roomNumber, sc);
                        case 2 -> hotel.orderFood(roomType, roomNumber, sc);
                        case 3 -> hotel.checkout(roomType, roomNumber);
                        case 4 -> hotel.viewRoom(roomType, roomNumber);
                    }
                } catch (NotAvailableException e) {
                    System.out.println(e);
                }
            }
        } while (choice != 5);

        sc.close();
    }
}
