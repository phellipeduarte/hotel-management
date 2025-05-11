import java.io.Serializable;

public class Guest implements Serializable {
    private String name;
    private String contact;
    private String gender;

    public Guest(String name, String contact, String gender) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }
}
