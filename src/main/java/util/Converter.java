package util;

public class Converter {
    public static int boolToInt(boolean value) {
        // Convert true to 1 and false to 0
        return value ? 1 : 0;
    }

    public static boolean intToBool(int value) {
        // Convert 1 to true and 0 to false
        return value == 1;
    }
}