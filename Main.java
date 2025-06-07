import java.io.*;
import java.util.*;

// --- CONSTANTES UTILIZADAS ---
final class Constants {
    static final int LUXURY_DOUBLE = 1;
    static final int DELUXE_DOUBLE = 2;
    static final int LUXURY_SINGLE = 3;
    static final int DELUXE_SINGLE = 4;

    static final int[] ROOM_START_NUMBERS = {1, 11, 31, 41};
    static final int[] ROOM_COUNTS = {10, 20, 10, 20};
    static final int[] ROOM_PRICES = {4000, 3000, 2200, 1200};

    static final String[] FOOD_ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};
    static final int[] FOOD_PRICES = {50, 60, 70, 30};

    private Constants() {} // impede instanciação
}

// --- EXCEÇÃO CUSTOMIZADA ---
class NotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Room Not Available!";
    }
}

// --- FOOD (ALIMENTO) ---
class Food implements Serializable {
    private final int itemNo;
    private final int quantity;
    private final float price;

    public Food(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        if (itemNo < 1 || itemNo > Constants.FOOD_PRICES.length) {
            throw new IllegalArgumentException("Invalid food item number");
        }
        this.price = quantity * Constants.FOOD_PRICES[itemNo - 1];
    }

    public int getItemNo() { return itemNo; }
    public int getQuantity() { return quantity; }
    public float getPrice() { return price; }
}

// --- INTERFACE ROOM (QUARTO) ---
interface Room extends Serializable {
    String getName();
    void setName(String name);

    String getContact();
    void setContact(String contact);

    String getGender();
    void setGender(String gender);

    List<Food> getFoodOrders();

    void addFoodOrder(Food food);
}

// --- IMPLEMENTAÇÃO DE QUARTO SIMPLES ---
class SingleRoom implements Room {
    private String name = "";
    private String contact;
    private String gender;
    private final List<Food> foodOrders = new ArrayList<>();

    public SingleRoom() {}

    public SingleRoom(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }

    @Override public String getContact() { return contact; }
    @Override public void setContact(String contact) { this.contact = contact; }

    @Override public String getGender() { return gender; }
    @Override public void setGender(String gender) { this.gender = gender; }

    @Override public List<Food> getFoodOrders() { return foodOrders; }

    @Override
    public void addFoodOrder(Food food) {
        foodOrders.add(food);
    }
}

// --- IMPLEMENTAÇÃO DE QUARTO DUPLO ---
class DoubleRoom extends SingleRoom {
    private String name2;
    private String contact2;
    private String gender2;

    public DoubleRoom() {
        super();
        this.name2 = "";
    }

    public DoubleRoom(String name1, String contact1, String gender1,
                      String name2, String contact2, String gender2) {
        super(name1, contact1, gender1);
        this.name2 = name2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }

    public String getName2() { return name2; }
    public void setName2(String name2) { this.name2 = name2; }

    public String getContact2() { return contact2; }
    public void setContact2(String contact2) { this.contact2 = contact2; }

    public String getGender2() { return gender2; }
    public void setGender2(String gender2) { this.gender2 = gender2; }
}

// --- HOLDER PARA OS QUARTOS ---
class Holder implements Serializable {
    private final DoubleRoom[] luxuryDoubleRooms = new DoubleRoom[Constants.ROOM_COUNTS[0]];
    private final DoubleRoom[] deluxeDoubleRooms = new DoubleRoom[Constants.ROOM_COUNTS[1]];
    private final SingleRoom[] luxurySingleRooms = new SingleRoom[Constants.ROOM_COUNTS[2]];
    private final SingleRoom[] deluxeSingleRooms = new SingleRoom[Constants.ROOM_COUNTS[3]];

    public DoubleRoom[] getLuxuryDoubleRooms() { return luxuryDoubleRooms; }
    public DoubleRoom[] getDeluxeDoubleRooms() { return deluxeDoubleRooms; }
    public SingleRoom[] getLuxurySingleRooms() { return luxurySingleRooms; }
    public SingleRoom[] getDeluxeSingleRooms() { return deluxeSingleRooms; }
}

// --- GERENCIADOR DE HOTEL ---
class Hotel {
    private final Holder holder;
    private final Scanner scanner;

    public Hotel(Holder holder, Scanner scanner) {
        this.holder = holder;
        this.scanner = scanner;
    }

