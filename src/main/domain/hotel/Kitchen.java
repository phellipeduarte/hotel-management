package domain.hotel;

import domain.order.Food;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class Kitchen {
    private static final Kitchen instance = new Kitchen();
    private List<Food> menu = getMenu();

    private Kitchen() {
        setMenu();
    }

    public static Kitchen getInstance(){
        return instance;
    }

    public List<Food> getMenu() {
        return menu;
    }

    public String getMenuOptions(){
        StringBuilder options = new StringBuilder("\nMenu:\n");
        Integer index = 1;

        for(Food food : this.getMenu()){
            options.append(index).append(". ").append(food.getName()).append(" - ").append(Utils.getValueLocalCurrency(food.getPrice())).append("\n");
            index += 1;
        }

        return options.toString();
    }

    private void setMenu() {
        menu = List.of(
            new Food("Sandwich", 50.0),
            new Food("Pasta", 60.0),
            new Food("Noodles", 70.0),
            new Food("Coke", 30.0)
        );
    }

    public void order(Integer menuIndex, Integer quantity, Room room){
        room.addOrder(menu.get(menuIndex - 1), quantity);
    }
}
