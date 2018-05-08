package zelongames.yodatosl;

import android.os.AsyncTask;
import android.util.Log;

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

public class FetchData  extends AsyncTask<Void,Void,Void>{

    //To contain data from database
    String data = "";

    //
    String dataParsed = "";
    String singleParsed = "";

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            //To get data from an URL
            URL url = new URL("https://api.myjson.com/bins/11f122");
            //Create url connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
            //To get Object from a JsonArray
            JSONArray JA = new JSONArray(data);
            for(int i =0; i < JA.length(); i++){
                JSONObject jsonObject = (JSONObject) JA.get(i);
                singleParsed = "Name:" + jsonObject.get("name") + "\n" +
                               "Password:" + jsonObject.get("password") + "\n" +
                               "Contact:" + jsonObject.get("contact") + "\n" +
                               "Country:" + jsonObject.get("country") + "\n";
                Log.e("SingleParsed",singleParsed);
                Log.e("JsonArray","It runs here"+String.valueOf(i));
                dataParsed = dataParsed + singleParsed + "\n";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        JsonTest.textView.setText(this.dataParsed); //this.data
    }
}
