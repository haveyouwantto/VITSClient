package hywt.tts.vitsclient.backend;

import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hywt.tts.vitsclient.proto.Speaker;
import hywt.tts.vitsclient.Utils;

public class ApiClient {
    private final String baseUrl;
    private Speaker[] speakers;
    private List<Voice> voices;
    private List<Locale> supportedLanguages;

    private static final Map<String, String> LANGUAGES_MAP = new HashMap<>();

    static {
        LANGUAGES_MAP.put("eng", "en");
        LANGUAGES_MAP.put("zho", "zh");
        LANGUAGES_MAP.put("jpn", "ja");
        LANGUAGES_MAP.put("kor", "ko");
        LANGUAGES_MAP.put("tha", "th");
        LANGUAGES_MAP.put("san", "sa");
    }

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getURL(Locale language, String text, int id, float lengthScale, float noiseScale, float noiseScaleW) {
        String query;
        if (language == null || supportedLanguages.size() <= 1) {
            query = text.strip();
        } else {
            String code = language.getLanguage();
            if (code.length() >= 3) {
                code = LANGUAGES_MAP.get(code);
            }
            code = code.toUpperCase();
            query = String.format("[%s]%s[%s]", code, text.strip(), code);
        }
        return baseUrl + "/tts?text=" + query + "&speaker=" + id + "&length_scale=" + lengthScale + "&noise_scale=" + noiseScale + "&noise_scale_w=" + noiseScaleW;
    }

    public InputStream generate(Locale language, String text, int id, float lengthScale, float noiseScale, float noiseScaleW) throws IOException {
        URL url = new URL(getURL(language, text, id, lengthScale, noiseScale, noiseScaleW));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }

    public InputStream generate(Locale language, String text, int speakerId) throws IOException {
        return generate(language, text, speakerId, 1.1f, 0.667f, 0.8f);
    }

    public InputStream generate(Locale language, String text) throws IOException {
        return generate(language, text, 0);
    }

    private void info() throws IOException {
        URL url = new URL(baseUrl + "/info");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream responseStream = connection.getInputStream();
        String responseString = new String(Utils.readAllBytes(responseStream));
        JsonObject infoObject = JsonParser.parseString(responseString).getAsJsonObject();

        supportedLanguages = new ArrayList<>();
        infoObject.get("languages").getAsJsonArray().forEach(jsonElement -> supportedLanguages.add(new Locale(jsonElement.getAsString())));
        Log.i(this.getClass().getName(), supportedLanguages.toString());

        speakers = new Gson().fromJson(infoObject.get("speakers"), Speaker[].class);
        // Convert the speaker list to a list of Voice objects
        voices = new ArrayList<>();
        for (Speaker speaker : speakers) {
            for (Locale locale : supportedLanguages) {
                Voice voice = new Voice(String.format("%s [%s]", speaker.toString(), locale.getLanguage()), locale, Voice.QUALITY_HIGH, Voice.LATENCY_HIGH, true, Collections.emptySet());
                voices.add(voice);
            }
        }

//        for (int i = 0; i < 1000; i++) {
//            voices.add(new Voice("voice " + i, Locale.CHINESE, Voice.QUALITY_HIGH, Voice.LATENCY_HIGH, true, Collections.emptySet()));
//        }

        Log.i(this.getClass().getName(), "info created");
    }

    public Speaker[] getSpeakers() {
        return speakers;
    }

    public Speaker getSpeaker(int index) {
        return speakers[index];
    }

    public void init() throws IOException {
        if (speakers == null) {
            info();
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<Voice> getVoices() {
        return voices;
    }

    public List<Locale> getSupportedLanguages() {
        return supportedLanguages;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("ApiClient address=%s status=%s", baseUrl, speakers != null ? "initialized" : "disconnected");
    }
}