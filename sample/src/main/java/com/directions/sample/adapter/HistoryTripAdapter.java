package com.directions.sample.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.sample.R;
import com.directions.sample.model.HistoryTripItem;
import com.directions.sample.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by RON on 11/8/2015.
 */
public class HistoryTripAdapter extends RecyclerView.Adapter<HistoryTripAdapter.MyViewHolder> {
    List<HistoryTripItem> data ;
    private LayoutInflater inflater;
    private Context context;

    public HistoryTripAdapter(List<HistoryTripItem> palettes) {
        this.data = new ArrayList<HistoryTripItem>();
        this.data.addAll(palettes);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.history_trip_row, parent, false);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_trip_row, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryTripItem current = data.get(position);
        holder.diemdon.setText(current.getDiemdon());
        holder.diemden.setText(current.getDiemden());
        holder.giatien.setText(current.getGiatien());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView diemdon;
        protected TextView diemden;
        protected TextView giatien;

        public MyViewHolder(View itemView) {
            super(itemView);
            diemdon = (TextView) itemView.findViewById(R.id.txt_diemdon);
            diemden = (TextView) itemView.findViewById(R.id.txt_diemden);
            giatien = (TextView) itemView.findViewById(R.id.giatien);
        }
    }
}