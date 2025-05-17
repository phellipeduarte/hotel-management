import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Hotel implements Serializable {

    private static final Hotel instance = new Hotel();

    private Room[] rooms = new Room[60];

    private Hotel(){}

    public static Hotel getInstance(){
        return instance;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public List<Integer> getRoomIntervalByRoomType(RoomTypeEnum roomType){
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

    public Room getRoom(int roomNumber) {
        if(roomNumber > 60 || roomNumber < 1){
            throw new IllegalArgumentException("Enter a valid option");
        }

        return rooms[roomNumber - 1];
    }

    public void allocateRoom(Room room, int roomNumber) throws NotAvailable {
        if(rooms[roomNumber - 1] == null){
            rooms[roomNumber - 1] = room;
        } else {
            throw new NotAvailable();
        }
    }

    public void deallocateRoom(int roomNumber){
        rooms[roomNumber - 1] = null;
    }
}