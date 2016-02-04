package database;

import org.json.JSONArray;
import org.json.JSONObject;
import util.Converter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseHelper {
    private static final Pattern NO_WHITESPACE = Pattern.compile("\\s+$");

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
        int order_id = 0;

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

    public JSONObject createJSON(int orderId, DataSource ds) {
        JSONObject jsonMain = new JSONObject();

        ArrayList<Integer> item_id = new ArrayList<>();
        ArrayList<Double> item_weight = new ArrayList<>();
        ArrayList<Integer> item_vol = new ArrayList<>();
        ArrayList<Integer> item_haz = new ArrayList<>();
        ArrayList<String> item_prod = new ArrayList<>();

        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT item_id FROM OrderItems WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    do {
                        item_id.add(rs.getInt("item_id"));
                    } while (rs.next());
                }
            }

            int dep_id = 0;
            int del_id = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_id, del_id FROM Orders WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dep_id = rs.getInt("dep_id");
                        del_id = rs.getInt("del_id");
                    }
                }
            }

            int dep_zip = 0;
            String dep_state = "";
            String dep_city = "";
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_zip, dep_state, dep_city "
                            + "FROM Departure "
                            + "WHERE dep_id = ?")) {
                ps.setInt(1, dep_id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dep_zip = rs.getInt("dep_zip");
                        dep_state = rs.getString("dep_state");
                        dep_city = rs.getString("dep_city");
                    }
                }
            }

            int del_zip = 0;
            String del_state = "";
            String del_city = "";
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT del_zip, del_state, del_city "
                            + "FROM Delivery "
                            + "WHERE del_id = ?")) {
                ps.setInt(1, del_id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        del_zip = rs.getInt("del_zip");
                        del_state = rs.getString("del_state");
                        del_city = rs.getString("del_city");
                    }
                }
            }

            for (int i = 0; i < item_id.size(); i++) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT item_weight, item_vol, item_haz, item_prod "
                                + "FROM Items "
                                + "WHERE item_id = ?")) {
                    ps.setInt(1, item_id.get(i));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            item_weight.add(i, rs.getDouble("item_weight"));
                            item_vol.add(i, rs.getInt("item_vol"));
                            item_haz.add(i, rs.getInt("item_haz"));
                            item_prod.add(i, rs.getString("item_prod"));
                        }
                    }
                }
            }

            for (int i = 0; i < item_prod.size(); i++) {
                item_prod.set(i, NO_WHITESPACE.matcher(item_prod.get(i)).replaceAll(""));
            }
            dep_city = NO_WHITESPACE.matcher(dep_city).replaceAll("");
            dep_state = NO_WHITESPACE.matcher(dep_state).replaceAll("");
            del_city = NO_WHITESPACE.matcher(del_city).replaceAll("");
            del_state = NO_WHITESPACE.matcher(del_state).replaceAll("");

            JSONObject jsonDeparture = new JSONObject();
            jsonDeparture.put("zip", dep_zip);
            jsonDeparture.put("state", dep_state);
            jsonDeparture.put("city", dep_city);

            JSONObject jsonDelivery = new JSONObject();
            jsonDelivery.put("zip", del_zip);
            jsonDelivery.put("state", del_state);
            jsonDelivery.put("city", del_city);

            JSONArray jsonLines = new JSONArray();
            List<JSONObject> jsonItemList = new ArrayList<>();

            List<Boolean> itemHazTemp = new ArrayList<>();
            for (Integer itemHazTempTemp : item_haz) {
                itemHazTemp.add(Converter.intToBool(itemHazTempTemp));
            }

            for (int i = 0; i < item_id.size(); i++) {
                jsonItemList.add(i, new JSONObject());
                jsonItemList.get(i).put("weight", item_weight.get(i));
                jsonItemList.get(i).put("volume", item_vol.get(i));
                jsonItemList.get(i).put("product", item_prod.get(i));
                jsonItemList.get(i).put("hazard", itemHazTemp.get(i));
                jsonLines.put(jsonItemList.get(i));
            }

            jsonMain.put("from", jsonDeparture);
            jsonMain.put("to", jsonDelivery);
            jsonMain.put("lines", jsonLines);
            return jsonMain;

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return null;
    }

    public int createOrder(
            DataSource ds,
            String dep_zipStr, String dep_state, String dep_city,
            String del_zipStr, String del_state, String del_city,
            ArrayList<Double> item_weight,
            ArrayList<Double> item_vol,
            ArrayList<Boolean> item_haz,
            ArrayList<String> item_prod
    ) {
        if (dep_zipStr == null) return -1;

        int order_id = 0;
        int dep_zip = Integer.parseInt(dep_zipStr);
        int del_zip = Integer.parseInt(del_zipStr);
        int itemSize = item_prod.size();

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement( // Departure
                    "INSERT INTO Departure "
                            + "(dep_id, dep_zip, dep_state, dep_city) "
                            + "VALUES (dep_seq.nextval, ?, ?, ?)")) {
                ps.setInt(1, dep_zip);
                ps.setString(2, dep_state);
                ps.setString(3, dep_city);
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println("Table \"Departure\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            try (PreparedStatement ps = conn.prepareStatement( // Delivery
                    "INSERT INTO Delivery "
                            + "(del_id, del_zip, del_state, del_city) "
                            + "VALUES (del_seq.nextval, ?, ?, ?)")) {
                ps.setInt(1, del_zip);
                ps.setString(2, del_state);
                ps.setString(3, del_city);
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println("Table \"Delivery\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            // Changing boolean type into the integer type
            List<Integer> itemHazard = new ArrayList<>();
            for (Boolean anItem_haz : item_haz) {
                itemHazard.add(Converter.boolToInt(anItem_haz));
            }

            try (PreparedStatement ps = conn.prepareStatement( // Items
                    "INSERT INTO Items "
                            + "(item_id, item_weight, item_vol, item_prod, item_haz) "
                            + "VALUES (item_seq.nextval, ?, ?, ?, ?)")) {
                int count = 0;
                int batchSize = 1000;

                for (int i = 0; i < itemSize; i++) {
                    ps.setDouble(1, item_weight.get(i));
                    ps.setDouble(2, item_vol.get(i));
                    ps.setString(3, item_prod.get(i));
                    ps.setInt(4, itemHazard.get(i));
                    ps.addBatch();
                    if ((++count % batchSize) == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println("Table \"Items\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            int dep_id = 0, del_id = 0; // Retrieving IDs
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dep_seq.currval, del_seq.currval FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dep_id = rs.getInt(1);
                    del_id = rs.getInt(2);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement( // Orders
                    "INSERT INTO Orders "
                            + "(order_id, dep_id, del_id) "
                            + "VALUES (order_seq.nextval, ?, ?)")) {

                ps.setInt(1, dep_id);
                ps.setInt(2, del_id);
                ps.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.err.println("Table \"Orders\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            order_id = getOrderID(conn);
            ArrayList<Integer> item_id = getItemID(conn, item_prod);
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
                conn.setAutoCommit(true);
                System.err.println("Table \"Orders\" has failed to be inserted in. Rollback... ");
                System.err.println("SQL error: " + e.getMessage());
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("NullPointerException error occurred: " + e.getMessage());
        }
        return order_id;
    }
}