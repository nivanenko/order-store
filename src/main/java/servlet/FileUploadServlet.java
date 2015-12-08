package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.OrderService;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "FileUploadServlet", urlPatterns = "/upload")
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 megabyte
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime = System.nanoTime();

        if (!ServletFileUpload.isMultipartContent(req)) {
            return;
        }
        try {
            DiskFileItemFactory fileFactory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(fileFactory);
            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();

                try (BufferedReader input = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                     PrintWriter out = resp.getWriter()) {
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = input.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    String content = sb.toString();
                    XMLParser.parseString(content);

                    // Configuring DataSource
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
        } catch (IOException e) {
            System.err.println("IO error:" + e.getMessage());
        } catch (FileUploadException e) {
            System.err.println("File upload error: " + e.getMessage());
        }
    }
}