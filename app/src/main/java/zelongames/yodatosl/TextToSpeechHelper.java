package zelongames.yodatosl;

import android.speech.tts.TextToSpeech;

/**
 * Created by Jonas on 2018-05-09.
 */

public class TextToSpeechHelper {

    private static boolean shouldSpeakOnResume = false;

    public static boolean getShouldSpeakOnResume() {
        return shouldSpeakOnResume;
    }

    public static void speak(TextToSpeech textToSpeech, String text) {
        textToSpeech.setPitch(1f);
        textToSpeech.setSpeechRate(1f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        shouldSpeakOnResume = false;
    }

    public static void pauseSpeech(TextToSpeech textToSpeech) {
        if (textToSpeech != null) {
            textToSpeech.stop();
            shouldSpeakOnResume = true;
        }
    }

    public static void stopSpeech(TextToSpeech textToSpeech) {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            shouldSpeakOnResume = false;
        }
    }
}
