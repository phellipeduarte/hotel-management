import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Write implements Runnable {
    Hotel hotel;
    Write(Hotel hotel){
        this.hotel = hotel;
    }

    @Override
    public void run() {
        try{
            FileOutputStream fileOutputStream = new FileOutputStream("backup");
            ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(hotel);
        }
        catch(Exception e){
            System.out.println("Error in writing " + e);
        }

    }

}