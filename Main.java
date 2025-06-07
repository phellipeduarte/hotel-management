import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// --- 1. Constantes e Enums ---
final class HotelConstants {
    public static final int LUXURY_DOUBLE_ROOM_PRICE = 4000;
    public static final int DELUXE_DOUBLE_ROOM_PRICE = 3000;
    public static final int LUXURY_SINGLE_ROOM_PRICE = 2200;
    public static final int DELUXE_SINGLE_ROOM_PRICE = 1200;

    public static final int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    public static final int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    public static final int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    public static final String FILE_BACKUP = "backup";

    private HotelConstants() {
        // Restringe a instanciação
    }
}

enum FoodItem {
    SANDWICH(1, "Sandwich", 50),
    PASTA(2, "Pasta", 60),
    NOODLES(3, "Noodles", 70),
    COKE(4, "Coke", 30);

    private final int itemNo;
    private final String name;
    private final float price;

    FoodItem(int itemNo, String name, float price) {
        this.itemNo = itemNo;
        this.name = name;
        this.price = price;
    }

    public int getItemNo() {
        return itemNo;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public static FoodItem fromItemNo(int itemNo) {
        for (FoodItem item : FoodItem.values()) {
            if (item.getItemNo() == itemNo) {
                return item;
            }
        }
        throw new IllegalArgumentException("Número de item de comida inválido: " + itemNo);
    }
}

enum RoomType {
    LUXURY_DOUBLE(1, "Luxury Double Room"),
    DELUXE_DOUBLE(2, "Deluxe Double Room"),
    LUXURY_SINGLE(3, "Luxury Single Room"),
    DELUXE_SINGLE(4, "Deluxe Single Room");

    private final int typeCode;
    private final String description;

    RoomType(int typeCode, String description) {
        this.typeCode = typeCode;
        this.description = description;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getDescription() {
        return description;
    }

    public static RoomType fromTypeCode(int typeCode) {
        for (RoomType type : RoomType.values()) {
            if (type.getTypeCode() == typeCode) {
                return type;
            }
        }
        throw new IllegalArgumentException("Código de tipo de quarto inválido: " + typeCode);
    }
}

// --- 2. Food Class Improvements ---
class Food implements Serializable {
    private static final long serialVersionUID = 1L;
    private FoodItem item;
    private int quantity;
    private float price;

    public Food(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.price = item.getPrice() * quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }
}

// --- 3. Room Hierarchy e Customer Class ---
class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public String getContact() {
        return contact;
    }

    public String getGender() {
        return gender;
    }
}

abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    protected List<Food> food;
    protected boolean isBooked;
    protected int price;

    public Room() {
        this.food = new ArrayList<>();
        this.isBooked = false;
    }

    public abstract String getRoomDetails();
    public abstract int getRoomPrice();
    public abstract void addFood(Food foodItem);

    public boolean isBooked() {
        return isBooked;
    }

    public void book() {
        this.isBooked = true;
    }

    public void checkout() {
        this.isBooked = false;
        this.food.clear();
    }

    public List<Food> getFood() {
        return food;
    }
}

class SingleRoom extends Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private Customer primaryGuest;

    public SingleRoom() {
        super();
    }

    public SingleRoom(Customer primaryGuest) {
        this();
        this.primaryGuest = primaryGuest;
        this.isBooked = true;
    }

    public void setPrimaryGuest(Customer primaryGuest) {
        this.primaryGuest = primaryGuest;
        this.isBooked = true;
    }

    public Customer getPrimaryGuest() {
        return primaryGuest;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de solteiro : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia: " + getRoomPrice();
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.LUXURY_SINGLE_ROOM_PRICE; // Preço base, será sobrescrito por tipos específicos
    }

    @Override
    public void addFood(Food foodItem) {
        this.food.add(foodItem);
    }
}

class LuxurySingleRoom extends SingleRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    public LuxurySingleRoom() {
        super();
        this.price = HotelConstants.LUXURY_SINGLE_ROOM_PRICE;
    }

    public LuxurySingleRoom(Customer primaryGuest) {
        super(primaryGuest);
        this.price = HotelConstants.LUXURY_SINGLE_ROOM_PRICE;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de solteiro : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia:" + HotelConstants.LUXURY_SINGLE_ROOM_PRICE;
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.LUXURY_SINGLE_ROOM_PRICE;
    }
}

