package com.example.courtmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends BaseAdapter {
    private Context context;
    private List<Training_Program> programList;

    public ProgramAdapter(Context context, List<Training_Program> programList) {
        this.context = context;
        this.programList = programList;
    }

    @Override
    public int getCount() {
        return programList.size();
    }

    @Override
    public Object getItem(int position) {
        return programList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * For each row in the ListView, inflate the layout, show the program name, and set up the RatingBar.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate or reuse an item layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_with_rating, parent, false);
        }

        // Grab references to views
        TextView programName = convertView.findViewById(R.id.listItemText);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);

        // Get the Training_Program for this position
        Training_Program program = programList.get(position);

        // Display the program name
        programName.setText(program.getName());

        ratingBar.setRating(program.getRating());


        return convertView;
    }
}