package com.directions.sample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.directions.sample.R;
import com.directions.sample.model.HistoryTripItem;
import com.directions.sample.model.RatingItem;
import com.directions.sample.model.RoundImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RON on 11/9/2015.
 */
public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {
    List<RatingItem> data ;
    private LayoutInflater inflater;
    private Context context;


    public RatingAdapter(List<RatingItem> palettes) {
        this.data = new ArrayList<RatingItem>();
        this.data.addAll(palettes);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.history_trip_row, parent, false);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_row, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RatingItem current = data.get(position);
        holder.ten.setText(current.getTen());
        holder.title.setText(current.getTitle());
        holder.content.setText(current.getContent());
        holder.raitingBar.setRating(current.getSo());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView ten;
        protected TextView title;
        protected TextView content;
        protected RatingBar raitingBar;
        protected ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ten = (TextView) itemView.findViewById(R.id.txt_tenrating);
            title = (TextView) itemView.findViewById(R.id.txt_title);
            content = (TextView) itemView.findViewById(R.id.txt_content);
            raitingBar=(RatingBar)itemView.findViewById(R.id.ratingBar3);
            imageView=(ImageView)itemView.findViewById(R.id.iv_avatar);
            LayerDrawable stars = (LayerDrawable) raitingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        }
    }
}