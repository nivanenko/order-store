package server;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import server.object.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        Database db = new Database();
        int order_id;

        if (isMultipart) {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
            upload.setSizeMax(1048576 * 2); // 2 Megabyte
            InputStream inputStream;

            try {
                FileItemIterator iterator = upload.getItemIterator(request);
                while (iterator.hasNext()) {

                    FileItemStream item = iterator.next();
                    inputStream = item.openStream();

                    try (BufferedReader input = new BufferedReader(new InputStreamReader(inputStream))) {
                        String line;
                        String contentOfFile = "";

                        while ((line = input.readLine()) != null) {
                            contentOfFile += line + "\n";
                        }

                        JAXBContext jaxbContext = JAXBContext.newInstance(Order.class);
                        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                        StreamSource source = new StreamSource(new StringReader(contentOfFile));
                        JAXBElement<Order> je = unmarshaller.unmarshal(source, Order.class);

                        Order order = je.getValue();

                        order_id = db.createOrder(
                                order.getFrom().getZip(),
                                order.getFrom().getState(),
                                order.getFrom().getCity(),
                                order.getTo().getZip(),
                                order.getTo().getState(),
                                order.getTo().getCity(),
                                order.getLines().getLine());

                        String orderID = Integer.toString(order_id);
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.append(orderID);
                        out.close();
                    } catch (JAXBException e) {
                        System.err.println("JAXB error: " + e.getMessage());
                    }
                }
            } catch (FileUploadException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while creating the file: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Request contents type is not supported by the servlet.");
        }
    }
}