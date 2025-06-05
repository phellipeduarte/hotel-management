import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// --- Enums ---

/**
 * Representa o tipo de comida disponível.
 */
enum FoodType {
    SANDWICH(1, 50.0, "Sandwich"),
    PASTA(2, 60.0, "Pasta"),
    NOODLES(3, 70.0, "Noodles"),
    COKE(4, 30.0, "Coke");

    private final int itemNumber;
    private final double price;
    private final String name;

    FoodType(int itemNumber, double price, String name) {
        this.itemNumber = itemNumber;
        this.price = price;
        this.name = name;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public static FoodType fromItemNumber(int itemNumber) {
        for (FoodType type : FoodType.values()) {
            if (type.getItemNumber() == itemNumber) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid food item number: " + itemNumber);
    }
}

/**
 * Representa o tipo de quarto.
 */
enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room", 4000.0, "Number of double beds : 1"),
    DELUXE_DOUBLE(2, "Deluxe Double Room", 3000.0, "Number of double beds : 1"),
    LUXURY_SINGLE(3, "Luxury Single Room", 2200.0, "Number of single beds : 1"),
    DELUXE_SINGLE(4, "Deluxe Single Room", 1200.0, "Number of single beds : 1");

    private final int typeNumber;
    private final String description;
    private final double chargePerDay;
    private final String capacityInfo; // Adicionado para armazenar a informação de capacidade diretamente no enum

    RoomType(int typeNumber, String description, double chargePerDay, String capacityInfo) {
        this.typeNumber = typeNumber;
        this.description = description;
        this.chargePerDay = chargePerDay;
        this.capacityInfo = capacityInfo;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getChargePerDay() {
        return chargePerDay;
    }

    public String getCapacityInfo() { // Método para obter a capacidade
        return capacityInfo;
    }

    public static RoomType fromTypeNumber(int typeNumber) {
        for (RoomType type : RoomType.values()) {
            if (type.getTypeNumber() == typeNumber) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid room type number: " + typeNumber);
    }
}

// --- Exceptions ---

/**
 * Exceção personalizada para quando um quarto não está disponível.
 */
class RoomNotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Room Not Available!";
    }
}

// --- Model Classes ---

/**
 * Representa um item de comida pedido por um cliente.
 */
class Food implements Serializable {
    private FoodType type;
    private int quantity;
    private double price;

    public Food(FoodType type, int quantity) {
        this.type = type;
        this.quantity = quantity;
        this.price = type.getPrice() * quantity;
    }

    public FoodType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

/**
 * Classe base abstrata para todos os tipos de quartos.
 */
abstract class Room implements Serializable {
    private String primaryGuestName;
    private String primaryGuestContact;
    private String primaryGuestGender;
    protected ArrayList<Food> orderedFood;
    private RoomType roomType;

    public Room(RoomType roomType) {
        this.roomType = roomType;
        this.primaryGuestName = "";
        this.primaryGuestContact = "";
        this.primaryGuestGender = "";
        this.orderedFood = new ArrayList<>();
    }

    public Room(RoomType roomType, String primaryGuestName, String primaryGuestContact, String primaryGuestGender) {
        this(roomType);
        this.primaryGuestName = primaryGuestName;
        this.primaryGuestContact = primaryGuestContact;
        this.primaryGuestGender = primaryGuestGender;
    }

    public String getPrimaryGuestName() {
        return primaryGuestName;
    }

    public String getPrimaryGuestContact() {
        return primaryGuestContact;
    }

    public String getPrimaryGuestGender() {
        return primaryGuestGender;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public ArrayList<Food> getOrderedFood() {
        return orderedFood;
    }

    public void addFood(Food food) {
        if (food != null) {
            this.orderedFood.add(food);
        }
    }

    // Este método abstrato não é mais necessário aqui, pois a capacidade é agora
    // uma propriedade do RoomType. No entanto, se houvesse alguma lógica
    // complexa de capacidade que dependesse da INSTÂNCIA do quarto (e.g.,
    // número real de ocupantes), ele poderia ser mantido e implementado.
    // Para a simplicidade atual, a informação é do RoomType.
    // public abstract String getRoomCapacityInfo(); // Removido
}

/**
 * Representa um quarto individual no hotel.
 */
class SingleRoom extends Room {
    public SingleRoom(RoomType roomType) {
        super(roomType);
    }

