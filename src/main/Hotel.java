import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Hotel {
    static Holder hotelOb = new Holder();
    static Scanner scanner = new Scanner(System.in);
    public static void custDetails(int option, int room){
        Guest guest1, guest2;
        String name, contact, gender;
        String name2 = null, contact2 = null;
        String gender2 = "";

        name = InputOutputHandler.waitStringAnswer("Enter customer name: ");

        contact = InputOutputHandler.waitStringAnswer("Enter contact number: ");

        gender = InputOutputHandler.waitStringAnswer("Enter gender: ");

        guest1 = new Guest(name, contact, gender);

        if(option > 4 || option < 1){
            throw new IllegalArgumentException("Enter a valid option.");
        }

        if(option < 3) {

            name2 = InputOutputHandler.waitStringAnswer("Enter second customer name: ");

            contact2= InputOutputHandler.waitStringAnswer("Enter contact number: ");

            gender2 = InputOutputHandler.waitStringAnswer("Enter gender: ");

            guest2 = new Guest(name2, contact2, gender2);

            if(option == 1){
                hotelOb.luxuryDoubleroom[room] = new Room(guest1, guest2);
            } else {
                hotelOb.deluxeDoubleroom[room] = new Room(guest1, guest2);
            }

            return;
        } else {

            if(option == 3) {
                hotelOb.luxurySingleeroom[room] = new Room(guest1);
            } else {
                hotelOb.deluxeSingleeroom[room] = new Room(guest1);
            }
        }
    }

    public static void bookroom(int i){
        int j;
        int rn;

        System.out.println("\nChoose room number from: ");

        switch (i) {
            case 1:
                for(j = 0; j < hotelOb.luxuryDoubleroom.length; j++){
                    if(hotelOb.luxuryDoubleroom[j] == null){
                        System.out.print(j+1+",");
                    }
                }

                try{
                    rn = InputOutputHandler.waitIntegerAnswer("Enter room number: ");
                    rn--;

                    if(hotelOb.luxuryDoubleroom[rn] != null)
                        throw new NotAvailable();
                    custDetails(i,rn);
                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }

                break;

            case 2:
                for(j = 0; j < hotelOb.deluxeDoubleroom.length; j++)
                {
                    if(hotelOb.deluxeDoubleroom[j] == null){
                        System.out.print(j + 11 + ",");
                    }
                }


                try{
                    rn = InputOutputHandler.waitIntegerAnswer("Enter room number: ");
                    rn = rn - 11;

                    if(hotelOb.deluxeDoubleroom[rn] != null)
                        throw new NotAvailable();
                    custDetails(i,rn);

                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }

                break;
            case 3:

                for(j = 0; j < hotelOb.luxurySingleeroom.length; j++){
                    if(hotelOb.luxurySingleeroom[j] == null){
                        System.out.print(j+31+",");
                    }
                }

                try{
                    rn = InputOutputHandler.waitIntegerAnswer("Enter room number: ");
                    rn = rn - 31;

                    if(hotelOb.luxurySingleeroom[rn] != null)
                        throw new NotAvailable();
                    custDetails(i,rn);

                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }
                break;

            case 4:
                for(j = 0; j < hotelOb.deluxeSingleeroom.length; j++){
                    if(hotelOb.deluxeSingleeroom[j] == null){
                        System.out.print(j + 41 + ",");
                    }
                }

                try{
                    rn = InputOutputHandler.waitIntegerAnswer("Enter room number: ");
                    rn = rn - 41;

                    if(hotelOb.deluxeSingleeroom[rn] != null)
                        throw new NotAvailable();

                    custDetails(i, rn);
                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }

                break;

            default:

                System.out.println("Enter valid option");
                break;
        }
        System.out.println("Room Booked");
    }

    public static String availability(int option)
    {
        String output;

        List<Room[]> roomArrayOptions = Arrays.asList(
            hotelOb.luxuryDoubleroom,
            hotelOb.deluxeDoubleroom,
            hotelOb.luxurySingleeroom,
            hotelOb.deluxeSingleeroom
        );

        try{
            output = "Number of rooms available: " + countAvailableRooms(roomArrayOptions.get(option - 1));
        } catch (IndexOutOfBoundsException exception){
            output = "Enter a valid option";
        }

        return output;
    }

    public static Integer countAvailableRooms(Room[] roomArray){
        Integer count = 0;

        for (Room room : roomArray) {
            if (room == null) {
                count++;
            }
        }

        return count;
    }

    public static void deallocate(int rn, int rtype){
        int j;
        char w;

        switch (rtype) {
            case 1:
                if(hotelOb.luxuryDoubleroom[rn] != null)
                    System.out.println("Room used by "+ hotelOb.luxuryDoubleroom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println("Do you want to checkout ?(y/n)");
                w= scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    hotelOb.luxuryDoubleroom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 2:
                if(hotelOb.deluxeDoubleroom[rn] != null)
                    System.out.println("Room used by " + hotelOb.deluxeDoubleroom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ?(y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    hotelOb.deluxeDoubleroom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 3:
                if(hotelOb.luxurySingleeroom[rn] != null)
                    System.out.println("Room used by "+ hotelOb.luxurySingleeroom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ? (y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w=='Y'){
                    hotelOb.luxurySingleeroom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 4:
                if(hotelOb.deluxeSingleeroom[rn] != null)
                    System.out.println("Room used by " + hotelOb.deluxeSingleeroom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ? (y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    hotelOb.deluxeSingleeroom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }
                break;

            default:
                System.out.println("\nEnter valid option : ");
                break;
        }
    }

    public static Room getRoom(int roomNumber) {
        Room room;

        if(roomNumber > 60 || roomNumber < 1){
            throw new IllegalArgumentException("Enter a valid option");
        }

        if(roomNumber < 11){
            room = hotelOb.luxuryDoubleroom[roomNumber - 1];
        } else{
            if(roomNumber < 31){
                room = hotelOb.deluxeDoubleroom[roomNumber - 11];
            } else {
                if(roomNumber < 41){
                    room = hotelOb.luxurySingleeroom[roomNumber - 31];
                } else {
                    room = hotelOb.deluxeSingleeroom[roomNumber - 41];
                }
            }
        }

        return room;
    }
}