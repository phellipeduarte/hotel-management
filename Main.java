import java.io.*;
import java.util.*;

// ---------------- Classe Customer ----------------
class Customer implements Serializable {
    String nome;
    String contato;
    String genero;

    public Customer(String nome, String contato, String genero) {
        this.nome = nome;
        this.contato = contato;
        this.genero = genero;
    }
}

// ---------------- Classe Room ----------------
class Room implements Serializable {
    String tipoQuarto;
    int preco;
    boolean reservado;
    Customer cliente;
    List<String> pedidos;

    public Room(String tipoQuarto, int preco) {
        this.tipoQuarto = tipoQuarto;
        this.preco = preco;
        this.reservado = false;
        this.cliente = null;
        this.pedidos = new ArrayList<>();
    }

    public void reservar(Customer cliente) {
        this.reservado = true;
        this.cliente = cliente;
    }

    public void checkout() {
        this.reservado = false;
        this.cliente = null;
        this.pedidos.clear();
    }
}

// ---------------- Classe Hotel ----------------
class Hotel implements Serializable {
    Room[] luxoDuplo = new Room[10];      // quartos 1-10
    Room[] deluxeDuplo = new Room[20];    // quartos 11-30
    Room[] luxoSolteiro = new Room[10];   // quartos 31-40
    Room[] deluxeSolteiro = new Room[20]; // quartos 41-60

    public Hotel() {
        inicializarQuartos();
    }

    private void inicializarQuartos() {
        for (int i = 0; i < luxoDuplo.length; i++)
            luxoDuplo[i] = new Room("Luxo Duplo", 4000);
        for (int i = 0; i < deluxeDuplo.length; i++)
            deluxeDuplo[i] = new Room("Deluxe Duplo", 3000);
        for (int i = 0; i < luxoSolteiro.length; i++)
            luxoSolteiro[i] = new Room("Luxo Solteiro", 2200);
        for (int i = 0; i < deluxeSolteiro.length; i++)
            deluxeSolteiro[i] = new Room("Deluxe Solteiro", 1200);
    }

    public void exibirDetalhesQuarto(int tipo) {
        switch (tipo) {
            case 1 -> System.out.println("Luxo Duplo - Capacidade 2 pessoas - Café incluso - Wi-Fi - Preço: 4000");
            case 2 -> System.out.println("Deluxe Duplo - Capacidade 2 pessoas - Preço: 3000");
            case 3 -> System.out.println("Luxo Solteiro - Capacidade 1 pessoa - Café incluso - Wi-Fi - Preço: 2200");
            case 4 -> System.out.println("Deluxe Solteiro - Capacidade 1 pessoa - Preço: 1200");
            default -> System.out.println("Tipo inválido.");
        }
    }

    public void exibirDisponibilidade(int tipo) {
        Room[] quartos = obterArrayQuartos(tipo);
        int disponiveis = 0;
        for (Room r : quartos) {
            if (!r.reservado) disponiveis++;
        }
        System.out.println("Quartos disponíveis: " + disponiveis + " de " + quartos.length);
    }

    public void reservarQuarto(int tipo, Scanner sc) {
        Room[] quartos = obterArrayQuartos(tipo);
        for (int i = 0; i < quartos.length; i++) {
            if (!quartos[i].reservado) {
                System.out.print("Nome: ");
                sc.nextLine();
                String nome = sc.nextLine();
                System.out.print("Contato: ");
                String contato = sc.nextLine();
                System.out.print("Gênero: ");
                String genero = sc.nextLine();
                quartos[i].reservar(new Customer(nome, contato, genero));
                System.out.println("Quarto reservado. Número do quarto: " + obterNumeroQuarto(tipo, i));
                return;
            }
        }
        System.out.println("Desculpe, não há quartos disponíveis deste tipo.");
    }

    public boolean realizarPedido(int numeroQuarto, Scanner sc) {
        Room r = localizarQuarto(numeroQuarto);
        if (r == null) return false;
        if (!r.reservado) {
            System.out.println("Quarto não está reservado.");
            return true;
        }

        System.out.println("Menu:\n1. Sanduíche - 50\n2. Pizza - 100\n3. Refrigerante - 30\n4. Sobremesa - 60");
        System.out.print("Escolha (1-4): ");
        int item = sc.nextInt();
        String pedido = switch (item) {
            case 1 -> "Sanduíche";
            case 2 -> "Pizza";
            case 3 -> "Refrigerante";
            case 4 -> "Sobremesa";
            default -> {
                System.out.println("Item inválido.");
                yield null;
            }
        };
        if (pedido != null) {
            r.pedidos.add(pedido);
            System.out.println("Pedido realizado: " + pedido);
        }
        return true;
    }

    public boolean realizarCheckout(int numeroQuarto) {
        Room r = localizarQuarto(numeroQuarto);
        if (r == null) return false;
        if (!r.reservado) {
            System.out.println("Quarto já está livre.");
            return true;
        }

        int total = r.preco;
        int consumo = r.pedidos.size() * 50;
        total += consumo;

        System.out.println("Cliente: " + r.cliente.nome);
        System.out.println("Valor do quarto: " + r.preco);
        System.out.println("Consumo: " + consumo);
        System.out.println("Total a pagar: " + total);

        r.checkout();
        System.out.println("Checkout concluído.");
        return true;
    }