class DeluxeSingleRoom extends SingleRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    public DeluxeSingleRoom() {
        super();
        this.price = HotelConstants.DELUXE_SINGLE_ROOM_PRICE;
    }

    public DeluxeSingleRoom(Customer primaryGuest) {
        super(primaryGuest);
        this.price = HotelConstants.DELUXE_SINGLE_ROOM_PRICE;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de solteiro : 1\nAC : Não\nCafé da manhã grátis : Sim\nPreço por dia:" + HotelConstants.DELUXE_SINGLE_ROOM_PRICE;
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.DELUXE_SINGLE_ROOM_PRICE;
    }
}

class DoubleRoom extends Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private Customer primaryGuest;
    private Customer secondGuest;

    public DoubleRoom() {
        super();
    }

    public DoubleRoom(Customer primaryGuest, Customer secondGuest) {
        this();
        this.primaryGuest = primaryGuest;
        this.secondGuest = secondGuest;
        this.isBooked = true;
    }

    public void setGuests(Customer primaryGuest, Customer secondGuest) {
        this.primaryGuest = primaryGuest;
        this.secondGuest = secondGuest;
        this.isBooked = true;
    }

    public Customer getPrimaryGuest() {
        return primaryGuest;
    }

    public Customer getSecondGuest() {
        return secondGuest;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de casal : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia: " + getRoomPrice();
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.LUXURY_DOUBLE_ROOM_PRICE; // Preço base, será sobrescrito por tipos específicos
    }

    @Override
    public void addFood(Food foodItem) {
        this.food.add(foodItem);
    }
}

class LuxuryDoubleRoom extends DoubleRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    public LuxuryDoubleRoom() {
        super();
        this.price = HotelConstants.LUXURY_DOUBLE_ROOM_PRICE;
    }

    public LuxuryDoubleRoom(Customer primaryGuest, Customer secondGuest) {
        super(primaryGuest, secondGuest);
        this.price = HotelConstants.LUXURY_DOUBLE_ROOM_PRICE;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de casal : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia:" + HotelConstants.LUXURY_DOUBLE_ROOM_PRICE;
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.LUXURY_DOUBLE_ROOM_PRICE;
    }
}

class DeluxeDoubleRoom extends DoubleRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    public DeluxeDoubleRoom() {
        super();
        this.price = HotelConstants.DELUXE_DOUBLE_ROOM_PRICE;
    }

    public DeluxeDoubleRoom(Customer primaryGuest, Customer secondGuest) {
        super(primaryGuest, secondGuest);
        this.price = HotelConstants.DELUXE_DOUBLE_ROOM_PRICE;
    }

    @Override
    public String getRoomDetails() {
        return "Número de camas de casal : 1\nAC : Não\nCafé da manhã grátis : Sim\nPreço por dia:" + HotelConstants.DELUXE_DOUBLE_ROOM_PRICE;
    }

    @Override
    public int getRoomPrice() {
        return HotelConstants.DELUXE_DOUBLE_ROOM_PRICE;
    }
}

// --- 4. NotAvailable Exception ---
class NotAvailableException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotAvailableException() {
        super("Não disponível!");
    }

    public NotAvailableException(String message) {
        super(message);
    }
}

