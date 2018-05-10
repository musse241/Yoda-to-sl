package zelongames.yodatosl.JSON_Trip;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-08.
 */

public class JSONPassListManager extends JSONObjectBase {
    private JSONArray passList = null;

    public JSONPassListManager(JSONObject stop) {
        super(null, stop, null);

        try {
            passList = STOP.getJSONObject("Stops").getJSONArray("Stop");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPassList(){
        return passList != null;
    }

    public String getName(int stopNumber){
        try {
            return getStop(stopNumber).get("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return getSimplifiedTime(getName(0));
    }

    public String getTime(int stopNumber){
        try {
            return getSimplifiedTime(getStop(stopNumber).get("arrTime").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getTime() {
        return getTime(0);
    }

    private JSONObject getStop(int stopNumber){
        try {
            return passList.getJSONObject(stopNumber);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getIntermediateStopCount() {
        return passList.length();
    }
}
