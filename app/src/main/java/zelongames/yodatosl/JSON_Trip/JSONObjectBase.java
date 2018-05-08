package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public abstract class JSONObjectBase {

    private final String OBJECT_NAME;
    private final String DATA;

    private JSONArray tripArray = null;
    private JSONObject jsonObject = null;

    public JSONObjectBase(String data, String objectName) {
        this.DATA = data;
        this.OBJECT_NAME = objectName;

        try {
            tripArray = getRoot().getJSONArray("Trip");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObjectBase(String data, String objectName, int tripNumber, int stopNumber) {
        this(data, objectName);

        setTrip(tripNumber, stopNumber);
    }

    public void setTrip(int tripNumber, int stopNumber) {
        try {
            jsonObject = ((JSONObject) getLegArray(tripNumber).get(stopNumber)).getJSONObject(OBJECT_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRoot() {
        try {
            return new JSONObject(DATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray getLegArray(int index) {
        try {
            if (index > tripArray.length() - 1)
                index = tripArray.length() - 1;
            return ((JSONObject) tripArray.get(index)).getJSONObject("LegList").getJSONArray("Leg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get Trip Information

    public String getName() {
        if (jsonObject == null)
            return null;

        try {
            return jsonObject.get("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getTime() {
        if (jsonObject == null)
            return null;

        try {
            return jsonObject.get("time").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getTripCount() {
        return tripArray.length();
    }

    public int getStopCount(int tripNumber) {
        return getLegArray(tripNumber).length();
    }
}
