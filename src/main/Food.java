import java.io.Serializable;
import java.util.Locale;

public class Food implements Serializable {

    private String name;
    int itemno;
    int quantity;
    float price;

    public Food(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public Food(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Food(int itemno, int quantity){
        this.itemno = itemno;
        this.quantity = quantity;

        switch(itemno){
            case 1: price = quantity * 50;
                break;
            case 2: price = quantity * 60;
                break;
            case 3: price = quantity * 70;
                break;
            case 4: price = quantity * 30;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public String getPrice(){
        return "R$" + String.format(Locale.US, "%.2f", price);
    }
}