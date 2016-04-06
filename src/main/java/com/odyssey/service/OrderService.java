package com.odyssey.service;

import com.odyssey.dao.OrderDAO;
import com.odyssey.model.Order;
import com.odyssey.util.Converter;
import com.odyssey.util.Util;
import com.odyssey.util.json.JSONHelper;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private OrderDAO dao;

    public void setDao(OrderDAO dao) {
        this.dao = dao;
    }

    @Transactional
    public int addOrder(Order order) {
        // Converting boolean type into the integer one for Oracle DB
        int itemSize = order.getItemHazBool().size();
        for (int i = 0; i < itemSize; i++) {
            order.getItemHazInt().add(i, Converter.boolToInt(order.getItemHazBool().get(i)));
        }

        return dao.add(order);
    }

    public JSONObject getOrder(int orderID) {
        Order order = dao.get(orderID);
        int itemSize = order.getItemID().size();

        // Deleting whitespaces
        for (int i = 0; i < itemSize; i++) {
            order.getItemProd().set(i, Util.deleteSpaces(order.getItemProd().get(i)));
        }
        order.setDepZip(Util.deleteSpaces(order.getDepZip()));
        order.setDepCity(Util.deleteSpaces(order.getDepCity()));
        order.setDepState(Util.deleteSpaces(order.getDepState()));
        order.setDelZip(Util.deleteSpaces(order.getDelZip()));
        order.setDelCity(Util.deleteSpaces(order.getDepCity()));
        order.setDelState(Util.deleteSpaces(order.getDelState()));

        // Converting hazard integer value into boolean one
        for (int i = 0; i < itemSize; i++) {
            order.getItemHazBool().add(i, Converter.intToBool(order.getItemHazInt().get(i)));
        }

        return JSONHelper.createJSONForOrder(
                order.getDepZip(), order.getDepState(), order.getDepCity(),
                order.getDelZip(), order.getDelState(), order.getDelCity(),
                order.getItemID(), order.getItemWeight(), order.getItemVol(),
                order.getItemProd(), order.getItemHazBool());
    }
}