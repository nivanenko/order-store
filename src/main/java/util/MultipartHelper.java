package util;

public class MultipartHelper {
    public static String extractBoundary(String line) {
        // Use lastIndexOf() because IE 4.01 on Win98 has been known to send the
        // "boundary=" string multiple times.  Thanks to David Wall for this fix.
        int index = line.lastIndexOf("boundary=");
        if (index == -1) {
            return null;
        }
        String boundary = line.substring(index + 9);  // 9 for "boundary="
        if (boundary.charAt(0) == '"') {
            // The boundary is enclosed in quotes, strip them
            index = boundary.lastIndexOf('"');
            boundary = boundary.substring(1, index);
        }
        // The real boundary is always preceded by an extra "--"
        boundary = "--" + boundary;
        return boundary;
    }
}