package util;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyReadListener implements ReadListener {
    private ServletInputStream input = null;
    private AsyncContext context = null;
//    String data;

    public MyReadListener(ServletInputStream in, AsyncContext ac) {
        this.input = in;
        this.context = ac;
    }

    @Override
    public void onDataAvailable() {
        try {
            StringBuilder sb = new StringBuilder();
            int len = -1;
            byte[] b = new byte[1024];
            while (input.isReady()
                    && (((len = input.read(b))) != -1)) {
                String data = new String(b, 0, len);

                String content = data.substring(
                        data.lastIndexOf("<order>"),
                        data.lastIndexOf("</order>") + 8);
                XMLParser.parseString(content);
            }
        } catch (IOException e) {
            Logger.getLogger(MyReadListener.class.getName()).log(Level.SEVERE, null, e);
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