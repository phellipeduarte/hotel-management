import java.util.Scanner;

public class Hotel {
    static Holder hotelOb = new Holder();
    static Scanner scanner = new Scanner(System.in);
    public static void CustDetails(int i, int rn){
        String name, contact, gender;
        String name2 = null, contact2 = null;
        String gender2 = "";

        System.out.print("\nEnter customer name: ");

        name = scanner.next();

        System.out.print("Enter contact number: ");

        contact = scanner.next();

        System.out.print("Enter gender: ");

        gender = scanner.next();

        if(i<3) {
            System.out.print("Enter second customer name: ");

            name2 = scanner.next();

            System.out.print("Enter contact number: ");

            contact2= scanner.next();

            System.out.print("Enter gender: ");

            gender2 = scanner.next();
        }

        switch (i) {
            case 1: hotelOb.luxuryDoublerrom[rn] = new Doubleroom(name,contact,gender,name2,contact2,gender2);
                break;
            case 2: hotelOb.deluxeDoublerrom[rn] = new Doubleroom(name,contact,gender,name2,contact2,gender2);
                break;
            case 3: hotelOb.luxurySingleerrom[rn] = new Singleroom(name,contact,gender);
                break;
            case 4: hotelOb.deluxeSingleerrom[rn] = new Singleroom(name,contact,gender);
                break;
            default: System.out.println("Wrong option");
                break;
        }
    }

