package com.example.chung_pike;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ReadingsListAdapter extends ArrayAdapter<Reading> {
    private Activity context;
    private List<Reading> readingsList;

    public ReadingsListAdapter(Activity context, List<Reading> readingsList) {
        super(context, R.layout.list_layout, readingsList);
        this.context = context;
        this.readingsList = readingsList;
    }

    public ReadingsListAdapter(Context context, int resource, List<Reading> objects,
                               Activity context1, List<Reading> readingsList) {
        super(context, resource, objects);
        this.context = context1;
        this.readingsList = readingsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem= inflater.inflate(R.layout.list_layout, null, true);

        TextView tvName = listViewItem.findViewById(R.id.textViewName);
        TextView tvSys = listViewItem.findViewById(R.id.textViewSystolic);
        TextView tvDias = listViewItem.findViewById(R.id.textViewDiastolic);
        TextView tvCondition = listViewItem.findViewById(R.id.textViewCondition);

        Reading read = readingsList.get(position);
        tvName.setText(read.getUserName());
        tvSys.setText(String.valueOf(read.getSystolicReading()));
        tvDias.setText(String.valueOf(read.getDiastolicReading()));
        tvCondition.setText(read.getCondition());

        return listViewItem;
    }
}
