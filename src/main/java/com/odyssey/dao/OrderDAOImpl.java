package com.odyssey.dao;

import com.odyssey.model.Order;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {

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
                        ps.setInt(4, order.getItemHazInt().get(i));
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
        final Order order = new Order();

        // Getting item IDs
        jdbcTemplate.queryForObject(
                "SELECT item_id FROM OrderItems WHERE order_id = ?", new Object[]{orderID},
                new RowMapper<Order>() {
                    @Override
                    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                        do {
                            order.getItemID().add(rs.getInt("item_id"));
                        } while (rs.next());
                        return order;
                    }
                });

        // Getting dep/del IDs
        jdbcTemplate.queryForObject(
                "SELECT dep_id, del_id FROM Orders WHERE order_id = ?", new Object[]{orderID},
                new RowMapper<Order>() {
                    @Override
                    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                        order.setDepID(rs.getInt("dep_id"));
                        order.setDelID(rs.getInt("del_id"));
                        return order;
                    }
                });

        // Getting departure info
        jdbcTemplate.queryForObject(
                "SELECT dep_zip, dep_state, dep_city "
                        + "FROM Departure "
                        + "WHERE dep_id = ?", new Object[]{order.getDepID()},
                new RowMapper<Order>() {
                    @Override
                    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                        order.setDepZip(rs.getString("dep_zip"));
                        order.setDepState(rs.getString("dep_state"));
                        order.setDepCity(rs.getString("dep_city"));
                        return order;
                    }
                });

        // Getting delivery info
        jdbcTemplate.queryForObject(
                "SELECT del_zip, del_state, del_city "
                        + "FROM Delivery "
                        + "WHERE del_id = ?", new Object[]{order.getDelID()},
                new RowMapper<Order>() {
                    @Override
                    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                        order.setDelZip(rs.getString("del_zip"));
                        order.setDelState(rs.getString("del_state"));
                        order.setDelCity(rs.getString("del_city"));
                        return order;
                    }
                });

        // Getting items
        int itemSize = order.getItemID().size();
        for (int i = 0; i < itemSize; i++) {
            jdbcTemplate.queryForObject(
                    "SELECT item_weight, item_vol, item_haz, item_prod "
                            + "FROM Items "
                            + "WHERE item_id = ?", new Object[]{order.getItemID().get(i)},
                    new RowMapper<Order>() {
                        @Override
                        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                            order.getItemWeight().add(rs.getDouble("item_weight"));
                            order.getItemVol().add(rs.getDouble("item_vol"));
                            order.getItemHazInt().add(rs.getInt("item_haz"));
                            order.getItemProd().add(rs.getString("item_prod"));
                            return order;
                        }
                    });
        }
        return order;
    }
}