    public static void bookroom(int i){
        int j;
        int rn;

        System.out.println("\nChoose room number from : ");

        switch (i) {
            case 1:
                for(j = 0; j < hotelOb.luxuryDoublerrom.length; j++){
                    if(hotelOb.luxuryDoublerrom[j] == null){
                        System.out.print(j+1+",");
                    }
                }

                System.out.print("\nEnter room number: ");

                try{
                    rn = scanner.nextInt();
                    rn--;

                    if(hotelOb.luxuryDoublerrom[rn] != null)
                        throw new NotAvailable();
                    CustDetails(i,rn);
                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }

                break;

            case 2:
                for(j = 0; j < hotelOb.deluxeDoublerrom.length; j++)
                {
                    if(hotelOb.deluxeDoublerrom[j] == null){
                        System.out.print(j + 11 + ",");
                    }
                }

                System.out.print("\nEnter room number: ");

                try{
                    rn = scanner.nextInt();
                    rn = rn - 11;

                    if(hotelOb.deluxeDoublerrom[rn] != null)
                        throw new NotAvailable();
                    CustDetails(i,rn);

                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }

                break;
            case 3:

                for(j = 0; j < hotelOb.luxurySingleerrom.length; j++){
                    if(hotelOb.luxurySingleerrom[j] == null){
                        System.out.print(j+31+",");
                    }
                }

                System.out.print("\nEnter room number: ");

                try{
                    rn = scanner.nextInt();
                    rn = rn - 31;

                    if(hotelOb.luxurySingleerrom[rn] != null)
                        throw new NotAvailable();
                    CustDetails(i,rn);

                }
                catch(Exception e){
                    System.out.println("Invalid Option");
                    return;
                }
                break;

            case 4:
                for(j = 0; j < hotelOb.deluxeSingleerrom.length; j++){
                    if(hotelOb.deluxeSingleerrom[j] == null){
                        System.out.print(j+41+",");
                    }
                }

                System.out.print("\nEnter room number: ");

                try{
                    rn = scanner.nextInt();
                    rn = rn - 41;

                    if(hotelOb.deluxeSingleerrom[rn] != null)
                        throw new NotAvailable();

                    CustDetails(i, rn);
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

    public static void features(int i){
        switch (i) {
            case 1: System.out.println("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:4000 ");
                break;
            case 2: System.out.println("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:3000  ");
                break;
            case 3: System.out.println("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:2200  ");
                break;
            case 4: System.out.println("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:1200 ");
                break;
            default:
                System.out.println("Enter valid option");
                break;
        }
    }

    public static void availability(int i)
    {
        int j, count = 0;

        switch (i) {
            case 1:
                for(j = 0; j < 10; j++){
                    if(hotelOb.luxuryDoublerrom[j] == null)
                        count++;
                }
                break;

            case 2:
                for(j = 0; j < hotelOb.deluxeDoublerrom.length; j++){
                    if(hotelOb.deluxeDoublerrom[j] == null)
                        count++;
                }
                break;

            case 3:
                for(j = 0; j < hotelOb.luxurySingleerrom.length; j++){
                    if(hotelOb.luxurySingleerrom[j] == null)
                        count++;
                }
                break;

            case 4:
                for(j = 0; j < hotelOb.deluxeSingleerrom.length; j++){
                    if(hotelOb.deluxeSingleerrom[j] == null)
                        count++;
                }
                break;

            default:
                System.out.println("Enter valid option");
                break;
        }

        System.out.println("Number of rooms available : "+count);
    }

    public static void bill(int rn, int rtype)
    {
        double amount = 0;
        String list[] = {"Sandwich","Pasta","Noodles","Coke"};
        System.out.println("\n*******");
        System.out.println(" Bill:-");
        System.out.println("*******");

        switch(rtype){
            case 1:
                amount += 4000;
                System.out.println("\nRoom Charge - "+4000);
                System.out.println("\n===============");
                System.out.println("Food Charges:- ");
                System.out.println("===============");
                System.out.println("Item   Quantity    Price");
                System.out.println("-------------------------");
                for(Food obb: hotelOb.luxuryDoublerrom[rn].orders){
                    amount += obb.price;
                    String format = "%-10s%-10s%-10s%n";
                    System.out.printf(format, list[obb.itemno - 1], obb.quantity, obb.price);
                }

                break;
            case 2:
                amount += 3000;
                System.out.println("Room Charge - "+3000);
                System.out.println("\nFood Charges:- ");
                System.out.println("===============");
                System.out.println("Item   Quantity    Price");
                System.out.println("-------------------------");
                for(Food obb: hotelOb.deluxeDoublerrom[rn].orders){
                    amount += obb.price;
                    String format = "%-10s%-10s%-10s%n";
                    System.out.printf(format, list[obb.itemno - 1], obb.quantity, obb.price);
                }
                break;

            case 3:
                amount += 2200;
                System.out.println("Room Charge - "+2200);
                System.out.println("\nFood Charges:- ");
                System.out.println("===============");
                System.out.println("Item   Quantity    Price");
                System.out.println("-------------------------");
                for(Food obb: hotelOb.luxurySingleerrom[rn].orders){
                    amount += obb.price;
                    String format = "%-10s%-10s%-10s%n";
                    System.out.printf(format, list[obb.itemno - 1], obb.quantity, obb.price);
                }
                break;

            case 4:
                amount += 1200;
                System.out.println("Room Charge - "+1200);
                System.out.println("\nFood Charges:- ");
                System.out.println("===============");
                System.out.println("Item   Quantity    Price");
                System.out.println("-------------------------");
                for(Food obb: hotelOb.deluxeSingleerrom[rn].orders){
                    amount += obb.price;
                    String format = "%-10s%-10s%-10s%n";
                    System.out.printf(format, list[obb.itemno - 1], obb.quantity, obb.price);
                }
                break;

            default:
                System.out.println("Not valid");
        }

        System.out.println("\nTotal Amount- "+amount);
    }

    public static void deallocate(int rn, int rtype){
        int j;
        char w;

        switch (rtype) {
            case 1:
                if(hotelOb.luxuryDoublerrom[rn] != null)
                    System.out.println("Room used by "+ hotelOb.luxuryDoublerrom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println("Do you want to checkout ?(y/n)");
                w= scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    bill(rn, rtype);
                    hotelOb.luxuryDoublerrom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 2:
                if(hotelOb.deluxeDoublerrom[rn] != null)
                    System.out.println("Room used by " + hotelOb.deluxeDoublerrom[rn].getGuestsNames());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ?(y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    bill(rn, rtype);
                    hotelOb.deluxeDoublerrom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 3:
                if(hotelOb.luxurySingleerrom[rn] != null)
                    System.out.println("Room used by "+ hotelOb.luxurySingleerrom[rn].getGuestName());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ? (y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w=='Y'){
                    bill(rn, rtype);
                    hotelOb.luxurySingleerrom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }

                break;

            case 4:
                if(hotelOb.deluxeSingleerrom[rn] != null)
                    System.out.println("Room used by " + hotelOb.deluxeSingleerrom[rn].getGuestName());
                else{
                    System.out.println("Empty Already");
                    return;
                }

                System.out.println(" Do you want to checkout ? (y/n)");
                w = scanner.next().charAt(0);

                if(w == 'y' || w == 'Y'){
                    bill(rn, rtype);
                    hotelOb.deluxeSingleerrom[rn] = null;
                    System.out.println("Deallocated succesfully");
                }
                break;

            default:
                System.out.println("\nEnter valid option : ");
                break;
        }
    }

    public static void order(int room, int roomType)
    {
        int i, q;
        char wish;

        try{
            System.out.println(Kitchen.getInstance().getMenuOptions());
            do {
                i = scanner.nextInt();
                System.out.print("Quantity - ");
                q = scanner.nextInt();

                switch(roomType){
                    case 1: hotelOb.luxuryDoublerrom[room].orders.add(new Food(i, q));
                        break;
                    case 2: hotelOb.deluxeDoublerrom[room].orders.add(new Food(i, q));
                        break;
                    case 3: hotelOb.luxurySingleerrom[room].orders.add(new Food(i, q));
                        break;
                    case 4: hotelOb.deluxeSingleerrom[room].orders.add(new Food(i,q));
                        break;
                }

                System.out.println("Do you want to order anything else ? (y/n)");
                wish = scanner.next().charAt(0);

            } while(wish == 'y' || wish == 'Y');
        }
        catch(NullPointerException e){
            System.out.println("\nRoom not booked");
        }
        catch(Exception e){
            System.out.println("Cannot be done");
        }
    }
}