    private Room localizarQuarto(int numero) {
        if (numero >= 1 && numero <= 10) return luxoDuplo[numero - 1];
        if (numero >= 11 && numero <= 30) return deluxeDuplo[numero - 11];
        if (numero >= 31 && numero <= 40) return luxoSolteiro[numero - 31];
        if (numero >= 41 && numero <= 60) return deluxeSolteiro[numero - 41];
        return null;
    }

    private Room[] obterArrayQuartos(int tipo) {
        return switch (tipo) {
            case 1 -> luxoDuplo;
            case 2 -> deluxeDuplo;
            case 3 -> luxoSolteiro;
            case 4 -> deluxeSolteiro;
            default -> new Room[0];
        };
    }

    private int obterNumeroQuarto(int tipo, int indice) {
        return switch (tipo) {
            case 1 -> indice + 1;
            case 2 -> indice + 11;
            case 3 -> indice + 31;
            case 4 -> indice + 41;
            default -> -1;
        };
    }
}

// ---------------- Classe WriteBackup (Thread) ----------------
class WriteBackup implements Runnable {
    Hotel hotel;

    WriteBackup(Hotel hotel) {
        this.hotel = hotel;
    }

    public void run() {
        try {
            FileOutputStream fout = new FileOutputStream("backup");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(hotel);
            oos.close();
            fout.close();
        } catch (Exception e) {
            System.out.println("Erro ao salvar backup.");
        }
    }
}

// ---------------- Classe Main ----------------
public class Main {
    public static void main(String[] args) {
        Hotel hotel = carregarDados();
        Scanner sc = new Scanner(System.in);
        char wish;

        do {
            exibirMenuPrincipal();
            int opcao = obterOpcao(sc);

            switch (opcao) {
                case 1 -> hotel.exibirDetalhesQuarto(selecionarTipoQuarto(sc));
                case 2 -> hotel.exibirDisponibilidade(selecionarTipoQuarto(sc));
                case 3 -> hotel.reservarQuarto(selecionarTipoQuarto(sc), sc);
                case 4 -> realizarPedido(hotel, sc);
                case 5 -> realizarCheckout(hotel, sc);
                case 6 -> {
                    salvarDados(hotel);
                    System.out.println("Sistema encerrado.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }

            wish = solicitarContinuacao(sc);

        } while (wish == 'y' || wish == 'Y');

        salvarDados(hotel);
    }

    // -------- Métodos auxiliares --------

    private static Hotel carregarDados() {
        try {
            File arquivo = new File("backup");
            if (arquivo.exists()) {
                FileInputStream fin = new FileInputStream(arquivo);
                ObjectInputStream ois = new ObjectInputStream(fin);
                Hotel hotel = (Hotel) ois.readObject();
                ois.close();
                fin.close();
                return hotel;
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados, iniciando novo hotel.");
        }
        return new Hotel();
    }

    private static void salvarDados(Hotel hotel) {
        Thread t = new Thread(new WriteBackup(hotel));
        t.start();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\nEscolha uma opção:");
        System.out.println("1. Exibir detalhes dos quartos");
        System.out.println("2. Exibir disponibilidade");
        System.out.println("3. Reservar quarto");
        System.out.println("4. Pedir comida");
        System.out.println("5. Checkout");
        System.out.println("6. Sair");
    }

    private static int obterOpcao(Scanner sc) {
        System.out.print("Opção: ");
        while (!sc.hasNextInt()) {
            System.out.print("Por favor, insira um número válido: ");
            sc.next();
        }
        return sc.nextInt();
    }

    private static int selecionarTipoQuarto(Scanner sc) {
        System.out.println("\nSelecione o tipo de quarto:");
        System.out.println("1. Luxo Duplo");
        System.out.println("2. Deluxe Duplo");
        System.out.println("3. Luxo Solteiro");
        System.out.println("4. Deluxe Solteiro");
        return obterOpcao(sc);
    }

    private static void realizarPedido(Hotel hotel, Scanner sc) {
        System.out.print("Número do quarto: ");
        int numero = obterOpcao(sc);
        if (!hotel.realizarPedido(numero, sc)) {
            System.out.println("Quarto inexistente.");
        }
    }

    private static void realizarCheckout(Hotel hotel, Scanner sc) {
        System.out.print("Número do quarto: ");
        int numero = obterOpcao(sc);
        if (!hotel.realizarCheckout(numero)) {
            System.out.println("Quarto inexistente.");
        }
    }

    private static char solicitarContinuacao(Scanner sc) {
        System.out.print("\nDeseja continuar? (y/n): ");
        char wish = sc.next().charAt(0);
        while (!(wish == 'y' || wish == 'Y' || wish == 'n' || wish == 'N')) {
            System.out.print("Opção inválida. Digite (y/n): ");
            wish = sc.next().charAt(0);
        }
        return wish;
    }
}
