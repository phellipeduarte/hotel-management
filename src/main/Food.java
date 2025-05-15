import java.io.Serializable;

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

}