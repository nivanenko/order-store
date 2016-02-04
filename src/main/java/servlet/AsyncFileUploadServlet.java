package servlet;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.FileUploadListener;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AsyncFileUploadServlet",
        urlPatterns = "/asyncUpload", asyncSupported = true)
public class AsyncFileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(req)) return;

        AsyncContext context = req.startAsync();
        ServletInputStream input = req.getInputStream();
        FileUploadListener listener = new FileUploadListener(input, context, resp);
        input.setReadListener(listener);
    }
}