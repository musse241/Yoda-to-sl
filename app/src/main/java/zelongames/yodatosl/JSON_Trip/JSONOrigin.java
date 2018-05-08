package zelongames.yodatosl.JSON_Trip;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONOrigin extends JSONObjectBase {

    public JSONOrigin(String data) {
        super(data, "Origin");
    }
    public JSONOrigin(String data, int tripNumber, int stopNumber) {
        super(data, "Origin", tripNumber, stopNumber);
    }
}
