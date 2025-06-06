import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Food implements Serializable {
    static final int SANDWICH = 1;
    static final int PASTA = 2;
    static final int NOODLES = 3;
    static final int COKE = 4;

    static final int[] PRICES = {0, 50, 60, 70, 30}; // index 0 unused

    int itemNo;
    int quantity;
    float price;

    Food(int itemNo, int quantity) {
        this.itemNo = itemNo;
        this.quantity = quantity;
        this.price = quantity * PRICES[itemNo];
    }
}

class Singleroom implements Serializable {
    String name;
    String contact;
    String gender;
    ArrayList<Food> food = new ArrayList<>();

    Singleroom() {
        this.name = "";
    }

    Singleroom(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }
}

class Doubleroom extends Singleroom implements Serializable {
    String name2;
    String contact2;
    String gender2;

    Doubleroom() {
        super();
        this.name2 = "";
    }

    Doubleroom(String name, String contact, String gender, String name2, String contact2, String gender2) {
        super(name, contact, gender);
        this.name2 = name2;
        this.contact2 = contact2;
        this.gender2 = gender2;
    }
}

class NotAvailable extends Exception {
    @Override
    public String toString() {
        return "Not Available !";
    }
}

class Holder implements Serializable {
    Doubleroom[] luxuryDoubleRooms = new Doubleroom[10]; // Luxury
    Doubleroom[] deluxeDoubleRooms = new Doubleroom[20]; // Deluxe
    Singleroom[] luxurySingleRooms = new Singleroom[10]; // Luxury
    Singleroom[] deluxeSingleRooms = new Singleroom[20]; // Deluxe
}

class Hotel {
    static Holder hotelData = new Holder();
    static Scanner sc = new Scanner(System.in);

    static final String[] FOOD_LIST = {"Sandwich", "Pasta", "Noodles", "Coke"};
    static final int[] ROOM_PRICES = {0, 4000, 3000, 2200, 1200}; // index = room type

    public static void custDetails(int roomType, int roomNumber) {
        System.out.print("\nEnter customer name: ");
        String name = sc.next();
        System.out.print("Enter contact number: ");
        String contact = sc.next();
        System.out.print("Enter gender: ");
        String gender = sc.next();

        if (roomType < 3) { // double rooms
            System.out.print("Enter second customer name: ");
            String name2 = sc.next();
            System.out.print("Enter contact number: ");
            String contact2 = sc.next();
            System.out.print("Enter gender: ");
            String gender2 = sc.next();

            Doubleroom room = new Doubleroom(name, contact, gender, name2, contact2, gender2);
            if (roomType == 1)
                hotelData.luxuryDoubleRooms[roomNumber] = room;
            else
                hotelData.deluxeDoubleRooms[roomNumber] = room;

        } else { // single rooms
            Singleroom room = new Singleroom(name, contact, gender);
            if (roomType == 3)
                hotelData.luxurySingleRooms[roomNumber] = room;
            else
                hotelData.deluxeSingleRooms[roomNumber] = room;
        }
    }

