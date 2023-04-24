package hywt.tts.vitsclient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hywt.tts.vitsclient.adapters.LocaleArrayAdapter;
import hywt.tts.vitsclient.backend.ApiClient;
import hywt.tts.vitsclient.proto.Speaker;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            TTSApp app = (TTSApp) requireActivity().getApplication();

            Preference reconnectButtonPreference = findPreference("reconnect_button");
            reconnectButtonPreference.setOnPreferenceClickListener(preference -> {
                new AsyncTask<Void, Void, ApiClient>() {
                    @Override
                    protected ApiClient doInBackground(Void... voids) {
                        try {
                            app.initClient();
                            ApiClient client = app.getTtsApiClient();
                            client.init();
                            return client;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(ApiClient client1) {
                        updateVoiceList(client1);
                    }
                }.execute();
                return true;
            });
            updateVoiceList(app.getTtsApiClient());
        }

        private void updateVoiceList(ApiClient client) {

            // Get the ListPreference instance
            ListPreference defaultVoicePreference = findPreference("default_voice");

            if (client != null) {
                Speaker[] speakers = client.getSpeakers();
                if (speakers != null) {
                    List<CharSequence> entryValuesList = new ArrayList<>();
                    List<CharSequence> displayNamesList = new ArrayList<>();

                    for (Speaker speaker : speakers) {
                        displayNamesList.add(speaker.toString());
                        entryValuesList.add(String.valueOf(speaker.id));
                    }

                    // Set the entry values and display names to the ListPreference
                    defaultVoicePreference.setEntryValues(entryValuesList.toArray(new CharSequence[0]));
                    defaultVoicePreference.setEntries(displayNamesList.toArray(new CharSequence[0]));

                    String currentValue = defaultVoicePreference.getValue();
                    if (currentValue != null)
                        defaultVoicePreference.setSummary(client.getSpeaker(Integer.parseInt(currentValue)).toString());

                    defaultVoicePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                        defaultVoicePreference.setSummary(client.getSpeaker(Integer.parseInt((String) newValue)).toString());
                        return true;
                    });
                    defaultVoicePreference.setEnabled(true);
                } else {
                    defaultVoicePreference.setEnabled(false);
                }
            } else {
                defaultVoicePreference.setEnabled(false);
            }
        }
    }
}