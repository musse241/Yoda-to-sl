package zelongames.yodatosl;

import android.location.Address;
import android.os.AsyncTask;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import zelongames.yodatosl.JSON_Trip.JSONDestination;
import zelongames.yodatosl.JSON_Trip.JSONPassListManager;
import zelongames.yodatosl.JSON_Trip.JSONOrigin;
import zelongames.yodatosl.JSON_Trip.JSONTripHelper;

/**
 * Created by Jonas on 2018-05-03.
 */

public class FetchData extends AsyncTask<Void, Void, Void> {

    public enum TextFormat {
        Info,
        Speech,
    }

    private boolean isTaskFinished = false;

    public boolean getIsTaskFinished() {
        return isTaskFinished;
    }

    private final boolean PASS_LIST;
    private final boolean USE_DATE_TIME;

    private String originName = null;
    private String originID = null;
    private String destinationName = null;
    private String destID = null;

    private String tripInfo = "";

    public String getTripInfo() {
        return tripInfo;
    }

    public StringBuilder data = null;

    public String getData() {
        return data.toString();
    }

    public FetchData(String originName, String destinationName, boolean passlist, boolean useDateTime) {
        this.originName = originName;
        this.destinationName = destinationName;
        this.PASS_LIST = passlist;
        this.USE_DATE_TIME = useDateTime;
    }

    @Override
    protected Void doInBackground(Void... objects) {
        LatLng originLocation = getLocationFromAddress(originName);
        LatLng destinationLocation = getLocationFromAddress(destinationName);

        ArrayList<String> originInfo ;
        ArrayList<String> destInfo;

        try {
            originInfo = getNearbyStationsInfo(originLocation, originName);
            destInfo = getNearbyStationsInfo(destinationLocation, destinationName);
            originID = originInfo.get(0);
            destID = destInfo.get(0);
        } catch (Exception e) {
            originID = destID = null;
            return null;
        }

        // Update to the exact station name
        originName = originInfo.get(1);
        destinationName = destInfo.get(1);

        if (originID != null || destID != null)
            updateTripData();

        isTaskFinished = true;

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isTaskFinished = false;
        MainActivity.txtSLGuide.setText("Laddar...");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (hasFailed()) {
            MainActivity.txtSLGuide.setText("Error");
            isTaskFinished = true;
            return;
        }

        isTaskFinished = true;

        if (originName != null && destinationName != null) {

            tripInfo = getTripInfo(TextFormat.Speech);
            MainActivity.txtSLGuide.setText(getTripInfo(TextFormat.Info));
        }
    }

    private String getTripInfo(TextFormat textFormat) {
        String tripGuide = "";

        // Only convert data to string once
        String data = getData();

        JSONObject root = JSONTripHelper.getRoot(data);
        JSONArray tripArray = JSONTripHelper.getTripArray(root);

        JSONOrigin dummyOrigin = new JSONOrigin(tripArray);

        int tripCount = dummyOrigin.getTripCount();

        for (int t = 0; t < tripCount; t++) {
            JSONArray legArray = JSONTripHelper.getLegArray(tripArray, t);

            int stopCount = legArray.length();
            for (int s = 0; s < stopCount; s++) {
                JSONObject stop = JSONTripHelper.getStop(legArray, s);

                JSONOrigin origin = new JSONOrigin(tripArray, stop);
                JSONDestination destination = new JSONDestination(tripArray, stop);

                switch (textFormat) {
                    case Info:
                        tripGuide += origin.getTime() + " " + origin.getName() + " - " + destination.getTime() + " " + destination.getName() + "\n";
                        break;
                    case Speech:
                        if (s == 0) {
                            tripGuide += "I will tell you how to go from " + originName + " to " + destinationName + ". ";
                            tripGuide += "Go to " + origin.getName() + " and take the buss " + origin.getBussLine() + ", " + origin.getTime() + " o'clock. ";
                        } else
                            tripGuide += " Then get off the buss at " + origin.getName() + " and enter the buss " + origin.getBussLine() + " at " + origin.getTime() + " o'clock. ";
                        break;
                }

                if (PASS_LIST) {
                    JSONPassListManager passListManager = new JSONPassListManager(stop);
                    if (passListManager.hasPassList()) {
                        int intermediateStops = passListManager.getIntermediateStopCount();
                        for (int i = 1; i < intermediateStops; i++) {
                            switch (textFormat) {
                                case Info:
                                    tripGuide += "- " + passListManager.getTime(i) + " " + passListManager.getName(i) + "\n";
                                    break;
                                case Speech:
                                    boolean onLastStop = s == stopCount - 1 && i == intermediateStops - 1;
                                    if (onLastStop)
                                        tripGuide += "And finally you will enter " + passListManager.getName(i) + ", at " + passListManager.getTime(i) + " o'clock. ";
                                    else
                                        tripGuide += "And then you will enter " + passListManager.getName(i) + ". ";
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

    private void updateTripData() {
        readData(getTripURL(originID, destID));
    }

    private void readData(URL url) {
        if (url == null)
            return;

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

    private ArrayList<String> getNearbyStationsInfo(LatLng location, String locationName) {
        URL url = getNearbyStationsURL(location);
        if (url == null)
            return null;

        readData(url);

        String data = getData();
        if (!data.isEmpty()) {
            return getStopInfo(data, locationName);
        }
        return null;
    }

    private ArrayList<String> getStopInfo(String data, String locationName) {
        ArrayList<String> results = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(data);
            JSONArray stopLocations = root.getJSONObject("LocationList").getJSONArray("StopLocation");

            // Search for the stop with the name of 'locationName'
            for (int i = 0; i < stopLocations.length(); i++) {
                JSONObject object = root.getJSONObject("LocationList").getJSONArray("StopLocation").getJSONObject(i);
                String name = object.get("name").toString();

                // Found it!
                if (name.toLowerCase().contains(locationName.toLowerCase())) {
                    results.add(object.get("id").toString());
                    results.add(object.get("name").toString());
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
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
        if (location == null)
            return null;

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

    private boolean hasFailed() {
        return originID == null || destID == null;
    }
}
