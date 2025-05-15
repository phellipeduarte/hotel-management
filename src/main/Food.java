import java.io.Serializable;
import java.util.Locale;

public class Food implements Serializable {

    private String name;

    private Double price;

    public Food(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Double getPrice(){
        return price;
    }

    public static String getValueLocalCurrency(Double value){
        return "R$" + String.format(Locale.US, "%.2f", value);
    }
}