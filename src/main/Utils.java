import java.util.Locale;

public class Utils {
    public static String getValueLocalCurrency(Double value){
        return "R$" + String.format(Locale.US, "%.2f", value);
    }
}
