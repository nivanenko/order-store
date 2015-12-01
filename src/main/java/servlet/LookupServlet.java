package servlet;

import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;
import db.Database;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LookupServlet", urlPatterns = "/lookup")
public class LookupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Database db = new Database();
        PrintWriter out = response.getWriter();
        String orderID = request.getParameter("value");

        try {
            // Configuring DataSource
            InitialContext initial = new InitialContext();
            HikariDataSource ds;
            ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");

            JSONObject json = db.createJSON(Integer.parseInt(orderID), ds);

            if (json == null) {
                response.setContentType("text/html");
                out.print("error");
                out.flush();
            } else {
                response.setContentType("application/json");
                out.print(json);
                out.flush();
            }
        } catch (NamingException e) {
            System.err.println("JNDI error: " + e.getMessage());
        }
    }
}