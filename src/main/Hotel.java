import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Hotel implements Serializable {

    private final Room[] rooms = new Room[60];

    public Room[] getRooms() {
        return rooms;
    }

    public void allocateRoom(Room room, int roomNumber) throws NotAvailable {
        if(rooms[roomNumber - 1] == null){
            rooms[roomNumber - 1] = room;
        } else {
            throw new NotAvailable();
        }
    }

    public void doubleRoomCustDetails(int roomNumber) throws NotAvailable {
        Guest guest1, guest2;

        guest1 = new Guest();
        guest2 = new Guest();

        allocateRoom(new Room(guest1, guest2), roomNumber);
    }

    public void singleRoomCustDetails(int roomNumber) throws NotAvailable {
        Guest guest;
        guest = new Guest();

        allocateRoom(new Room(guest), roomNumber);
    }

    public void bookRoom(RoomTypeEnum roomType) throws NotAvailable {
        System.out.println("\nChoose room number from: ");

        List<Integer> roomInterval = getRoomIntervalByRoomType(roomType);

        int from = roomInterval.get(0);
        int to = roomInterval.get(1);

        System.out.println(getEmptyRooms(from, to));

        int roomNumber = InputOutputHandler.waitIntegerAnswer("");

        if(roomNumber > 30){
            singleRoomCustDetails(roomNumber);
        } else {
            doubleRoomCustDetails(roomNumber);
        }

        System.out.println("Room Booked");
    }

    public static List<Integer> getRoomIntervalByRoomType(RoomTypeEnum roomType){
        int from = -1, to = -1;

        switch (roomType){
            case LUXURY_DOUBLEROOM -> {
                from = 1;
                to = 10;
            }
            case DELUXE_DOUBLEROOM -> {
                from = 11;
                to = 30;
            }
            case LUXURY_SINGLEROOM -> {
                from = 31;
                to = 40;
            }
            case DELUXE_SINGLEROOM -> {
                from = 41;
                to = 60;
            }
        }

        return Arrays.asList(from, to);
    }

    public String getEmptyRooms(int from, int to){
        StringBuilder emptyRooms = new StringBuilder();

        for(int roomNumber = from; roomNumber < to + 1; roomNumber++){
            if(rooms[roomNumber - 1] == null){
                emptyRooms.append(roomNumber);

                String joiner = roomNumber == to ? "." : ", ";
                emptyRooms.append(joiner);
            }
        }

        return emptyRooms.toString();
    }

    public String availability(RoomTypeEnum roomType) {
        String output;

        try{
            output = "Number of rooms available: " + countAvailableRooms(roomType);
        } catch (IndexOutOfBoundsException exception){
            output = "Enter a valid option";
        }

        return output;
    }

    public Integer countAvailableRooms(RoomTypeEnum roomType){
        Integer count = 0;

        List<Integer> roomInterval = getRoomIntervalByRoomType(roomType);

        int from = roomInterval.get(0);
        int to = roomInterval.get(1);

        Room[] slicedRooms = Arrays.copyOfRange(rooms, from - 1, to);

        for (Room room : slicedRooms) {
            if (room == null) {
                count++;
            }
        }

        return count;
    }

    public void deallocate(int roomNumber){
        Room room = getRoom(roomNumber);

        if(room != null){
            String guestString = room.getGuestsNames();
            String question = "\nDo you want to checkout ?(y/n)\n";

            char wish = InputOutputHandler.waitStringAnswer(guestString + question).charAt(0);

            if(wish == 'y' || wish == 'Y'){
                rooms[roomNumber] = null;
                System.out.println("Room deallocated succesfully");
            }
        } else {
            System.out.println("Room empty already.");
        }
    }

    public Room getRoom(int roomNumber) {
        if(roomNumber > 60 || roomNumber < 1){
            throw new IllegalArgumentException("Enter a valid option");
        }

        return rooms[roomNumber - 1];
    }
}