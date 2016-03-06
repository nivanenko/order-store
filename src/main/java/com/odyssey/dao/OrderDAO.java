package com.odyssey.dao;

import com.odyssey.model.Order;

public interface OrderDAO {

    int add(Order order);

    Order get(int orderID);
}
