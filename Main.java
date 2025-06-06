import java.io.*;
import java.util.*;

// Classe Principal
public class Main {
    public static void main(String[] args) {
        try {
            carregarBackup();

            Scanner sc = new Scanner(System.in);
            char wish;

            do {
                exibirMenuPrincipal();
                int escolha = sc.nextInt();

                switch (escolha) {
                    case 1 -> exibirDetalhesQuarto(sc);
                    case 2 -> exibirDisponibilidade(sc);
                    case 3 -> realizarReserva(sc);
                    case 4 -> realizarPedido(sc);
                    case 5 -> realizarCheckout(sc);
                    case 6 -> {
                        salvarBackup();
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }

                wish = solicitarContinuacao(sc);

            } while (wish == 'y' || wish == 'Y');

            salvarBackup();

        } catch (Exception e) {
            System.out.println("Not a valid input");
        }
    }

    private static void carregarBackup() {
        File f = new File("backup");
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                Hotel.hotel_ob = (holder) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Erro ao carregar backup: " + e.getMessage());
            }
        }
    }

    private static void salvarBackup() {
        Thread t = new Thread(new write(Hotel.hotel_ob));
        t.start();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("""
                
                Enter your choice:
                1. Display room details
                2. Display room availability
                3. Book
                4. Order food
                5. Checkout
                6. Exit
                """);
    }

    private static void exibirMenuTipoQuarto() {
        System.out.println("""
                
                Choose room type:
                1. Luxury Double Room
                2. Deluxe Double Room
                3. Luxury Single Room
                4. Deluxe Single Room
                """);
    }

    private static void exibirDetalhesQuarto(Scanner sc) {
        exibirMenuTipoQuarto();
        int tipo = sc.nextInt();
        Hotel.features(tipo);
    }

    private static void exibirDisponibilidade(Scanner sc) {
        exibirMenuTipoQuarto();
        int tipo = sc.nextInt();
        Hotel.availability(tipo);
    }

    private static void realizarReserva(Scanner sc) {
        exibirMenuTipoQuarto();
        int tipo = sc.nextInt();
        Hotel.bookroom(tipo);
    }

    private static void realizarPedido(Scanner sc) {
        System.out.print("Room Number - ");
        int numeroQuarto = sc.nextInt();
        int tipo = obterTipoQuartoPorNumero(numeroQuarto);
        int indice = obterIndiceQuarto(numeroQuarto, tipo);

        if (tipo == -1) {
            System.out.println("Room doesn't exist");
        } else {
            Hotel.order(indice, tipo);
        }
    }

    private static void realizarCheckout(Scanner sc) {
        System.out.print("Room Number - ");
        int numeroQuarto = sc.nextInt();
        int tipo = obterTipoQuartoPorNumero(numeroQuarto);
        int indice = obterIndiceQuarto(numeroQuarto, tipo);

        if (tipo == -1) {
            System.out.println("Room doesn't exist");
        } else {
            Hotel.deallocate(indice, tipo);
        }
    }

    private static char solicitarContinuacao(Scanner sc) {
        System.out.println("\nContinue : (y/n)");
        char wish = sc.next().charAt(0);

        while (!(wish == 'y' || wish == 'Y' || wish == 'n' || wish == 'N')) {
            System.out.println("Invalid Option");
            System.out.println("\nContinue : (y/n)");
            wish = sc.next().charAt(0);
        }
        return wish;
    }

    private static int obterTipoQuartoPorNumero(int numero) {
        if (numero >= 1 && numero <= 10) return 1;
        else if (numero >= 11 && numero <= 30) return 2;
        else if (numero >= 31 && numero <= 40) return 3;
        else if (numero >= 41 && numero <= 60) return 4;
        else return -1;
    }

    private static int obterIndiceQuarto(int numero, int tipo) {
        return switch (tipo) {
            case 1 -> numero - 1;
            case 2 -> numero - 11;
            case 3 -> numero - 31;
            case 4 -> numero - 41;
            default -> -1;
        };
    }
}

// Classe Hotel
class Hotel {
    static holder hotel_ob = new holder();

    static Scanner sc = new Scanner(System.in);

