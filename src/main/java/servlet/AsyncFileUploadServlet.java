package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.OrderService;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import java.io.ByteArrayOutputStream;
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
        if (!ServletFileUpload.isMultipartContent(req)) {
            return;
        }

        // TODO: make it work

        String boundary = extractBoundary(req.getHeader("Content-Type"));
        final AsyncContext context = req.startAsync();
        ServletInputStream input = req.getInputStream();
        MyReadListener listener = new MyReadListener(input, context);
        input.setReadListener(listener);
        listener.onDataAvailable();

        @SuppressWarnings("deprecation")
        MultipartStream stream = new MultipartStream(input, boundary.getBytes(), 1024);
        boolean nextPart = stream.skipPreamble();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (nextPart) {
            stream.readBodyData(outputStream);
            nextPart = stream.readBoundary();
        }

        try (PrintWriter out = resp.getWriter()) {
//             Configuring DataSource
            InitialContext initial = new InitialContext();
            HikariDataSource ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");

            int orderId = OrderService.createOrder(ds,
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

    private String extractBoundary(String line) {
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