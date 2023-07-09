package it.xtreamdev.gflbe.util;

import java.text.NumberFormat;

public class FormatUtils {

    static public NumberFormat percentInstanceFormatter;

    static {
        percentInstanceFormatter = NumberFormat.getPercentInstance();
        percentInstanceFormatter.setMinimumIntegerDigits(1);
        percentInstanceFormatter.setMinimumFractionDigits(0);
        percentInstanceFormatter.setMaximumFractionDigits(2);
    }

}
