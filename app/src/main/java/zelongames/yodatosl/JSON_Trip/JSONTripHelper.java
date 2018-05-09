package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-09.
 */

public class JSONTripHelper {

    public static JSONObject getRoot(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getTripArray(JSONObject root){
        try {
          return root.getJSONArray("Trip");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLegArray(JSONArray tripArray, int index) {
        try {
            if (index > tripArray.length() - 1)
                index = tripArray.length() - 1;
            return ((JSONObject) tripArray.get(index)).getJSONObject("LegList").getJSONArray("Leg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject getStop(JSONArray legArray, int stopNumber) {
        try {
            return legArray.getJSONObject(stopNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
