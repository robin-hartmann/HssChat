package de.hss.sae.sue.chat.client;

/**
 * Created by Robin on 05.01.2017.
 */

public class Utils {
    private Utils(){
    }

    public static boolean isValidInteger(String value) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPort(String value) {
        if (!isValidInteger(value)) return false;
        int port = Integer.parseInt(value);
        return port >= 0 && port <= 65535;
    }
}
