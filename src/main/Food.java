import java.io.Serializable;
import java.util.Locale;

public class Food implements Serializable {

    private String name;

    private float price;

    public Food(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice(){
        return "R$" + String.format(Locale.US, "%.2f", price);
    }
}