    public static void bookRoom(int roomType) {
        int roomNumber;
        System.out.println("\nChoose room number from: ");

        int offset = getRoomOffset(roomType);
        Object[] rooms = getRoomsArray(roomType);

        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
                System.out.print((i + offset) + ",");
            }
        }

        System.out.print("\nEnter room number: ");
        try {
            roomNumber = sc.nextInt() - offset;
            if (roomNumber < 0 || roomNumber >= rooms.length || rooms[roomNumber] != null)
                throw new NotAvailable();
            custDetails(roomType, roomNumber);
            System.out.println("Room Booked");
        } catch (Exception e) {
            System.out.println("Invalid Option");
        }
    }

    private static Object[] getRoomsArray(int roomType) {
        switch (roomType) {
            case 1:
                return hotelData.luxuryDoubleRooms;
            case 2:
                return hotelData.deluxeDoubleRooms;
            case 3:
                return hotelData.luxurySingleRooms;
            case 4:
                return hotelData.deluxeSingleRooms;
            default:
                throw new IllegalArgumentException("Invalid room type");
        }
    }

    private static int getRoomOffset(int roomType) {
        switch (roomType) {
            case 1:
                return 1;
            case 2:
                return 11;
            case 3:
                return 31;
            case 4:
                return 41;
            default:
                throw new IllegalArgumentException("Invalid room type");
        }
    }

    public static void features(int roomType) {
        switch (roomType) {
            case 1:
                System.out.println("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:4000 ");
                break;
            case 2:
                System.out.println("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:3000  ");
                break;
            case 3:
                System.out.println("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:2200  ");
                break;
            case 4:
                System.out.println("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:1200 ");
                break;
            default:
                System.out.println("Enter valid option");
        }
    }

    public static void availability(int roomType) {
        Object[] rooms = getRoomsArray(roomType);
        int count = 0;
        for (Object room : rooms) {
            if (room == null) count++;
        }
        System.out.println("Number of rooms available : " + count);
    }

    public static void bill(int roomNumber, int roomType) {
        double amount = ROOM_PRICES[roomType];
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");
        System.out.println("\nRoom Charge - " + ROOM_PRICES[roomType]);
        System.out.println("\n===============");
        System.out.println("Food Charges:- ");
        System.out.println("===============");
        System.out.println("Item       Quantity   Price");
        System.out.println("-------------------------");

        ArrayList<Food> foodList = getFoodList(roomNumber, roomType);
        for (Food f : foodList) {
            amount += f.price;
            System.out.printf("%-10s %-10d %-10.2f%n", FOOD_LIST[f.itemNo - 1], f.quantity, f.price);
        }
        System.out.println("\nTotal Amount- " + amount);
    }

    private static ArrayList<Food> getFoodList(int roomNumber, int roomType) {
        switch (roomType) {
            case 1:
                return hotelData.luxuryDoubleRooms[roomNumber].food;
            case 2:
                return hotelData.deluxeDoubleRooms[roomNumber].food;
            case 3:
                return hotelData.luxurySingleRooms[roomNumber].food;
            case 4:
                return hotelData.deluxeSingleRooms[roomNumber].food;
            default:
                return new ArrayList<>();
        }
    }

    public static void deallocate(int roomNumber, int roomType) {
        Object[] rooms = getRoomsArray(roomType);
        if (roomNumber < 0 || roomNumber >= rooms.length || rooms[roomNumber] == null) {
            System.out.println("Empty Already");
            return;
        }

        Singleroom room;
        if (roomType < 3)
            room = (Doubleroom) rooms[roomNumber];
        else
            room = (Singleroom) rooms[roomNumber];

        System.out.println("Room used by " + room.name);
        System.out.println("Do you want to checkout? (y/n)");
        char choice = sc.next().charAt(0);
        if (choice == 'y' || choice == 'Y') {
            bill(roomNumber, roomType);
            rooms[roomNumber] = null;
            System.out.println("Deallocated successfully");
        }
    }

    public static void order(int roomNumber, int roomType) {
        try {
            System.out.println("\n==========\n   Menu:  \n==========\n\n1.Sandwich\tRs.50\n2.Pasta\t\tRs.60\n3.Noodles\tRs.70\n4.Coke\t\tRs.30\n");
            char wish;
            do {
                int item = sc.nextInt();
                System.out.print("Quantity- ");
                int quantity = sc.nextInt();
                Food foodItem = new Food(item, quantity);
                addFoodToRoom(roomNumber, roomType, foodItem);

                System.out.println("Do you want to order anything else? (y/n)");
                wish = sc.next().charAt(0);
            } while (wish == 'y' || wish == 'Y');
        } catch (NullPointerException e) {
            System.out.println("\nRoom not booked");
        } catch (Exception e) {
            System.out.println("Cannot be done");
        }
    }

    private static void addFoodToRoom(int roomNumber, int roomType, Food food) {
        switch (roomType) {
            case 1:
                hotelData.luxuryDoubleRooms[roomNumber].food.add(food);
                break;
            case 2:
                hotelData.deluxeDoubleRooms[roomNumber].food.add(food);
                break;
            case 3:
                hotelData.luxurySingleRooms[roomNumber].food.add(food);
                break;
            case 4:
                hotelData.deluxeSingleRooms[roomNumber].food.add(food);
                break;
        }
    }
}

class Write implements Runnable {
    Holder hotelData;

    Write(Holder hotelData) {
        this.hotelData = hotelData;
    }

    @Override
    public void run() {
        try (FileOutputStream fout = new FileOutputStream("backup");
             ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(hotelData);
        } catch (Exception e) {
            System.out.println("Error in writing " + e);
        }
    }
}


