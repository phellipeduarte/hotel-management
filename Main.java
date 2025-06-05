import java.io.*;
import java.util.*;

// ================== Classe Principal ==================
public class Main {

    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        // Carregar backup se existir
        File backup = new File("backup");
        if (backup.exists()) {
            try (FileInputStream fin = new FileInputStream(backup);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                hotel.setHotelData((Holder) ois.readObject());
            } catch (Exception e) {
                System.out.println("Erro ao carregar backup: " + e.getMessage());
            }
        }

        Scanner sc = new Scanner(System.in);
        char wish;

        mainLoop:
        do {
            try {
                System.out.println("\nEscolha uma opção:\n" +
                        "1. Detalhes dos quartos\n" +
                        "2. Verificar disponibilidade\n" +
                        "3. Reservar\n" +
                        "4. Fazer pedido de comida\n" +
                        "5. Checkout\n" +
                        "6. Sair");
                int option = sc.nextInt();

                switch (option) {
                    case 1 -> hotel.showRoomFeatures(askRoomType(sc));
                    case 2 -> hotel.showAvailability(askRoomType(sc));
                    case 3 -> hotel.bookRoom(askRoomType(sc));
                    case 4 -> handleOrder(hotel, sc);
                    case 5 -> handleCheckout(hotel, sc);
                    case 6 -> {
                        break mainLoop;
                    }
                    default -> System.out.println("Opção inválida.");
                }

                System.out.println("\nDeseja continuar? (y/n)");
                wish = sc.next().charAt(0);
                if (!isValidWish(wish)) {
                    System.out.println("Opção inválida. Digite y ou n.");
                    wish = sc.next().charAt(0);
                }

            } catch (Exception e) {
                System.out.println("Entrada inválida. Tente novamente.");
                sc.nextLine();
                wish = 'y';
            }
        } while (wish == 'y' || wish == 'Y');

        // Salvar backup
        Thread backupThread = new Thread(new WriteBackup(hotel.getHotelData()));
        backupThread.start();
    }

    // Métodos auxiliares

    private static int askRoomType(Scanner sc) {
        System.out.println("\nEscolha o tipo de quarto:\n" +
                "1. Luxury Double Room\n" +
                "2. Deluxe Double Room\n" +
                "3. Luxury Single Room\n" +
                "4. Deluxe Single Room");
        return sc.nextInt();
    }

    private static boolean isValidWish(char c) {
        return c == 'y' || c == 'Y' || c == 'n' || c == 'N';
    }

    private static void handleOrder(Hotel hotel, Scanner sc) {
        System.out.print("Número do quarto: ");
        int roomNumber = sc.nextInt();
        RoomMapping mapping = mapRoomNumber(roomNumber);

        if (mapping != null) {
            hotel.orderFood(mapping.index, mapping.roomType);
        } else {
            System.out.println("Quarto não existe.");
        }
    }

    private static void handleCheckout(Hotel hotel, Scanner sc) {
        System.out.print("Número do quarto: ");
        int roomNumber = sc.nextInt();
        RoomMapping mapping = mapRoomNumber(roomNumber);

        if (mapping != null) {
            hotel.deallocate(mapping.index, mapping.roomType);
        } else {
            System.out.println("Quarto não existe.");
        }
    }

    private static RoomMapping mapRoomNumber(int roomNumber) {
        if (roomNumber > 60 || roomNumber <= 0) return null;
        if (roomNumber > 40) return new RoomMapping(roomNumber - 41, 4);
        if (roomNumber > 30) return new RoomMapping(roomNumber - 31, 3);
        if (roomNumber > 10) return new RoomMapping(roomNumber - 11, 2);
        return new RoomMapping(roomNumber - 1, 1);
    }

    private record RoomMapping(int index, int roomType) {}
}

// ================== Classe Hotel ==================
class Hotel {
    private Holder hotelData;

    public Hotel() {
        hotelData = new Holder();
    }

    public Holder getHotelData() {
        return hotelData;
    }

    public void setHotelData(Holder data) {
        this.hotelData = data;
    }

    public void showRoomFeatures(int roomType) {
        hotelData.showRoomFeatures(roomType);
    }

    public void showAvailability(int roomType) {
        hotelData.showAvailability(roomType);
    }

    public void bookRoom(int roomType) {
        hotelData.bookRoom(roomType);
    }

    public void orderFood(int index, int roomType) {
        hotelData.orderFood(index, roomType);
    }

    public void deallocate(int index, int roomType) {
        hotelData.deallocate(index, roomType);
    }
}

// ================== Classe Holder ==================
class Holder implements Serializable {
    Room luxuryDouble[] = new Room[10];
    Room deluxeDouble[] = new Room[20];
    Room luxurySingle[] = new Room[10];
    Room deluxeSingle[] = new Room[20];

