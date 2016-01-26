package database;

import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseHelper {
    private static final Pattern NO_WHITESPACE = Pattern.compile("\\s+$");

    private int boolToInt(boolean value) {
        // Convert true to 1 and false to 0
        return value ? 1 : 0;
    }

    private boolean intToBool(int value) {
        // Convert 1 to true and 0 to false
        return value == 1;
    }

    public JSONObject createJSON(int orderId, HikariDataSource ds) {
        JSONObject jo_main = new JSONObject();

        List<Integer> item_id = new ArrayList<>();
        List<Double> item_weight = new ArrayList<>();
        List<Integer> item_vol = new ArrayList<>();
        List<Integer> item_haz = new ArrayList<>();
        List<String> item_prod = new ArrayList<>();

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

            JSONObject jo_dep = new JSONObject();
            jo_dep.put("zip", dep_zip);
            jo_dep.put("state", dep_state);
            jo_dep.put("city", dep_city);

            JSONObject jo_del = new JSONObject();
            jo_del.put("zip", del_zip);
            jo_del.put("state", del_state);
            jo_del.put("city", del_city);

            JSONArray ja_lines = new JSONArray();
            List<JSONObject> joList = new ArrayList<>();

            List<Boolean> item_haz_bool = new ArrayList<>();
            for (Integer anItem_haz : item_haz) {
                item_haz_bool.add(intToBool(anItem_haz));
            }

            for (int i = 0; i < item_id.size(); i++) {
                joList.add(i, new JSONObject());
                joList.get(i).put("weight", item_weight.get(i));
                joList.get(i).put("volume", item_vol.get(i));
                joList.get(i).put("product", item_prod.get(i));
                joList.get(i).put("hazard", item_haz_bool.get(i));
                ja_lines.put(joList.get(i));
            }

            jo_main.put("from", jo_dep);
            jo_main.put("to", jo_del);
            jo_main.put("lines", ja_lines);
            return jo_main;

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return null;
    }

    public int createOrder(
            HikariDataSource ds,
            String dep_zipStr, String dep_state, String dep_city,
            String del_zipStr, String del_state, String del_city,
            List<Double> item_weight,
            List<Double> item_vol,
            List<Boolean> item_haz,
            List<String> item_prod
    ) {
        if (dep_zipStr == null) return -1;

        int dep_zip = Integer.parseInt(dep_zipStr);
        int del_zip = Integer.parseInt(del_zipStr);
        int order_id = 0;
        int itemSize = item_prod.size();
        List<Integer> item_id = new ArrayList<>();

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
            }

            try (PreparedStatement ps = conn.prepareStatement( // Delivery
                    "INSERT INTO Delivery "
                            + "(del_id, del_zip, del_state, del_city) "
                            + "VALUES (del_seq.nextval, ?, ?, ?)")) {

                ps.setInt(1, del_zip);
                ps.setString(2, del_state);
                ps.setString(3, del_city);
                ps.executeUpdate();
            }

            List<Integer> itemHazard = new ArrayList<>();
            for (Boolean anItem_haz : item_haz) {
                itemHazard.add(boolToInt(anItem_haz));
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
            }

            int itemIdTemp = 0; // getting item ID
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT item_seq.currval FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itemIdTemp = rs.getInt(1);
                }
            }
            itemIdTemp -= item_prod.size();
            for (int i = 0; i < item_prod.size(); i++) {
                itemIdTemp++;
                item_id.add(i, itemIdTemp);
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
            }

            try (PreparedStatement ps = conn.prepareStatement( // Retrieving order ID
                    "SELECT order_seq.currval FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    order_id = rs.getInt(1);
                }
            }

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
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("SQL error occurred: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("NullPointerException error occurred: " + e.getMessage());
        }
        return order_id;
    }
}