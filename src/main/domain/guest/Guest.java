package domain.guest;

import utils.InputOutputHandler;

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

    public Guest(){
        this.name = InputOutputHandler.waitStringAnswer("Enter guest name: ");
        this.contact = InputOutputHandler.waitStringAnswer("Enter guest contact number: ");
        this.gender = InputOutputHandler.waitStringAnswer("Enter guest gender: ");
    }

    public String getName() {
        return name;
    }

    public String getGuestInformation(){
        return "Guest name: " + name + "\nGuest contact: " + contact + "\nGuest gender: " + gender + "\n";
    }
}
