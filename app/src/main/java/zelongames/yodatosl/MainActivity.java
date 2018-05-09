package zelongames.yodatosl;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "USE_YOUR_OWN_KEY";
    private static final String API_URL = "https://sv.wikipedia.org/w/api.php?";

    private String action = "action=query&formatversion=2";
    private String titles = "titles=Skanstull";
    private String prop = "prop=revisions";
    private String rvprop = "rvprop=content";
    private String format = "format=json";
    private String texts = "";

    private AutoCompleteTextView fromStationTextView;
    private AutoCompleteTextView toStationTextView;
    private Button searchButton;

    private String fromStation;
    private String toStation;


    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setTexts(String texts) {
        this.texts = texts;
    }

    public String getTexts() {
        return texts;
    }

    public static TextView txtSLGuide = null;
    public static Geocoder coder = null;

    private FetchData fetchData = null;
    private Button btnListenStart = null;
    private Button btnListenStop = null;
    private TextToSpeech textToSpeech = null;
    String tripInfo;
    public static TextToSpeech getTextToSpeech() {
        return getTextToSpeech();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromStationTextView = findViewById(R.id.fromStationTextView);
        toStationTextView = findViewById(R.id.toStationTextView);
        searchButton = findViewById(R.id.searchButton);



        coder = new Geocoder(this);
        txtSLGuide = findViewById(R.id.txtSLGuide);

        searchButton.setOnClickListener(this);


    }

    private void initializeSpeechButtons() {
        btnListenStart = findViewById(R.id.btnListenStart);
        btnListenStop = findViewById(R.id.btnListenStop);

        btnListenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 tripInfo = fetchData.getTripInfo();

                if (!tripInfo.isEmpty()) {
                    textToSpeech.setPitch(1f);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak(tripInfo, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        btnListenStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech != null)
                    textToSpeech.stop();
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
      //  textToSpeech.speak(tripInfo, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();

        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i){
            case R.id.searchButton :
                initializeTextToSpeech();
                setFromStation(fromStationTextView.getText().toString());
                setToStation(toStationTextView.getText().toString());
                Log.d(TAG, "onClick: "+getFromStation());
                Log.d(TAG, "onClick: "+getToStation());
                fetchData = new FetchData(FetchData.TextFormat.Speech, getFromStation() , getToStation(), true, true);
                fetchData.execute();

                initializeSpeechButtons();
                break;
        }
    }
}