    // Obtem o array do tipo correto
    private Object[] getRoomsByType(int type) {
        return switch(type) {
            case Constants.LUXURY_DOUBLE -> holder.getLuxuryDoubleRooms();
            case Constants.DELUXE_DOUBLE -> holder.getDeluxeDoubleRooms();
            case Constants.LUXURY_SINGLE -> holder.getLuxurySingleRooms();
            case Constants.DELUXE_SINGLE -> holder.getDeluxeSingleRooms();
            default -> throw new IllegalArgumentException("Tipo inválido de quarto");
        };
    }

    // Pede os dados do cliente
    private void collectCustomerDetails(int roomType, int roomIndex) {
        System.out.print("Enter first customer name: ");
        String name1 = scanner.next();
        System.out.print("Enter contact number: ");
        String contact1 = scanner.next();
        System.out.print("Enter gender: ");
        String gender1 = scanner.next();

        if (roomType == Constants.LUXURY_DOUBLE || roomType == Constants.DELUXE_DOUBLE) {
            System.out.print("Enter second customer name: ");
            String name2 = scanner.next();
            System.out.print("Enter contact number: ");
            String contact2 = scanner.next();
            System.out.print("Enter gender: ");
            String gender2 = scanner.next();

            DoubleRoom[] rooms = (DoubleRoom[]) getRoomsByType(roomType);
            rooms[roomIndex] = new DoubleRoom(name1, contact1, gender1, name2, contact2, gender2);
        } else {
            SingleRoom[] rooms = (SingleRoom[]) getRoomsByType(roomType);
            rooms[roomIndex] = new SingleRoom(name1, contact1, gender1);
        }
    }