    public void showRoomFeatures(int type) {
        switch (type) {
            case 1 -> System.out.println("Luxury Double Room\nCamas: 2\nAC: Sim\nCafé: Sim\nPreço: 4000");
            case 2 -> System.out.println("Deluxe Double Room\nCamas: 2\nAC: Não\nCafé: Sim\nPreço: 3000");
            case 3 -> System.out.println("Luxury Single Room\nCamas: 1\nAC: Sim\nCafé: Sim\nPreço: 2200");
            case 4 -> System.out.println("Deluxe Single Room\nCamas: 1\nAC: Não\nCafé: Sim\nPreço: 1200");
            default -> System.out.println("Tipo inválido.");
        }
    }

    public void showAvailability(int type) {
        int count = switch (type) {
            case 1 -> countAvailable(luxuryDouble);
            case 2 -> countAvailable(deluxeDouble);
            case 3 -> countAvailable(luxurySingle);
            case 4 -> countAvailable(deluxeSingle);
            default -> -1;
        };
        if (count != -1) {
            System.out.println("Quartos disponíveis: " + count);
        } else {
            System.out.println("Tipo inválido.");
        }
    }

    public void bookRoom(int type) {
        Room[] rooms = getRoomArray(type);
        if (rooms == null) {
            System.out.println("Tipo inválido.");
            return;
        }

        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                Scanner sc = new Scanner(System.in);
                System.out.print("Nome: ");
                String name = sc.nextLine();
                System.out.print("Contato: ");
                String contact = sc.nextLine();
                System.out.print("Gênero: ");
                String gender = sc.nextLine();
                rooms[i] = new Room(name, contact, gender);
                System.out.println("Reserva feita. Número do quarto: " + getRoomNumber(i, type));
                return;
            }
        }
        System.out.println("Nenhum quarto disponível.");
    }

    public void orderFood(int index, int type) {
        Room room = getRoom(index, type);
        if (room == null) {
            System.out.println("Quarto vazio.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        MenuItem.showMenu();
        int choice = sc.nextInt();
        MenuItem item = MenuItem.getItem(choice);
        if (item != null) {
            room.addCharge(item.price);
            System.out.println("Pedido: " + item.name);
        } else {
            System.out.println("Item inválido.");
        }
    }

    public void deallocate(int index, int type) {
        Room[] rooms = getRoomArray(type);
        if (rooms == null || index < 0 || index >= rooms.length || rooms[index] == null) {
            System.out.println("Quarto vazio ou inválido.");
            return;
        }

        Room room = rooms[index];
        double total = calculateRoomCharge(type) + room.getCharges();
        System.out.println("======== Checkout ========");
        System.out.println("Nome: " + room.getName());
        System.out.println("Total: R$ " + total);
        System.out.println("==========================");
        rooms[index] = null;
    }

    // Métodos auxiliares

    private Room[] getRoomArray(int type) {
        return switch (type) {
            case 1 -> luxuryDouble;
            case 2 -> deluxeDouble;
            case 3 -> luxurySingle;
            case 4 -> deluxeSingle;
            default -> null;
        };
    }

    private Room getRoom(int index, int type) {
        Room[] rooms = getRoomArray(type);
        if (rooms == null || index < 0 || index >= rooms.length) return null;
        return rooms[index];
    }

    private int getRoomNumber(int index, int type) {
        return switch (type) {
            case 1 -> index + 1;
            case 2 -> index + 11;
            case 3 -> index + 31;
            case 4 -> index + 41;
            default -> -1;
        };
    }

    private int countAvailable(Room[] rooms) {
        int count = 0;
        for (Room r : rooms) {
            if (r == null) count++;
        }
        return count;
    }

    private double calculateRoomCharge(int type) {
        return switch (type) {
            case 1 -> 4000;
            case 2 -> 3000;
            case 3 -> 2200;
            case 4 -> 1200;
            default -> 0;
        };
    }
}

// ================== Classe Room ==================
class Room implements Serializable {
    private final String name;
    private final String contact;
    private final String gender;
    private double charges;

    public Room(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.charges = 0;
    }

    public void addCharge(double amount) {
        charges += amount;
    }

    public double getCharges() {
        return charges;
    }

    public String getName() {
        return name;
    }
}

// ================== Classe MenuItem ==================
enum MenuItem {
    SANDWICH(1, "Sanduíche", 50),
    PASTA(2, "Massa", 60),
    NOODLES(3, "Yakissoba", 70),
    COKE(4, "Coca-Cola", 30);

    public final int code;
    public final String name;
    public final double price;

    MenuItem(int code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public static void showMenu() {
        System.out.println("\n====== Menu ======");
        for (MenuItem item : values()) {
            System.out.println(item.code + ". " + item.name + " - R$" + item.price);
        }
    }

    public static MenuItem getItem(int code) {
        for (MenuItem item : values()) {
            if (item.code == code) return item;
        }
        return null;
    }
}

// ================== Classe WriteBackup ==================
class WriteBackup implements Runnable {
    private final Holder data;

    public WriteBackup(Holder data) {
        this.data = data;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(data);
        } catch (Exception e) {
            System.out.println("Erro ao salvar backup: " + e.getMessage());
        }
    }
}
