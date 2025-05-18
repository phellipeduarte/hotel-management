import java.io.*;
import java.util.Map;

public class Main {
    static Reception reception = Reception.getInstance();
    static Kitchen kitchen = Kitchen.getInstance();

    static Hotel hotel = Hotel.getInstance();

    static final String CHOOSE_ROOM_TYPE = "Choose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room \n4.Deluxe Single Room";
    static final String ROOM_NUMBER = "Room Number - ";

    public static void main(String[] args){
        
        try {
            File file = new File("backup");

            if(file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                hotel = (Hotel) objectInputStream.readObject();
            }

            char wish;

            do {
                int answer = InputOutputHandler.waitIntegerAnswer("Enter your choice :\n1.Display room details\n2.Display room availability \n3.Book\n4.Order food\n5.Checkout\n6.Exit");

                Map<Integer, Runnable> options = Map.of(
                        1, Main::displayDetails,
                        2, Main::displayAvailability,
                        3, Main::bookRoom,
                        4, Main::orderFood,
                        5, Main::checkoutRoom,
                        6, Main::exit
                );

                Runnable option = options.get(answer);

                option.run();

                wish = InputOutputHandler.waitStringAnswer("\nContinue : (y/n)").charAt(0);

                if (!(wish == 'y' || wish == 'Y' || wish == 'n' || wish == 'N')) {
                    System.out.println("Invalid Option");
                    wish = InputOutputHandler.waitStringAnswer("\nContinue : (y/n)").charAt(0);
                }

            } while (wish == 'y' || wish == 'Y');

            Thread thread = new Thread(new Write(hotel));
            thread.start();

        }
        catch(FileNotFoundException exception){
            System.out.println(exception.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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
            kitchen.order(menuOption, quantity, hotel.getRoom(getRoomNumber()));
        } catch (IllegalArgumentException exception){
            System.out.println(exception.getMessage());
        }
    }

    private static void checkoutRoom() {
        reception.checkout(getRoomNumber());
    }

    private static void exit() {
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
