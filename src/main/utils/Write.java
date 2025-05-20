package utils;

import domain.hotel.Hotel;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Write implements Runnable {
    Hotel hotel;
    public Write(Hotel hotel){
        this.hotel = hotel;
    }

    @Override
    public void run() {

        try(
            FileOutputStream fileOutputStream = new FileOutputStream("backup");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        ){
            objectOutputStream.writeObject(hotel);
        }
        catch(Exception e){
            System.out.println("Error in writing " + e);
        }

    }

}