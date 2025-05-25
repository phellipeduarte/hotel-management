import java.io.*;
import java.util.*;

// --- CONSTANTES ---
final class Constants {
    static final int LUXURY_DOUBLE = 1;
    static final int DELUXE_DOUBLE = 2;
    static final int LUXURY_SINGLE = 3;
    static final int DELUXE_SINGLE = 4;

    static final Map<Integer, Integer> ROOM_BASE_PRICE = Map.of(
            LUXURY_DOUBLE, 4000,
            DELUXE_DOUBLE, 3000,
            LUXURY_SINGLE, 2200,
            DELUXE_SINGLE, 1200
    );

    static final Map<Integer, String> ROOM_TYPE_NAMES = Map.of(
            LUXURY_DOUBLE, "Luxury Double Room",
            DELUXE_DOUBLE, "Deluxe Double Room",
            LUXURY_SINGLE, "Luxury Single Room",
            DELUXE_SINGLE, "Deluxe Single Room"
    );

    static final Map<Integer, String> MENU_ITEMS = Map.of(
            1, "Sandwich",
            2, "Pasta",
            3, "Noodles",
            4, "Coke"
    );

    static final Map<Integer, Integer> MENU_PRICES = Map.of(
            1, 50,
            2, 60,
            3, 70,
            4, 30
    );
}

// --- ENTIDADES ---

class Food implements Serializable {
    private final int itemNo;
    private final int quantity;
    private final int price;

    public Food(int itemNo, int quantity) {
        if (!Constants.MENU_PRICES.containsKey(itemNo))
            throw new IllegalArgumentException("Invalid menu item number");

        this.itemNo = itemNo;
        this.quantity = quantity;
        this.price = quantity * Constants.MENU_PRICES.get(itemNo);
    }

    public int getItemNo() { return itemNo; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
}

class Occupant implements Serializable {
    private final String name;
    private final String contact;
    private final String gender;

    public Occupant(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getGender() { return gender; }
}

abstract class Room implements Serializable {
    protected final List<Food> foodOrders = new ArrayList<>();
    public abstract void addOccupants(Scanner sc);
    public abstract String getOccupantsDescription();
    public List<Food> getFoodOrders() { return foodOrders; }
}

class SingleRoom extends Room {
    private Occupant occupant;

    public SingleRoom() {}

    public SingleRoom(Occupant occupant) {
        this.occupant = occupant;
    }

    @Override
    public void addOccupants(Scanner sc) {
        System.out.print("Enter customer name: ");
        String name = sc.next();
        System.out.print("Enter contact number: ");
        String contact = sc.next();
        System.out.print("Enter gender: ");
        String gender = sc.next();
        occupant = new Occupant(name, contact, gender);
    }

    @Override
    public String getOccupantsDescription() {
        if (occupant == null) return "No occupant";
        return occupant.getName();
    }
}

class DoubleRoom extends Room {
    private Occupant occupant1;
    private Occupant occupant2;

    public DoubleRoom() {}

    public DoubleRoom(Occupant occupant1, Occupant occupant2) {
        this.occupant1 = occupant1;
        this.occupant2 = occupant2;
    }

    @Override
    public void addOccupants(Scanner sc) {
        System.out.print("Enter first customer name: ");
        String name1 = sc.next();
        System.out.print("Enter contact number: ");
        String contact1 = sc.next();
        System.out.print("Enter gender: ");
        String gender1 = sc.next();

        System.out.print("Enter second customer name: ");
        String name2 = sc.next();
        System.out.print("Enter contact number: ");
        String contact2 = sc.next();
        System.out.print("Enter gender: ");
        String gender2 = sc.next();

        occupant1 = new Occupant(name1, contact1, gender1);
        occupant2 = new Occupant(name2, contact2, gender2);
    }

    @Override
    public String getOccupantsDescription() {
        String first = occupant1 == null ? "No occupant" : occupant1.getName();
        String second = occupant2 == null ? "No occupant" : occupant2.getName();
        return first + " & " + second;
    }
}

// --- EXCEÇÃO ---
class NotAvailableException extends Exception {
    public NotAvailableException() {
        super("Not Available!");
    }
}

// --- HOLDER ---
class HotelHolder implements Serializable {
    final DoubleRoom[] luxuryDoubleRooms = new DoubleRoom[10];
    final DoubleRoom[] deluxeDoubleRooms = new DoubleRoom[20];
    final SingleRoom[] luxurySingleRooms = new SingleRoom[10];
    final SingleRoom[] deluxeSingleRooms = new SingleRoom[20];
}

class Write implements Runnable {
    private HotelHolder hotelHolder;

