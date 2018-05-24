package zelongames.yodatosl;

import android.content.Intent;
import android.location.Geocoder;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    Intent intent;
    Destination destination;

    private AutoCompleteTextView fromStationTV;
    private AutoCompleteTextView toStationTV;
    private Button searchTrip;
    public static TextView txtSLGuide = null;
    public static Geocoder coder = null;

    private FetchData fetchData = null;
    private Button btnListenStart = null;
    private Button btnListenStop = null;
    private Button btnWiki;
    private TextToSpeech textToSpeech = null;
    String tripInfo;

    public static TextToSpeech getTextToSpeech() {
        return getTextToSpeech();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coder = new Geocoder(this);
        txtSLGuide = findViewById(R.id.txtSLGuide);
        fromStationTV = findViewById(R.id.fromStationTextView);
        toStationTV = findViewById(R.id.toStationTextView);
        searchTrip = findViewById(R.id.searchTrip);
        btnWiki = findViewById(R.id.btnWikipedia);

        searchTrip.setOnClickListener(this);
        btnWiki.setOnClickListener(this);


    }

    private void initializeSpeechButtons() {
        btnListenStart = findViewById(R.id.btnListenStart);
        btnListenStop = findViewById(R.id.btnListenStop);

        btnListenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchData == null || !fetchData.getIsTaskFinished())
                    return;

                tripInfo = fetchData.getTripInfo();

                if (!tripInfo.isEmpty())
                    TextToSpeechHelper.speak(textToSpeech, tripInfo);
            }
        });

        btnListenStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextToSpeechHelper.stopSpeech(textToSpeech);
            }
        });
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language is not supported");
                    } else
                        btnListenStart.setEnabled(true);
                } else
                    Log.e("TTS", "Initialization failed");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextToSpeechHelper.getShouldSpeakOnResume())
            TextToSpeechHelper.speak(textToSpeech, tripInfo);
    }

    @Override
    protected void onPause() {
        TextToSpeechHelper.pauseSpeech(textToSpeech);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        TextToSpeechHelper.stopSpeech(textToSpeech);

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i){
            case R.id.searchTrip :
                String fromStation = fromStationTV.getText().toString();
                String toStation = toStationTV.getText().toString();

                Intent memeIntent =new Intent(MainActivity.this,MemeGenerator.class);
                memeIntent.putExtra("ActivityKey",1);
                startActivity(memeIntent);

                destination = new Destination();
                destination.Destination(fromStation,toStation);

                initializeTextToSpeech();



                fetchData = new FetchData(destination.getFromStation(), destination.getToStation(), true, true);
                fetchData.execute();

                initializeSpeechButtons();
                break;
            case R.id.btnWikipedia:

                memeIntent=new Intent(MainActivity.this,MemeGenerator.class);
                startActivity(memeIntent);


                intent = new Intent(MainActivity.this, Wikipedia.class);
                intent.putExtra("Destination",destination);
                startActivity(intent);





                break;
        }
    }
}
