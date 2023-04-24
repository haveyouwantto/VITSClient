package hywt.tts.vitsclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hywt.tts.vitsclient.backend.StringResult;

public class Utils {
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    public static List<StringResult> splitByQuotes(String input) {
        List<StringResult> results = new ArrayList<>();

        int start = 0;
        int index = 0;

        boolean inQuote = false;
        while (index < input.length()) {
            char c = input.charAt(index);
            if (c == '"' || c == '“' || c == '”' || c == '「' || c == '」') {
                String segment = input.substring(start, index).strip();
                if (segment.length() > 0) {
                    results.add(
                            new StringResult(
                                    segment,
                                    inQuote
                            )
                    );
                }
                start = index + 1;
                inQuote = !inQuote;
            }
            index++;
        }

        // Final
        String segment = input.substring(start, index).strip();
        if (segment.length() > 0) {
            results.add(
                    new StringResult(
                            segment,
                            inQuote
                    )
            );
        }

        return results;
    }
}
