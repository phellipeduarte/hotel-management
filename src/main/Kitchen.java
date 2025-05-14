import java.util.ArrayList;

public final class Kitchen {
    private static Kitchen instance = new Kitchen();
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
        String options = "\nMenu: \n";
        Integer index = 1;

        for(Food food : this.getMenu()){
            options += index + ". " + food.getName() + " - " + food.getPrice() + "\n";
            index += 1;
        }

        return options;
    }

    private void setMenu() {
        menu = new ArrayList<>();
        menu.add(new Food("Sandwich", 50));
        menu.add(new Food("Pasta", 60));
        menu.add(new Food("Noodles", 70));
        menu.add(new Food("Coke", 30));
    }

    public void order(Integer menuIndex, Integer quantity, Room room){
        Food choosenFood = menu.get(menuIndex - 1);
        room.addOrder(new Food(choosenFood.getName(), choosenFood.price, quantity));
    }
}
