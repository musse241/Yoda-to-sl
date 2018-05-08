package zelongames.yodatosl.JSON_Trip;

import org.json.JSONObject;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONDestination extends JSONObjectBase {

    public JSONDestination(JSONObject root) {
        super(root, "Destination");
    }

    public JSONDestination(JSONObject root, int tripNumber, int stopNumber) {
        super(root, "Destination", tripNumber, stopNumber);


    }
}
