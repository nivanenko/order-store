package com.odyssey.controllers;

import com.odyssey.service.OrderService;
import com.odyssey.util.FileUploadListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class FileUploadController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.POST)
    public void uploadFile(HttpServletRequest req, HttpServletResponse resp) {
        final AsyncContext context = req.startAsync(req, resp);

        // TODO: Implement true-async
        context.start(() -> {
            try {
                ServletInputStream input = req.getInputStream();
                FileUploadListener listener = new FileUploadListener(input, context, resp, orderService);
                input.setReadListener(listener);
            } catch (IOException e) {
                System.out.println("IO error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}