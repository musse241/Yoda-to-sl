package zelongames.yodatosl;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech li_TTS;
    private EditText li_editTest;
    private SeekBar li_seekBar_pitch;
    private SeekBar li_seekBar_speed;
    private Button li_btn_speak;
    private Button li_btn_stop_speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        li_btn_speak = findViewById(R.id.btn_speak_li);
        li_btn_stop_speak = findViewById(R.id.btn_stop_speak_li);

        li_TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
               if(status == TextToSpeech.SUCCESS) {
                   int result = li_TTS.setLanguage(Locale.ENGLISH);
                   if(result == TextToSpeech.LANG_MISSING_DATA
                           || result == TextToSpeech.LANG_NOT_SUPPORTED){
                       Log.e("TTS","Language is not supported");
                   }else{
                       //Set the button work
                       li_btn_speak.setEnabled(true);
                   }
               }else{
                  Log.e("TTS","Initialization failed");
               }
            }
        });

        li_editTest = findViewById(R.id.editText_li);
        li_seekBar_pitch = findViewById(R.id.seek_bar_pitch_li);
        li_seekBar_speed = findViewById(R.id.seek_bar_speed_li);

        li_btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toReadText();
            }
        });

        li_btn_stop_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(li_TTS != null)
                    li_TTS.stop();
            }
        });
    }

    //To read the text out
    private void toReadText() {

        String textToRead = li_editTest.getText().toString();

        float pitch = (float) li_seekBar_pitch.getProgress()/50;
        if(pitch<0.1)
            pitch = 0.1f;
        float speed = (float) li_seekBar_speed.getProgress()/50;
        if(speed<0.1)
            speed = 0.1f;

        li_TTS.setPitch(pitch);
        li_TTS.setSpeechRate(speed);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            li_TTS.speak(textToRead,TextToSpeech.QUEUE_FLUSH,null,null);
        //} else {
         //   li_TTS.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null);
        //}
    }

    @Override
    protected void onDestroy() {
        if(li_TTS != null){
            li_TTS.stop();
            li_TTS.shutdown();
        }
        super.onDestroy();

    }
}
