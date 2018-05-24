package zelongames.yodatosl;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Wikipedia extends AppCompatActivity  implements View.OnClickListener{
    private static final String TAG = "Wikipedia";
    private static final String API_KEY = "USE_YOUR_OWN_KEY";
    private static final String API_URL = "https://sv.wikipedia.org/w/api.php?";


    private TextView wikipediaText;

    private String action = "action=query&formatversion=2";
    private String titles = "titles=Skanstull";
    private String prop = "prop=revisions";
    private String rvprop = "rvprop=content";
    private String format = "format=json";

    private Button goBackBtn;
    String texts = "";

    public void setTexts(String texts) {
        this.texts = texts;
    }

    public String getTexts() {
        return texts;
    }

    Destination destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wikipedia);
        goBackBtn = findViewById(R.id.goBackbtn);
        wikipediaText = findViewById(R.id.wikipediaText);

        Intent intent = getIntent();
        destination = (Destination) intent.getSerializableExtra("Destination");
        Log.d(TAG, "onCreate: "+destination.getToStation());



        new RetrieveFeedTask().execute();

        goBackBtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.goBackbtn:
                Intent intent = new Intent(Wikipedia.this,MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            wikipediaText.setText("Laddar...");
        }

        @Override
        protected String doInBackground(Void... urls) {

            String searchText = correctInput(destination.getToStation());
            String data="";
            try{
                Log.d(TAG, "doInBackground: Before");
                //       URL url = new URL(API_URL+action+"&"+titles+"&"+prop+"&"+rvprop+"&"+format);
                URL url = new URL(API_URL+action+"&"+"titles="+searchText+"&"+prop+"&"+rvprop+"&"+format);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    Log.d(TAG, "doInBackground: Inner");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");
                        data = data + line;
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e){
                Log.d(TAG, "doInBackground: Error");
                Toast.makeText(Wikipedia.this,"Fetch status: ERROR", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        private String correctInput(String toStation) {
            String newText = "";
            for (int i = 0; i < toStation.length(); i++) {

                if (toStation.charAt(i)==' ')
                    newText += "_";
                else
                    newText += toStation.charAt(i);
            }
            return newText;
        }

        protected void onPostExecute(String response){
            String text="";

            if(response != null && !response.equals("")){
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "onPostExecute: 1");
                    JSONObject queryObject = jsonObject.getJSONObject("query");
                    Log.d(TAG, "onPostExecute: 2");
                    /*JSONObject queryObjectInner = queryObject.getJSONObject("pages");
                    Log.d(TAG, "onPostExecute: 3");
                    JSONObject queryObjectInner2 = queryObjectInner.getJSONObject("4285704");
                    Log.d(TAG, "onPostExecute: 4");*/
                    JSONArray searchObjects = queryObject.getJSONArray("pages");
                    Log.d(TAG, "onPostExecute: 5");


                    for (int i = 0; i < searchObjects.length() ; i++) {
                        JSONObject jsonObject1 = searchObjects.getJSONObject(i);
                        Log.d(TAG, "onPostExecute: 9");
                        JSONArray searchObjects2 = jsonObject1.getJSONArray("revisions");
                        Log.d(TAG, "onPostExecute: 6");
                        JSONObject searchObject = searchObjects2.getJSONObject(i);
                        Log.d(TAG, "onPostExecute: 7");
                        text = searchObject.getString("content"); //Specifik
                        Log.d(TAG, "onPostExecute: INNER: "+text);
                        textCorrection(text);
                    }

                    Log.d(TAG, "onPostExecute: 8");
                    Log.d(TAG, "onPostExecute: OUTER: "+text);

                    /*Iterator iterator = jsonObject.keys();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        jsonArray.put(jsonObject.get(key));
                    }
                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject jsonObjectRes = jsonArray.getJSONObject(i);

                        String title = jsonObjectRes.getString("title");
                        Log.d(TAG, "onPostExecute: "+title);

                    }*/
                } catch (JSONException e) {
                    Log.d(TAG, "onPostExecute: ERROR");
                    e.printStackTrace();
                }
            }

            else if(response == null){
                response = "THERE WAS A ERROR";
            }
            Log.d(TAG, "onPostExecute: After");

        }

        private void textCorrection(String text) {
            String newText="";
            for (int i = 0; i < text.length() ; i++) {
                char word = text.charAt(i);
                if( word == '{' || word == '}'|| word == '['|| word == ']' || word == '<' || word == '>'){
                }
                else{
                    newText=newText+word;
                }
            }
            wikipediaText.setText(newText);
        }
    }

}
