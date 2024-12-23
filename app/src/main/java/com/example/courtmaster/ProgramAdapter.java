package com.example.courtmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_with_rating, parent, false);
        }

        TextView programName = convertView.findViewById(R.id.listItemText);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);

        Training_Program program = programList.get(position);
        programName.setText(program.getName());
        ratingBar.setRating(program.getRating());

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (fromUser) {
                program.setRating(rating);
            }
        });

        return convertView;
    }
}
