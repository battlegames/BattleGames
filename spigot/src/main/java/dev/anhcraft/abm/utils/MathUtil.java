package dev.anhcraft.abm.utils;

import java.text.DecimalFormat;

public class MathUtil {
    private static final DecimalFormat format = new DecimalFormat();

    static {
        format.setGroupingUsed(false);
    }

    public static String round(double number, int fractionDigits){
        format.setMaximumFractionDigits(fractionDigits);
        return format.format(number);
    }
}
