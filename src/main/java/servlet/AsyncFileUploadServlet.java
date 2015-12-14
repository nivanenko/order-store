package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.OrderHelper;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.MultipartHelper;
import util.MyReadListener;
import util.XMLParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AsyncFileUploadServlet",
        urlPatterns = "/asyncUpload", asyncSupported = true)
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 megabyte
public class AsyncFileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime = System.nanoTime();
        if (!ServletFileUpload.isMultipartContent(req)) return;

        // TODO: make it work

        final AsyncContext context = req.startAsync();
        ServletInputStream input = req.getInputStream();

        String boundary = MultipartHelper.extractBoundary(req.getHeader("Content-Type"));
        MyReadListener listener = new MyReadListener(input, context, boundary);
        input.setReadListener(listener);
        listener.onDataAvailable();

        try (PrintWriter out = resp.getWriter()) {
            InitialContext initial = new InitialContext();
            HikariDataSource ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");

            int orderId = OrderHelper.createOrder(ds,
                    XMLParser.getDepZip(), XMLParser.getDepState(),
                    XMLParser.getDepCity(), XMLParser.getDelZip(),
                    XMLParser.getDelState(), XMLParser.getDelCity(),
                    XMLParser.getItemWeight(), XMLParser.getItemVol(),
                    XMLParser.getItemHaz(), XMLParser.getItemProd()
            );

            long stopTime = System.nanoTime();
            System.err.println("Finished the database filling in "
                    + (stopTime - startTime) / 1000000000 + " sec");

            String orderIdStr = Integer.toString(orderId);
            resp.setContentType("text/html");
            out.append(orderIdStr);
        } catch (NamingException e) {
            System.err.println("JNDI error: " + e.getMessage());
        }
    }
}