    public Write(HotelHolder hotelHolder) {
        this.hotelHolder = hotelHolder;
    }

    @Override
    public void run() {
        // código para salvar o hotelHolder no arquivo "backup"
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("backup"))) {
            oos.writeObject(hotelHolder);
            System.out.println("Dados salvos com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados.");
            e.printStackTrace();
        }
    }
}

// --- SERVIÇO PRINCIPAL ---
class Hotel {
    private HotelHolder hotelHolder = new HotelHolder();
    private final Scanner sc;

    public Hotel(Scanner sc, HotelHolder holder) {
        this.sc = sc;
        this.hotelHolder = holder;
    }

    // Helper para acessar arrays por tipo
    private Room[] getRoomArray(int roomType) {
        return switch (roomType) {
            case Constants.LUXURY_DOUBLE -> hotelHolder.luxuryDoubleRooms;
            case Constants.DELUXE_DOUBLE -> hotelHolder.deluxeDoubleRooms;
            case Constants.LUXURY_SINGLE -> hotelHolder.luxurySingleRooms;
            case Constants.DELUXE_SINGLE -> hotelHolder.deluxeSingleRooms;
            default -> throw new IllegalArgumentException("Invalid room type");
        };
    }

    // Helper para índice exibido para usuário
    private int roomDisplayNumber(int roomType, int index) {
        return switch (roomType) {
            case Constants.LUXURY_DOUBLE -> index + 1;
            case Constants.DELUXE_DOUBLE -> index + 11;
            case Constants.LUXURY_SINGLE -> index + 31;
            case Constants.DELUXE_SINGLE -> index + 41;
            default -> index + 1;
        };
    }

    // Helper para converter número do usuário para índice interno
    private int roomIndexFromDisplayNumber(int roomType, int displayNumber) {
        return switch (roomType) {
            case Constants.LUXURY_DOUBLE -> displayNumber - 1;
            case Constants.DELUXE_DOUBLE -> displayNumber - 11;
            case Constants.LUXURY_SINGLE -> displayNumber - 31;
            case Constants.DELUXE_SINGLE -> displayNumber - 41;
            default -> displayNumber - 1;
        };
    }

