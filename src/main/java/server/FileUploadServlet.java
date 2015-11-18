package server;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

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
            upload.setSizeMax(1048576); // 1 Megabyte
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
//                        System.out.println(contentOfFile); // for test

                        int dep_zip = Integer.parseInt(Parser.getAttrStr("from", "zip", inputStream));
                        String dep_state = Parser.getAttrStr("from", "state", inputStream);
                        String dep_city = Parser.getAttrStr("from", "city", inputStream);
                        int des_zip = Integer.parseInt(Parser.getAttrStr("to", "zip", inputStream));
                        String des_state = Parser.getAttrStr("to", "state", inputStream);
                        String des_city = Parser.getAttrStr("to", "city", inputStream);

                        ArrayList<Double> item_weight = Parser.getAttrItemDouble("weight", inputStream);
                        ArrayList<Integer> item_vol = Parser.getAttrItemInt("volume", inputStream);
                        ArrayList<Boolean> item_haz = Parser.getAttrItemBool("hazard", inputStream);
                        ArrayList<String> item_prod = Parser.getAttrItemStr("product", inputStream);

                        order_id = db.createOrder(dep_zip, dep_state, dep_city, des_zip, des_state, des_city, item_weight, item_vol, item_haz, item_prod);

                        String orderID = Integer.toString(order_id);
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.append(orderID);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
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