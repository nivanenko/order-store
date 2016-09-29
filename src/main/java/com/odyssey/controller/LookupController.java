package com.odyssey.controller;

import com.odyssey.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lookup")
public class LookupController {
    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String lookup(@RequestParam("value") String orderID) {
        return orderService.getOrder(Integer.parseInt(orderID)).toString();
    }
}