    public void bookRoom(int roomType) {
        Room[] rooms = getRoomArray(roomType);

        System.out.println("\nChoose room number from available: ");
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print(roomDisplayNumber(roomType, i) + ", ");
            }
        }

        System.out.print("\nEnter room number: ");
        try {
            int displayNumber = sc.nextInt();
            int index = roomIndexFromDisplayNumber(roomType, displayNumber);

            if (index < 0 || index >= rooms.length)
                throw new NotAvailableException();

            if (rooms[index] != null)
                throw new NotAvailableException();

            Room room = createRoomByType(roomType);
            room.addOccupants(sc);
            rooms[index] = room;
            System.out.println("Room Booked");
        } catch (NotAvailableException e) {
            System.out.println("Room Not Available");
        } catch (InputMismatchException | IllegalArgumentException e) {
            System.out.println("Invalid input");
            sc.nextLine(); // Clear invalid input
        }
    }

    private Room createRoomByType(int roomType) {
        return switch (roomType) {
            case Constants.LUXURY_DOUBLE, Constants.DELUXE_DOUBLE -> new DoubleRoom();
            case Constants.LUXURY_SINGLE, Constants.DELUXE_SINGLE -> new SingleRoom();
            default -> throw new IllegalArgumentException("Invalid room type");
        };
    }

    public void showFeatures(int roomType) {
        switch (roomType) {
            case Constants.LUXURY_DOUBLE -> System.out.println(
                    "Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:4000");
            case Constants.DELUXE_DOUBLE -> System.out.println(
                    "Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:3000");
            case Constants.LUXURY_SINGLE -> System.out.println(
                    "Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:2200");
            case Constants.DELUXE_SINGLE -> System.out.println(
                    "Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:1200");
            default -> System.out.println("Enter valid option");
        }
    }

    public void showAvailability(int roomType) {
        Room[] rooms = getRoomArray(roomType);
        long count = Arrays.stream(rooms).filter(Objects::isNull).count();
        System.out.println("Number of rooms available : " + count);
    }

    public void orderFood(int roomNumber, int roomType) {
        Room[] rooms = getRoomArray(roomType);
        int index = roomIndexFromDisplayNumber(roomType, roomNumber);
        try {
            Room room = rooms[index];
            if (room == null) {
                System.out.println("Room not booked");
                return;
            }

            System.out.println("\n==========\n   Menu:  \n==========");
            Constants.MENU_ITEMS.forEach((k, v) -> System.out.printf("%d.%s\tRs.%d\n", k, v, Constants.MENU_PRICES.get(k)));

            char wish;
            do {
                int itemNo = sc.nextInt();
                System.out.print("Quantity- ");
                int quantity = sc.nextInt();
                room.getFoodOrders().add(new Food(itemNo, quantity));
                System.out.println("Do you want to order anything else ? (y/n)");
                wish = sc.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid room number");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            sc.nextLine();
        }
    }

    public void generateBill(int roomNumber, int roomType) {
        Room[] rooms = getRoomArray(roomType);
        int index = roomIndexFromDisplayNumber(roomType, roomNumber);
        try {
            Room room = rooms[index];
            if (room == null) {
                System.out.println("Room not booked");
                return;
            }
            double amount = Constants.ROOM_BASE_PRICE.get(roomType);
            System.out.println("\n*******");
            System.out.println(" Bill:-");
            System.out.println("*******");
            System.out.println("Room Charge - " + (int) amount);
            System.out.println("\n===============");
            System.out.println("Food Charges:- ");
            System.out.println("===============");
            System.out.println("Item   Quantity    Price");
            System.out.println("-------------------------");

            for (Food food : room.getFoodOrders()) {
                amount += food.getPrice();
                System.out.printf("%-10s%-10d%-10d%n",
                        Constants.MENU_ITEMS.get(food.getItemNo()), food.getQuantity(), food.getPrice());
            }
            System.out.println("\nTotal Amount- " + amount);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid room number");
        }
    }

    public void deallocateRoom(int roomNumber, int roomType) {
        Room[] rooms = getRoomArray(roomType);
        int index = roomIndexFromDisplayNumber(roomType, roomNumber);
        try {
            Room room = rooms[index];
            if (room == null) {
                System.out.println("Empty Already");
                return;
            }

            System.out.println("Room used by " + room.getOccupantsDescription());
            System.out.println("Do you want to checkout ?(y/n)");
            char wish = sc.next().charAt(0);
            if (wish == 'y' || wish == 'Y') {
                rooms[index] = null;
                System.out.println("Room Deallocated");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid room number");
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hotel.dat"))) {
            oos.writeObject(hotelHolder);
            System.out.println("Data saved");
        } catch (IOException e) {
            System.out.println("Failed to save data");
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hotel.dat"))) {
            HotelHolder loaded = (HotelHolder) ois.readObject();
            // copiar dados carregados para o hotelHolder atual
            System.arraycopy(loaded.luxuryDoubleRooms, 0, hotelHolder.luxuryDoubleRooms, 0, loaded.luxuryDoubleRooms.length);
            System.arraycopy(loaded.deluxeDoubleRooms, 0, hotelHolder.deluxeDoubleRooms, 0, loaded.deluxeDoubleRooms.length);
            System.arraycopy(loaded.luxurySingleRooms, 0, hotelHolder.luxurySingleRooms, 0, loaded.luxurySingleRooms.length);
            System.arraycopy(loaded.deluxeSingleRooms, 0, hotelHolder.deluxeSingleRooms, 0, loaded.deluxeSingleRooms.length);
            System.out.println("Data loaded");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        HotelHolder holder = new HotelHolder();
        File backupFile = new File("backup");

        // Carregar dados salvos, se existir
        if (backupFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
                holder = (HotelHolder) ois.readObject();
                System.out.println("Dados carregados do backup.");
            } catch (Exception e) {
                System.out.println("Falha ao carregar dados do backup.");
            }
        }

        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel(sc, holder);
        // Injeta o holder carregado na instância do hotel (supondo método para isso)
        // Se quiser, pode modificar Hotel para receber holder por parâmetro
        // Vou modificar Hotel para aceitar holder via construtor:
        // hotel = new Hotel(sc, holder);

        // Mas no código refatorado acima o Hotel instancia seu próprio holder,
        // então vamos mudar Hotel para aceitar Holder no construtor:
        // Para agora, vou alterar a classe Hotel para ter isso:
        // public Hotel(Scanner sc, HotelHolder holder) { this.sc = sc; this.hotelHolder = holder; }

        // Então, ajustando isso, supondo que o construtor modificado existe:
        hotel = new Hotel(sc, holder);

        int choice, subChoice;
        char wish;

        menuLoop:
        do {
            System.out.println("\nEnter your choice :\n" +
                    "1.Display room details\n" +
                    "2.Display room availability\n" +
                    "3.Book\n" +
                    "4.Order food\n" +
                    "5.Checkout\n" +
                    "6.Exit\n");

            try {
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                    case 2:
                    case 3:
                        System.out.println("\nChoose room type :\n" +
                                "1.Luxury Double Room\n" +
                                "2.Deluxe Double Room\n" +
                                "3.Luxury Single Room\n" +
                                "4.Deluxe Single Room\n");
                        subChoice = sc.nextInt();
                        if (subChoice < 1 || subChoice > 4) {
                            System.out.println("Invalid room type");
                            break;
                        }
                        switch (choice) {
                            case 1 -> hotel.showFeatures(subChoice);
                            case 2 -> hotel.showAvailability(subChoice);
                            case 3 -> hotel.bookRoom(subChoice);
                        }
                        break;

                    case 4:
                        System.out.print("Room Number - ");
                        int roomNum = sc.nextInt();
                        int roomType = inferRoomTypeFromRoomNumber(roomNum);
                        if (roomType == -1) {
                            System.out.println("Room doesn't exist");
                        } else {
                            int index = convertRoomNumberToIndex(roomNum, roomType);
                            hotel.orderFood(roomNum, roomType);
                        }
                        break;

                    case 5:
                        System.out.print("Room Number -");
                        int ch2 = sc.nextInt();
                        if(ch2 > 60)
                            System.out.println("Room doesn't exist");
                        else if(ch2 > 40) {
                            // Aqui você pode chamar o bill antes de desalocar
                            hotel.generateBill(ch2 - 41, 4);
                            hotel.deallocateRoom(ch2 - 41, 4);
                        }
                        else if(ch2 > 30) {
                            hotel.generateBill(ch2 - 31, 3);
                            hotel.deallocateRoom(ch2 - 31, 3);
                        }
                        else if(ch2 > 10) {
                            hotel.generateBill(ch2 - 11, 2);
                            hotel.deallocateRoom(ch2 - 11, 2);
                        }
                        else if(ch2 > 0) {
                            hotel.generateBill(ch2 - 1, 1);
                            hotel.deallocateRoom(ch2 - 1, 1);
                        }
                        else
                            System.out.println("Room doesn't exist");
                        break;

                    case 6:
                        break menuLoop;

                    default:
                        System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Not a valid input");
                sc.nextLine(); // Limpar entrada inválida
            }

            System.out.println("\nContinue : (y/n)");
            wish = sc.next().charAt(0);
            while (!(wish == 'y' || wish == 'Y' || wish == 'n' || wish == 'N')) {
                System.out.println("Invalid Option");
                System.out.println("\nContinue : (y/n)");
                wish = sc.next().charAt(0);
            }

        } while (wish == 'y' || wish == 'Y');

        // Salvar dados em arquivo usando thread
        Thread t = new Thread(new Write(holder));
        t.start();

        sc.close();
    }

    // Auxiliares para determinar tipo e índice de quarto pelo número informado

    private static int inferRoomTypeFromRoomNumber(int roomNumber) {
        if (roomNumber > 0 && roomNumber <= 10)
            return Constants.LUXURY_DOUBLE;
        else if (roomNumber > 10 && roomNumber <= 30)
            return Constants.DELUXE_DOUBLE;
        else if (roomNumber > 30 && roomNumber <= 40)
            return Constants.LUXURY_SINGLE;
        else if (roomNumber > 40 && roomNumber <= 60)
            return Constants.DELUXE_SINGLE;
        else
            return -1;
    }

    private static int convertRoomNumberToIndex(int roomNumber, int roomType) {
        return switch (roomType) {
            case Constants.LUXURY_DOUBLE -> roomNumber - 1;
            case Constants.DELUXE_DOUBLE -> roomNumber - 11;
            case Constants.LUXURY_SINGLE -> roomNumber - 31;
            case Constants.DELUXE_SINGLE -> roomNumber - 41;
            default -> -1;
        };
    }
}

