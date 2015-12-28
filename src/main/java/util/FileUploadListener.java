package util;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

public class FileUploadListener implements ReadListener {
    private ServletInputStream input = null;
    private AsyncContext context = null;

    public FileUploadListener(ServletInputStream in, AsyncContext ac) {
        this.input = in;
        this.context = ac;
    }

    public void onDataAvailable() {
        System.out.println("In onDataAvailable");
        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[2*1024]; // 2 KB
            do {
                int length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (input.isReady());

            System.out.println("SB length " + sb.length());

            String content = sb.toString();
            String boundary = content.substring(0, content.indexOf("\r\n"));
            System.out.println(boundary);
//            Pattern pattern1 = Pattern.compile("(?<=Content\\-Type:)(.*?)(?=\\r\\n\\r\\n)");
//            Pattern pattern2 = Pattern.compile("[\\r\\n]+\\-");

        } catch (IOException e) {
            System.err.println("IO error " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + e.getMessage());
        }
    }

    public void onAllDataRead() {
        System.out.println("All data read");
        context.complete();
    }

    public void onError(Throwable t) {
        System.err.println("Error!");
        context.complete();
    }
}