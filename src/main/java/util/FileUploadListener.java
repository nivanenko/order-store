package util;

import database.DatabaseHelper;
import util.xml.XMLParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUploadListener implements ReadListener {
    private ServletInputStream input = null;
    private AsyncContext context = null;
    private String xml = null;
    private HttpServletResponse resp = null;

    public FileUploadListener(ServletInputStream in, AsyncContext ac, HttpServletResponse resp) {
        this.input = in;
        this.context = ac;
        this.resp = resp;
    }

    @Override
    public void onDataAvailable() {
        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024]; // 1 KB
            do {
                int length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (input.isReady());

            String content = sb.toString();
            xml = content.substring(content.indexOf("<order>"), content.indexOf("</order>") + 8);
        } catch (IOException e) {
            System.err.println("IO error " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + e.getMessage());
        }
    }

    @Override
    public void onAllDataRead() {
        try (PrintWriter out = resp.getWriter()) {
            InitialContext initial = new InitialContext();
            DataSource ds = (DataSource) initial.lookup("java:comp/env/jdbc/op");

            XMLParser parser = new XMLParser(xml);
            DatabaseHelper db = new DatabaseHelper();
            int orderId = db.createOrder(ds,
                    parser.getDepZip(), parser.getDepState(),
                    parser.getDepCity(), parser.getDelZip(),
                    parser.getDelState(), parser.getDelCity(),
                    parser.getItemWeight(), parser.getItemVol(),
                    parser.getItemHaz(), parser.getItemProd()
            );

            String orderIdStr = Integer.toString(orderId);
            resp.setContentType("text/html");
            out.append(orderIdStr);
        } catch (NamingException e) {
            System.err.println("JNDI error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }

        context.complete();
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
        context.complete();
    }
}