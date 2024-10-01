package org.ys.game.runtime.exception;

public class ShouldNeverHappenException extends Exception {

    public ShouldNeverHappenException() {
    }

    public ShouldNeverHappenException(String message) {
        super(message);
    }

}
