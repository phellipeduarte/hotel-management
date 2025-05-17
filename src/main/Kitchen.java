import java.util.ArrayList;

public final class Kitchen {
    private static final Kitchen instance = new Kitchen();
    private ArrayList<Food> menu = getMenu();

    private Kitchen() {
        setMenu();
    }

    public static Kitchen getInstance(){
        return instance;
    }

    public ArrayList<Food> getMenu() {
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
        menu = new ArrayList<>();
        menu.add(new Food("Sandwich", 50.0));
        menu.add(new Food("Pasta", 60.0));
        menu.add(new Food("Noodles", 70.0));
        menu.add(new Food("Coke", 30.0));
    }

    public void order(Integer menuIndex, Integer quantity, Room room){
        room.addOrder(menu.get(menuIndex - 1), quantity);
    }
}
