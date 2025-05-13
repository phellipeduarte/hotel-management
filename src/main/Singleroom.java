import java.io.Serializable;

public class Singleroom extends Room implements Serializable {
    private Guest guest;

    public Singleroom(String name, String contact, String gender) {
        this.setBedCapacity(1);
        this.guest = new Guest(name, contact, gender);
    }

    public Singleroom(Boolean ac, Double charge){
        this.setBedCapacity(1);
        this.setAc(ac);
        this.setCharge(charge);
    }

    public String getGuestName(){
        return guest.getName();
    }
}