package com.directions.sample.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.directions.sample.R;
import com.directions.sample.model.MdmItem;

import java.util.ArrayList;

public class MdmAdapter extends ArrayAdapter<MdmItem>{
	
	private Context context;
	private int resourceId;
	private ArrayList<MdmItem> listsData;
	
	public MdmAdapter(Context context, int resourceId, ArrayList<MdmItem> collections) {
		super(context, resourceId, collections);
		
		this.context = context;
		this.resourceId = resourceId;
		
		if(collections!=null){
			listsData = collections;
		}else{
			listsData = new ArrayList<MdmItem>();
		}
	}
	
	@Override
	public int getCount() {
		return listsData.size();
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(resourceId, parent, false);
		}
		
		TextView txtMethodName = ViewHolder.get(convertView, R.id.txt_method_name);
		TextView txtMethodDesc = ViewHolder.get(convertView, R.id.txt_method_desc);
		
		MdmItem item = listsData.get(position);
		txtMethodName.setText(item.methodName);
		txtMethodDesc.setText(item.desc);
		
		return convertView;
	}
	
}
