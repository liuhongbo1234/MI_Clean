package com.xiaomi.supercleanmaster.widget.textcounter.formatters;

import com.xiaomi.supercleanmaster.widget.textcounter.Formatter;

import java.text.DecimalFormat;

public class DecimalFormatter implements Formatter {

    private final DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public String format(String prefix, String suffix, float value) {
        return prefix + format.format(value) + suffix;
    }
}
