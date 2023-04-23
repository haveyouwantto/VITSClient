package hywt.tts.vitsclient;

import android.media.AudioFormat;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;

import androidx.preference.PreferenceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TTSService extends TextToSpeechService {
    private ApiClient client;

    public TTSService() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        System.out.println("created");
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        float rate = 2.5f- request.getSpeechRate()/200f;
        String log = String.format("text synthesis %s %s %f %s\n", request.getLanguage(), request.getVoiceName(),rate, request.getCharSequenceText());
        System.out.println(log);
//        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();

        SupportedLanguage language = SupportedLanguage.forName(request.getLanguage());
        String text = request.getCharSequenceText().toString();

        callback.start(22050, AudioFormat.ENCODING_PCM_16BIT, 1);
        if (isPunctuationOnly(text)) {
            callback.done();
        } else {
            try {
                int speakerId = 0;
                Pattern pattern = Pattern.compile("^\\[(\\d+)]"); // 匹配以方括号开头，后跟一个或多个数字的字符串
                Matcher matcher = pattern.matcher(request.getVoiceName());
                if (matcher.find()) {
                    String matchedText = matcher.group(1); // 获取第一个匹配组（即方括号中的数字）
                    speakerId = Integer.parseInt(matchedText);
                }

                InputStream audio = client.generate(language, text, speakerId, rate, 0.667f, 0.8f);
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

                callback.done();
            } catch (IOException e) {
                callback.error();
                e.printStackTrace();
            }
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
        System.out.println("load language " + lang);
        int result = onIsLanguageAvailable(lang, country, variant);
//        Toast.makeText(this, "load language " + lang, Toast.LENGTH_SHORT).show();
        return result;
    }

    @Override
    public List<Voice> onGetVoices() {
        // Fetch the list of speakers from your API client
        Speaker[] speakers = client.getSpeakers();

        Locale[] locales = new Locale[]{
                new Locale("en", "US"),
                new Locale("zh", "CN"),
                new Locale("ja", "JP"),
                new Locale("ko", "KR")
        };

        // Convert the speaker list to a list of Voice objects
        List<Voice> voices = new ArrayList<>();
        for (Speaker speaker : speakers) {
            for (Locale locale : locales) {
                Voice voice = new Voice(String.format("%s [%s]", speaker.toString(), locale.getLanguage()), locale, Voice.QUALITY_HIGH, Voice.LATENCY_HIGH, true, Collections.emptySet());
                voices.add(voice);
            }
        }
        System.out.println(voices);

        return voices;
    }

    @Override
    public int onLoadVoice(String voiceName) {
        System.out.println("load voice " + voiceName);
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
            client = new ApiClient(PreferenceManager.getDefaultSharedPreferences(this).getString("server_url", ""));
            try {
                client.loadSpeakers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (client.getSpeakers() != null && client.getSpeakers().length > 0) {
            return client.getSpeakers()[0].toString();
        } else return "[0] default";
    }
}