    public SingleRoom(RoomType roomType, String primaryGuestName, String primaryGuestContact, String primaryGuestGender) {
        super(roomType, primaryGuestName, primaryGuestContact, primaryGuestGender);
    }
}

/**
 * Representa um quarto duplo no hotel.
 */
class DoubleRoom extends Room {
    private String secondGuestName;
    private String secondGuestContact;
    private String secondGuestGender;

    public DoubleRoom(RoomType roomType) {
        super(roomType);
        this.secondGuestName = "";
        this.secondGuestContact = "";
        this.secondGuestGender = "";
    }

    public DoubleRoom(RoomType roomType, String primaryGuestName, String primaryGuestContact, String primaryGuestGender,
                      String secondGuestName, String secondGuestContact, String secondGuestGender) {
        super(roomType, primaryGuestName, primaryGuestContact, primaryGuestGender);
        this.secondGuestName = secondGuestName;
        this.secondGuestContact = secondGuestContact;
        this.secondGuestGender = secondGuestGender;
    }

    public String getSecondGuestName() {
        return secondGuestName;
    }

    public String getSecondGuestContact() {
        return secondGuestContact;
    }

    public String getSecondGuestGender() {
        return secondGuestGender;
    }
}

/**
 * Armazena todos os arrays de quartos do hotel.
 */
class HotelRoomHolder implements Serializable {
    private DoubleRoom[] luxuryDoubleRooms = new DoubleRoom[10];
    private DoubleRoom[] deluxeDoubleRooms = new DoubleRoom[20];
    private SingleRoom[] luxurySingleRooms = new SingleRoom[10];
    private SingleRoom[] deluxeSingleRooms = new SingleRoom[20];

    // Getters para os arrays de quartos
    public DoubleRoom[] getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public DoubleRoom[] getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public SingleRoom[] getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public SingleRoom[] getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }
}

// --- Service/Utility Classes ---

/**
 * Gerencia operações relacionadas aos quartos do hotel.
 */
class RoomManager {
    private HotelRoomHolder roomHolder;
    private Scanner scanner;

    public RoomManager(HotelRoomHolder roomHolder, Scanner scanner) {
        this.roomHolder = roomHolder;
        this.scanner = scanner;
    }

    /**
     * Coleta os detalhes do cliente com base no tipo de quarto e os atribui a um quarto.
     *
     * @param roomType   O tipo de quarto.
     * @param roomNumber O número específico do quarto (índice zero).
     */
    public void assignCustomerDetailsToRoom(RoomType roomType, int roomNumber) {
        System.out.print("\nEnter primary customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contact = scanner.nextLine();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();

        switch (roomType) {
            case LUXURY_DOUBLE:
            case DELUXE_DOUBLE:
                System.out.print("Enter second customer name: ");
                String name2 = scanner.nextLine();
                System.out.print("Enter contact number: ");
                String contact2 = scanner.nextLine();
                System.out.print("Enter gender: ");
                String gender2 = scanner.nextLine();
                if (roomType == RoomType.LUXURY_DOUBLE) {
                    roomHolder.getLuxuryDoubleRooms()[roomNumber] = new DoubleRoom(roomType, name, contact, gender, name2, contact2, gender2);
                } else {
                    roomHolder.getDeluxeDoubleRooms()[roomNumber] = new DoubleRoom(roomType, name, contact, gender, name2, contact2, gender2);
                }
                break;
            case LUXURY_SINGLE:
            case DELUXE_SINGLE:
                if (roomType == RoomType.LUXURY_SINGLE) {
                    roomHolder.getLuxurySingleRooms()[roomNumber] = new SingleRoom(roomType, name, contact, gender);
                } else {
                    roomHolder.getDeluxeSingleRooms()[roomNumber] = new SingleRoom(roomType, name, contact, gender);
                }
                break;
            default:
                System.out.println("Invalid room type specified for customer details.");
                break;
        }
    }

    /**
     * Reserva um quarto do tipo especificado.
     *
     * @param roomType O tipo de quarto a ser reservado.
     */
    public void bookRoom(RoomType roomType) {
        int roomIndex = -1;
        int displayRoomNumberOffset = 0; // Deslocamento para exibir números de quarto amigáveis ao usuário

        Room[] rooms;
        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = roomHolder.getLuxuryDoubleRooms();
                displayRoomNumberOffset = 1;
                break;
            case DELUXE_DOUBLE:
                rooms = roomHolder.getDeluxeDoubleRooms();
                displayRoomNumberOffset = 11;
                break;
            case LUXURY_SINGLE:
                rooms = roomHolder.getLuxurySingleRooms();
                displayRoomNumberOffset = 31;
                break;
            case DELUXE_SINGLE:
                rooms = roomHolder.getDeluxeSingleRooms();
                displayRoomNumberOffset = 41;
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        System.out.print("\nChoose room number from available: ");
        boolean foundAvailable = false;
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print((i + displayRoomNumberOffset) + ",");
                foundAvailable = true;
            }
        }
        System.out.println(); // Nova linha após imprimir os quartos disponíveis