// --- 5. HotelRoomRegistry (formerly holder) ---
class HotelRoomRegistry implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<LuxuryDoubleRoom> luxuryDoubleRooms;
    private List<DeluxeDoubleRoom> deluxeDoubleRooms;
    private List<LuxurySingleRoom> luxurySingleRooms;
    private List<DeluxeSingleRoom> deluxeSingleRooms;

    public HotelRoomRegistry() {
        luxuryDoubleRooms = new ArrayList<>(HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY);
        for (int i = 0; i < HotelConstants.LUXURY_DOUBLE_ROOM_CAPACITY; i++) {
            luxuryDoubleRooms.add(null);
        }

        deluxeDoubleRooms = new ArrayList<>(HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY);
        for (int i = 0; i < HotelConstants.DELUXE_DOUBLE_ROOM_CAPACITY; i++) {
            deluxeDoubleRooms.add(null);
        }

        luxurySingleRooms = new ArrayList<>(HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY);
        for (int i = 0; i < HotelConstants.LUXURY_SINGLE_ROOM_CAPACITY; i++) {
            luxurySingleRooms.add(null);
        }

        deluxeSingleRooms = new ArrayList<>(HotelConstants.DELUXE_SINGLE_ROOM_CAPACITY);
        for (int i = 0; i < HotelConstants.DELUXE_SINGLE_ROOM_CAPACITY; i++) {
            deluxeSingleRooms.add(null);
        }
    }

    public List<LuxuryDoubleRoom> getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public List<DeluxeDoubleRoom> getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public List<LuxurySingleRoom> getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public List<DeluxeSingleRoom> getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }

    public Room getRoom(RoomType roomType, int index) {
        switch (roomType) {
            case LUXURY_DOUBLE:
                return luxuryDoubleRooms.get(index);
            case DELUXE_DOUBLE:
                return deluxeDoubleRooms.get(index);
            case LUXURY_SINGLE:
                return luxurySingleRooms.get(index);
            case DELUXE_SINGLE:
                return deluxeSingleRooms.get(index);
            default:
                throw new IllegalArgumentException("Tipo de quarto inválido: " + roomType);
        }
    }

    public void setRoom(RoomType roomType, int index, Room room) {
        switch (roomType) {
            case LUXURY_DOUBLE:
                luxuryDoubleRooms.set(index, (LuxuryDoubleRoom) room);
                break;
            case DELUXE_DOUBLE:
                deluxeDoubleRooms.set(index, (DeluxeDoubleRoom) room);
                break;
            case LUXURY_SINGLE:
                luxurySingleRooms.set(index, (LuxurySingleRoom) room);
                break;
            case DELUXE_SINGLE:
                deluxeSingleRooms.set(index, (DeluxeSingleRoom) room);
                break;
            default:
                throw new IllegalArgumentException("Tipo de quarto inválido: " + roomType);
        }
    }
}

// --- 6. Hotel Class Refinements ---
class Hotel {
    private HotelRoomRegistry roomRegistry;
    private Scanner scanner;

    public Hotel(HotelRoomRegistry roomRegistry, Scanner scanner) {
        this.roomRegistry = roomRegistry;
        this.scanner = scanner;
    }

    private Customer getCustomerDetails() {
        System.out.print("\nDigite o nome do cliente: ");
        String name = scanner.next();
        System.out.print("Digite o número de contato: ");
        String contact = scanner.next();
        System.out.print("Digite o gênero: ");
        String gender = scanner.next();
        return new Customer(name, contact, gender);
    }

    private void bookRoomInternal(RoomType roomType, int roomIndex) throws NotAvailableException {
        Room room = roomRegistry.getRoom(roomType, roomIndex);

        if (room != null && room.isBooked()) {
            throw new NotAvailableException("Quarto já reservado!");
        }

        Customer primaryGuest = getCustomerDetails();
        if (roomType == RoomType.LUXURY_DOUBLE || roomType == RoomType.DELUXE_DOUBLE) {
            System.out.print("Digite o nome do segundo cliente: ");
            String name2 = scanner.next();
            System.out.print("Digite o número de contato: ");
            String contact2 = scanner.next();
            System.out.print("Digite o gênero: ");
            String gender2 = scanner.next();
            Customer secondGuest = new Customer(name2, contact2, gender2);

            if (roomType == RoomType.LUXURY_DOUBLE) {
                roomRegistry.setRoom(roomType, roomIndex, new LuxuryDoubleRoom(primaryGuest, secondGuest));
            } else {
                roomRegistry.setRoom(roomType, roomIndex, new DeluxeDoubleRoom(primaryGuest, secondGuest));
            }
        } else {
            if (roomType == RoomType.LUXURY_SINGLE) {
                roomRegistry.setRoom(roomType, roomIndex, new LuxurySingleRoom(primaryGuest));
            } else {
                roomRegistry.setRoom(roomType, roomIndex, new DeluxeSingleRoom(primaryGuest));
            }
        }
        System.out.println("Quarto reservado com sucesso!");
    }

    public void bookRoom(RoomType roomType) {
        System.out.println("\nEscolha o número do quarto entre os quartos disponíveis:");
        List<? extends Room> rooms;
        int offset;

        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = roomRegistry.getLuxuryDoubleRooms();
                offset = 1; // Números de quartos 1-10
                break;
            case DELUXE_DOUBLE:
                rooms = roomRegistry.getDeluxeDoubleRooms();
                offset = 11; // Números de quartos 11-30
                break;
            case LUXURY_SINGLE:
                rooms = roomRegistry.getLuxurySingleRooms();
                offset = 31; // Números de quartos 31-40
                break;
            case DELUXE_SINGLE:
                rooms = roomRegistry.getDeluxeSingleRooms();
                offset = 41; // Números de quartos 41-60
                break;
            default:
                System.out.println("Tipo de quarto inválido.");
                return;
        }

