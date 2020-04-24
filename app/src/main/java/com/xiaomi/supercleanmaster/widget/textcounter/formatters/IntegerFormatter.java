package com.xiaomi.supercleanmaster.widget.textcounter.formatters;

import com.xiaomi.supercleanmaster.widget.textcounter.Formatter;

import java.text.NumberFormat;
import java.util.Locale;

public class IntegerFormatter implements Formatter {
    @Override
    public String format(String prefix, String suffix, float value) {
        return prefix + NumberFormat.getNumberInstance(Locale.US).format(value) + suffix;
    }
}
