package com.directions.sample.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.directions.sample.R;
import com.directions.sample.adapter.HistoryTripAdapter;
import com.directions.sample.model.HistoryTripItem;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryTrip;
import com.ibm.bluelist.Item;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by RON on 11/8/2015.
 */
public class HistoryTripFragment extends Fragment {
    List<HistoryTrip> itemList;
    BlueListApplication blApplication;
    String ten="";
    ProgressBar pbar;
    RecyclerView recyclerView;
    public static final String CLASS_NAME = "HistoryTripFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getHistoryTrips();
        ten = getArguments().getString("ten");
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.historytrip_fragment, container, false);


        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerListTrip);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new HistoryTripAdapter(hienthi()));



        return layout;
    }
    @Override
    public void onResume() {
        super.onResume();
       listItemsHistoryTrip();
    }

    @Override
    public void onPause() {
        super.onPause();
        listItemsHistoryTrip();
    }
    private ArrayList<HistoryTripItem> hienthi() {
        ArrayList<HistoryTripItem> palettes = new ArrayList<>();
        for (int i=0;i<itemList.size();i++) {
            if (itemList.get(i).getNAME().toString().equals(ten)){
            palettes.add(new HistoryTripItem(itemList.get(i).getSTART(), itemList.get(i).getDESTINATION(), itemList.get(i).getMONEY()));
        }
        }
        return palettes;
    }

    public void listItemsHistoryTrip() {
        try {
            IBMQuery<HistoryTrip> query = IBMQuery.queryForClass(HistoryTrip.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<HistoryTrip>, Void>() {

                @Override
                public Void then(Task<List<HistoryTrip>> task) throws Exception {
                    final List<HistoryTrip> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");

                    }
                    // Log error message, if the find task fails.
                    else if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                    }
                    // If the result succeeds, load the list.
                    else {
                        // Clear local itemList.
                        // We'll be reordering and repopulating from DataService.
                        itemList.clear();
                        for (IBMDataObject item : objects) {
                            itemList.add((HistoryTrip) item);
                        }
                        // Start long running operation in a background thread
                        sortItemsHistoryTrip(itemList);
                        //lvArrayAdapter.notifyDataSetChanged();

                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);


        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }
    private void sortItemsHistoryTrip(List<HistoryTrip> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<HistoryTrip>() {
            public int compare(HistoryTrip lhs,
                               HistoryTrip rhs) {
                String lhsName = lhs.getNAME();
                String rhsName = rhs.getNAME();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }
}
