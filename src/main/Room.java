import java.util.ArrayList;

public class Room {
    ArrayList<Food> orders = new ArrayList<>();

    public void addOrder(Food food){
        orders.add(food);
    }

    public Food getOrder(Integer orderIndex){
        return orders.get(orderIndex);
    }
}
