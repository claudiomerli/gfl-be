package it.xtreamdev.gflbe.util;

import com.github.bogdanovmn.humanreadablevalues.FractionDefinition;
import com.github.bogdanovmn.humanreadablevalues.FractionSpecification;
import com.github.bogdanovmn.humanreadablevalues.FractionatedValue;

import java.text.NumberFormat;

public class FormatUtils {

    static public NumberFormat percentInstanceFormatter;

    static {
        percentInstanceFormatter = NumberFormat.getPercentInstance();
        percentInstanceFormatter.setMinimumIntegerDigits(1);
        percentInstanceFormatter.setMinimumFractionDigits(0);
        percentInstanceFormatter.setMaximumFractionDigits(2);
    }

    public static class HighNumberValue extends FractionatedValue {
        public HighNumberValue(long value) {
            super(value, new FractionSpecification(
                    FractionDefinition
                            .builder()
                            .shortNotation("K")
                            .minimalUnitsAmount(1000)
                            .build(),
                    FractionDefinition
                            .builder()
                            .shortNotation("M")
                            .minimalUnitsAmount(1000000)
                            .build()
            ));
        }
    }

}
