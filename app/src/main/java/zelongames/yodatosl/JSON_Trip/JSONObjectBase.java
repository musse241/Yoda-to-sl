package zelongames.yodatosl.JSON_Trip;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public abstract class JSONObjectBase {

    private final String OBJECT_NAME;

    private JSONArray tripArray = null;
    protected JSONObject jsonObject = null;
    protected JSONObject stop = null;


    public JSONObjectBase(JSONObject root, String objectName) {
        this.OBJECT_NAME = objectName;

        try {
            tripArray = root.getJSONArray("Trip");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObjectBase(JSONObject root, String objectName, int tripNumber, int stopNumber) {
        this(root, objectName);

        setTrip(tripNumber, stopNumber);
        Log.d("", "");
    }

    public void setTrip(int tripNumber, int stopNumber) {
        try {
            stop = ((JSONObject) getLegArray(tripNumber).get(stopNumber));
            if (OBJECT_NAME != null)
                jsonObject = stop.getJSONObject(OBJECT_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            return getSimplifiedTime(jsonObject.get("time").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String getSimplifiedTime(String longTime){
        String[] timeSplit = longTime.split(":");
        String minutes = timeSplit[1];


        String time = minutes.equals("00") ? timeSplit[0] : timeSplit[0] + ":" + minutes;

        return time;
    }

    public int getTripCount() {
        return tripArray.length();
    }

    public int getStopCount(int tripNumber) {
        return getLegArray(tripNumber).length();
    }
}
