package servlet;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.zaxxer.hikari.HikariDataSource;
import database.OrderService;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.AsyncReaderWrapper;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "FileUploadServlet",
        urlPatterns = "/asyncUpload", asyncSupported = true)
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 megabyte
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime = System.nanoTime();
        final AsyncContext context = req.startAsync(req, resp);

        if (!ServletFileUpload.isMultipartContent(req)) {
            return;
        }

        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(fileFactory);
        try {
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

                    AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
                    AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = inputFactory.createAsyncForByteArray();
                    AsyncReaderWrapper wrapper = new AsyncReaderWrapper(reader, 1, content);

                    String depZip = "";
                    String depState = "";
                    String depCity = "";
                    String delZip = "";
                    String delState = "";
                    String delCity = "";
                    List<Double> itemWeight = new ArrayList<>();
                    List<Double> itemVol = new ArrayList<>();
                    List<Boolean> itemHaz = new ArrayList<>();
                    List<String> itemProd = new ArrayList<>();

                    int type = wrapper.nextToken();
                    while (type != XMLStreamConstants.END_DOCUMENT) {
                        if (type == XMLStreamConstants.START_ELEMENT) {
                            switch (reader.getName().toString()) {
                                case "from":
                                    depZip = reader.getAttributeValue(0);
                                    depState = reader.getAttributeValue(1);
                                    depCity = reader.getAttributeValue(2);
                                    break;
                                case "to":
                                    delZip = reader.getAttributeValue(0);
                                    delState = reader.getAttributeValue(1);
                                    delCity = reader.getAttributeValue(2);
                                    break;
                                case "line":
                                    itemWeight.add(reader.getAttributeAsDouble(0));
                                    itemVol.add(reader.getAttributeAsDouble(1));
                                    itemHaz.add(reader.getAttributeAsBoolean(2));
                                    itemProd.add(reader.getAttributeValue(3));
                                    break;
                                default:
                                    break;
                            }
                        }
                        type = wrapper.nextToken();
                    }
                    reader.close();

                    long stopTime = System.nanoTime();
                    System.err.println("Finished parsing in " + (stopTime - startTime) / 1000000000
                            + " sec. Starting to fill the database...");
                    // Configuring DataSource
                    InitialContext initial = new InitialContext();
                    HikariDataSource ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");

                    startTime = System.nanoTime();
                    int orderId = OrderService.createOrder(ds,
                            depZip, depState, depCity,
                            delZip, delState, delCity,
                            itemWeight, itemVol, itemHaz, itemProd
                    );
                    stopTime = System.nanoTime();
                    System.err.println("Finished the database filling in "
                            + (stopTime - startTime) / 1000000000 + " sec");

                    String orderIdStr = Integer.toString(orderId);
                    resp.setContentType("text/html");
                    out.append(orderIdStr);
                } catch (NamingException e) {
                    System.err.println("JNDI error: " + e.getMessage());
                } catch (XMLStreamException e) {
                    System.out.println("XML stream error: " + e.getMessage());
                }
            }
            context.complete();
        } catch (IOException e) {
            System.err.println("IO error:" + e.getMessage());
        } catch (FileUploadException e) {
            System.err.println("File upload error: " + e.getMessage());
        }
    }
}