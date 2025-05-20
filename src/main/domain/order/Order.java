package domain.order;

import java.io.Serializable;

public class Order implements Serializable {

    private Food food;
    private int quantity;

    public Order(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getTotal(){
        return food.getPrice() * quantity;
    }
}
