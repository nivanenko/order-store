package com.odyssey.util;

import com.odyssey.service.OrderService;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUploadListener implements ReadListener {
    private ServletInputStream input;
    private AsyncContext context;
    private HttpServletResponse resp;
    private String xml;
    private OrderService orderService;

    public FileUploadListener(ServletInputStream in, AsyncContext ac, HttpServletResponse resp, OrderService orderService) {
        this.input = in;
        this.context = ac;
        this.resp = resp;
        this.orderService = orderService;
    }

    @Override
    public void onDataAvailable() {
        // TODO: Implement true-async

        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024]; // 1 KB
            do {
                int length = input.read(buffer);
                sb.append(new String(buffer, 0, length));
            } while (input.isReady() && !input.isFinished());

            String content = sb.toString();
            xml = content.substring(content.indexOf("<order>"), content.indexOf("</order>") + 8);
        } catch (IOException e) {
            System.err.println("IO error " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onAllDataRead() {
        int orderID = orderService.addOrder(xml);

        try (PrintWriter out = resp.getWriter()) {
            out.print(orderID);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
        }

        context.complete();
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
        t.printStackTrace();
        context.complete();
    }
}