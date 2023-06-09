package hywt.tts.vitsclient;

import android.app.Application;

import androidx.preference.PreferenceManager;

import hywt.tts.vitsclient.backend.ApiClient;

public class TTSApp extends Application {
    private ApiClient ttsApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the TTS API client
        createClient();
    }

    public ApiClient getTtsApiClient() {
        return this.ttsApiClient;
    }

    public void createClient() {
        this.ttsApiClient = new ApiClient(PreferenceManager.getDefaultSharedPreferences(this).getString("server_url", ""));
    }

}
