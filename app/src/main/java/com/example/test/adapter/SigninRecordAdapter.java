package com.example.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.SigninRecord;

import java.util.List;
import com.example.test.R;

public class SigninRecordAdapter extends ArrayAdapter<SigninRecord> {

    private int resourceId;

    public SigninRecordAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<SigninRecord> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SigninRecord signinRecord = getItem(position);
        final View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rankTV = view.findViewById(R.id.rank_Tv);
            viewHolder.timeTV = view.findViewById(R.id.time_Tv);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.rankTV.setText(String.valueOf(signinRecord.getRank()));
        viewHolder.timeTV.setText(String.valueOf(signinRecord.getTime()));
        return view;
    }
    class ViewHolder{
        TextView rankTV;
        TextView timeTV;
    }
}