    // Mostra os números disponíveis para reserva e retorna o índice selecionado
    private int selectRoomNumber(int roomType) throws NotAvailableException {
        Object[] rooms = getRoomsByType(roomType);
        int startNum = Constants.ROOM_START_NUMBERS[roomType - 1];

        System.out.println("\nAvailable rooms:");
        boolean available = false;
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print((startNum + i) + " ");
                available = true;
            }
        }
        if (!available) {
            throw new NotAvailableException();
        }

        System.out.print("\nEnter room number: ");
        int roomNum = scanner.nextInt();
        int index = roomNum - startNum;
        if (index < 0 || index >= rooms.length || rooms[index] != null) {
            throw new NotAvailableException();
        }
        return index;
    }

    public void bookRoom(int roomType) {
        try {
            int roomIndex = selectRoomNumber(roomType);
            collectCustomerDetails(roomType, roomIndex);
            System.out.println("Room booked successfully.");
        } catch (NotAvailableException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine(); // limpar buffer
        }
    }

    public void displayFeatures(int roomType) {
        switch (roomType) {
            case Constants.LUXURY_DOUBLE ->
                    System.out.println("Number of double beds: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: 4000");
            case Constants.DELUXE_DOUBLE ->
                    System.out.println("Number of double beds: 1\nAC: No\nFree breakfast: Yes\nCharge per day: 3000");
            case Constants.LUXURY_SINGLE ->
                    System.out.println("Number of single beds: 1\nAC: Yes\nFree breakfast: Yes\nCharge per day: 2200");
            case Constants.DELUXE_SINGLE ->
                    System.out.println("Number of single beds: 1\nAC: No\nFree breakfast: Yes\nCharge per day: 1200");
            default -> System.out.println("Invalid option");
        }
    }

    public void displayAvailability(int roomType) {
        Object[] rooms = getRoomsByType(roomType);
        long count = Arrays.stream(rooms).filter(Objects::isNull).count();
        System.out.println("Rooms available: " + count);
    }

    public void checkOut(int roomType) {
        Object[] rooms = getRoomsByType(roomType);
        int startNum = Constants.ROOM_START_NUMBERS[roomType - 1];

        try {
            System.out.print("Enter room number to checkout: ");
            int roomNum = scanner.nextInt();
            int index = roomNum - startNum;
            if (index < 0 || index >= rooms.length || rooms[index] == null) {
                System.out.println("Room not occupied");
                return;
            }

            if (roomType == Constants.LUXURY_DOUBLE || roomType == Constants.DELUXE_DOUBLE) {
                ((DoubleRoom) rooms[index]).setName("");
            } else {
                ((SingleRoom) rooms[index]).setName("");
            }
            rooms[index] = null;
            System.out.println("Checkout done");
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    public void orderFood(int roomType) {
        Object[] rooms = getRoomsByType(roomType);
        int startNum = Constants.ROOM_START_NUMBERS[roomType - 1];

        try {
            System.out.print("Enter room number for food order: ");
            int roomNum = scanner.nextInt();
            int index = roomNum - startNum;
            if (index < 0 || index >= rooms.length || rooms[index] == null) {
                System.out.println("Room not occupied");
                return;
            }

            System.out.println("Food Menu:");
            for (int i = 0; i < Constants.FOOD_ITEMS.length; i++) {
                System.out.println((i + 1) + ". " + Constants.FOOD_ITEMS[i] + " - Rs." + Constants.FOOD_PRICES[i]);
            }
            System.out.print("Enter food item number: ");
            int itemNo = scanner.nextInt();
            System.out.print("Enter quantity: ");
            int qty = scanner.nextInt();

            Food foodOrder = new Food(itemNo, qty);

            if (roomType == Constants.LUXURY_DOUBLE || roomType == Constants.DELUXE_DOUBLE) {
                ((DoubleRoom) rooms[index]).addFoodOrder(foodOrder);
            } else {
                ((SingleRoom) rooms[index]).addFoodOrder(foodOrder);
            }
            System.out.println("Food order added");
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    public void displayFoodBill(int roomType) {
        Object[] rooms = getRoomsByType(roomType);
        int startNum = Constants.ROOM_START_NUMBERS[roomType - 1];

        try {
            System.out.print("Enter room number for food bill: ");
            int roomNum = scanner.nextInt();
            int index = roomNum - startNum;
            if (index < 0 || index >= rooms.length || rooms[index] == null) {
                System.out.println("Room not occupied");
                return;
            }

            List<Food> foodList;
            if (roomType == Constants.LUXURY_DOUBLE || roomType == Constants.DELUXE_DOUBLE) {
                foodList = ((DoubleRoom) rooms[index]).getFoodOrders();
            } else {
                foodList = ((SingleRoom) rooms[index]).getFoodOrders();
            }

            if (foodList.isEmpty()) {
                System.out.println("No food ordered");
                return;
            }

            float total = 0;
            System.out.println("Food Bill:");
            for (Food f : foodList) {
                String foodName = Constants.FOOD_ITEMS[f.getItemNo() - 1];
                System.out.println(foodName + " x " + f.getQuantity() + " = Rs." + f.getPrice());
                total += f.getPrice();
            }
            System.out.println("Total = Rs." + total);
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }
}

// --- SERIALIZAÇÃO E PERSISTÊNCIA ---
class DataPersistence {
    private static final String FILE_NAME = "backup";

    public static void save(Holder holder) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(holder);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static Holder load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Holder) in.readObject();
        } catch (FileNotFoundException e) {
            // Arquivo não existe, retorna novo Holder
            return new Holder();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
            return new Holder();
        }
    }
}

// --- CLASSE MAIN ---
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Holder holder = DataPersistence.load();
        Hotel hotel = new Hotel(holder, scanner);

        while (true) {
            System.out.println("\n--- Hotel Menu ---");
            System.out.println("1. Book Luxury Double Room");
            System.out.println("2. Book Deluxe Double Room");
            System.out.println("3. Book Luxury Single Room");
            System.out.println("4. Book Deluxe Single Room");
            System.out.println("5. Display Room Features");
            System.out.println("6. Display Room Availability");
            System.out.println("7. Check Out");
            System.out.println("8. Order Food");
            System.out.println("9. Display Food Bill");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                scanner.nextLine(); // limpar buffer
                continue;
            }

            switch (choice) {
                case 1 -> hotel.bookRoom(Constants.LUXURY_DOUBLE);
                case 2 -> hotel.bookRoom(Constants.DELUXE_DOUBLE);
                case 3 -> hotel.bookRoom(Constants.LUXURY_SINGLE);
                case 4 -> hotel.bookRoom(Constants.DELUXE_SINGLE);
                case 5 -> {
                    System.out.print("Enter room type (1-4): ");
                    int type = scanner.nextInt();
                    hotel.displayFeatures(type);
                }
                case 6 -> {
                    System.out.print("Enter room type (1-4): ");
                    int type = scanner.nextInt();
                    hotel.displayAvailability(type);
                }
                case 7 -> {
                    System.out.print("Enter room type (1-4): ");
                    int type = scanner.nextInt();
                    hotel.checkOut(type);
                }
                case 8 -> {
                    System.out.print("Enter room type (1-4): ");
                    int type = scanner.nextInt();
                    hotel.orderFood(type);
                }
                case 9 -> {
                    System.out.print("Enter room type (1-4): ");
                    int type = scanner.nextInt();
                    hotel.displayFoodBill(type);
                }
                case 0 -> {
                    DataPersistence.save(holder);
                    System.out.println("Thank you for using the system.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
