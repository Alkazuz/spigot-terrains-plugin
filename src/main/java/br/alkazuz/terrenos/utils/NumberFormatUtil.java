package br.alkazuz.terrenos.utils;

import java.text.NumberFormat;

public class NumberFormatUtil {
    public static String format(double d) {
        return NumberFormat.getInstance().format(d);
    }

}
