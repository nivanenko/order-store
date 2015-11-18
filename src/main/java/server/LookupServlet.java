package server;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@MultipartConfig
public class LookupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Database db = new Database();
        PrintWriter out = response.getWriter();
        String orderID = request.getParameter("value");
        JSONObject json = db.createJSON(Integer.parseInt(orderID));

        if (json == null) {
            response.setContentType("text/html");
            out.print("error");
            out.flush();
        } else {
            response.setContentType("application/json");
            out.print(json);
            out.flush();
        }
    }
}