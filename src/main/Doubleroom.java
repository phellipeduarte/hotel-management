import java.io.Serializable;
import java.util.ArrayList;

public class Doubleroom extends Room implements Serializable {
    private ArrayList<Guest> guests;

    public Doubleroom(String name1, String name2, String contact1, String contact2, String gender1, String gender2) {
        this.setBedCapacity(2);

        Guest guest1 = new Guest(name1, contact1, gender1);
        Guest guest2 = new Guest(name2,contact2, gender2);

        this.guests = new ArrayList<>();

        this.guests.add(guest1);
        this.guests.add(guest2);
    }

    public Doubleroom(Boolean ac, Double charge){
        this.setBedCapacity(2);
        this.setAc(ac);
        this.setCharge(charge);
    }

    public String getGuestsNames(){
        return getGuest(0).getName() + " - " + getGuest(1).getName();
    }

    public Guest getGuest(Integer index){
        return getGuests().get(index);
    }

    public ArrayList<Guest> getGuests() {
        return this.guests;
    }
}
