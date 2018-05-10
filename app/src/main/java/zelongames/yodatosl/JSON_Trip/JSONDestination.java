package zelongames.yodatosl.JSON_Trip;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONDestination extends JSONObjectBase {

    public JSONDestination(JSONArray tripArray) {
        super(tripArray, "Destination");
    }

    public JSONDestination(JSONArray tripArray, JSONObject stop) {
        super(tripArray, stop, "Destination");


    }
}
