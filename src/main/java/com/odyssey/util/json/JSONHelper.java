package com.odyssey.util.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONHelper {
    public static JSONObject createJSONForOrder(
            String dep_zip, String dep_state, String dep_city,
            String del_zip, String del_state, String del_city,
            ArrayList<Integer> item_id, ArrayList<Double> item_weight, ArrayList<Double> item_vol,
            ArrayList<String> item_prod, ArrayList<Boolean> item_haz) {
        JSONObject jsonMain = new JSONObject();

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

        for (int i = 0; i < item_id.size(); i++) {
            jsonItemList.add(i, new JSONObject());
            jsonItemList.get(i).put("weight", item_weight.get(i));
            jsonItemList.get(i).put("volume", item_vol.get(i));
            jsonItemList.get(i).put("product", item_prod.get(i));
            jsonItemList.get(i).put("hazard", item_haz.get(i));
            jsonLines.put(jsonItemList.get(i));
        }

        jsonMain.put("from", jsonDeparture);
        jsonMain.put("to", jsonDelivery);
        jsonMain.put("lines", jsonLines);
        return jsonMain;
    }
}