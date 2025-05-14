public class Order {

    private Food food;
    private int quantity;

    public Order(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }
}
