package domain.hotel;

import domain.order.Food;
import domain.order.Order;
import domain.guest.Guest;
import utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private Integer capacity;
    private Boolean luxury;
    private Double charge;

    private List<Order> orders = new ArrayList<>();

    private List<Guest> guests;

    public Room(Integer capacity, Boolean luxury, Double charge) {
        this.capacity = capacity;
        this.luxury = luxury;
        this.charge = charge;
    }

    public Room(List<Guest> guestList, Room roomFrame){
        capacity = roomFrame.capacity;
        luxury = roomFrame.luxury;
        charge = roomFrame.charge;
        guests = guestList;
    }


    public Integer getCapacity() {
        return capacity;
    }

    public void setLuxury(Boolean luxury) {
        this.luxury = luxury;
    }

    public Double getCharge() {
        return charge;
    }

    public Double getOrdersTotalValue(){
        return orders.stream().mapToDouble(Order::getTotal).sum();
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public void addOrder(Food food, int quantity){
        orders.add(new Order(food, quantity));
    }

    public Order getOrder(Integer orderIndex){
        return orders.get(orderIndex);
    }

    public List<Order> getOrders(){
        return orders;
    }

    public String getAc(){
        if(Boolean.TRUE.equals(luxury)){
            return "Yes";
        } else {
            return "No";
        }
    }

    public String getGuestsNames(){
        StringBuilder output = new StringBuilder();

        if(guests.size() == 1){
            output = new StringBuilder(guests.get(0).getName());
        } else {
            for(Guest guest : guests) output.append(guest.getName()).append(guest.equals(guests.get(guests.size() - 1)) ? "." : ", ");
        }

        return output.toString();
    }

    public String getRoomDetails(){
        String bed = "Room capacity: " + getCapacity() + "\n";
        String ac = "AC: " + getAc() + "\n";
        String freeBreakfast = "Free breakfast: Yes\n";
        String chargeDay = "Charge per day: " + Utils.getValueLocalCurrency(getCharge()) + "\n";
        return bed + ac + freeBreakfast + chargeDay;
    }

    public String getGuestsInformation(){
        StringBuilder guestInformation = new StringBuilder();

        for(Guest guest : guests){
            guestInformation.append(guest.getGuestInformation());
        }

        return guestInformation.toString();
    }
}
