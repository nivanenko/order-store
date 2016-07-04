package com.odyssey.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * The type Util.
 */
public class Util {
    public static String deleteSpaces(String str) {
        return Pattern.compile("\\s+$").matcher(str).replaceAll("");
    }

    /**
     * Check whether all the elements in the byte array are identical.<br>
     * True - identical.<br>
     * False - different.
     * @param bytes byte array to check it
     * @return true - bytes have equal elements
     */
    public static boolean equalElements(byte[] bytes) {
        boolean flag = true;
        byte first = bytes[0];

        for (int i = 1; i < bytes.length && flag; i++) {
            if (bytes[i] != first) flag = false;
        }
        return flag;
    }


    /**
     * Method deletes redundant 0 (zero) bytes
     * from the byte array
     * @param bytes with w/ zeroes
     * @return bytes[] w/o zeroes
     */
    public static byte[] trimBytes(byte[] bytes) {
        // Before body
        int len = bytes.length;
        int i = 0;
        while ((bytes[i] == 0) && i++ < len) ;
        byte[] temp = new byte[len - i];
        System.arraycopy(bytes, i, temp, 0, len - i);

        // After body
        int j = temp.length;
        while (j-- > 0 && temp[j] == 0) ;
        byte[] output = new byte[j + 1];
        System.arraycopy(temp, 0, output, 0, j + 1);

        return output;
    }

    public static String extractBoundary(String str) {
        int i = str.lastIndexOf("boundary=");
        String boundary = str.substring(i + 9);
        boundary = "--" + boundary;
        return boundary;
    }

    public static void writeToFile(byte[] bytes) {
        try {
            Path path = Paths.get("C:\\Users\\Nazar\\IdeaProjects\\order-store\\out.txt");
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}