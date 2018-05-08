package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-08.
 */

public class JSONPassListHelper extends JSONObjectBase {
    private JSONArray passList = null;

    public JSONPassListHelper(JSONObject root, int tripNumber, int stopNumber) {
        super(root, null, tripNumber, stopNumber);

        try {
            passList = stop.getJSONObject("Stops").getJSONArray("Stop");
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
        return getName(0);
    }

    public String getTime(int stopNumber){
        try {
            return getStop(stopNumber).get("arrTime").toString();
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
