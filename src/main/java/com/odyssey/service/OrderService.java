package com.odyssey.service;

import com.odyssey.dao.OrderDAO;
import com.odyssey.model.Order;
import com.odyssey.util.json.JSONHelper;
import com.odyssey.util.xml.XMLParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private Order order;

    private OrderDAO dao;

    public void setDao(OrderDAO dao) {
        this.dao = dao;
    }

    @Transactional
    public int addOrder(String xml) {
        XMLParser parser = new XMLParser(order, xml);
        parser.parseString();

        return dao.add(order);
    }

    public JSONObject getOrder(int orderID) {
        order = dao.get(orderID);

        return JSONHelper.createJSONForOrder(
                order.getDepZip(), order.getDepState(), order.getDepCity(),
                order.getDelZip(), order.getDelState(), order.getDelCity(),
                order.getItemID(), order.getItemWeight(), order.getItemVol(),
                order.getItemProd(), order.getItemHaz());
    }
}
