package servlet.async;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.zaxxer.hikari.HikariDataSource;
import db.Database;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AsyncFileUploadServlet", urlPatterns = "/asyncUpload", asyncSupported = true)
@MultipartConfig(maxFileSize = 1048576 * 10) // 10 megabyte
public class AsyncFileUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        final AsyncContext context = request.startAsync(request, response);

        if (!ServletFileUpload.isMultipartContent(request)) return;

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);

        try {
            FileItemIterator iterator = upload.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream inputStream = item.openStream();

                try (BufferedReader input = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                     PrintWriter out = response.getWriter()) {
                    String line, content;
                    StringBuilder sb = new StringBuilder();
                    while ((line = input.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    content = sb.toString();

                    AsyncXMLInputFactory factory = new InputFactoryImpl();
                    AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = factory.createAsyncForByteArray();
                    AsyncReaderWrapper wrapper = new AsyncReaderWrapper(reader, 1, content);

                    String dep_zip = "";
                    String dep_state = "";
                    String dep_city = "";
                    String del_zip = "";
                    String del_state = "";
                    String del_city = "";
                    List<Double> item_weight = new ArrayList<>();
                    List<Double> item_vol = new ArrayList<>();
                    List<Boolean> item_haz = new ArrayList<>();
                    List<String> item_prod = new ArrayList<>();

                    int type = wrapper.nextToken();
                    while (type != XMLEvent.END_DOCUMENT) {
                        if (type == XMLEvent.START_ELEMENT) {
                            switch (reader.getName().toString()) {
                                case "from":
                                    dep_zip = reader.getAttributeValue(0);
                                    dep_state = reader.getAttributeValue(1);
                                    dep_city = reader.getAttributeValue(2);
                                    break;
                                case "to":
                                    del_zip = reader.getAttributeValue(0);
                                    del_state = reader.getAttributeValue(1);
                                    del_city = reader.getAttributeValue(2);
                                    break;
                                case "line":
                                    item_weight.add(reader.getAttributeAsDouble(0));
                                    item_vol.add(reader.getAttributeAsDouble(1));
                                    item_haz.add(reader.getAttributeAsBoolean(2));
                                    item_prod.add(reader.getAttributeValue(3));
                                    break;
                            }
                        }
                        type = wrapper.nextToken();
                    }
                    reader.close();

                    // Configuring DataSource
                    InitialContext initial = new InitialContext();
                    HikariDataSource ds;
                    ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");

                    Database db = new Database();
                    int order_id = db.createOrder(ds,
                            dep_zip, dep_state, dep_city,
                            del_zip, del_state, del_city,
                            item_weight, item_vol, item_haz, item_prod
                    );

                    String orderID = Integer.toString(order_id);
                    response.setContentType("text/html");
                    out.append(orderID);
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