public class Main {
    private static final int LUXURY_DOUBLE_START = 1;
    private static final int DELUXE_DOUBLE_START = 11;
    private static final int LUXURY_SINGLE_START = 31;
    private static final int DELUXE_SINGLE_START = 41;
    private static final int MAX_ROOM_NUMBER = 60;

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            loadData();

            char wish;
            do {
                int choice = showMainMenuAndGetChoice();
                switch (choice) {
                    case 1:
                        int featureType = askRoomType();
                        Hotel.features(featureType);
                        break;
                    case 2:
                        int availabilityType = askRoomType();
                        Hotel.availability(availabilityType);
                        break;
                    case 3:
                        int bookType = askRoomType();
                        Hotel.bookRoom(bookType);
                        break;
                    case 4:
                        int orderRoom = askRoomNumber();
                        if (orderRoom != -1) {
                            int orderType = getRoomTypeFromRoomNumber(orderRoom);
                            if (orderType != -1) {
                                Hotel.order(roomIndex(orderRoom, orderType), orderType);
                            } else {
                                System.out.println("Room doesn't exist");
                            }
                        }
                        break;
                    case 5:
                        int checkoutRoom = askRoomNumber();
                        if (checkoutRoom != -1) {
                            int checkoutType = getRoomTypeFromRoomNumber(checkoutRoom);
                            if (checkoutType != -1) {
                                Hotel.deallocate(roomIndex(checkoutRoom, checkoutType), checkoutType);
                            } else {
                                System.out.println("Room doesn't exist");
                            }
                        }
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        saveData();
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }

                wish = askContinue();
            } while (wish == 'y' || wish == 'Y');

            saveData();

        } catch (Exception e) {
            System.out.println("Not a valid input");
        }
    }

    private static void loadData() {
        File f = new File("backup");
        if (f.exists()) {
            try (FileInputStream fin = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fin)) {
                Hotel.hotelData = (Holder) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
    }

    private static void saveData() {
        Thread t = new Thread(new Write(Hotel.hotelData));
        t.start();
    }

    private static int showMainMenuAndGetChoice() {
        System.out.println("\nEnter your choice :");
        System.out.println("1. Display room details");
        System.out.println("2. Display room availability");
        System.out.println("3. Book");
        System.out.println("4. Order food");
        System.out.println("5. Checkout");
        System.out.println("6. Exit");
        System.out.print("Choice: ");
        return sc.nextInt();
    }

    private static int askRoomType() {
        System.out.println("\nChoose room type :");
        System.out.println("1. Luxury Double Room");
        System.out.println("2. Deluxe Double Room");
        System.out.println("3. Luxury Single Room");
        System.out.println("4. Deluxe Single Room");
        System.out.print("Choice: ");
        return sc.nextInt();
    }

    private static int askRoomNumber() {
        System.out.print("Room Number - ");
        int roomNum = sc.nextInt();
        if (roomNum <= 0 || roomNum > MAX_ROOM_NUMBER) {
            System.out.println("Room doesn't exist");
            return -1;
        }
        return roomNum;
    }

    private static int getRoomTypeFromRoomNumber(int roomNum) {
        if (roomNum >= LUXURY_DOUBLE_START && roomNum < DELUXE_DOUBLE_START)
            return 1;
        if (roomNum >= DELUXE_DOUBLE_START && roomNum < LUXURY_SINGLE_START)
            return 2;
        if (roomNum >= LUXURY_SINGLE_START && roomNum < DELUXE_SINGLE_START)
            return 3;
        if (roomNum >= DELUXE_SINGLE_START && roomNum <= MAX_ROOM_NUMBER)
            return 4;
        return -1;
    }

    private static int roomIndex(int roomNum, int roomType) {
        switch (roomType) {
            case 1: return roomNum - LUXURY_DOUBLE_START;
            case 2: return roomNum - DELUXE_DOUBLE_START;
            case 3: return roomNum - LUXURY_SINGLE_START;
            case 4: return roomNum - DELUXE_SINGLE_START;
            default: return -1;
        }
    }

    private static char askContinue() {
        System.out.print("\nContinue? (y/n): ");
        char wish = sc.next().charAt(0);
        while (wish != 'y' && wish != 'Y' && wish != 'n' && wish != 'N') {
            System.out.println("Invalid Option");
            System.out.print("Continue? (y/n): ");
            wish = sc.next().charAt(0);
        }
        return wish;
    }
}
