import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class Room {
    private Integer bedCapacity;
    private Boolean ac;
    private Double charge;

    private ArrayList<Order> orders = new ArrayList<>();

    public void addOrder(Food food, int quantity){
        orders.add(new Order(food, quantity));
    }

    public Order getOrder(Integer orderIndex){
        return orders.get(orderIndex);
    }

    public ArrayList<Order> getOrders(){
        return orders;
    }

    public Integer getBedCapacity() {
        return bedCapacity;
    }

    public void setBedCapacity(Integer bedCapacity) {
        this.bedCapacity = bedCapacity;
    }

    public Boolean getAc() {
        return ac;
    }

    public void setAc(Boolean ac) {
        this.ac = ac;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public String getRoomDetails(){
        String bed = "Bed capacity: " + getBedCapacity() + "\n";
        String ac = "AC: " + (getAc() ? "Yes" : "No") + "\n";
        String freeBreakfast = "Free breakfast: Yes\n";
        String chargeDay = "Charge per day: R$" + String.format(Locale.US, "%.2f", getCharge()) + "\n";
        return bed + ac + freeBreakfast + chargeDay;
    }
}
