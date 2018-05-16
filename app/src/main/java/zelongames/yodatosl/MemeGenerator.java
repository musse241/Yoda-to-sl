package zelongames.yodatosl;

/**
 * Created by Erhan on 2018-05-16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Random;


public class MemeGenerator extends AppCompatActivity {

    //EditText textInput;
    TextView responseView;
    ProgressBar progressBar;
    //private static final String TAG = "MemeGenerator";
    //private static final String API_KEY = "faba1397-dc12-4049-917d-a41d0586721e";
    private static final String API_URL = "https://api.imgflip.com/get_memes";
    //private static final String API_URL = "https://swapi.co/api/";
    public static ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_generator);

        responseView = findViewById(R.id.responseView);
        //textInput = findViewById(R.id.inputText);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        Button queryButton = findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String text = textInput.getText().toString();
            // Do some validation here

            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            // TODO: check this.exception
            // TODO: do something with the feed

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject data = object.getJSONObject("data");
                JSONArray array = data.getJSONArray("memes");

                int i = randomNumberGenerator();


                JSONObject id = array.getJSONObject(i);
                String urlLink = id.getString("url");
                String imageText = id.getString("name");
                responseView.setText(imageText);

                Log.d("nejnej", array.get(0).toString());
                Log.d("nejnej", urlLink);


                Picasso.get().load(urlLink).into(imageView);


            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
                e.printStackTrace();
                // Do something to recover ... or kill the app.
            }
        }
    }

    public int randomNumberGenerator()
    {
        Random rand = new Random();

        int  n = rand.nextInt(100) + 1;
        return n;
    }
}