package hywt.tts.vitsclient;

import android.content.Context;

public enum SupportedLanguage {
    ENGLISH(R.string.english,"EN"),
    CHINESE(R.string.chinese,"ZH"),
    JAPANESE(R.string.japanese,"JA"),
    KOREAN(R.string.korean,"KO");

    private final int nameResourceId;
    private final String code;

    SupportedLanguage(int nameResourceId, String code) {
        this.nameResourceId = nameResourceId;
        this.code = code;
    }

    public String getName(Context context) {
        return context.getResources().getString(nameResourceId);
    }

    public String getCode(){
        return code;
    }

    public static SupportedLanguage forName(String name){
        switch (name){
            case "eng":
                return ENGLISH;
            case "zho":
                return CHINESE;
            case "jpn":
                return JAPANESE;
            case "kor":
                return KOREAN;
            default:
                return null;
        }
    }
}
