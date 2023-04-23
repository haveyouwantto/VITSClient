package hywt.tts.vitsclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ApiClient {
    private String baseUrl;
    private Speaker[] speakers;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getURL(SupportedLanguage language, String text, int id, float lengthScale, float noiseScale, float noiseScaleW) {
        String query;
        if (language == null) {
            query = text.strip();
        } else {
            query = String.format("[%s]%s[%s]", language.getCode(), text.strip(), language.getCode());
        }
        return baseUrl + "/tts?text=" + query + "&speaker=" + id + "&length_scale=" + lengthScale + "&noise_scale=" + noiseScale + "&noise_scale_w=" + noiseScaleW;
    }

    public InputStream generate(SupportedLanguage language, String text, int id, float lengthScale, float noiseScale, float noiseScaleW) throws IOException {
        URL url = new URL(getURL(language, text, id, lengthScale, noiseScale, noiseScaleW));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        return connection.getInputStream();
    }

    public InputStream generate(SupportedLanguage language, String text, int speakerId) throws IOException {
        return generate(language, text, speakerId, 1.1f, 0.667f, 0.8f);
    }

    public InputStream generate(SupportedLanguage language, String text) throws IOException {
        return generate(language, text, 0);
    }

    private void list() throws IOException {
        URL url = new URL(baseUrl + "/list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream responseStream = connection.getInputStream();
        String responseString = new String(Utils.readAllBytes(responseStream));
        speakers = new Gson().fromJson(responseString, Speaker[].class);
        System.out.println("speaker list created");
    }

    public Speaker[] getSpeakers() {
        return speakers;
    }

    public void loadSpeakers() throws IOException {
        if (speakers == null) list();
    }

}