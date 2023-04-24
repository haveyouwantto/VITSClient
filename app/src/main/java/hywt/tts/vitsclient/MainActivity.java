package hywt.tts.vitsclient;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

import hywt.tts.vitsclient.adapters.LocaleArrayAdapter;
import hywt.tts.vitsclient.backend.ApiClient;
import hywt.tts.vitsclient.proto.Speaker;

public class MainActivity extends AppCompatActivity {

    private EditText mTextInput;
    private Spinner mSpeakerSpinner;
    private TextView mLengthScaleText;
    private TextView mNoiseScaleText;
    private TextView mNoiseScaleWText;
    private SeekBar mLengthScaleSeekBar;
    private SeekBar mNoiseScaleSeekBar;
    private SeekBar mNoiseScaleWSeekBar;

    private Spinner languageSpinner;

    private ApiClient mApiClient;
    private Locale selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextInput = findViewById(R.id.text_input);
        mSpeakerSpinner = findViewById(R.id.speaker_spinner);
        mLengthScaleText = findViewById(R.id.length_scale_label);
        mNoiseScaleText = findViewById(R.id.noise_scale_label);
        mNoiseScaleWText = findViewById(R.id.noise_scale_w_label);
        mLengthScaleSeekBar = findViewById(R.id.length_scale_seekbar);
        mNoiseScaleSeekBar = findViewById(R.id.noise_scale_seekbar);
        mNoiseScaleWSeekBar = findViewById(R.id.noise_scale_w_seekbar);


        languageSpinner = findViewById(R.id.language_spinner);


        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.open_tts).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        mSpeakerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = ((Locale) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mLengthScaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLengthScaleText.setText(getString(R.string.length_scale_text, progress / 40.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mNoiseScaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mNoiseScaleText.setText(getString(R.string.noise_scale_text, progress / 40.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mNoiseScaleWSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mNoiseScaleWText.setText(getString(R.string.noise_scale_w_text, progress / 40.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mLengthScaleSeekBar.setProgress(48);
        mNoiseScaleSeekBar.setProgress(24);
        mNoiseScaleWSeekBar.setProgress(35);
        loadTTS();
    }

    private void loadTTS() {
        mApiClient = ((TTSApp)getApplication()).getTtsApiClient();
        new AsyncTask<Void, Void, Speaker[]>() {
            @Override
            protected Speaker[] doInBackground(Void... voids) {
                try {
                    mApiClient.init();
                    return mApiClient.getSpeakers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Speaker[] speakers) {
                if (speakers != null) {
                    ArrayAdapter<Speaker> speakerAdapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, speakers);
                    mSpeakerSpinner.setAdapter(speakerAdapter);
                    ArrayAdapter<Locale> localeArrayAdapter = new LocaleArrayAdapter(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, mApiClient.getSupportedLanguages());

                    languageSpinner.setAdapter(localeArrayAdapter);

                } else {
                    Toast.makeText(MainActivity.this, "Error fetching speaker list", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public void onTtsButtonClick(View view) {
        String text = mTextInput.getText().toString();
        Speaker speaker = (Speaker) mSpeakerSpinner.getSelectedItem();
        float lengthScale = mLengthScaleSeekBar.getProgress() / 40f;
        float noiseScale = mNoiseScaleSeekBar.getProgress() / 40.0f;
        float noiseScaleW = mNoiseScaleWSeekBar.getProgress() / 40.0f;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InputStream audio = mApiClient.generate(selectedLanguage, text, speaker.getId(), lengthScale, noiseScale, noiseScaleW);
                    audio.skip(80);
                    byte[] b = Utils.readAllBytes(audio);
                    ByteArrayInputStream bis = new ByteArrayInputStream(b);

                    int sampleRateInHz = 22050;
                    int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

                    int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

                    AudioTrack audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            sampleRateInHz,
                            channelConfig,
                            audioFormat,
                            bufferSizeInBytes,
                            AudioTrack.MODE_STREAM);

                    byte[] buffer = new byte[4096];

                    audioTrack.play();

                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) > 0) {
                        audioTrack.write(buffer, 0, bytesRead);
                    }

                    audioTrack.stop();
                    audioTrack.release();
                    audio.close();
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }
}
