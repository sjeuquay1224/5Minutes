package com.directions.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.directions.sample.R;
import com.directions.sample.model.HistoryCardItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RON on 11/9/2015.
 */
public class HistoryCardAdapter
        extends RecyclerView.Adapter<HistoryCardAdapter.MyViewHolder> {
    List<HistoryCardItem> data ;
    private LayoutInflater inflater;
    private Context context;

    public HistoryCardAdapter(List<HistoryCardItem> palettes) {
        this.data = new ArrayList<HistoryCardItem>();
        this.data.addAll(palettes);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.history_trip_row, parent, false);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card_row, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryCardItem current = data.get(position);
        holder.seri.setText(current.getSeri());
        holder.gia.setText(current.getGia());
        holder.loai.setText(current.getLoai());
        holder.ngay.setText(current.getDate());
        holder.id.setText(current.getId());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView seri;
        protected TextView gia;
        protected TextView ngay;
        protected TextView loai;
        protected TextView id;

        public MyViewHolder(View itemView) {
            super(itemView);
            seri = (TextView) itemView.findViewById(R.id.txt_soseri);
            gia = (TextView) itemView.findViewById(R.id.txt_menhgia);
            ngay = (TextView) itemView.findViewById(R.id.txt_date);
            loai = (TextView) itemView.findViewById(R.id.txt_type);
            id = (TextView) itemView.findViewById(R.id.txt_id);
        }
    }
}