        for (int j = 0; j < rooms.size(); j++) {
            if (rooms.get(j) == null || !rooms.get(j).isBooked()) {
                System.out.print((j + offset) + ", ");
            }
        }
        System.out.print("\nDigite o número do quarto: ");
        try {
            int roomNumber = scanner.nextInt();
            int roomIndex = roomNumber - offset;

            if (roomIndex < 0 || roomIndex >= rooms.size()) {
                throw new NotAvailableException("Número de quarto inválido para o tipo selecionado.");
            }

            bookRoomInternal(roomType, roomIndex);

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
            scanner.next(); // Consumir a entrada inválida
        } catch (NotAvailableException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    public void displayFeatures(RoomType roomType) {
        switch (roomType) {
            case LUXURY_DOUBLE:
                System.out.println("Número de camas de casal : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia: " + HotelConstants.LUXURY_DOUBLE_ROOM_PRICE);
                break;
            case DELUXE_DOUBLE:
                System.out.println("Número de camas de casal : 1\nAC : Não\nCafé da manhã grátis : Sim\nPreço por dia: " + HotelConstants.DELUXE_DOUBLE_ROOM_PRICE);
                break;
            case LUXURY_SINGLE:
                System.out.println("Número de camas de solteiro : 1\nAC : Sim\nCafé da manhã grátis : Sim\nPreço por dia: " + HotelConstants.LUXURY_SINGLE_ROOM_PRICE);
                break;
            case DELUXE_SINGLE:
                System.out.println("Número de camas de solteiro : 1\nAC : Não\nCafé da manhã grátis : Sim\nPreço por dia: " + HotelConstants.DELUXE_SINGLE_ROOM_PRICE);
                break;
            default:
                System.out.println("Digite uma opção válida");
                break;
        }
    }

    public void checkAvailability(RoomType roomType) {
        int count = 0;
        List<? extends Room> rooms;

        switch (roomType) {
            case LUXURY_DOUBLE:
                rooms = roomRegistry.getLuxuryDoubleRooms();
                break;
            case DELUXE_DOUBLE:
                rooms = roomRegistry.getDeluxeDoubleRooms();
                break;
            case LUXURY_SINGLE:
                rooms = roomRegistry.getLuxurySingleRooms();
                break;
            case DELUXE_SINGLE:
                rooms = roomRegistry.getDeluxeSingleRooms();
                break;
            default:
                System.out.println("Digite uma opção válida");
                return;
        }

        for (Room room : rooms) {
            if (room == null || !room.isBooked()) {
                count++;
            }
        }
        System.out.println("Número de quartos disponíveis : " + count);
    }

    public void generateBill(int roomNumber, RoomType roomType) {
        Room room = getRoomFromNumber(roomNumber);

        if (room == null || !room.isBooked()) {
            System.out.println("Quarto não encontrado ou não reservado.");
            return;
        }

        double totalAmount = room.getRoomPrice();
        System.out.println("\n*******");
        System.out.println(" Conta:-");
        System.out.println("*******");
        System.out.println("\nCusto do Quarto - " + room.getRoomPrice());
        System.out.println("\n===============");
        System.out.println("Custos de Alimentação:- ");
        System.out.println("===============");
        System.out.println("Item        Quantidade   Preço");
        System.out.println("-------------------------");

        for (Food foodItem : room.getFood()) {
            totalAmount += foodItem.getPrice();
            String format = "%-10s%-10s%-10s%n";
            System.out.printf(format, foodItem.getItem().getName(), foodItem.getQuantity(), foodItem.getPrice());
        }
        System.out.println("\nValor Total - " + totalAmount);
    }

    public void checkoutRoom(int roomNumber) {
        RoomType roomType = getRoomTypeFromNumber(roomNumber);
        if (roomType == null) {
            System.out.println("O quarto não existe.");
            return;
        }

        int roomIndex;
        List<? extends Room> rooms;
        String roomUserName = "N/A";

        switch (roomType) {
            case LUXURY_DOUBLE:
                roomIndex = roomNumber - 1;
                rooms = roomRegistry.getLuxuryDoubleRooms();
                if (roomIndex >= 0 && roomIndex < rooms.size() && rooms.get(roomIndex) != null) {
                    DoubleRoom dr = (DoubleRoom) rooms.get(roomIndex);
                    if (dr.getPrimaryGuest() != null) roomUserName = dr.getPrimaryGuest().getName();
                }
                break;
            case DELUXE_DOUBLE:
                roomIndex = roomNumber - 11;
                rooms = roomRegistry.getDeluxeDoubleRooms();
                if (roomIndex >= 0 && roomIndex < rooms.size() && rooms.get(roomIndex) != null) {
                    DoubleRoom dr = (DoubleRoom) rooms.get(roomIndex);
                    if (dr.getPrimaryGuest() != null) roomUserName = dr.getPrimaryGuest().getName();
                }
                break;
            case LUXURY_SINGLE:
                roomIndex = roomNumber - 31;
                rooms = roomRegistry.getLuxurySingleRooms();
                if (roomIndex >= 0 && roomIndex < rooms.size() && rooms.get(roomIndex) != null) {
                    SingleRoom sr = (SingleRoom) rooms.get(roomIndex);
                    if (sr.getPrimaryGuest() != null) roomUserName = sr.getPrimaryGuest().getName();
                }
                break;
            case DELUXE_SINGLE:
                roomIndex = roomNumber - 41;
                rooms = roomRegistry.getDeluxeSingleRooms();
                if (roomIndex >= 0 && roomIndex < rooms.size() && rooms.get(roomIndex) != null) {
                    SingleRoom sr = (SingleRoom) rooms.get(roomIndex);
                    if (sr.getPrimaryGuest() != null) roomUserName = sr.getPrimaryGuest().getName();
                }
                break;
            default:
                System.out.println("Tipo de quarto inválido para checkout.");
                return;
        }

        Room room = roomRegistry.getRoom(roomType, roomIndex);
        if (room == null || !room.isBooked()) {
            System.out.println("Quarto vazio ou não reservado.");
            return;
        }

        System.out.println("Quarto usado por " + roomUserName);
        System.out.println("Deseja fazer o checkout? (s/n)");
        char wish = scanner.next().charAt(0);
        if (wish == 's' || wish == 'S') {
            generateBill(roomNumber, roomType);
            room.checkout();
            roomRegistry.setRoom(roomType, roomIndex, null);
            System.out.println("Desalocado com sucesso!");
        }
    }

    public void orderFood(int roomNumber) {
        Room room = getRoomFromNumber(roomNumber);

        if (room == null || !room.isBooked()) {
            System.out.println("\nQuarto não reservado ou não existe.");
            return;
        }

        System.out.println("\n==========\n    Menu: \n==========\n\n1.Sandwich\tRs." + FoodItem.SANDWICH.getPrice() +
                "\n2.Pasta\t\tRs." + FoodItem.PASTA.getPrice() +
                "\n3.Noodles\tRs." + FoodItem.NOODLES.getPrice() +
                "\n4.Coke\t\tRs." + FoodItem.COKE.getPrice() + "\n");
        char wish;
        do {
            try {
                System.out.print("Digite o número do item: ");
                int itemNo = scanner.nextInt();
                FoodItem foodItem = FoodItem.fromItemNo(itemNo);

                System.out.print("Quantidade: ");
                int quantity = scanner.nextInt();

                room.addFood(new Food(foodItem, quantity));
                System.out.println("Item adicionado ao pedido.");

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número para item e quantidade.");
                scanner.next();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocorreu um erro ao pedir comida: " + e.getMessage());
            }
            System.out.println("Deseja pedir mais alguma coisa? (s/n)");
            wish = scanner.next().charAt(0);
        } while (wish == 's' || wish == 'S');
    }

    private Room getRoomFromNumber(int roomNumber) {
        if (roomNumber > 60 || roomNumber <= 0) {
            return null;
        } else if (roomNumber > 40) { // Deluxe Single
            return roomRegistry.getDeluxeSingleRooms().get(roomNumber - 41);
        } else if (roomNumber > 30) { // Luxury Single
            return roomRegistry.getLuxurySingleRooms().get(roomNumber - 31);
        } else if (roomNumber > 10) { // Deluxe Double
            return roomRegistry.getDeluxeDoubleRooms().get(roomNumber - 11);
        } else { // Luxury Double
            return roomRegistry.getLuxuryDoubleRooms().get(roomNumber - 1);
        }
    }

    private RoomType getRoomTypeFromNumber(int roomNumber) {
        if (roomNumber > 60 || roomNumber <= 0) {
            return null;
        } else if (roomNumber > 40) {
            return RoomType.DELUXE_SINGLE;
        } else if (roomNumber > 30) {
            return RoomType.LUXURY_SINGLE;
        } else if (roomNumber > 10) {
            return RoomType.DELUXE_DOUBLE;
        } else {
            return RoomType.LUXURY_DOUBLE;
        }
    }
}

// --- 7. HotelDataWriter (formerly write) ---
class HotelDataWriter implements Runnable {
    private HotelRoomRegistry roomRegistry;

