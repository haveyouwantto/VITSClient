package hywt.tts.vitsclient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class LocaleArrayAdapter extends ArrayAdapter<Locale> {
    public LocaleArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public LocaleArrayAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public LocaleArrayAdapter(@NonNull Context context, int resource, @NonNull Locale[] objects) {
        super(context, resource, objects);
    }

    public LocaleArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Locale[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public LocaleArrayAdapter(@NonNull Context context, int resource, @NonNull List<Locale> objects) {
        super(context, resource, objects);
    }

    public LocaleArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Locale> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        Locale locale = getItem(position);
        textView.setText(locale.getDisplayName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        Locale locale = getItem(position);
        textView.setText(locale.getDisplayName());
        return view;
    }
}
