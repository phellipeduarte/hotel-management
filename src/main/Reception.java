import java.util.ArrayList;
import java.util.Arrays;

public final class Reception {
    private static final Reception instance = new Reception();

    private Reception(){};

    public static Reception getInstance(){
        return instance;
    }

    public String features(int option){
        ArrayList<Room> roomOptions = new ArrayList<>(Arrays.asList(
                new Doubleroom(true, 4000.0),
                new Doubleroom(false, 3000.0),
                new Singleroom(true, 2200.0),
                new Singleroom(false, 1200.0)
        ));

        return roomOptions.get(option - 1).getRoomDetails();
    }
}
