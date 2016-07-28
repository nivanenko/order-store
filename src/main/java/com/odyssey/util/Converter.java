package com.odyssey.util;

public class Converter {
    /**
     * Convert true to 1 and false to 0
     *
     * @param value to be converted
     * @return 1 if true, 0 if false
     */
    public static int boolToInt(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Convert 1 to true and 0 to false
     *
     * @param value to be converted
     * @return true, if 1, false if 0
     */
    public static boolean intToBool(int value) {
        return value == 1;
    }
}