package zelongames.yodatosl.JSON_Trip;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public abstract class JSONObjectBase {

    protected final JSONObject STOP;
    private final JSONArray TRIP_ARRAY;
    private final String OBJECT_NAME;

    protected JSONObject jsonObject = null;


    public JSONObjectBase(JSONArray tripArray, String objectName) {
        this.OBJECT_NAME = objectName;
        this.TRIP_ARRAY = tripArray;
        this.STOP = null;
    }

    public JSONObjectBase(JSONArray tripArray, JSONObject stop, String objectName) {
        this.OBJECT_NAME = objectName;
        this.TRIP_ARRAY = tripArray;
        this.STOP = stop;

        setTrip();
        Log.d("", "");
    }

    public void setTrip() {
        try {
            if (OBJECT_NAME != null)
                jsonObject = STOP.getJSONObject(OBJECT_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getBussLine(){
        try {
            return getProduct().get("line").toString();
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
        String hours = timeSplit[0];
        String minutes = timeSplit[1];

        String time = minutes.equals("00") ? hours : hours + ":" + minutes;

        return time;
    }

    private JSONObject getProduct(){
        try {
            return STOP.getJSONObject("Product");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getTripCount() {
        return TRIP_ARRAY.length();
    }
}
