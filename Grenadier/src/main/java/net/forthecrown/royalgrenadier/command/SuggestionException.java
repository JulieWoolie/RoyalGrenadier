package net.forthecrown.royalgrenadier.command;

public class SuggestionException extends RuntimeException {
    public static final String MESSAGE = "Exception occurred during suggestion creation, if you believe this is a fault with the Grenadier plugin, please report the issue";

    static SuggestionException create(Throwable cause) {
        return new SuggestionException(MESSAGE, cause);
    }

    public SuggestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
