package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONOrigin extends JSONObjectBase {

    public JSONOrigin(JSONObject root) {
        super(root, "Origin");
    }
    public JSONOrigin(JSONObject root, int tripNumber, int stopNumber) {
        super(root, "Origin", tripNumber, stopNumber);
    }
}
