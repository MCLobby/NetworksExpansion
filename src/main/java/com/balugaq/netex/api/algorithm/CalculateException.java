package com.balugaq.netex.api.algorithm;

import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 */
@NullMarked
public class CalculateException extends Exception {
    public CalculateException(String message) {
        super(message);
    }
}
