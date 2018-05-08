package zelongames.yodatosl.JSON_Trip;

/**
 * Created by Jonas on 2018-05-03.
 */

public class JSONDestination extends JSONObjectBase {

    public JSONDestination(String data) {
        super(data, "Destination");
    }

    public JSONDestination(String data, int tripNumber, int stopNumber) {
        super(data, "Destination", tripNumber, stopNumber);
    }
}
