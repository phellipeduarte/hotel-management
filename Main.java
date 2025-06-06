import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// Custom exception for when a room is not available
class NotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Room Not Available!";
    }
}

// Represents a food item ordered by a guest
class Food implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    private int itemNo;
    private int quantity;
    private double price;

    public Food(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        calculatePrice();
    }

    private void calculatePrice() {
        // Using an enum for food items would be even better for clarity and extensibility
        switch (itemNo) {
            case 1: // Sandwich
                price = quantity * 50;
                break;
            case 2: // Pasta
                price = quantity * 60;
                break;
            case 3: // Noodles
                price = quantity * 70;
                break;
            case 4: // Coke
                price = quantity * 30;
                break;
            default:
                price = 0; // Or throw an exception for invalid item
                break;
        }
    }

    public int getItemNo() {
        return itemNo;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

// Represents a guest staying in a room
class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
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
}

// Abstract base class for all room types
abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Guest primaryGuest;
    protected ArrayList<Food> food;
    protected int roomNumber; // Added for easier identification and management
    protected double roomCharge; // Added to store the base room charge

    public Room(int roomNumber, double roomCharge) {
        this.roomNumber = roomNumber;
        this.food = new ArrayList<>();
        this.roomCharge = roomCharge;
    }

    public void setPrimaryGuest(Guest primaryGuest) {
        this.primaryGuest = primaryGuest;
    }

    public Guest getPrimaryGuest() {
        return primaryGuest;
    }

    public ArrayList<Food> getFood() {
        return food;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public double getRoomCharge() {
        return roomCharge;
    }

    public abstract void displayFeatures();
    public abstract String getRoomType();
    public abstract int getMaxOccupancy(); // New method to define max occupancy

    public boolean isAvailable() {
        return primaryGuest == null;
    }

    public void addFood(Food foodItem) {
        if (foodItem != null) {
            this.food.add(foodItem);
        }
    }

    public void clearFoodOrders() {
        this.food.clear();
    }

    public double calculateFoodBill() {
        double totalFoodPrice = 0;
        for (Food f : food) {
            totalFoodPrice += f.getPrice();
        }
        return totalFoodPrice;
    }

    public void checkout() {
        primaryGuest = null;
        clearFoodOrders();
    }
}

// Represents a single room
class SingleRoom extends Room {
    private static final long serialVersionUID = 1L;

    public SingleRoom(int roomNumber, double roomCharge) {
        super(roomNumber, roomCharge);
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of single beds : 1\nAC : " + (roomCharge == 2200 ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + (int)roomCharge);
    }

    @Override
    public String getRoomType() {
        return (roomCharge == 2200) ? "Luxury Single Room" : "Deluxe Single Room";
    }

    @Override
    public int getMaxOccupancy() {
        return 1;
    }
}

// Represents a double room
class DoubleRoom extends Room {
    private static final long serialVersionUID = 1L;
    private Guest secondaryGuest;

    public DoubleRoom(int roomNumber, double roomCharge) {
        super(roomNumber, roomCharge);
    }

    public void setSecondaryGuest(Guest secondaryGuest) {
        this.secondaryGuest = secondaryGuest;
    }

    public Guest getSecondaryGuest() {
        return secondaryGuest;
    }

    @Override
    public void displayFeatures() {
        System.out.println("Number of double beds : 1\nAC : " + (roomCharge == 4000 ? "Yes" : "No") + "\nFree breakfast : Yes\nCharge per day: " + (int)roomCharge);
    }

    @Override
    public String getRoomType() {
        return (roomCharge == 4000) ? "Luxury Double Room" : "Deluxe Double Room";
    }

    @Override
    public int getMaxOccupancy() {
        return 2;
    }

    @Override
    public void checkout() {
        super.checkout(); // Clear primary guest and food
        secondaryGuest = null; // Clear secondary guest
    }
}

// Holds all the rooms in the hotel
class RoomHolder implements Serializable {
    private static final long serialVersionUID = 1L;
    private Room[] luxuryDoubleRooms = new DoubleRoom[10];
    private Room[] deluxeDoubleRooms = new DoubleRoom[20];
    private Room[] luxurySingleRooms = new SingleRoom[10];
    private Room[] deluxeSingleRooms = new SingleRoom[20];