        if (!foundAvailable) {
            System.out.println("No rooms of this type are available.");
            return;
        }

        try {
            System.out.print("Enter desired room number: ");
            int chosenRoomNumber = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            roomIndex = chosenRoomNumber - displayRoomNumberOffset;

            if (roomIndex < 0 || roomIndex >= rooms.length || rooms[roomIndex] != null) {
                throw new RoomNotAvailableException();
            }

            assignCustomerDetailsToRoom(roomType, roomIndex);
            System.out.println("Room Booked Successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consumir a entrada inválida
        } catch (RoomNotAvailableException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Exibe as características de um determinado tipo de quarto.
     *
     * @param roomType O tipo de quarto.
     */
    public void displayRoomFeatures(RoomType roomType) {
        System.out.println(roomType.getCapacityInfo()); // Correção aqui!
        System.out.println("AC : " + (roomType == RoomType.LUXURY_DOUBLE || roomType == RoomType.LUXURY_SINGLE ? "Yes" : "No"));
        System.out.println("Free breakfast : Yes");
        System.out.println("Charge per day: " + roomType.getChargePerDay());
    }

    /**
     * Verifica e exibe a disponibilidade de um determinado tipo de quarto.
     *
     * @param roomType O tipo de quarto.
     */
    public void checkRoomAvailability(RoomType roomType) {
        int count = 0;
        Room[] rooms;

        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = roomHolder.getLuxuryDoubleRooms();
                break;
            case DELUXE_DOUBLE:
                rooms = roomHolder.getDeluxeDoubleRooms();
                break;
            case LUXURY_SINGLE:
                rooms = roomHolder.getLuxurySingleRooms();
                break;
            case DELUXE_SINGLE:
                rooms = roomHolder.getDeluxeSingleRooms();
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        for (Room room : rooms) {
            if (room == null) {
                count++;
            }
        }
        System.out.println("Number of rooms available : " + count);
    }

    /**
     * Gera e exibe a conta de um quarto específico.
     *
     * @param roomNumber O número do quarto (índice zero).
     * @param roomType   O tipo do quarto.
     */
    public void generateBill(int roomNumber, RoomType roomType) {
        Room room;
        switch (roomType) {
            case LUXURY_DOUBLE:
                room = roomHolder.getLuxuryDoubleRooms()[roomNumber];
                break;
            case DELUXE_DOUBLE:
                room = roomHolder.getDeluxeDoubleRooms()[roomNumber];
                break;
            case LUXURY_SINGLE:
                room = roomHolder.getLuxurySingleRooms()[roomNumber];
                break;
            case DELUXE_SINGLE:
                room = roomHolder.getDeluxeSingleRooms()[roomNumber];
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        if (room == null) {
            System.out.println("Room is not booked.");
            return;
        }

        double totalAmount = 0;
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");

        System.out.println("\nRoom Charge - " + room.getRoomType().getChargePerDay());
        totalAmount += room.getRoomType().getChargePerDay();

        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item        Quantity    Price");
        System.out.println("---------------------------------");

        for (Food food : room.getOrderedFood()) {
            totalAmount += food.getPrice();
            String format = "%-12s%-12s%-10.2f%n";
            System.out.printf(format, food.getType().getName(), food.getQuantity(), food.getPrice());
        }

        System.out.println("\nTotal Amount - " + String.format("%.2f", totalAmount));
    }

    /**
     * Desaloca (faz o checkout) de um quarto.
     *
     * @param roomNumber O número do quarto (índice zero).
     * @param roomType   O tipo do quarto.
     */
    public void deallocateRoom(int roomNumber, RoomType roomType) {
        Room[] rooms;
        String guestName = "";

        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = roomHolder.getLuxuryDoubleRooms();
                if (rooms[roomNumber] != null) guestName = rooms[roomNumber].getPrimaryGuestName();
                break;
            case DELUXE_DOUBLE:
                rooms = roomHolder.getDeluxeDoubleRooms();
                if (rooms[roomNumber] != null) guestName = rooms[roomNumber].getPrimaryGuestName();
                break;
            case LUXURY_SINGLE:
                rooms = roomHolder.getLuxurySingleRooms();
                if (rooms[roomNumber] != null) guestName = rooms[roomNumber].getPrimaryGuestName();
                break;
            case DELUXE_SINGLE:
                rooms = roomHolder.getDeluxeSingleRooms();
                if (rooms[roomNumber] != null) guestName = rooms[roomNumber].getPrimaryGuestName();
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        if (rooms[roomNumber] != null) {
            System.out.println("Room currently occupied by " + guestName);
        } else {
            System.out.println("Room is already empty.");
            return;
        }

        System.out.print("Do you want to check out? (y/n): ");
        char choice = scanner.next().charAt(0);
        scanner.nextLine(); // Consumir nova linha

        if (Character.toLowerCase(choice) == 'y') {
            generateBill(roomNumber, roomType);
            rooms[roomNumber] = null; // Desalocar o quarto
            System.out.println("Deallocated successfully.");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    /**
     * Permite pedir comida para um quarto específico.
     *
     * @param roomNumber O número do quarto (índice zero).
     * @param roomType   O tipo do quarto.
     */
    public void orderFood(int roomNumber, RoomType roomType) {
        Room room;
        switch (roomType) {
            case LUXURY_DOUBLE:
                room = roomHolder.getLuxuryDoubleRooms()[roomNumber];
                break;
            case DELUXE_DOUBLE:
                room = roomHolder.getDeluxeDoubleRooms()[roomNumber];
                break;
            case LUXURY_SINGLE:
                room = roomHolder.getLuxurySingleRooms()[roomNumber];
                break;
            case DELUXE_SINGLE:
                room = roomHolder.getDeluxeSingleRooms()[roomNumber];
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }

        if (room == null) {
            System.out.println("\nRoom not booked. Cannot order food.");
            return;
        }

        try {
            System.out.println("\n==========\n   Menu:  \n==========\n");
            for (FoodType foodItem : FoodType.values()) {
                System.out.printf("%d. %s\t\tRs. %.2f%n", foodItem.getItemNumber(), foodItem.getName(), foodItem.getPrice());
            }

            char wish;
            do {
                System.out.print("Enter item number: ");
                int itemNum = scanner.nextInt();
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consumir nova linha

                FoodType foodType = FoodType.fromItemNumber(itemNum);
                room.addFood(new Food(foodType, quantity));

                System.out.print("Do you want to order anything else? (y/n): ");
                wish = scanner.next().charAt(0);
                scanner.nextLine(); // Consumir nova linha
            } while (Character.toLowerCase(wish) == 'y');

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number for item or quantity.");
            scanner.nextLine(); // Consumir a entrada inválida
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while ordering food: " + e.getMessage());
        }
    }
}


/**
 * Classe principal para executar o sistema de gerenciamento do hotel.
 */
class Hotel {
    private static HotelRoomHolder hotelOb = new HotelRoomHolder();
    private static Scanner sc = new Scanner(System.in);
    private static RoomManager roomManager = new RoomManager(hotelOb, sc);

    public static void main(String[] args) {
        // Carregar dados na inicialização
        loadHotelData();

        int choice;
        int roomTypeChoice;
        int roomNumber;

        do {
            System.out.println("\n--- HOTEL MANAGEMENT SYSTEM ---");
            System.out.println("1. Display Room Features");
            System.out.println("2. Book Room");
            System.out.println("3. Check Room Availability");
            System.out.println("4. Order Food");
            System.out.println("5. Checkout Room");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consumir nova linha

                switch (choice) {
                    case 1: // Exibir Características do Quarto
                        System.out.println("\nSelect Room Type:");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type choice: ");
                        roomTypeChoice = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha
                        roomManager.displayRoomFeatures(RoomType.fromTypeNumber(roomTypeChoice));
                        break;

                    case 2: // Reservar Quarto
                        System.out.println("\nSelect Room Type to Book:");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type choice: ");
                        roomTypeChoice = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha
                        roomManager.bookRoom(RoomType.fromTypeNumber(roomTypeChoice));
                        break;

                    case 3: // Verificar Disponibilidade do Quarto
                        System.out.println("\nSelect Room Type to Check Availability:");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type choice: ");
                        roomTypeChoice = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha
                        roomManager.checkRoomAvailability(RoomType.fromTypeNumber(roomTypeChoice));
                        break;

                    case 4: // Pedir Comida
                        System.out.println("\nSelect Room Type for Food Order:");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type choice: ");
                        roomTypeChoice = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha

                        RoomType selectedFoodRoomType = RoomType.fromTypeNumber(roomTypeChoice);
                        System.out.print("Enter room number (relative to its type's range, e.g., 1 for Luxury Double Room 1): ");
                        roomNumber = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha
                        roomNumber = convertToZeroIndexedRoomNumber(roomNumber, selectedFoodRoomType);
                        roomManager.orderFood(roomNumber, selectedFoodRoomType);
                        break;

                    case 5: // Checkout do Quarto
                        System.out.println("\nSelect Room Type to Checkout:");
                        System.out.println("1. Luxury Double Room");
                        System.out.println("2. Deluxe Double Room");
                        System.out.println("3. Luxury Single Room");
                        System.out.println("4. Deluxe Single Room");
                        System.out.print("Enter room type choice: ");
                        roomTypeChoice = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha

                        RoomType selectedCheckoutRoomType = RoomType.fromTypeNumber(roomTypeChoice);
                        System.out.print("Enter room number (relative to its type's range): ");
                        roomNumber = sc.nextInt();
                        sc.nextLine(); // Consumir nova linha
                        roomNumber = convertToZeroIndexedRoomNumber(roomNumber, selectedCheckoutRoomType);
                        roomManager.deallocateRoom(roomNumber, selectedCheckoutRoomType);
                        break;

                    case 6: // Sair
                        System.out.println("Exiting Hotel Management System. Saving data...");
                        saveHotelData();
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Consumir a entrada inválida
                choice = 0; // Definir escolha como 0 para continuar o loop
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                choice = 0; // Definir escolha como 0 para continuar o loop
            }
        } while (choice != 6);

        sc.close();
    }

    /**
     * Converte um número de quarto amigável ao usuário (por exemplo, 1, 11, 31, 41) para um índice de array (baseado em zero).
     *
     * @param userRoomNumber O número do quarto inserido pelo usuário.
     * @param roomType       O tipo do quarto.
     * @return O índice do array (baseado em zero).
     */
    private static int convertToZeroIndexedRoomNumber(int userRoomNumber, RoomType roomType) {
        switch (roomType) {
            case LUXURY_DOUBLE:
                return userRoomNumber - 1;
            case DELUXE_DOUBLE:
                return userRoomNumber - 11;
            case LUXURY_SINGLE:
                return userRoomNumber - 31;
            case DELUXE_SINGLE:
                return userRoomNumber - 41;
            default:
                throw new IllegalArgumentException("Invalid room type for conversion.");
        }
    }

    /**
     * Salva os dados atuais do hotel em um arquivo.
     */
    private static void saveHotelData() {
        // Usa um thread separado para a escrita, como no código original
        Thread t = new Thread(new DataWriter(hotelOb));
        t.start();
        try {
            t.join(); // Espera o thread de escrita terminar para garantir que os dados sejam salvos antes de sair
        } catch (InterruptedException e) {
            System.err.println("Data saving thread interrupted: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados do hotel de um arquivo na inicialização.
     */
    private static void loadHotelData() {
        try (FileInputStream fin = new FileInputStream("backup");
             ObjectInputStream ois = new ObjectInputStream(fin)) {
            hotelOb = (HotelRoomHolder) ois.readObject();
            System.out.println("Hotel data loaded successfully from backup.");
        } catch (java.io.FileNotFoundException e) {
            System.out.println("No backup file found. Starting with empty hotel data.");
        } catch (Exception e) {
            System.err.println("Error loading hotel data: " + e.getMessage());
        }
    }
}

/**
 * Classe Runnable para escrever dados do hotel em um arquivo em um thread separado.
 */
class DataWriter implements Runnable {
    private HotelRoomHolder hotelData;

    public DataWriter(HotelRoomHolder hotelData) {
        this.hotelData = hotelData;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(hotelData);
            System.out.println("Data backup completed.");
        } catch (Exception e) {
            System.err.println("Error in data writing thread: " + e.getMessage());
        }
    }
}

// A classe Main agora simplesmente chama o método main da classe Hotel.
public class Main {
    public static void main(String[] args) {
        Hotel.main(args);
    }
}