package com.odyssey.controllers;

import com.odyssey.service.OrderService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/lookup")
public class LookupController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET)
    public void lookup(@RequestParam("value") String orderID, HttpServletResponse resp) {
        JSONObject json = orderService.getOrder(Integer.parseInt(orderID));

        try(PrintWriter out = resp.getWriter()) {
            if (json == null) {
                resp.setContentType("text/html");
                out.print("error");
            } else {
                resp.setContentType("application/json");
                out.print(json);
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}