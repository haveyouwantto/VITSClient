package hywt.tts.vitsclient.backend;

public class StringResult {
    public final String text;
    public final boolean inQuote;

    public StringResult(String text, boolean inQuote) {
        this.text = text;
        this.inQuote = inQuote;
    }

    @Override
    public String toString() {
        return "StringResult{" +
                "string='" + text + '\'' +
                ", inQuote=" + inQuote +
                '}';
    }
}
