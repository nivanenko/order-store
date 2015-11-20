package server;

import org.json.JSONArray;
import org.json.JSONObject;
import server.object.Line;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Connection connection = null;

    Database() {
        try {
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/op");
            connection = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected JSONObject createJSON(int order_id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        JSONObject jo_main = new JSONObject();

        int dep_id = 0, del_id = 0;
        int dep_zip = 0, del_zip = 0;
        String dep_state = "", del_state = "", dep_city = "", del_city = "";

        List<Integer> item_id = new ArrayList<>();
        List<Double> item_weight = new ArrayList<>();
        List<Integer> item_vol = new ArrayList<>();
        List<Integer> item_haz = new ArrayList<>();
        List<String> item_prod = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement
                    ("SELECT item_id FROM OrderItems WHERE order_id = ?");
            preparedStatement.setInt(1, order_id);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            do {
                item_id.add(resultSet.getInt("item_id"));
            } while (resultSet.next());

            preparedStatement = connection.prepareStatement
                    ("SELECT dep_id, del_id FROM Orders WHERE order_id = ?");
            preparedStatement.setInt(1, order_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dep_id = resultSet.getInt("dep_id");
                del_id = resultSet.getInt("del_id");
            }

            preparedStatement = connection.prepareStatement
                    ("SELECT dep_zip, dep_state, dep_city " +
                            "FROM Departure " +
                            "WHERE dep_id = ?");
            preparedStatement.setInt(1, dep_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dep_zip = resultSet.getInt("dep_zip");
                dep_state = resultSet.getString("dep_state");
                dep_city = resultSet.getString("dep_city");
            }

            preparedStatement = connection.prepareStatement
                    ("SELECT del_zip, del_state, del_city " +
                            "FROM Delivery " +
                            "WHERE del_id = ?");
            preparedStatement.setInt(1, del_id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                del_zip = resultSet.getInt("del_zip");
                del_state = resultSet.getString("del_state");
                del_city = resultSet.getString("del_city");
            }

            for (int i = 0; i < item_id.size(); i++) {
                preparedStatement = connection.prepareStatement
                        ("SELECT item_weight, item_vol, item_haz, item_prod " +
                                "FROM Items " +
                                "WHERE item_id = ?");
                preparedStatement.setInt(1, item_id.get(i));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    item_weight.add(i, resultSet.getDouble("item_weight"));
                    item_vol.add(i, resultSet.getInt("item_vol"));
                    item_haz.add(i, resultSet.getInt("item_haz"));
                    item_prod.add(i, resultSet.getString("item_prod"));
                }
            }

            // Deleting whitespaces where it shouldn't be
            for (int i = 0; i < item_prod.size(); i++) {
                item_prod.set(i, item_prod.get(i).replaceAll("\\s+$", ""));
            }
            dep_city = dep_city.replaceAll("\\s+$", "");
            dep_state = dep_state.replaceAll("\\s+$", "");
            del_city = del_city.replaceAll("\\s+$", "");
            del_state = del_state.replaceAll("\\s+$", "");

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

            List<Boolean> item_hazBool = new ArrayList<>();
            for (int i = 0; i < item_haz.size(); i++) {
                if (item_haz.get(i) == 1) {
                    item_hazBool.add(i, true);
                } else {
                    item_hazBool.add(i, false);
                }
            }

            for (int i = 0; i < item_id.size(); i++) {
                joList.add(i, new JSONObject());
                joList.get(i).put("weight", item_weight.get(i));
                joList.get(i).put("volume", item_vol.get(i));
                joList.get(i).put("product", item_prod.get(i));
                joList.get(i).put("hazard", item_hazBool.get(i));
                ja_lines.put(joList.get(i));
            }

            jo_main.put("from", jo_dep);
            jo_main.put("to", jo_del);
            jo_main.put("lines", ja_lines);
            return jo_main;

        } catch (SQLException e) {
            System.err.println("SQL error: " + e);
        }
        return null;
    }

    protected int createOrder(
            String dep_zipStr, String dep_state, String dep_city,
            String del_zipStr, String del_state, String del_city,
            List<Line> lineList
    ) {
        int dep_zip = Integer.parseInt(dep_zipStr);
        int del_zip = Integer.parseInt(del_zipStr);
        int order_id = 0;
        int dep_id = 0;
        int del_id = 0;

        List<Integer> item_id = new ArrayList<>();
        List<Double> item_weight = new ArrayList<>();
        List<Double> item_vol = new ArrayList<>();
        List<Boolean> item_haz = new ArrayList<>();
        List<String> item_prod = new ArrayList<>();

        for (Line aLineList : lineList) {
            item_weight.add(aLineList.getWeight());
            item_vol.add(aLineList.getVolume());
            item_haz.add(aLineList.isHazard());
            item_prod.add(aLineList.getProduct());
        }

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO Departure " +
                            "(dep_id, dep_zip, dep_state, dep_city) " +
                            "VALUES (dep_seq.nextval, ?, ?, ?)");
            preparedStatement.setInt(1, dep_zip);
            preparedStatement.setString(2, dep_state);
            preparedStatement.setString(3, dep_city);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO Delivery " +
                            "(del_id, del_zip, del_state, del_city) " +
                            "VALUES (del_seq.nextval, ?, ?, ?)");
            preparedStatement.setInt(1, del_zip);
            preparedStatement.setString(2, del_state);
            preparedStatement.setString(3, del_city);
            preparedStatement.executeUpdate();

            // Converting hazard boolean -> short
            List<Integer> itemHazard = new ArrayList<>();
            for (int i = 0; i < item_haz.size(); i++) {
                if (item_haz.get(i)) {
                    itemHazard.add(i, 1);
                } else {
                    itemHazard.add(i, 0);
                }
            }

            for (int i = 0; i < item_prod.size(); i++) {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO Items " +
                                "(item_id, item_weight, item_vol, item_prod, item_haz) " +
                                "VALUES (item_seq.nextval, ?, ?, ?, ?)");
                preparedStatement.setDouble(1, item_weight.get(i));
                preparedStatement.setDouble(2, item_vol.get(i));
                preparedStatement.setString(3, item_prod.get(i));
                preparedStatement.setInt(4, itemHazard.get(i));
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement
                        ("SELECT item_seq.currval FROM dual");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    item_id.add(i, resultSet.getInt(1));
                }
            }

            preparedStatement = connection.prepareStatement
                    ("SELECT dep_seq.currval, del_seq.currval FROM dual");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dep_id = resultSet.getInt(1);
                del_id = resultSet.getInt(2);
            }

            preparedStatement = connection.prepareStatement(
                    "INSERT INTO Orders " +
                            "(order_id, dep_id, del_id) " +
                            "VALUES (order_seq.nextval, ?, ?)");
            preparedStatement.setInt(1, dep_id);
            preparedStatement.setInt(2, del_id);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement
                    ("SELECT order_seq.currval FROM dual");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order_id = resultSet.getInt(1);
            }

            for (int i = 0; i < item_prod.size(); i++) {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO OrderItems " +
                                "(order_item, order_id, item_id) " +
                                "VALUES (orderitem_seq.nextval, ?, ?)");
                preparedStatement.setInt(1, order_id);
                preparedStatement.setInt(2, item_id.get(i));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e);
        }
        return order_id;
    }
}