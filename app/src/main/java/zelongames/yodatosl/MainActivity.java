package zelongames.yodatosl;

import android.location.Geocoder;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static TextView txtSLGuide = null;
    public static Geocoder coder = null;

    private FetchData fetchData = null;
    private Button btnListenStart = null;
    private Button btnListenStop = null;
    private TextToSpeech textToSpeech = null;

    public static TextToSpeech getTextToSpeech() {
        return getTextToSpeech();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coder = new Geocoder(this);
        txtSLGuide = findViewById(R.id.txtSLGuide);

        initializeTextToSpeech();

        fetchData = new FetchData(FetchData.TextFormat.Speech, "Rimbo station", "Tekniska högskolan", true, true);
        fetchData.execute();

        initializeSpeechButtons();
    }

    private void initializeSpeechButtons() {
        btnListenStart = findViewById(R.id.btnListenStart);
        btnListenStop = findViewById(R.id.btnListenStop);

        btnListenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripInfo = fetchData.getTripInfo();

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
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
