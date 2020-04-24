package com.xiaomi.supercleanmaster.widget.textcounter.formatters;

import com.xiaomi.supercleanmaster.widget.textcounter.Formatter;

/**
 * Performs no formatting
 */
public class NoFormatter implements Formatter {

    @Override
    public String format(String prefix, String suffix, float value) {
        return prefix + value + suffix;
    }
}
