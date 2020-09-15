package com.github.booster.core.exception;

/**
 * @author Yujiumin
 * @version 2020/09/13
 */
public class BoosterException extends RuntimeException {

    public BoosterException(String format, Object... args) {
        super(String.format(format, args));
    }

    public BoosterException(String message) {
        super(message);
    }

}
