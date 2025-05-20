import domain.hotel.Hotel;
import domain.hotel.Kitchen;
import domain.hotel.Reception;
import enums.RoomTypeEnum;
import exceptions.NotAvailable;
import utils.InputOutputHandler;
import utils.Write;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Map;

public class Main {
    static Reception reception = Reception.getInstance();
    static Kitchen kitchen = Kitchen.getInstance();

    static Hotel hotel = Hotel.getInstance();

    static final String CHOOSE_ROOM_TYPE = "Choose room type:\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room \n4.Deluxe Single Room";
    static final String ROOM_NUMBER = "Room Number - ";

    static final String INVALID_OPTION = "Invalid option.";

    private static boolean executing = true;

    public static void main(String[] args) {

        File file = new File("backup");

        if(!file.exists()){
            Write write = new Write(hotel);
            write.run();
        }

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ){

            hotel = (Hotel) objectInputStream.readObject();

            while (executing){
                int answer = InputOutputHandler.waitIntegerAnswer("Enter your choice:\n1.Display room details\n2.Display room availability \n3.Book\n4.Order food\n5.Checkout\n6.Exit");

                Map<Integer, Runnable> options = Map.of(
                        1, Main::displayDetails,
                        2, Main::displayAvailability,
                        3, Main::bookRoom,
                        4, Main::orderFood,
                        5, Main::checkoutRoom,
                        6, Main::exit
                );

                Runnable option = options.get(answer);

                runChoosedOption(option);

                if(executing) {
                    char wish = InputOutputHandler.waitStringAnswer("\nContinue? (y/n)").toLowerCase().charAt(0);

                    if (wish != 'y' && wish != 'n') {
                        System.out.println(INVALID_OPTION);
                    } else {
                        if (wish == 'n') {
                            exit();
                        }
                    }
                }
            }

            Thread thread = new Thread(new Write(hotel));
            thread.start();

        }
        catch(FileNotFoundException exception){
            System.out.println(exception.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("File compromised.");
        }
    }

    private static void runChoosedOption(Runnable option){
        try {
            option.run();
        } catch (NullPointerException | ArrayIndexOutOfBoundsException exception){
            System.out.println(INVALID_OPTION);
        }
    }

    private static void displayDetails() {
        String features = reception.roomFeatures(getRoomTypePositionFromInput());
        System.out.println(features);
    }

    private static void displayAvailability() {
        String availability = hotel.availability(getRoomTypeEnumFromInput());
        System.out.println(availability);
    }

    private static void bookRoom() {
        try{
            reception.checkin(getRoomTypeEnumFromInput());
        } catch (NotAvailable exception){
            System.out.println(exception.getMessage());
        }

    }

    private static void orderFood() {
        try {
            Integer menuOption = InputOutputHandler.waitIntegerAnswer(Kitchen.getInstance().getMenuOptions());
            Integer quantity = InputOutputHandler.waitIntegerAnswer("Quantity: ");

            if(quantity < 1) throw new InvalidParameterException("Invalid quantity.");

            kitchen.order(menuOption, quantity, hotel.getRoom(getRoomNumber()));

        } catch (IllegalArgumentException exception){
            System.out.println(exception.getMessage());
        } catch (IndexOutOfBoundsException exception){
            System.out.println(INVALID_OPTION);
        }
    }

    private static void checkoutRoom() {
        reception.checkout(getRoomNumber());
    }

    private static void exit() {
        executing = false;
    }

    private static int getRoomTypePositionFromInput(){
        return InputOutputHandler.waitIntegerAnswer(CHOOSE_ROOM_TYPE);
    }

    private static RoomTypeEnum getRoomTypeEnumFromInput(){
        return RoomTypeEnum.values()[getRoomTypePositionFromInput() - 1];
    }

    private static int getRoomNumber(){
        return InputOutputHandler.waitIntegerAnswer(ROOM_NUMBER);
    }
}
