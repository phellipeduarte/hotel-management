import java.util.ArrayList;
import java.util.Arrays;

public final class Reception {
    private static final Reception instance = new Reception();

    private Reception(){};

    public static Reception getInstance(){
        return instance;
    }

    public String roomFeatures(int option){
        String features;

        ArrayList<Room> roomOptions = new ArrayList<>(Arrays.asList(
                new Room(2, true, 4000.0),
                new Room(2, false, 3000.0),
                new Room(1, true, 2200.0),
                new Room(1, false, 1200.0)
        ));

        try{
            features = roomOptions.get(option - 1).getRoomDetails();
        } catch (IndexOutOfBoundsException exception){
            features = "Enter a valid option";
        }

        return features;
    }
}