    static void features(int tipo) {
        switch (tipo) {
            case 1 -> System.out.println("\nNumber of Double beds : 2\nAC : Yes\nFree breakfast : Yes\nCharge per day : 4000 ");
            case 2 -> System.out.println("\nNumber of Double beds : 2\nAC : No\nFree breakfast : Yes\nCharge per day : 3000 ");
            case 3 -> System.out.println("\nNumber of Single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day : 2200 ");
            case 4 -> System.out.println("\nNumber of Single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day : 1200 ");
            default -> System.out.println("Invalid option");
        }
    }

    static void availability(int tipo) {
        int available = switch (tipo) {
            case 1 -> 10 - hotel_ob.luxury_dbl.length;
            case 2 -> 20 - hotel_ob.deluxe_dbl.length;
            case 3 -> 10 - hotel_ob.luxury_sgl.length;
            case 4 -> 20 - hotel_ob.deluxe_sgl.length;
            default -> {
                System.out.println("Invalid option");
                yield 0;
            }
        };
        System.out.println("Number of rooms available : " + available);
    }

    static void bookroom(int tipo) {
        Room[] rooms = getRoomsByType(tipo);
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                rooms[i] = new Room();
                rooms[i].book();
                System.out.println("Room Booked Successfully.");
                return;
            }
        }
        System.out.println("No rooms available.");
    }

    static void order(int indice, int tipo) {
        Room[] rooms = getRoomsByType(tipo);
        if (rooms[indice] != null) {
            rooms[indice].order();
        } else {
            System.out.println("Room is not booked.");
        }
    }

    static void deallocate(int indice, int tipo) {
        Room[] rooms = getRoomsByType(tipo);
        if (rooms[indice] != null) {
            System.out.println("Room deallocated successfully.");
            rooms[indice] = null;
        } else {
            System.out.println("Room is not booked.");
        }
    }

    static Room[] getRoomsByType(int tipo) {
        return switch (tipo) {
            case 1 -> hotel_ob.luxury_dbl;
            case 2 -> hotel_ob.deluxe_dbl;
            case 3 -> hotel_ob.luxury_sgl;
            case 4 -> hotel_ob.deluxe_sgl;
            default -> null;
        };
    }
}

// Classe Room
class Room implements Serializable {
    String name;
    String contact;
    String gender;
    ArrayList<Food> food = new ArrayList<>();

    void book() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter customer name: ");
        name = sc.nextLine();
        System.out.print("Enter contact number: ");
        contact = sc.nextLine();
        System.out.print("Enter gender: ");
        gender = sc.nextLine();
    }

    void order() {
        Scanner sc = new Scanner(System.in);
        int choice;
        char wish;
        do {
            System.out.println("\nMenu:\n1.Sandwich Rs.50\n2.Pasta Rs.60\n3.Noodles Rs.70\n4.Coke Rs.30");
            choice = sc.nextInt();
            switch (choice) {
                case 1 -> food.add(new Food("Sandwich", 50));
                case 2 -> food.add(new Food("Pasta", 60));
                case 3 -> food.add(new Food("Noodles", 70));
                case 4 -> food.add(new Food("Coke", 30));
                default -> System.out.println("Invalid choice");
            }
            System.out.println("Do you want to order anything else? (y/n)");
            wish = sc.next().charAt(0);
        } while (wish == 'y' || wish == 'Y');
    }
}

// Classe Food
class Food implements Serializable {
    String item;
    int price;

    Food(String item, int price) {
        this.item = item;
        this.price = price;
    }
}

// Classe holder (estrutura para armazenar os quartos)
class holder implements Serializable {
    Room[] luxury_dbl = new Room[10];
    Room[] deluxe_dbl = new Room[20];
    Room[] luxury_sgl = new Room[10];
    Room[] deluxe_sgl = new Room[20];
}

// Classe para salvar dados
class write implements Runnable {
    holder hotel_ob;

    write(holder hotel_ob) {
        this.hotel_ob = hotel_ob;
    }

    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(hotel_ob);
        } catch (Exception e) {
            System.out.println("Error in backup: " + e.getMessage());
        }
    }
}
