package com.odyssey.controllers;

import com.odyssey.service.OrderService;
import com.odyssey.util.Util;
import com.odyssey.util.file.MultiPart;
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
public class FileProcessController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.POST)
    public void processFile(HttpServletRequest req, HttpServletResponse resp) {
        final AsyncContext context = req.startAsync(req, resp);
        context.start(() -> {
            try {
                ServletInputStream input = req.getInputStream();
                String boundary = Util.extractBoundary(req.getHeader("Content-Type"));
                MultiPart listener = new MultiPart(input, context, resp, orderService, boundary);
                input.setReadListener(listener);
            } catch (IOException e) {
                System.out.println("IO error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}