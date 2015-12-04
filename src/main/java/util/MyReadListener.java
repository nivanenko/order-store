package util;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

public class MyReadListener implements ReadListener {
    private ServletInputStream input = null;
    private AsyncContext context = null;

    public MyReadListener(ServletInputStream in, AsyncContext ac) {
        this.input = in;
        this.context = ac;
    }

    @Override
    public void onDataAvailable() {
        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024];
            do {
                int length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (input.isReady());
            System.out.println(sb);
           String content = sb.substring(
                    sb.lastIndexOf("<order>"),
                    sb.lastIndexOf("</order>") + 8);
            XMLParser.parseString(content);
        } catch (IOException e) {
            System.err.println("IO error " + e.getMessage());
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