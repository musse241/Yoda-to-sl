package zelongames.yodatosl;

import android.location.Address;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zelongames.yodatosl.JSON_Trip.JSONDestination;
import zelongames.yodatosl.JSON_Trip.JSONPassListHelper;
import zelongames.yodatosl.JSON_Trip.JSONOrigin;

/**
 * Created by Jonas on 2018-05-03.
 */

public class FetchData extends AsyncTask<Void, Void, Void> {

    public enum TextFormat {
        Info,
        Speech,
    }

    private final boolean PASS_LIST;
    private final boolean USE_DATE_TIME;
    private final TextFormat TEXT_FORMAT;

    private String originName = null;
    private String originID = null;
    private String destinationName = null;
    private String destID = null;

    private String tripInfo = "";

    public String getTripInfo() {
        return tripInfo;
    }

    private StringBuilder data = null;

    public String getData() {
        return data.toString();
    }

    public FetchData(TextFormat textFormat, String originName, String destinationName, boolean passlist, boolean useDateTime) {
        this.TEXT_FORMAT = textFormat;
        this.originName = originName;
        this.destinationName = destinationName;
        this.PASS_LIST = passlist;
        this.USE_DATE_TIME = useDateTime;
    }

    @Override
    protected Void doInBackground(Void... objects) {
        LatLng originLocation = getLocationFromAddress(originName);
        LatLng destinationLocation = getLocationFromAddress(destinationName);

        originID = getNearbyStationsID(originLocation, originName);
        destID = getNearbyStationsID(destinationLocation, destinationName);

        updateTripData();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (originName != null && destinationName != null) {

            tripInfo = getTripInfo(1);
            MainActivity.txtSLGuide.setText(tripInfo);
        }
    }

    private String getTripInfo(Integer tripNumber) {
        String tripGuide = "";

        // Only convert data to string once
        String data = getData();

        JSONObject root = getRoot(data);

        JSONOrigin dummyOrigin = new JSONOrigin(root);

        int tripCount = dummyOrigin.getTripCount();

        for (int t = 0; t < tripCount; t++) {
            int stopCount = dummyOrigin.getStopCount(t);
            for (int s = 0; s < stopCount; s++) {
                JSONOrigin origin = new JSONOrigin(root, t, s);
                JSONDestination destination = new JSONDestination(root, t, s);

                switch (TEXT_FORMAT) {
                    case Info:
                        tripGuide += origin.getTime() + " " + origin.getName() + " - " + destination.getTime() + " " + destination.getName() + "\n";
                        break;
                    case Speech:
                        if (s == 0) {
                            tripGuide += "I will tell you how to go from " + origin.getName() + " to " + destination.getName() + ". ";
                            tripGuide += "Go to " + origin.getName() + ", " + origin.getTime() + " o'clock. ";
                        } else {
                            tripGuide += " Then get off the buss at " + origin.getName() + " and enter the buss at " + origin.getTime() + " o'clock. ";
                        }
                        break;
                }

                if (PASS_LIST) {
                    JSONPassListHelper passListHelper = new JSONPassListHelper(root, t, s);
                    if (passListHelper.hasPassList()) {
                        int intermediateStops = passListHelper.getIntermediateStopCount();
                        for (int i = 1; i < intermediateStops; i++) {
                            switch (TEXT_FORMAT) {
                                case Info:
                                    tripGuide += "---" + passListHelper.getTime(i) + " " + passListHelper.getName(i) + "\n";
                                    break;
                                case Speech:
                                    boolean onLastStop = s == stopCount - 1 && i == intermediateStops - 1;

                                    if (i == 1)
                                        tripGuide += "Then you have to go to " + passListHelper.getName(i) + " at " + passListHelper.getTime(i) + " o'clock. ";
                                    else if (onLastStop)
                                        tripGuide += "And finally you will enter " + passListHelper.getName(i) + ", at " + passListHelper.getTime(i) + " o'clock. ";
                                    else
                                        tripGuide += "And then you will enter " + passListHelper.getName(i) + ", " + passListHelper.getTime(i) + " o'clock. ";
                                    break;
                            }
                        }
                    }
                }
            }
            // Only get the first trip of the list
            if (USE_DATE_TIME)
                break;

            tripGuide += "\n";
        }

        return tripGuide;
    }

    public JSONObject getRoot(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateTripData() {
        readData(getTripURL(originID, destID));
    }

    private void readData(URL url) {
        data = new StringBuilder();

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = bufferedReader.readLine();

            while (line != null) {
                data.append(line);
                line = bufferedReader.readLine();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNearbyStationsID(LatLng location, String locationName) {
        readData(getNearbyStationsURL(location));

        String data = getData();
        if (!data.isEmpty())
            return getStopID(data, locationName);

        return null;
    }

    private String getStopID(String data, String locationName) {
        String id = "";

        try {
            JSONObject root = new JSONObject(data);
            JSONArray stopLocations = root.getJSONObject("LocationList").getJSONArray("StopLocation");

            // Search for the stop with the name of 'locationName'
            for (int i = 0; i < stopLocations.length(); i++) {
                JSONObject object = root.getJSONObject("LocationList").getJSONArray("StopLocation").getJSONObject(i);
                String name = object.get("name").toString();
                id = object.get("id").toString();

                // Found it!
                if (name.contains(locationName))
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id;
    }

    private String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    }

    private LatLng getLocationFromAddress(String locationName) {

        try {
            List<Address> address = MainActivity.coder.getFromLocationName(locationName, 1);

            if (address == null || address.size() == 0)
                return null;

            Address location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private URL getNearbyStationsURL(LatLng location) {
        try {
            return new URL("http://api.sl.se/api2/nearbystops.json?key=" + API_Keys.NEARBY_STOPS_KEY +
                    "&originCoordLat=" + location.latitude +
                    "&originCoordLong=" + location.longitude);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private URL getTripURL(String originID, String destID) {
        try {
            if (USE_DATE_TIME)
                return new URL("http://api.sl.se/api2/TravelplannerV3/trip.json?key=" + API_Keys.RESPLANERARE_KEY +
                        "&originID=" + originID +
                        "&destID=" + destID +
                        "&searchForArrival=0" +
                        "&passlist=" + (PASS_LIST == true ? 1 : 0) +
                        "&date=" + getDateTime() +
                        "&time=" + getTime());
            else
                return new URL("http://api.sl.se/api2/TravelplannerV3/trip.json?key=" + API_Keys.RESPLANERARE_KEY +
                        "&originID=" + originID +
                        "&destID=" + destID +
                        "&searchForArrival=0" +
                        "&passlist=" + (PASS_LIST == true ? 1 : 0));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}