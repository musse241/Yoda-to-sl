package zelongames.yodatosl;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static TextView txtSLGuide = null;
    public static Geocoder coder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coder = new Geocoder(this);
        txtSLGuide = findViewById(R.id.txtSLGuide);

        FetchData fetchData = new FetchData("Rimbo station", "Tekniska högskolan", true);
        fetchData.execute();
    }


    public String getClosestStopID(String data) {
        String id = "";

        try {
            JSONObject root = new JSONObject(data);
            id = root.getJSONObject("LocationList").getJSONArray("StopLocation").getJSONObject(0).get("id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id;
    }

    public LatLng getLocationFromAddress(String locationName) {
        Geocoder coder = new Geocoder(this);

        try {
            List<Address> address = coder.getFromLocationName(locationName, 1);

            if (address == null || address.size() == 0)
                return null;

            Address location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }
}