    public RoomHolder() {
        // Initialize rooms with their respective room numbers and charges
        for (int i = 0; i < luxuryDoubleRooms.length; i++) {
            luxuryDoubleRooms[i] = new DoubleRoom(i + 1, 4000); // Rooms 1-10
        }
        for (int i = 0; i < deluxeDoubleRooms.length; i++) {
            deluxeDoubleRooms[i] = new DoubleRoom(i + 11, 3000); // Rooms 11-30
        }
        for (int i = 0; i < luxurySingleRooms.length; i++) {
            luxurySingleRooms[i] = new SingleRoom(i + 31, 2200); // Rooms 31-40
        }
        for (int i = 0; i < deluxeSingleRooms.length; i++) {
            deluxeSingleRooms[i] = new SingleRoom(i + 41, 1200); // Rooms 41-60
        }
    }

    public Room[] getRooms(int roomTypeChoice) {
        switch (roomTypeChoice) {
            case 1: return luxuryDoubleRooms;
            case 2: return deluxeDoubleRooms;
            case 3: return luxurySingleRooms;
            case 4: return deluxeSingleRooms;
            default: return null;
        }
    }
}

// Manages hotel operations
class Hotel {
    static RoomHolder roomHolder = new RoomHolder(); // Changed to RoomHolder for better naming
    static Scanner sc = new Scanner(System.in);

    // Helper method to get room array and adjusted index based on room number
    private static Room getRoomByIndex(int roomNumber, int roomTypeChoice) throws NotAvailableException {
        Room[] rooms = roomHolder.getRooms(roomTypeChoice);
        if (rooms == null) {
            throw new IllegalArgumentException("Invalid room type choice.");
        }

        int adjustedIndex = -1;
        // Determine the actual index in the array based on the room number ranges
        if (roomTypeChoice == 1 && roomNumber >= 1 && roomNumber <= 10) {
            adjustedIndex = roomNumber - 1;
        } else if (roomTypeChoice == 2 && roomNumber >= 11 && roomNumber <= 30) {
            adjustedIndex = roomNumber - 11;
        } else if (roomTypeChoice == 3 && roomNumber >= 31 && roomNumber <= 40) {
            adjustedIndex = roomNumber - 31;
        } else if (roomTypeChoice == 4 && roomNumber >= 41 && roomNumber <= 60) {
            adjustedIndex = roomNumber - 41;
        }

        if (adjustedIndex >= 0 && adjustedIndex < rooms.length) {
            return rooms[adjustedIndex];
        } else {
            throw new NotAvailableException(); // Room number does not match room type or is out of bounds
        }
    }

    // Handles customer details input
    private static void getCustomerDetails(Room room) {
        System.out.print("\nEnter primary customer name: ");
        String name1 = sc.next();
        System.out.print("Enter contact number: ");
        String contact1 = sc.next();
        System.out.print("Enter gender: ");
        String gender1 = sc.next();
        room.setPrimaryGuest(new Guest(name1, contact1, gender1));

        if (room instanceof DoubleRoom) {
            DoubleRoom doubleRoom = (DoubleRoom) room;
            System.out.print("Enter secondary customer name: ");
            String name2 = sc.next();
            System.out.print("Enter contact number: ");
            String contact2 = sc.next();
            System.out.print("Enter gender: ");
            String gender2 = sc.next();
            doubleRoom.setSecondaryGuest(new Guest(name2, contact2, gender2));
        }
    }

