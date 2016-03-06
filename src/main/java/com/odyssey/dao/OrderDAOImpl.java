package com.odyssey.dao;

import com.odyssey.model.Order;
import com.odyssey.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Autowired
    private Order order;

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int add(Order order) {
        int itemSize = order.getItemProd().size();

        // Departure table inserting
        jdbcTemplate.update(
                "INSERT INTO Departure " +
                        "(dep_id, dep_zip, dep_state, dep_city) " +
                        "VALUES (dep_seq.nextval, ?, ?, ?)",
                order.getDepZip(), order.getDepState(), order.getDepCity());

        // Delivery table inserting
        jdbcTemplate.update(
                "INSERT INTO Delivery " +
                        "(del_id, del_zip, del_state, del_city) " +
                        "VALUES (del_seq.nextval, ?, ?, ?)",
                order.getDelZip(), order.getDelState(), order.getDelCity());

        // Converting boolean type into the integer one for Oracle DB
        ArrayList<Integer> itemHaz = new ArrayList<>();
        for (Boolean anItemHaz : order.getItemHaz()) {
            itemHaz.add(Converter.boolToInt(anItemHaz));
        }

        // Items table inserting
        jdbcTemplate.batchUpdate(
                "INSERT INTO Items " +
                        "(item_id, item_weight, item_vol, item_prod, item_haz) " +
                        "VALUES (item_seq.nextval, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setDouble(1, order.getItemWeight().get(i));
                        ps.setDouble(2, order.getItemVol().get(i));
                        ps.setString(3, order.getItemProd().get(i));
                        ps.setInt(4, itemHaz.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return itemSize;
                    }
                });

        // Retrieving IDs
        int depID = jdbcTemplate.queryForObject("SELECT dep_seq.currval FROM dual", Integer.class);
        int delID = jdbcTemplate.queryForObject("SELECT del_seq.currval FROM dual", Integer.class);

        // Orders table inserting
        jdbcTemplate.update(
                "INSERT INTO Orders " +
                        "(order_id, dep_id, del_id) " +
                        "VALUES (order_seq.nextval, ?, ?)",
                depID, delID);

        int orderID = jdbcTemplate.queryForObject("SELECT order_seq.currval FROM dual", Integer.class);
        int itemID = jdbcTemplate.queryForObject("SELECT item_seq.currval FROM dual", Integer.class);

        // Creating list of item's IDs
        itemID -= itemSize;
        ArrayList<Integer> itemIDList = new ArrayList<>();
        for (int i = 0; i < itemSize; i++) {
            itemID++;
            itemIDList.add(i, itemID);
        }


        // OrderItems table inserting
        jdbcTemplate.batchUpdate(
                "INSERT INTO OrderItems " +
                        "(order_item, order_id, item_id) " +
                        "VALUES (orderitem_seq.nextval, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, orderID);
                        ps.setInt(2, itemIDList.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return itemSize;
                    }
                });

        return orderID;
    }

    @Override
    public Order get(int orderID) {
        // Getting item_id

        return order;
    }
}
