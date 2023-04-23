package hywt.tts.vitsclient.backend;

import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hywt.tts.vitsclient.TTSApp;
import hywt.tts.vitsclient.Utils;

public class TTSService extends TextToSpeechService {
    private ApiClient client;

    public TTSService() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        Log.i(this.getClass().getName(), "created tts service");
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {

        try {

            float rate;
            float pitch;
            float scaleW;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("override_parameters", false)) {
                rate = Float.parseFloat(preferences.getString("length_scale", "1.2"));
                pitch = Float.parseFloat(preferences.getString("noise_scale", "0.6"));
                scaleW = Float.parseFloat(preferences.getString("noise_scale_w", "0.65"));
            } else {
                rate = 2.5f - request.getSpeechRate() / 200f;
                pitch = request.getPitch() / 200f;
                scaleW = 0.8f;
            }

            String log = String.format("text synthesis lang=%s voice=%s rate=%f pitch=%f text=%s\n", request.getLanguage(), request.getVoiceName(), rate, pitch, request.getCharSequenceText());
            Log.i(this.getClass().getName(), log);
//        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();

            Locale language = new Locale(request.getLanguage());
            String text = request.getCharSequenceText().toString();

            callback.start(22050, AudioFormat.ENCODING_PCM_16BIT, 1);
            if (isPunctuationOnly(text)) {
                callback.done();
            } else {
                int speakerId = 0;
                Pattern pattern = Pattern.compile("^\\[(\\d+)]"); // 匹配以方括号开头，后跟一个或多个数字的字符串
                Matcher matcher = pattern.matcher(request.getVoiceName());
                if (matcher.find()) {
                    String matchedText = matcher.group(1); // 获取第一个匹配组（即方括号中的数字）
                    speakerId = Integer.parseInt(matchedText);
                }

                InputStream audio = client.generate(language, text, speakerId, rate, pitch, scaleW);
                audio.skip(80);
                byte[] b = Utils.readAllBytes(audio);
                ByteArrayInputStream bis = new ByteArrayInputStream(b);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    callback.audioAvailable(buffer, 0, len);
                }
                audio.close();
                bis.close();
            }
        } catch (Exception e) {
            callback.error();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            callback.done();
        }
    }

    private static final String PUNCTUATION_REGEX = "^[，。？！（）…“”]+$";
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile(PUNCTUATION_REGEX);

    public static boolean isPunctuationOnly(String sentence) {
        Matcher matcher = PUNCTUATION_PATTERN.matcher(sentence);
        return matcher.matches();
    }

    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {
        return TextToSpeech.LANG_COUNTRY_AVAILABLE;
    }

    @Override
    protected Set<String> onGetFeaturesForLanguage(String lang, String country, String variant) {
        return Collections.emptySet();
    }

    @Override
    protected String[] onGetLanguage() {
        return new String[]{"eng", "usa", ""};
    }

    @Override
    protected int onLoadLanguage(String lang, String country, String variant) {
        Log.i(this.getClass().getName(), "load language " + lang);
        //        Toast.makeText(this, "load language " + lang, Toast.LENGTH_SHORT).show();
        return onIsLanguageAvailable(lang, country, variant);
    }

    @Override
    public List<Voice> onGetVoices() {
        List<Voice> voices = client.getVoices();
        Log.i(this.getClass().getName(), "total voice count: " + voices.size());

        return voices;
    }

    @Override
    public int onLoadVoice(String voiceName) {
        Log.i(this.getClass().getName(), "load voice " + voiceName);
//        Toast.makeText(this, "load voice " + voiceName, Toast.LENGTH_SHORT).show();
        return TextToSpeech.SUCCESS;
    }

    @Override
    public int onIsValidVoiceName(String voiceName) {
        return TextToSpeech.SUCCESS;
    }

    @Override
    public String onGetDefaultVoiceNameFor(String lang, String country, String variant) {
        if (client == null) {
            client = ((TTSApp) getApplication()).getTtsApiClient();
            try {
                client.init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (client.getSpeakers() != null && client.getSpeakers().length > 0) {
            return client.getSpeakers()[0].toString();
        } else return "[0] default";
    }
}
