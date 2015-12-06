package com.directions.sample.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.sample.R;

/**
 * Created by RON on 11/14/2015.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] itemmail;
   // private final Integer[] imgid;

    public CustomListAdapter(Activity context, String[] itemname, String[] itemmail) {
        super(context, R.layout.customer_list_item_1, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.itemmail=itemmail;
        //this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.customer_list_item_1, null,true);

        TextView acc = (TextView) rowView.findViewById(R.id.txt_acc_admin);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView mail = (TextView) rowView.findViewById(R.id.txt_mail_admin);

        acc.setText(itemname[position]);
        //imageView.setImageResource(imgid[position]);
        mail.setText(itemmail[position]);
        return rowView;

    };
}