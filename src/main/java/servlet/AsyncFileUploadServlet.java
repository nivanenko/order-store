package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.DatabaseHelper;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.FileUploadListener;
import util.xml.XMLParser;

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
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 MB
public class AsyncFileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime = System.nanoTime();
        if (!ServletFileUpload.isMultipartContent(req)) return;

        ServletInputStream input = req.getInputStream();
        AsyncContext context = req.startAsync();
        input.setReadListener(new FileUploadListener(input, context));

        try (PrintWriter out = resp.getWriter()) {
            InitialContext initial = new InitialContext();
            HikariDataSource ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op"); // JNDI. See META-INF/context.xml

            XMLParser parser = new XMLParser();
            DatabaseHelper db = new DatabaseHelper();
            int orderId = db.createOrder(ds,
                    parser.getDepZip(), parser.getDepState(),
                    parser.getDepCity(), parser.getDelZip(),
                    parser.getDelState(), parser.getDelCity(),
                    parser.getItemWeight(), parser.getItemVol(),
                    parser.getItemHaz(), parser.getItemProd()
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