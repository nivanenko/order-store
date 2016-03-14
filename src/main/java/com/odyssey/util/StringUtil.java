package com.odyssey.util;

import java.util.regex.Pattern;

public class StringUtil {
    public static String deleteSpaces(String str) {
        return Pattern.compile("\\s+$").matcher(str).replaceAll("");
    }
}