package servlet;

import database.DatabaseHelper;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.xml.XMLParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "FileUploadServlet", urlPatterns = "/upload")
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(req)) return;

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

                    XMLParser parser = new XMLParser(sb.toString());

                    InitialContext initial = new InitialContext();
                    DataSource ds = (DataSource) initial.lookup("java:comp/env/jdbc/op");

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
                    System.err.println("JNDI error occurred: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("IO error occurred:" + e.getMessage());
        } catch (FileUploadException e) {
            System.err.println("File upload error occurred: " + e.getMessage());
        }
    }
}