    // Books a room
    static void bookRoom(int roomTypeChoice) {
        Room[] rooms = roomHolder.getRooms(roomTypeChoice);
        if (rooms == null) {
            System.out.println("Invalid room type choice.");
            return;
        }

        System.out.println("\nChoose room number from available: ");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.print(room.getRoomNumber() + ",");
            }
        }
        System.out.print("\nEnter room number: ");

        try {
            int roomNumber = sc.nextInt();
            Room chosenRoom = getRoomByIndex(roomNumber, roomTypeChoice);

            if (!chosenRoom.isAvailable()) {
                throw new NotAvailableException();
            }

            getCustomerDetails(chosenRoom);
            System.out.println("Room Booked Successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next(); // Consume the invalid input
        } catch (NotAvailableException e) {
            System.out.println(e.toString() + " or invalid room number for selected type.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Displays features of a room type
    static void displayRoomFeatures(int roomTypeChoice) {
        // Create a dummy room of the specified type to display features
        // This is a bit of a workaround since features are static for a room type.
        // A better approach would be to have a separate RoomType class or enum.
        Room dummyRoom;
        switch (roomTypeChoice) {
            case 1: dummyRoom = new DoubleRoom(0, 4000); break; // Luxury Double
            case 2: dummyRoom = new DoubleRoom(0, 3000); break; // Deluxe Double
            case 3: dummyRoom = new SingleRoom(0, 2200); break; // Luxury Single
            case 4: dummyRoom = new SingleRoom(0, 1200); break; // Deluxe Single
            default:
                System.out.println("Enter valid option");
                return;
        }
        dummyRoom.displayFeatures();
    }

    // Displays availability of a room type
    static void displayRoomAvailability(int roomTypeChoice) {
        Room[] rooms = roomHolder.getRooms(roomTypeChoice);
        if (rooms == null) {
            System.out.println("Invalid room type choice.");
            return;
        }

        int count = 0;
        for (Room room : rooms) {
            if (room.isAvailable()) {
                count++;
            }
        }
        System.out.println("Number of " + (rooms.length > 0 ? rooms[0].getRoomType() : "rooms") + " available : " + count);
    }

    // Generates the bill for a room
    static void generateBill(int roomNumber, int roomTypeChoice) {
        try {
            Room room = getRoomByIndex(roomNumber, roomTypeChoice);
            if (room.isAvailable()) {
                System.out.println("Room " + roomNumber + " is not booked.");
                return;
            }

            double totalAmount = room.getRoomCharge();
            System.out.println("\n*******");
            System.out.println(" Bill:-");
            System.out.println("*******");
            System.out.println("\nRoom Charge - " + (int)room.getRoomCharge());
            System.out.println("\n===============");
            System.out.println("Food Charges:- ");
            System.out.println("===============");
            System.out.println("Item   Quantity    Price");
            System.out.println("-------------------------");

            String[] foodList = {"Sandwich", "Pasta", "Noodles", "Coke"};
            for (Food foodItem : room.getFood()) {
                totalAmount += foodItem.getPrice();
                String format = "%-10s%-10s%-10.2f%n";
                System.out.printf(format, foodList[foodItem.getItemNo() - 1], foodItem.getQuantity(), foodItem.getPrice());
            }

            System.out.println("\nTotal Amount - " + String.format("%.2f", totalAmount));

        } catch (NotAvailableException e) {
            System.out.println("Room " + roomNumber + " doesn't exist or is not of the specified type.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Deallocates (checks out) a room
    static void deallocateRoom(int roomNumber, int roomTypeChoice) {
        try {
            Room room = getRoomByIndex(roomNumber, roomTypeChoice);

            if (room.isAvailable()) {
                System.out.println("Room " + roomNumber + " is already empty.");
                return;
            }

            System.out.println("Room " + room.getRoomNumber() + " used by " + room.getPrimaryGuest().getName());
            System.out.print("Do you want to checkout ?(y/n): ");
            char choice = sc.next().charAt(0);

            if (choice == 'y' || choice == 'Y') {
                generateBill(roomNumber, roomTypeChoice); // Generate bill before clearing details
                room.checkout(); // Clears guests and food
                System.out.println("Deallocated successfully.");
            } else {
                System.out.println("Checkout cancelled.");
            }
        } catch (NotAvailableException e) {
            System.out.println("Room " + roomNumber + " doesn't exist or is not of the specified type.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Orders food for a room
    static void orderFood(int roomNumber, int roomTypeChoice) {
        try {
            Room room = getRoomByIndex(roomNumber, roomTypeChoice);

            if (room.isAvailable()) {
                System.out.println("\nRoom " + roomNumber + " is not booked. Cannot order food.");
                return;
            }

            System.out.println("\n==========\n   Menu:  \n==========\n\n1.Sandwich\tRs.50\n2.Pasta\t\tRs.60\n3.Noodles\tRs.70\n4.Coke\t\tRs.30\n");
            char wish;
            do {
                System.out.print("Enter item number: ");
                int item = sc.nextInt();
                System.out.print("Enter quantity: ");
                int quantity = sc.nextInt();

                room.addFood(new Food(item, quantity));
                System.out.print("Do you want to order anything else ? (y/n): ");
                wish = sc.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.next(); // Consume the invalid input
        } catch (NotAvailableException e) {
            System.out.println("Room " + roomNumber + " doesn't exist or is not of the specified type.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Helper to determine room type choice from a given room number
    static int getRoomTypeChoiceFromRoomNumber(int roomNumber) {
        if (roomNumber >= 1 && roomNumber <= 10) return 1; // Luxury Double
        else if (roomNumber >= 11 && roomNumber <= 30) return 2; // Deluxe Double
        else if (roomNumber >= 31 && roomNumber <= 40) return 3; // Luxury Single
        else if (roomNumber >= 41 && roomNumber <= 60) return 4; // Deluxe Single
        return -1; // Invalid
    }
}

// Runnable for saving hotel data to a file
class HotelDataWriter implements Runnable {
    private RoomHolder roomHolder;

    public HotelDataWriter(RoomHolder roomHolder) {
        this.roomHolder = roomHolder;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(roomHolder);
            System.out.println("\nHotel data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving hotel data: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            File backupFile = new File("backup");
            if (backupFile.exists()) {
                try (FileInputStream fin = new FileInputStream(backupFile);
                     ObjectInputStream ois = new ObjectInputStream(fin)) {
                    Hotel.roomHolder = (RoomHolder) ois.readObject();
                    System.out.println("Hotel data loaded from backup.");
                } catch (ClassNotFoundException e) {
                    System.out.println("Error loading backup: Class not found - " + e.getMessage());
                }
            }

            Scanner sc = Hotel.sc; // Using the shared scanner from Hotel class
            int choice, subChoice;
            char continueOption;

            x: // Label for breaking out of the outer loop
            do {
                System.out.println("\n--- Hotel Management System ---");
                System.out.println("1. Display room details");
                System.out.println("2. Display room availability");
                System.out.println("3. Book a room");
                System.out.println("4. Order food");
                System.out.println("5. Checkout");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                try {
                    choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            System.out.println("\nChoose room type :");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            subChoice = sc.nextInt();
                            Hotel.displayRoomFeatures(subChoice);
                            break;
                        case 2:
                            System.out.println("\nChoose room type :");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            subChoice = sc.nextInt();
                            Hotel.displayRoomAvailability(subChoice);
                            break;
                        case 3:
                            System.out.println("\nChoose room type :");
                            System.out.println("1. Luxury Double Room");
                            System.out.println("2. Deluxe Double Room");
                            System.out.println("3. Luxury Single Room");
                            System.out.println("4. Deluxe Single Room");
                            System.out.print("Enter room type: ");
                            subChoice = sc.nextInt();
                            Hotel.bookRoom(subChoice);
                            break;
                        case 4:
                            System.out.print("Enter room number: ");
                            subChoice = sc.nextInt();
                            int roomTypeForOrder = Hotel.getRoomTypeChoiceFromRoomNumber(subChoice);
                            if (roomTypeForOrder != -1) {
                                Hotel.orderFood(subChoice, roomTypeForOrder);
                            } else {
                                System.out.println("Invalid room number. Room doesn't exist.");
                            }
                            break;
                        case 5:
                            System.out.print("Enter room number to checkout: ");
                            subChoice = sc.nextInt();
                            int roomTypeForCheckout = Hotel.getRoomTypeChoiceFromRoomNumber(subChoice);
                            if (roomTypeForCheckout != -1) {
                                Hotel.deallocateRoom(subChoice, roomTypeForCheckout);
                            } else {
                                System.out.println("Invalid room number. Room doesn't exist.");
                            }
                            break;
                        case 6:
                            break x; // Exit the program
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.next(); // Consume the invalid input
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }

                System.out.print("\nContinue with another operation? (y/n): ");
                continueOption = sc.next().charAt(0);
                while (!(continueOption == 'y' || continueOption == 'Y' || continueOption == 'n' || continueOption == 'N')) {
                    System.out.println("Invalid option. Please enter 'y' or 'n'.");
                    System.out.print("Continue with another operation? (y/n): ");
                    continueOption = sc.next().charAt(0);
                }

            } while (continueOption == 'y' || continueOption == 'Y');

            // Save hotel data before exiting
            Thread t = new Thread(new HotelDataWriter(Hotel.roomHolder));
            t.start();
            t.join(); // Wait for the thread to finish writing
            System.out.println("Exiting Hotel Management System. Goodbye!");

        } catch (Exception e) {
            System.out.println("An error occurred in the main program: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (Hotel.sc != null) {
                Hotel.sc.close();
            }
        }
    }
}