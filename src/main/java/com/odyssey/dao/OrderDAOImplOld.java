package com.odyssey.dao;

import com.odyssey.model.Order;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImplOld implements OrderDAO {

    private DataSource ds;

    @Autowired
    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public int add(Order order) {
        int order_id = 0;
        int itemSize = order.getItemProd().size();

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            // Departure
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Departure "
                            + "(dep_id, dep_zip, dep_state, dep_city) "
                            + "VALUES (dep_seq.nextval, ?, ?, ?)")) {
                ps.setString(1, order.getDepZip());
                ps.setString(2, order.getDelState());
                ps.setString(3, order.getDepCity());
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Table \"Departure\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            // Delivery
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Delivery "
                            + "(del_id, del_zip, del_state, del_city) "
                            + "VALUES (del_seq.nextval, ?, ?, ?)")) {
                ps.setString(1, order.getDelZip());
                ps.setString(2, order.getDelState());
                ps.setString(3, order.getDelCity());
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Table \"Delivery\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            // Items
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Items "
                            + "(item_id, item_weight, item_vol, item_prod, item_haz) "
                            + "VALUES (item_seq.nextval, ?, ?, ?, ?)")) {
                int count = 0;
                int batchSize = 1000;

                for (int i = 0; i < itemSize; i++) {
                    ps.setDouble(1, order.getItemWeight().get(i));
                    ps.setDouble(2, order.getItemVol().get(i));
                    ps.setString(3, order.getItemProd().get(i));
                    ps.setInt(4, order.getItemHazInt().get(i));
                    ps.addBatch();
                    if ((++count % batchSize) == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Table \"Items\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            // Retrieving IDs
            int dep_id = 0, del_id = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_seq.currval, del_seq.currval FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dep_id = rs.getInt(1);
                    del_id = rs.getInt(2);
                }
            }

            // Orders
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Orders "
                            + "(order_id, dep_id, del_id) "
                            + "VALUES (order_seq.nextval, ?, ?)")) {

                ps.setInt(1, dep_id);
                ps.setInt(2, del_id);
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Table \"Orders\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }


            order_id = getOrderID(conn);
            ArrayList<Integer> item_id = getItemID(conn, order.getItemProd());
            try (PreparedStatement ps = conn.prepareStatement( // OrderItems
                    "INSERT INTO OrderItems "
                            + "(order_item, order_id, item_id) "
                            + "VALUES (orderitem_seq.nextval, ?, ?)")) {
                int count = 0;
                int batchSize = 1000;
                for (int i = 0; i < itemSize; i++) {
                    ps.setInt(1, order_id);
                    ps.setInt(2, item_id.get(i));
                    ps.addBatch();
                    if ((++count % batchSize) == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Table \"Orders\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }

        return order_id;
    }

    @Override
    public Order get(int orderID) {
        Order order = new Order();

        try (Connection conn = ds.getConnection()) {
            // Getting item IDs
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT item_id FROM OrderItems WHERE order_id = ?")) {
                ps.setInt(1, orderID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    do {
                        order.getItemID().add(rs.getInt("item_id"));
                    } while (rs.next());
                }
            }

            // Getting dep/del IDs
            int dep_id = 0;
            int del_id = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_id, del_id FROM Orders WHERE order_id = ?")) {
                ps.setInt(1, orderID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dep_id = rs.getInt("dep_id");
                        del_id = rs.getInt("del_id");
                    }
                }
            }

            // Getting departure info
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_zip, dep_state, dep_city "
                            + "FROM Departure "
                            + "WHERE dep_id = ?")) {
                ps.setInt(1, dep_id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        order.setDepZip(rs.getString("dep_zip"));
                        order.setDepState(rs.getString("dep_state"));
                        order.setDepCity(rs.getString("dep_city"));
                    }
                }
            }

            // Getting delivery info
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT del_zip, del_state, del_city "
                            + "FROM Delivery "
                            + "WHERE del_id = ?")) {
                ps.setInt(1, del_id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        order.setDelZip(rs.getString("del_zip"));
                        order.setDelState(rs.getString("del_state"));
                        order.setDelCity(rs.getString("del_city"));
                    }
                }
            }

            // Getting items
            int itemSize = order.getItemID().size();

            for (int i = 0; i < itemSize; i++) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT item_weight, item_vol, item_haz, item_prod "
                                + "FROM Items "
                                + "WHERE item_id = ?")) {
                    ps.setInt(1, order.getItemID().get(i));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            order.getItemWeight().add(i, rs.getDouble("item_weight"));
                            order.getItemVol().add(i, rs.getDouble("item_vol"));
                            order.getItemHazInt().add(i, rs.getInt("item_haz"));
                            order.getItemProd().add(i, rs.getString("item_prod"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return order;
    }

    private ArrayList<Integer> getItemID(Connection conn, ArrayList<String> item_prod) {
        ArrayList<Integer> item_id = new ArrayList<>();
        int itemIdTemp = 0;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT item_seq.currval FROM dual");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                itemIdTemp = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while getting the item ID: " + e.getMessage());
        }

        itemIdTemp -= item_prod.size();
        for (int i = 0; i < item_prod.size(); i++) {
            itemIdTemp++;
            item_id.add(i, itemIdTemp);
        }

        return item_id;
    }

    private int getOrderID(Connection conn) {
        int order_id = -1;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT order_seq.currval FROM dual");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                order_id = rs.getInt(1);

                if (order_id != 0) {
                    return order_id;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while getting the item ID: " + e.getMessage());
        }
        return order_id;
    }
}