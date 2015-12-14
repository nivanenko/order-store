package util;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyReadListener implements ReadListener {
    private ServletInputStream input = null;
    private AsyncContext context = null;
    private String boundary = null;

    public MyReadListener(ServletInputStream in, AsyncContext ac, String boundary) {
        this.input = in;
        this.context = ac;
        this.boundary = boundary;
    }

    @Override
    public void onDataAvailable() {
        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[2048];
            do {
                int length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (input.isReady());
            String content = sb.toString();
            String boundary = content.substring(0, content.indexOf("\r\n"));
            System.out.println(boundary);
            Pattern pattern = Pattern.compile("(?<=Content\\-Type:)(.*?)(?=\\r\\n\\r\\n)");
            Matcher contentTypeMatch = pattern.matcher(content);
            pattern = Pattern.compile("[\\r\\n]+\\-");
            Matcher boundaryMatch = pattern.matcher(content);
            int start = contentTypeMatch.regionEnd();
            int end = boundaryMatch.regionStart();
            System.err.println(content.substring(start, end));

//            XMLParser.parseString(test);
        } catch (IOException e) {
            System.err.println("IO error " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + e.getMessage());
        }
    }

    @Override
    public void onAllDataRead() {
        System.out.println("All data read");
        context.complete();
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error!");
        context.complete();
    }
}