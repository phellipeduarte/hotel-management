import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

// --- Constantes Significativas ---
interface HotelConstants {
    String BACKUP_FILE_NAME = "backup";
    int LUXURY_DOUBLE_ROOM_CAPACITY = 10;
    int DELUXE_DOUBLE_ROOM_CAPACITY = 20;
    int LUXURY_SINGLE_ROOM_CAPACITY = 10;
    int DELUXE_SINGLE_ROOM_CAPACITY = 20;

    double LUXURY_DOUBLE_ROOM_PRICE = 4000.0;
    double DELUXE_DOUBLE_ROOM_PRICE = 3000.0;
    double LUXURY_SINGLE_ROOM_PRICE = 2200.0;
    double DELUXE_SINGLE_ROOM_PRICE = 1200.0;

    String SANDWICH_NAME = "Sandwich";
    double SANDWICH_PRICE = 50.0;
    String PASTA_NAME = "Pasta";
    double PASTA_PRICE = 60.0;
    String NOODLES_NAME = "Noodles";
    double NOODLES_PRICE = 70.0;
    String COKE_NAME = "Coke";
    double COKE_PRICE = 30.0;
}

// --- Exceções Customizadas ---
class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

class InvalidRoomNumberException extends Exception {
    public InvalidRoomNumberException(String message) {
        super(message);
    }
}

// --- Classes de Modelo (Representação de Dados) ---

/**
 * Representa um item de comida com seu preço fixo.
 * SRP: Responsabilidade única de definir um item de comida.
 */
class MenuItem implements Serializable {
    private final int id;
    private final String name;
    private final double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return id + ". " + name + "\tRs." + price;
    }
}

/**
 * Representa um pedido de comida, contendo o item e a quantidade.
 * SRP: Responsabilidade única de representar um item de comida pedido.
 */
class FoodOrder implements Serializable {
    private final MenuItem item;
    private final int quantity;
    private final double totalPrice;

