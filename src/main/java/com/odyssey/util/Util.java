package com.odyssey.util;

import com.odyssey.model.Order;

import java.util.regex.Pattern;

public class Util {
    /**
     * Delete all spaces from the given String
     *
     * @param str w/ spaces
     * @return the string w/o spaces
     */
    private static String deleteSpaces(String str) {
        return Pattern.compile("\\s+$").matcher(str).replaceAll("");
    }

    /**
     * Deletes all spaces in Order's strings: DepZip, DepCity, DepState,
     * DelZip, DelCity and DepState.
     *
     * @param order w/ spaces
     * @return order w/o spaces
     */
    public static Order deleteSpaces(Order order) {
        int itemSize = order.getItemID().size();
        for (int i = 0; i < itemSize; i++) {
            order.getItemProd().set(i, Util.deleteSpaces(order.getItemProd().get(i)));
        }

        order.setDepZip(Util.deleteSpaces(order.getDepZip()));
        order.setDepCity(Util.deleteSpaces(order.getDepCity()));
        order.setDepState(Util.deleteSpaces(order.getDepState()));
        order.setDelZip(Util.deleteSpaces(order.getDelZip()));
        order.setDelCity(Util.deleteSpaces(order.getDepCity()));
        order.setDelState(Util.deleteSpaces(order.getDelState()));
        return order;
    }

    /**
     * Check whether all the elements in the byte array are identical.<br>
     * For example, byte array is empty and contains only zeroes.
     * So all elements are the same and true will be returned.<br>
     * True - identical.<br>
     * False - different.
     *
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
     * This method deletes zero (0) bytes in the beginning and in the end
     * of the byte array and return byte array with its actual size.
     *
     * @param bytes with w/ zeroes
     * @return bytes[] w/o zeroes
     */
    public static byte[] trimBytes(byte[] bytes) {
        // Before body
        int len = bytes.length;
        int i = 0;
        while ((bytes[i] == 0) && i++ < len) ; //
        byte[] temp = new byte[len - i];
        System.arraycopy(bytes, i, temp, 0, len - i);

        // After body
        int j = temp.length;
        while (j-- > 0 && temp[j] == 0) ;
        byte[] output = new byte[j + 1];
        System.arraycopy(temp, 0, output, 0, j + 1);

        return output;
    }

    /**
     * The method extracts the boundary from the header's string and returns it.
     *
     * @param str header w/ boundary inside
     * @return boundary string
     */
    public static String extractBoundary(String str) {
        int i = str.lastIndexOf("boundary=");
        String boundary = str.substring(i + 9);
        boundary = "--" + boundary;
        return boundary;
    }
}