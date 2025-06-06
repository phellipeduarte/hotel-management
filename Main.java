import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

// --- Enums and Constants ---
enum RoomType {
    LUXURY_DOUBLE,
    DELUXE_DOUBLE,
    LUXURY_SINGLE,
    DELUXE_SINGLE
}

final class RoomConstants {
    public static final int LUXURY_DOUBLE_PRICE = 4000;
    public static final int DELUXE_DOUBLE_PRICE = 3000;
    public static final int LUXURY_SINGLE_PRICE = 2200;
    public static final int DELUXE_SINGLE_PRICE = 1200;

    public static final int LUXURY_DOUBLE_ROOMS = 10;
    public static final int DELUXE_DOUBLE_ROOMS = 20;
    public static final int LUXURY_SINGLE_ROOMS = 10;
    public static final int DELUXE_SINGLE_ROOMS = 20;

    public static final String[] FOOD_ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};
    public static final int[] FOOD_PRICES = {50, 60, 70, 30};
}

// --- Custom Exception ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

// --- Model Classes ---
class Guest implements Serializable {
    private String name;
    private String contact;
    private String gender;

    public Guest(String name, String contact, String gender) {
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

class FoodItem implements Serializable {
    private int itemNumber;
    private int quantity;
    private double price; // Changed to double for better precision

    public FoodItem(int itemNumber, int quantity) {
        this.itemNumber = itemNumber;
        this.quantity = quantity;
        this.price = calculatePrice(itemNumber, quantity);
    }

    private double calculatePrice(int itemNumber, int quantity) {
        if (itemNumber > 0 && itemNumber <= RoomConstants.FOOD_PRICES.length) {
            return (double) quantity * RoomConstants.FOOD_PRICES[itemNumber - 1];
        }
        return 0.0;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

abstract class Room implements Serializable {
    private List<Guest> guests = new ArrayList<>();
    private List<FoodItem> orderedFood = new ArrayList<>();
    private RoomType roomType;
    private int roomPrice;

    public Room(RoomType roomType, int roomPrice) {
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public void addGuest(Guest guest) {
        this.guests.add(guest);
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void addFoodItem(FoodItem food) {
        this.orderedFood.add(food);
    }

    public List<FoodItem> getOrderedFood() {
        return orderedFood;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public abstract String getFeatures();
    public abstract int getMaxOccupancy();
}

class SingleRoom extends Room {
    public SingleRoom(RoomType roomType, int roomPrice) {
        super(roomType, roomPrice);
    }

    @Override
    public String getFeatures() {
        return "Number of single beds : 1\nAC : " + (getRoomType() == RoomType.LUXURY_SINGLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day:" + getRoomPrice();
    }

    @Override
    public int getMaxOccupancy() {
        return 1;
    }
}

class DoubleRoom extends Room {
    public DoubleRoom(RoomType roomType, int roomPrice) {
        super(roomType, roomPrice);
    }

    @Override
    public String getFeatures() {
        return "Number of double beds : 1\nAC : " + (getRoomType() == RoomType.LUXURY_DOUBLE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day:" + getRoomPrice();
    }

    @Override
    public int getMaxOccupancy() {
        return 2;
    }
}

class HotelData implements Serializable {
    private Room[] luxuryDoubleRooms = new DoubleRoom[RoomConstants.LUXURY_DOUBLE_ROOMS];
    private Room[] deluxeDoubleRooms = new DoubleRoom[RoomConstants.DELUXE_DOUBLE_ROOMS];
    private Room[] luxurySingleRooms = new SingleRoom[RoomConstants.LUXURY_SINGLE_ROOMS];
    private Room[] deluxeSingleRooms = new SingleRoom[RoomConstants.DELUXE_SINGLE_ROOMS];

    public HotelData() {
        // Initialize rooms as null, indicating they are available
    }

    public Room[] getRooms(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE:
                return luxuryDoubleRooms;
            case DELUXE_DOUBLE:
                return deluxeDoubleRooms;
            case LUXURY_SINGLE:
                return luxurySingleRooms;
            case DELUXE_SINGLE:
                return deluxeSingleRooms;
            default:
                return null; // Should not happen
        }
    }

    public int getRoomStartingIndex(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE: return 1;
            case DELUXE_DOUBLE: return 11;
            case LUXURY_SINGLE: return 31;
            case DELUXE_SINGLE: return 41;
            default: return 0;
        }
    }

    public int getRoomOffset(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE: return 1;
            case DELUXE_DOUBLE: return 11;
            case LUXURY_SINGLE: return 31;
            case DELUXE_SINGLE: return 41;
            default: return 0;
        }
    }
}

// --- Hotel Management Class ---
class HotelManagement {
    private static HotelData hotelData = new HotelData();
    private static Scanner scanner = new Scanner(System.in);

    public static void loadHotelData() {
        File file = new File("backup");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                hotelData = (HotelData) ois.readObject();
                System.out.println("Hotel data loaded from backup.");
            } catch (Exception e) {
                System.err.println("Error loading backup: " + e.getMessage());
            }
        }
    }

    public static void saveHotelData() {
        Thread t = new Thread(() -> {
            try (FileOutputStream fos = new FileOutputStream("backup");
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(hotelData);
                System.out.println("Hotel data saved.");
            } catch (Exception e) {
                System.err.println("Error saving backup: " + e.getMessage());
            }
        });
        t.start();
    }

    public static void displayRoomFeatures(RoomType type) {
        switch (type) {
            case LUXURY_DOUBLE:
                System.out.println(new DoubleRoom(RoomType.LUXURY_DOUBLE, RoomConstants.LUXURY_DOUBLE_PRICE).getFeatures());
                break;
            case DELUXE_DOUBLE:
                System.out.println(new DoubleRoom(RoomType.DELUXE_DOUBLE, RoomConstants.DELUXE_DOUBLE_PRICE).getFeatures());
                break;
            case LUXURY_SINGLE:
                System.out.println(new SingleRoom(RoomType.LUXURY_SINGLE, RoomConstants.LUXURY_SINGLE_PRICE).getFeatures());
                break;
            case DELUXE_SINGLE:
                System.out.println(new SingleRoom(RoomType.DELUXE_SINGLE, RoomConstants.DELUXE_SINGLE_PRICE).getFeatures());
                break;
            default:
                System.out.println("Tipo de quarto inválido.");
        }
    }

    public static void checkRoomAvailability(RoomType type) {
        Room[] rooms = hotelData.getRooms(type);
        if (rooms == null) {
            System.out.println("Tipo de quarto inválido.");
            return;
        }

        long availableCount = Arrays.stream(rooms).filter(room -> room == null).count();
        System.out.println("Número de quartos disponíveis: " + availableCount);
    }

    public static void bookRoom(RoomType type) {
        Room[] rooms = hotelData.getRooms(type);
        if (rooms == null) {
            System.out.println("Tipo de quarto inválido.");
            return;
        }

        System.out.print("\nEscolha o número do quarto disponível (");
        int offset = hotelData.getRoomOffset(type);
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print((i + offset) + (i == rooms.length - 1 ? "" : ", "));
            }
        }
        System.out.print("):\n");

        try {
            int roomNumberInput = scanner.nextInt();
            int roomIndex = roomNumberInput - offset;

            if (roomIndex < 0 || roomIndex >= rooms.length) {
                throw new RoomNotAvailableException("Número do quarto inválido.");
            }
            if (rooms[roomIndex] != null) {
                throw new RoomNotAvailableException("Quarto já ocupado.");
            }

            Room newRoom;
            if (type == RoomType.LUXURY_DOUBLE) {
                newRoom = new DoubleRoom(RoomType.LUXURY_DOUBLE, RoomConstants.LUXURY_DOUBLE_PRICE);
            } else if (type == RoomType.DELUXE_DOUBLE) {
                newRoom = new DoubleRoom(RoomType.DELUXE_DOUBLE, RoomConstants.DELUXE_DOUBLE_PRICE);
            } else if (type == RoomType.LUXURY_SINGLE) {
                newRoom = new SingleRoom(RoomType.LUXURY_SINGLE, RoomConstants.LUXURY_SINGLE_PRICE);
            } else { // DELUXE_SINGLE
                newRoom = new SingleRoom(RoomType.DELUXE_SINGLE, RoomConstants.DELUXE_SINGLE_PRICE);
            }

            System.out.print("Digite o nome do cliente: ");
            String name = scanner.next();
            System.out.print("Digite o número de contato: ");
            String contact = scanner.next();
            System.out.print("Digite o gênero: ");
            String gender = scanner.next();
            newRoom.addGuest(new Guest(name, contact, gender));

            if (newRoom.getMaxOccupancy() == 2) {
                System.out.print("Digite o nome do segundo cliente: ");
                String name2 = scanner.next();
                System.out.print("Digite o número de contato do segundo cliente: ");
                String contact2 = scanner.next();
                System.out.print("Digite o gênero do segundo cliente: ");
                String gender2 = scanner.next();
                newRoom.addGuest(new Guest(name2, contact2, gender2));
            }

            rooms[roomIndex] = newRoom;
            System.out.println("Quarto reservado com sucesso!");

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next(); // Consume the invalid input
        } catch (RoomNotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao reservar o quarto: " + e.getMessage());
        }
    }

    public static void orderFood(int roomNumberInput) {
        Room room = findRoomByNumber(roomNumberInput);

        if (room == null) {
            System.out.println("Quarto não existe ou não está reservado.");
            return;
        }
        if (room.getGuests().isEmpty()) {
            System.out.println("Este quarto não está ocupado.");
            return;
        }

        System.out.println("\n==========\n    Menu: \n==========\n");
        for (int i = 0; i < RoomConstants.FOOD_ITEMS.length; i++) {
            System.out.printf("%d. %s\t\tRs.%d%n", i + 1, RoomConstants.FOOD_ITEMS[i], RoomConstants.FOOD_PRICES[i]);
        }

        char wish;
        do {
            try {
                System.out.print("Escolha o item (número): ");
                int itemChoice = scanner.nextInt();
                System.out.print("Quantidade: ");
                int quantity = scanner.nextInt();

                if (itemChoice > 0 && itemChoice <= RoomConstants.FOOD_ITEMS.length && quantity > 0) {
                    room.addFoodItem(new FoodItem(itemChoice, quantity));
                    System.out.println("Item adicionado ao pedido.");
                } else {
                    System.out.println("Escolha de item ou quantidade inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.next(); // Consume the invalid input
            } catch (Exception e) {
                System.out.println("Erro ao fazer o pedido: " + e.getMessage());
            }

            System.out.println("Deseja pedir mais alguma coisa? (y/n)");
            wish = scanner.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');
    }

    public static void checkoutRoom(int roomNumberInput) {
        Room room = findRoomByNumber(roomNumberInput);
        if (room == null || room.getGuests().isEmpty()) {
            System.out.println("Quarto vazio ou não existe.");
            return;
        }

        System.out.println("Quarto utilizado por " + room.getGuests().get(0).getName());
        System.out.println("Deseja fazer o checkout? (y/n)");
        char wish = scanner.next().charAt(0);

        if (wish == 'y' || wish == 'Y') {
            generateBill(room);
            clearRoom(roomNumberInput);
            System.out.println("Checkout realizado com sucesso!");
        } else {
            System.out.println("Checkout cancelado.");
        }
    }

    private static void generateBill(Room room) {
        double totalAmount = 0;

        System.out.println("\n*******");
        System.out.println(" Conta:");
        System.out.println("*******");

        totalAmount += room.getRoomPrice();
        System.out.println("\nCusto do Quarto - " + room.getRoomPrice());

        if (!room.getOrderedFood().isEmpty()) {
            System.out.println("\n===============");
            System.out.println("Custos de Comida:- ");
            System.out.println("===============");
            System.out.println("Item        Quantidade  Preço");
            System.out.println("-------------------------");
            for (FoodItem food : room.getOrderedFood()) {
                totalAmount += food.getPrice();
                String format = "%-12s%-12s%-10.2f%n";
                System.out.printf(format, RoomConstants.FOOD_ITEMS[food.getItemNumber() - 1], food.getQuantity(), food.getPrice());
            }
        }
        System.out.println("\nValor Total - " + totalAmount);
    }

    private static void clearRoom(int roomNumberInput) {
        int roomTypeIndex = 0;
        int roomIndex = 0;

        if (roomNumberInput >= 1 && roomNumberInput <= 10) { // Luxury Double
            roomTypeIndex = 1;
            roomIndex = roomNumberInput - 1;
            hotelData.getRooms(RoomType.LUXURY_DOUBLE)[roomIndex] = null;
        } else if (roomNumberInput >= 11 && roomNumberInput <= 30) { // Deluxe Double
            roomTypeIndex = 2;
            roomIndex = roomNumberInput - 11;
            hotelData.getRooms(RoomType.DELUXE_DOUBLE)[roomIndex] = null;
        } else if (roomNumberInput >= 31 && roomNumberInput <= 40) { // Luxury Single
            roomTypeIndex = 3;
            roomIndex = roomNumberInput - 31;
            hotelData.getRooms(RoomType.LUXURY_SINGLE)[roomIndex] = null;
        } else if (roomNumberInput >= 41 && roomNumberInput <= 60) { // Deluxe Single
            roomTypeIndex = 4;
            roomIndex = roomNumberInput - 41;
            hotelData.getRooms(RoomType.DELUXE_SINGLE)[roomIndex] = null;
        }
    }


    private static Room findRoomByNumber(int roomNumberInput) {
        if (roomNumberInput >= 1 && roomNumberInput <= 10) { // Luxury Double
            return hotelData.getRooms(RoomType.LUXURY_DOUBLE)[roomNumberInput - 1];
        } else if (roomNumberInput >= 11 && roomNumberInput <= 30) { // Deluxe Double
            return hotelData.getRooms(RoomType.DELUXE_DOUBLE)[roomNumberInput - 11];
        } else if (roomNumberInput >= 31 && roomNumberInput <= 40) { // Luxury Single
            return hotelData.getRooms(RoomType.LUXURY_SINGLE)[roomNumberInput - 31];
        } else if (roomNumberInput >= 41 && roomNumberInput <= 60) { // Deluxe Single
            return hotelData.getRooms(RoomType.DELUXE_SINGLE)[roomNumberInput - 41];
        }
        return null;
    }
}

// --- Main Application Class ---
public class Main {
    public static void main(String[] args) {
        HotelManagement.loadHotelData();
        Scanner sc = new Scanner(System.in);
        int choice, roomTypeChoice;
        char continueOption;

        do {
            System.out.println("\nDigite sua escolha:");
            System.out.println("1. Detalhes do quarto");
            System.out.println("2. Disponibilidade do quarto");
            System.out.println("3. Reservar quarto");
            System.out.println("4. Pedir comida");
            System.out.println("5. Checkout");
            System.out.println("6. Sair");
            System.out.print("Escolha: ");

            try {
                choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEscolha o tipo de quarto:");
                        System.out.println("1. Quarto Duplo Luxo");
                        System.out.println("2. Quarto Duplo Deluxe");
                        System.out.println("3. Quarto Individual Luxo");
                        System.out.println("4. Quarto Individual Deluxe");
                        System.out.print("Escolha: ");
                        roomTypeChoice = sc.nextInt();
                        HotelManagement.displayRoomFeatures(getRoomTypeFromInt(roomTypeChoice));
                        break;
                    case 2:
                        System.out.println("\nEscolha o tipo de quarto:");
                        System.out.println("1. Quarto Duplo Luxo");
                        System.out.println("2. Quarto Duplo Deluxe");
                        System.out.println("3. Quarto Individual Luxo");
                        System.out.println("4. Quarto Individual Deluxe");
                        System.out.print("Escolha: ");
                        roomTypeChoice = sc.nextInt();
                        HotelManagement.checkRoomAvailability(getRoomTypeFromInt(roomTypeChoice));
                        break;
                    case 3:
                        System.out.println("\nEscolha o tipo de quarto:");
                        System.out.println("1. Quarto Duplo Luxo");
                        System.out.println("2. Quarto Duplo Deluxe");
                        System.out.println("3. Quarto Individual Luxo");
                        System.out.println("4. Quarto Individual Deluxe");
                        System.out.print("Escolha: ");
                        roomTypeChoice = sc.nextInt();
                        HotelManagement.bookRoom(getRoomTypeFromInt(roomTypeChoice));
                        break;
                    case 4:
                        System.out.print("Número do Quarto: ");
                        int roomNumOrder = sc.nextInt();
                        HotelManagement.orderFood(roomNumOrder);
                        break;
                    case 5:
                        System.out.print("Número do Quarto: ");
                        int roomNumCheckout = sc.nextInt();
                        HotelManagement.checkoutRoom(roomNumCheckout);
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                sc.next(); // Consume the invalid input
                choice = 0; // Set choice to something that will continue the loop
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                choice = 0; // Set choice to something that will continue the loop
            }

            if (choice != 6) {
                System.out.println("\nContinuar: (y/n)");
                continueOption = sc.next().charAt(0);
            } else {
                continueOption = 'n'; // Exit loop if choice is 6
            }

        } while (continueOption == 'y' || continueOption == 'Y');

        HotelManagement.saveHotelData();
        sc.close();
    }

    private static RoomType getRoomTypeFromInt(int typeInt) {
        switch (typeInt) {
            case 1: return RoomType.LUXURY_DOUBLE;
            case 2: return RoomType.DELUXE_DOUBLE;
            case 3: return RoomType.LUXURY_SINGLE;
            case 4: return RoomType.DELUXE_SINGLE;
            default: throw new IllegalArgumentException("Opção de tipo de quarto inválida.");
        }
    }
}