    public FoodOrder(MenuItem item, int quantity) {
        this.item = Objects.requireNonNull(item);
        this.quantity = quantity;
        this.totalPrice = item.getPrice() * quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

/**
 * Representa um hóspede do hotel.
 * SRP: Responsabilidade única de armazenar os dados de um hóspede.
 */
class Guest implements Serializable {
    private final String name;
    private final String contact;
    private final String gender;

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

/**
 * Abstração para um quarto de hotel.
 * OCP: Aberto para extensão (novos tipos de quarto), fechado para modificação.
 * LSP: Subclasses de Room (DoubleRoom, SingleRoom) podem ser usadas intercambiavelmente.
 */
abstract class Room implements Serializable {
    protected List<Guest> guests = new ArrayList<>();
    protected List<FoodOrder> foodOrders = new ArrayList<>();
    protected double basePrice;
    protected int roomNumber;

    public Room(double basePrice) {
        this.basePrice = basePrice;
    }

    public abstract int getCapacity();

    public boolean isOccupied() {
        return !guests.isEmpty();
    }

    public void addGuest(Guest guest) {
        if (guests.size() < getCapacity()) {
            guests.add(guest);
        } else {
            throw new IllegalStateException("Room is at full capacity.");
        }
    }

    public List<Guest> getGuests() {
        return new ArrayList<>(guests); // Retorna uma cópia para encapsulamento
    }

    public void addFoodOrder(FoodOrder order) {
        foodOrders.add(order);
    }

    public List<FoodOrder> getFoodOrders() {
        return new ArrayList<>(foodOrders); // Retorna uma cópia
    }

    public void clearOccupancy() {
        guests.clear();
        foodOrders.clear();
    }

    public double calculateBill() {
        double totalFoodCharges = foodOrders.stream()
                .mapToDouble(FoodOrder::getTotalPrice)
                .sum();
        return basePrice + totalFoodCharges;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}

/**
 * Implementação de um quarto individual.
 */
class SingleRoom extends Room implements HotelConstants {
    public SingleRoom(double basePrice) {
        super(basePrice);
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    @Override
    public String toString() {
        return "Number of single beds : 1\nAC : " + (basePrice == LUXURY_SINGLE_ROOM_PRICE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + basePrice;
    }
}

/**
 * Implementação de um quarto duplo.
 */
class DoubleRoom extends Room implements HotelConstants {
    public DoubleRoom(double basePrice) {
        super(basePrice);
    }

    @Override
    public int getCapacity() {
        return 2;
    }

    @Override
    public String toString() {
        return "Number of double beds : 1\nAC : " + (basePrice == LUXURY_DOUBLE_ROOM_PRICE ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + basePrice;
    }
}

// --- Gerenciamento de Quartos ---

/**
 * Responsável por armazenar e gerenciar todas as coleções de quartos.
 * SRP: Única responsabilidade de ser um contêiner para os diferentes tipos de quartos.
 */
class RoomRegistry implements Serializable, HotelConstants {
    private final Room[] luxuryDoubleRooms = new DoubleRoom[LUXURY_DOUBLE_ROOM_CAPACITY];
    private final Room[] deluxeDoubleRooms = new DoubleRoom[DELUXE_DOUBLE_ROOM_CAPACITY];
    private final Room[] luxurySingleRooms = new SingleRoom[LUXURY_SINGLE_ROOM_CAPACITY];
    private final Room[] deluxeSingleRooms = new SingleRoom[DELUXE_SINGLE_ROOM_CAPACITY];

    public RoomRegistry() {
        initializeRooms();
    }

    private void initializeRooms() {
        for (int i = 0; i < LUXURY_DOUBLE_ROOM_CAPACITY; i++) {
            luxuryDoubleRooms[i] = new DoubleRoom(LUXURY_DOUBLE_ROOM_PRICE);
            luxuryDoubleRooms[i].setRoomNumber(i + 1); // 1-10
        }
        for (int i = 0; i < DELUXE_DOUBLE_ROOM_CAPACITY; i++) {
            deluxeDoubleRooms[i] = new DoubleRoom(DELUXE_DOUBLE_ROOM_PRICE);
            deluxeDoubleRooms[i].setRoomNumber(i + 11); // 11-30
        }
        for (int i = 0; i < LUXURY_SINGLE_ROOM_CAPACITY; i++) {
            luxurySingleRooms[i] = new SingleRoom(LUXURY_SINGLE_ROOM_PRICE);
            luxurySingleRooms[i].setRoomNumber(i + 31); // 31-40
        }
        for (int i = 0; i < DELUXE_SINGLE_ROOM_CAPACITY; i++) {
            deluxeSingleRooms[i] = new SingleRoom(DELUXE_SINGLE_ROOM_PRICE);
            deluxeSingleRooms[i].setRoomNumber(i + 41); // 41-60
        }
    }

    public Room[] getLuxuryDoubleRooms() {
        return luxuryDoubleRooms;
    }

    public Room[] getDeluxeDoubleRooms() {
        return deluxeDoubleRooms;
    }

    public Room[] getLuxurySingleRooms() {
        return luxurySingleRooms;
    }

    public Room[] getDeluxeSingleRooms() {
        return deluxeSingleRooms;
    }

    /**
     * Retorna o array de quartos com base no tipo de quarto.
     * @param roomType O tipo de quarto (1-Luxury Double, 2-Deluxe Double, 3-Luxury Single, 4-Deluxe Single).
     * @return O array de quartos correspondente.
     * @throws IllegalArgumentException se o tipo de quarto for inválido.
     */
    public Room[] getRoomsByType(int roomType) {
        switch (roomType) {
            case 1: return luxuryDoubleRooms;
            case 2: return deluxeDoubleRooms;
            case 3: return luxurySingleRooms;
            case 4: return deluxeSingleRooms;
            default: throw new IllegalArgumentException("Tipo de quarto inválido: " + roomType);
        }
    }

    /**
     * Retorna um quarto específico pelo seu número e tipo.
     * @param roomNumber O número do quarto.
     * @param roomType O tipo de quarto (1-Luxury Double, 2-Deluxe Double, 3-Luxury Single, 4-Deluxe Single).
     * @return O objeto Room correspondente.
     * @throws InvalidRoomNumberException se o número do quarto for inválido para o tipo especificado.
     */
    public Room getRoom(int roomNumber, int roomType) throws InvalidRoomNumberException {
        Room[] rooms;
        int adjustedRoomNumber;

        switch (roomType) {
            case 1:
                rooms = luxuryDoubleRooms;
                adjustedRoomNumber = roomNumber - 1;
                break;
            case 2:
                rooms = deluxeDoubleRooms;
                adjustedRoomNumber = roomNumber - 11;
                break;
            case 3:
                rooms = luxurySingleRooms;
                adjustedRoomNumber = roomNumber - 31;
                break;
            case 4:
                rooms = deluxeSingleRooms;
                adjustedRoomNumber = roomNumber - 41;
                break;
            default:
                throw new InvalidRoomNumberException("Tipo de quarto inválido.");
        }

        if (adjustedRoomNumber < 0 || adjustedRoomNumber >= rooms.length) {
            throw new InvalidRoomNumberException("Número de quarto inválido para o tipo selecionado.");
        }
        return rooms[adjustedRoomNumber];
    }
}

// --- Serviço de Hotel (Lógica de Negócio) ---

/**
 * Classe de serviço que contém a lógica de negócio do hotel.
 * SRP: Responsabilidade única de gerenciar as operações do hotel.
 * DIP: Depende de RoomRegistry (abstração) e não de implementações concretas de arrays.
 */
class HotelService implements HotelConstants {
    private final RoomRegistry roomRegistry;
    private final Map<Integer, MenuItem> menuItems;

    public HotelService(RoomRegistry roomRegistry) {
        this.roomRegistry = roomRegistry;
        this.menuItems = new HashMap<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menuItems.put(1, new MenuItem(1, SANDWICH_NAME, SANDWICH_PRICE));
        menuItems.put(2, new MenuItem(2, PASTA_NAME, PASTA_PRICE));
        menuItems.put(3, new MenuItem(3, NOODLES_NAME, NOODLES_PRICE));
        menuItems.put(4, new MenuItem(4, COKE_NAME, COKE_PRICE));
    }

    public MenuItem getMenuItem(int id) {
        return menuItems.get(id);
    }

    public void displayRoomDetails(int roomType) {
        switch (roomType) {
            case 1: System.out.println(new DoubleRoom(LUXURY_DOUBLE_ROOM_PRICE)); break;
            case 2: System.out.println(new DoubleRoom(DELUXE_DOUBLE_ROOM_PRICE)); break;
            case 3: System.out.println(new SingleRoom(LUXURY_SINGLE_ROOM_PRICE)); break;
            case 4: System.out.println(new SingleRoom(DELUXE_SINGLE_ROOM_PRICE)); break;
            default: System.out.println("Opção inválida.");
        }
    }

    public int getAvailableRoomsCount(int roomType) {
        Room[] rooms = roomRegistry.getRoomsByType(roomType);
        int count = 0;
        for (Room room : rooms) {
            if (!room.isOccupied()) {
                count++;
            }
        }
        return count;
    }

    public void displayAvailableRoomNumbers(int roomType) {
        Room[] rooms = roomRegistry.getRoomsByType(roomType);
        System.out.print("\nEscolha o número do quarto disponível: ");
        for (Room room : rooms) {
            if (!room.isOccupied()) {
                System.out.print(room.getRoomNumber() + ",");
            }
        }
        System.out.println();
    }

    public void bookRoom(int roomType, int roomNumber, List<Guest> guests) throws RoomNotAvailableException, InvalidRoomNumberException {
        Room room = roomRegistry.getRoom(roomNumber, roomType);
        if (room.isOccupied()) {
            throw new RoomNotAvailableException("Quarto " + roomNumber + " já está ocupado.");
        }
        if (guests.size() > room.getCapacity()) {
            throw new IllegalArgumentException("Número de hóspedes excede a capacidade do quarto " + room.getCapacity());
        }

        room.clearOccupancy(); // Garante que o quarto está limpo antes de reservar
        for (Guest guest : guests) {
            room.addGuest(guest);
        }
    }

    public void orderFood(int roomNumber, int roomType, int menuItemId, int quantity) throws InvalidRoomNumberException {
        Room room = roomRegistry.getRoom(roomNumber, roomType);
        if (!room.isOccupied()) {
            throw new IllegalStateException("Quarto não reservado.");
        }
        MenuItem item = getMenuItem(menuItemId);
        if (item == null) {
            throw new IllegalArgumentException("Item de menu inválido.");
        }
        room.addFoodOrder(new FoodOrder(item, quantity));
    }

    public double checkoutRoom(int roomNumber, int roomType) throws InvalidRoomNumberException, RoomNotAvailableException {
        Room room = roomRegistry.getRoom(roomNumber, roomType);
        if (!room.isOccupied()) {
            throw new RoomNotAvailableException("Quarto " + roomNumber + " já está vazio.");
        }
        double bill = room.calculateBill();
        room.clearOccupancy();
        return bill;
    }

    public Room getRoomInfo(int roomNumber, int roomType) throws InvalidRoomNumberException {
        return roomRegistry.getRoom(roomNumber, roomType);
    }

    public Map<Integer, MenuItem> getMenu() {
        return new HashMap<>(menuItems);
    }
}

// --- Persistência de Dados ---

/**
 * Responsável pela persistência do estado do hotel.
 * SRP: Responsabilidade única de salvar e carregar dados.
 */
class HotelDataStore implements HotelConstants {
    public void save(RoomRegistry roomRegistry) {
        try (FileOutputStream fout = new FileOutputStream(BACKUP_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(roomRegistry);
            System.out.println("Dados do hotel salvos com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados do hotel: " + e.getMessage());
        }
    }

    public RoomRegistry load() {
        File file = new File(BACKUP_FILE_NAME);
        if (file.exists()) {
            try (FileInputStream fin = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                RoomRegistry loadedRegistry = (RoomRegistry) ois.readObject();
                System.out.println("Dados do hotel carregados com sucesso.");
                return loadedRegistry;
            } catch (Exception e) {
                System.err.println("Erro ao carregar dados do hotel: " + e.getMessage());
                return new RoomRegistry(); // Retorna um novo registro se houver erro ao carregar
            }
        }
        return new RoomRegistry(); // Inicia um novo se o arquivo não existir
    }
}

// --- Interface do Usuário (Camada de Apresentação) ---

/**
 * Classe responsável pela interação com o usuário.
 * SRP: Responsabilidade única de lidar com entrada/saída do usuário.
 * DIP: Depende de HotelService (abstração), não de detalhes de implementação.
 */
class HotelCLI {
    private final HotelService hotelService;
    private final Scanner scanner;

    public HotelCLI(HotelService hotelService, Scanner scanner) {
        this.hotelService = hotelService;
        this.scanner = scanner;
    }

    public void start() {
        int choice;
        do {
            displayMainMenu();
            choice = getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        handleDisplayRoomDetails();
                        break;
                    case 2:
                        handleDisplayRoomAvailability();
                        break;
                    case 3:
                        handleBookRoom();
                        break;
                    case 4:
                        handleOrderFood();
                        break;
                    case 5:
                        handleCheckout();
                        break;
                    case 6:
                        System.out.println("Saindo do sistema. Obrigado!");
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                scanner.next(); // Limpa o buffer do scanner
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }

            if (choice != 6) {
                System.out.println("\nContinuar: (y/n)");
                char wish = scanner.next().charAt(0);
                if (!(wish == 'y' || wish == 'Y')) {
                    choice = 6; // Sai do loop
                }
            }
        } while (choice != 6);
    }

    private void displayMainMenu() {
        System.out.println("\nEscolha sua opção:");
        System.out.println("1. Detalhes dos quartos");
        System.out.println("2. Disponibilidade dos quartos");
        System.out.println("3. Reservar quarto");
        System.out.println("4. Pedir comida");
        System.out.println("5. Fazer checkout");
        System.out.println("6. Sair");
        System.out.print("Sua escolha: ");
    }

    private int getUserChoice() {
        return scanner.nextInt();
    }

    private int getRoomTypeChoice() {
        System.out.println("\nEscolha o tipo de quarto:");
        System.out.println("1. Quarto Duplo Luxo");
        System.out.println("2. Quarto Duplo Deluxe");
        System.out.println("3. Quarto Individual Luxo");
        System.out.println("4. Quarto Individual Deluxe");
        System.out.print("Sua escolha: ");
        return scanner.nextInt();
    }

    private void handleDisplayRoomDetails() {
        int roomType = getRoomTypeChoice();
        hotelService.displayRoomDetails(roomType);
    }

    private void handleDisplayRoomAvailability() {
        int roomType = getRoomTypeChoice();
        int available = hotelService.getAvailableRoomsCount(roomType);
        System.out.println("Número de quartos disponíveis: " + available);
    }

    private void handleBookRoom() throws RoomNotAvailableException, InvalidRoomNumberException {
        int roomType = getRoomTypeChoice();
        hotelService.displayAvailableRoomNumbers(roomType);
        System.out.print("Digite o número do quarto: ");
        int roomNumber = scanner.nextInt();

        Room room = hotelService.getRoomInfo(roomNumber, roomType);
        List<Guest> guests = new ArrayList<>();
        System.out.print("Quantos hóspedes (máximo " + room.getCapacity() + "): ");
        int numGuests = scanner.nextInt();

        if (numGuests > room.getCapacity()) {
            System.out.println("Número de hóspedes excede a capacidade do quarto. Tente novamente.");
            return;
        }

        for (int i = 0; i < numGuests; i++) {
            System.out.println("\nDados do Hóspede " + (i + 1) + ":");
            System.out.print("Nome: ");
            String name = scanner.next();
            System.out.print("Contato: ");
            String contact = scanner.next();
            System.out.print("Gênero: ");
            String gender = scanner.next();
            guests.add(new Guest(name, contact, gender));
        }

        hotelService.bookRoom(roomType, roomNumber, guests);
        System.out.println("Quarto reservado com sucesso!");
    }

    private void handleOrderFood() {
        System.out.print("Número do Quarto: ");
        int roomNumber = scanner.nextInt();
        try {
            // Determinar o tipo de quarto com base no número do quarto
            int roomType;
            if (roomNumber >= 1 && roomNumber <= 10) roomType = 1;
            else if (roomNumber >= 11 && roomNumber <= 30) roomType = 2;
            else if (roomNumber >= 31 && roomNumber <= 40) roomType = 3;
            else if (roomNumber >= 41 && roomNumber <= 60) roomType = 4;
            else {
                System.out.println("Número de quarto inválido.");
                return;
            }

            Room room = hotelService.getRoomInfo(roomNumber, roomType);
            if (!room.isOccupied()) {
                System.out.println("Quarto não reservado. Não é possível pedir comida.");
                return;
            }

            System.out.println("\n==========\n   Menu:  \n==========\n");
            hotelService.getMenu().values().forEach(System.out::println);

            char wish;
            do {
                System.out.print("Escolha o item (ID): ");
                int itemId = scanner.nextInt();
                System.out.print("Quantidade: ");
                int quantity = scanner.nextInt();

                hotelService.orderFood(roomNumber, roomType, itemId, quantity);
                System.out.println("Pedido adicionado.");

                System.out.println("Deseja pedir mais alguma coisa? (y/n)");
                wish = scanner.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');

        } catch (InvalidRoomNumberException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void handleCheckout() {
        System.out.print("Número do Quarto: ");
        int roomNumber = scanner.nextInt();
        try {
            // Determinar o tipo de quarto com base no número do quarto
            int roomType;
            if (roomNumber >= 1 && roomNumber <= 10) roomType = 1;
            else if (roomNumber >= 11 && roomNumber <= 30) roomType = 2;
            else if (roomNumber >= 31 && roomNumber <= 40) roomType = 3;
            else if (roomNumber >= 41 && roomNumber <= 60) roomType = 4;
            else {
                System.out.println("Número de quarto inválido.");
                return;
            }

            Room room = hotelService.getRoomInfo(roomNumber, roomType);
            if (!room.isOccupied()) {
                System.out.println("Quarto já está vazio.");
                return;
            }
            System.out.println("Quarto usado por: " + room.getGuests().get(0).getName()); // Exibe o primeiro hóspede

            System.out.println("Deseja fazer checkout? (y/n)");
            char wish = scanner.next().charAt(0);
            if (wish == 'y' || wish == 'Y') {
                System.out.println("\n*******");
                System.out.println("  Conta:-");
                System.out.println("*******");
                System.out.println("\nEncargo do Quarto - " + room.getBasePrice());
                System.out.println("\n===============");
                System.out.println("Encargos de Comida:- ");
                System.out.println("===============");
                System.out.println("Item        Quantidade  Preço");
                System.out.println("-------------------------");
                for (FoodOrder order : room.getFoodOrders()) {
                    String format = "%-12s%-12s%-10s%n";
                    System.out.printf(format, order.getItem().getName(), order.getQuantity(), order.getTotalPrice());
                }

                double totalBill = hotelService.checkoutRoom(roomNumber, roomType);
                System.out.println("\nTotal da Conta - " + totalBill);
                System.out.println("Checkout realizado com sucesso!");
            }
        } catch (InvalidRoomNumberException | RoomNotAvailableException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}

// --- Classe Principal (Orquestrador) ---
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelDataStore dataStore = new HotelDataStore();
        RoomRegistry roomRegistry = dataStore.load(); // Carrega os dados persistidos ou inicializa um novo
        HotelService hotelService = new HotelService(roomRegistry);
        HotelCLI hotelCLI = new HotelCLI(hotelService, sc);

        hotelCLI.start();

        // Salva os dados antes de sair
        dataStore.save(roomRegistry);
        sc.close();
    }
}