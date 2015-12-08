package servlet;

import com.zaxxer.hikari.HikariDataSource;
import database.OrderService;
import org.json.JSONObject;

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

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        String orderID = req.getParameter("value");

        try (PrintWriter out = resp.getWriter()) {
            InitialContext initial = new InitialContext();
            HikariDataSource ds = (HikariDataSource) initial.lookup("java:comp/env/jdbc/op");
            JSONObject json = OrderService.createJSON(Integer.parseInt(orderID), ds);

            if (json == null) {
                resp.setContentType("text/html");
                out.print("error");
                out.flush();
            } else {
                resp.setContentType("application/json");
                out.print(json);
                out.flush();
            }
        } catch (NamingException e) {
            System.err.println("JNDI error: " + e.getMessage());
        }
    }
}