package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONOrigin extends JSONObjectBase {

    public JSONOrigin(JSONArray tripArray) {
        super(tripArray, "Origin");
    }
    public JSONOrigin(JSONObject stop) {
        super(stop, "Origin");
    }
}
