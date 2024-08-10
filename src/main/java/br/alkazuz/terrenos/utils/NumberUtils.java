package br.alkazuz.terrenos.utils;

public class NumberUtils {

    public static final String[] SUFFIXS = new String[]{
            "", "K", "KK", "KKK", "KKKK"};

    public static String formatWithSuffix(double value) {
        if (value == Double.NEGATIVE_INFINITY) return "Extremely Low";
        if (value < 0) return "-" + formatWithSuffix(-value);
        if (value < 1000) return String.format("%.2f", value);
        int mag = (int) (Math.log(value) / Math.log(1000));
        if (mag >= SUFFIXS.length) return "HUGE";
        return String.format("%.1f%s", value / Math.pow(1000, mag), SUFFIXS[mag]);
    }

    public static String formatWithSuffix(int value) {
        if (value == Double.NEGATIVE_INFINITY) return "Extremely Low";
        if (value < 0) return "-" + formatWithSuffix(-value);
        if (value < 1000) return String.valueOf(value);
        int mag = (int) (Math.log(value) / Math.log(1000));
        if (mag >= SUFFIXS.length) return "HUGE";
        return String.format("%.1f%s", value / Math.pow(1000, mag), SUFFIXS[mag]);
    }

    public static boolean canParse(String text) {
        if (text == null || text.isEmpty()) return false;
        if (text.matches("-?[0-9]+\\.?[0-9]*")) return true;

        int suffixIndex = findSuffixIndex(text);
        if (suffixIndex == -1) {
            return false;
        }

        return true;
    }

    public static double parseWithSuffix(String text) {
        if (text == null || text.isEmpty()) return 0.0;
        text = text.toUpperCase();
        if (text.matches("-?[0-9]+\\.?[0-9]*")) return Double.parseDouble(text);

        int suffixIndex = findSuffixIndex(text);
        if (suffixIndex == -1) {
            throw new IllegalArgumentException("Invalid suffix in input string.");
        }
        double multiplier = Math.pow(1000, suffixIndex);
        double numberPart = Double.parseDouble(text.replaceAll("[^\\d.-]", ""));
        return numberPart * multiplier;
    }

    private static int findSuffixIndex(String text) {
        for (int i = SUFFIXS.length - 1; i >= 0; i--) {
            if (text.endsWith(SUFFIXS[i])) {
                return i;
            }
        }
        return -1;
    }

}
