package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.OrderHelper;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.XMLParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AsyncFileUploadServlet",
        urlPatterns = "/asyncUpload", asyncSupported = true)
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 MB
public class AsyncFileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final long timeout = 3600000;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime = System.nanoTime();
        if (!ServletFileUpload.isMultipartContent(req)) return;

        /**
            Not implemented yet
        */

        // DB stuff
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