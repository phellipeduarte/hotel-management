import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Main {
    public static void main(String[] args){
        
        try {
            File file = new File("backup");

            if(file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                Hotel.hotelOb = (Holder)ois.readObject();
            }

            int answer,answer2;
            char wish;
            x:

        do{
            answer = InputOutputHandler.waitIntegerAnswer("Enter your choice :\n1.Display room details\n2.Display room availability \n3.Book\n4.Order food\n5.Checkout\n6.Exit");

            final String CHOOSE_ROOM_TYPE = "Choose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room \n4.Deluxe Single Room";
            final String ROOM_NUMBER = "Room Number - ";

            switch(answer){
                case 1:
                    answer2 = InputOutputHandler.waitIntegerAnswer(CHOOSE_ROOM_TYPE);
                    String features = Reception.getInstance().roomFeatures(answer2);
                    System.out.println(features);
                    break;

                case 2:
                    answer2 = InputOutputHandler.waitIntegerAnswer(CHOOSE_ROOM_TYPE);
                    String availability = Hotel.availability(answer2);
                    System.out.println(availability);
                    break;

                case 3:
                    answer2 = InputOutputHandler.waitIntegerAnswer(CHOOSE_ROOM_TYPE);
                    Hotel.bookroom(answer2);
                    break;

                case 4:
                    answer2 = InputOutputHandler.waitIntegerAnswer(ROOM_NUMBER);
                    try {
                        Integer answer3 = InputOutputHandler.waitIntegerAnswer(Kitchen.getInstance().getMenuOptions());
                        Integer quantity = InputOutputHandler.waitIntegerAnswer("Quantity: ");
                        Kitchen.getInstance().order(answer3, quantity, Hotel.getRoom(answer2));
                        break;
                    } catch (IllegalArgumentException exception){
                        System.out.println(exception.getMessage());
                    }


                case 5:
                     answer2 = InputOutputHandler.waitIntegerAnswer(ROOM_NUMBER);
                     
                     if(answer2>60)
                         System.out.println("Room doesn't exist");
                     else if(answer2>40)
                         Hotel.deallocate(answer2-41,4);
                     else if(answer2>30)
                         Hotel.deallocate(answer2-31,3);
                     else if(answer2>10)
                         Hotel.deallocate(answer2-11,2);
                     else if(answer2>0)
                         Hotel.deallocate(answer2-1,1);
                     else
                         System.out.println("Room doesn't exist");
                     break;

                case 6:
                    break x;

        }
            wish = InputOutputHandler.waitStringAnswer("\nContinue : (y/n)").charAt(0);

            if(!(wish=='y'||wish=='Y'||wish=='n'||wish=='N')){
                System.out.println("Invalid Option");
                wish = InputOutputHandler.waitStringAnswer("\nContinue : (y/n)").charAt(0);
            }

        } while(wish=='y'||wish=='Y');

            Thread t=new Thread(new Write(Hotel.hotelOb));
            t.start();

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