    public HotelDataWriter(HotelRoomRegistry roomRegistry) {
        this.roomRegistry = roomRegistry;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream(HotelConstants.FILE_BACKUP);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(roomRegistry);
            System.out.println("Dados do hotel salvos com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao escrever dados do hotel: " + e.getMessage());
        }
    }
}

// --- 8. Main Class ---
public class Main {
    public static void main(String[] args) {
        HotelRoomRegistry hotelRoomRegistry = new HotelRoomRegistry();
        Scanner scanner = new Scanner(System.in);

        try {
            File f = new File(HotelConstants.FILE_BACKUP);
            if (f.exists()) {
                try (FileInputStream fin = new FileInputStream(f);
                     ObjectInputStream ois = new ObjectInputStream(fin)) {
                    hotelRoomRegistry = (HotelRoomRegistry) ois.readObject();
                    System.out.println("Dados do hotel carregados do backup.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do hotel do backup: " + e.getMessage());
        }

        Hotel hotel = new Hotel(hotelRoomRegistry, scanner);

        int choice;
        char continueChoice;
        do {
            System.out.println("\n--- Sistema de Gerenciamento de Hotéis ---");
            System.out.println("1. Exibir detalhes do quarto");
            System.out.println("2. Exibir disponibilidade do quarto");
            System.out.println("3. Reservar um quarto");
            System.out.println("4. Pedir comida");
            System.out.println("5. Checkout");
            System.out.println("6. Sair");
            System.out.print("Digite sua escolha: ");

            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("\nEscolha o tipo de quarto:");
                        displayRoomTypeOptions();
                        int roomTypeChoice = scanner.nextInt();
                        hotel.displayFeatures(RoomType.fromTypeCode(roomTypeChoice));
                        break;
                    case 2:
                        System.out.println("\nEscolha o tipo de quarto:");
                        displayRoomTypeOptions();
                        roomTypeChoice = scanner.nextInt();
                        hotel.checkAvailability(RoomType.fromTypeCode(roomTypeChoice));
                        break;
                    case 3:
                        System.out.println("\nEscolha o tipo de quarto:");
                        displayRoomTypeOptions();
                        roomTypeChoice = scanner.nextInt();
                        hotel.bookRoom(RoomType.fromTypeCode(roomTypeChoice));
                        break;
                    case 4:
                        System.out.print("Digite o número do quarto: ");
                        int roomNumber = scanner.nextInt();
                        hotel.orderFood(roomNumber);
                        break;
                    case 5:
                        System.out.print("Digite o número do quarto: ");
                        roomNumber = scanner.nextInt();
                        hotel.checkoutRoom(roomNumber);
                        break;
                    case 6:
                        System.out.println("Saindo do aplicativo. Salvando dados...");
                        break;
                    default:
                        System.out.println("Escolha inválida. Por favor, digite um número entre 1 e 6.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.next();
                choice = 0;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                choice = 0;
            }

            if (choice != 6) {
                System.out.println("\nContinuar? (s/n)");
                continueChoice = scanner.next().charAt(0);
                while (!isValidYesNo(continueChoice)) {
                    System.out.println("Opção inválida. Por favor, digite 's' ou 'n'.");
                    System.out.println("\nContinuar? (s/n)");
                    continueChoice = scanner.next().charAt(0);
                }
            } else {
                continueChoice = 'n';
            }

        } while (continueChoice == 's' || continueChoice == 'S');

        Thread t = new Thread(new HotelDataWriter(hotelRoomRegistry));
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            System.err.println("Erro ao aguardar a thread de salvamento de dados: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        scanner.close();
        System.out.println("Aplicativo encerrado.");
    }

    private static void displayRoomTypeOptions() {
        System.out.println("1. Quarto Duplo Luxo");
        System.out.println("2. Quarto Duplo Deluxe");
        System.out.println("3. Quarto Solteiro Luxo");
        System.out.println("4. Quarto Solteiro Deluxe");
    }

    private static boolean isValidYesNo(char c) {
        return c == 's' || c == 'S' || c == 'n' || c == 'N';
    }
}