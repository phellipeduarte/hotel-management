package utils;

import java.util.Locale;

public class Utils {

    private Utils(){}

    public static String getValueLocalCurrency(Double value){
        return "R$" + String.format(Locale.US, "%.2f", value);
    }
}
