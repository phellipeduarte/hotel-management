import java.util.ArrayList;

public class Room {
    private Integer capacity;
    private Boolean luxury;
    private Double charge;

    private ArrayList<Order> orders = new ArrayList<>();

    private ArrayList<Guest> guests;

    public Room(Integer capacity, Boolean luxury, Double charge) {
        this.capacity = capacity;
        this.luxury = luxury;
        this.charge = charge;
    }

    public Room(Guest guest) {
        capacity = 1;
        guests = new ArrayList<>();
        guests.add(guest);
    }

    public Room(Guest guest1, Guest guest2) {
        capacity = 2;
        guests = new ArrayList<>();
        guests.add(guest1);
        guests.add(guest2);
    }


    public Integer getCapacity() {
        return capacity;
    }

    public Boolean getLuxury() {
        return luxury;
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

    public ArrayList<Order> getOrders(){
        return orders;
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
        String ac = "AC: " + (getLuxury() ? "Yes" : "No") + "\n";
        String freeBreakfast = "Free breakfast: Yes\n";
        String chargeDay = "Charge per day: " + Utils.getValueLocalCurrency(getCharge()) + "\n";
        return bed + ac + freeBreakfast + chargeDay;
    }
}
