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
import android.widget.ProgressBar;

import com.directions.sample.R;
import com.directions.sample.adapter.HistoryCardAdapter;
import com.directions.sample.adapter.HistoryTripAdapter;
import com.directions.sample.model.HistoryCardItem;
import com.directions.sample.model.HistoryTripItem;
import com.ibm.bluelist.BlueListApplication;
import com.ibm.bluelist.HistoryCard;
import com.ibm.bluelist.HistoryTrip;
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
public class HistoryCardFragment extends Fragment {
    List<HistoryTrip> itemList;
    BlueListApplication blApplication;
    List<HistoryCard> historyCards;
    RecyclerView recyclerView;
    String ten;
    public static final String CLASS_NAME = "HistoryCardFragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        blApplication = (BlueListApplication) getActivity().getApplication();
        itemList = blApplication.getHistoryTrips();
        historyCards=blApplication.getHistoryCards();
        View layout = inflater.inflate(R.layout.historycard, container, false);


        ten = getArguments().getString("ten");
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerListCard);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(new HistoryCardAdapter(hienthi()));

        return layout;
    }
    private ArrayList<HistoryCardItem> hienthi() {
        ArrayList<HistoryCardItem> palettes = new ArrayList<>();
        for (int i=0;i<historyCards.size();i++) {
            if (historyCards.get(i).getName().equals(ten)){
                palettes.add(new HistoryCardItem(historyCards.get(i).getSeri(), historyCards.get(i).getMenhgia(),historyCards.get(i).getDatetime(), historyCards.get(i).getType(),historyCards.get(i).getMagd()));
            }
        }
        return palettes;
    }
    @Override
    public void onResume() {
        super.onResume();
        listItemsHistoryCard();
    }

    @Override
    public void onPause() {
        listItemsHistoryCard();
        super.onPause();

    }
    public void listItemsHistoryCard() {
        try {
            IBMQuery<HistoryCard> query = IBMQuery.queryForClass(HistoryCard.class);
            // Query all the Item objects from the server.
            query.find().continueWith(new Continuation<List<HistoryCard>, Void>() {

                @Override
                public Void then(Task<List<HistoryCard>> task) throws Exception {
                    final List<HistoryCard> objects = task.getResult();
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
                        historyCards.clear();
                        for (IBMDataObject item : objects) {
                            historyCards.add((HistoryCard) item);
                        }
                        sortItemsHistoryCard(historyCards);

                    }
                    return null;
                }
            }, Task.UI_THREAD_EXECUTOR);

        } catch (IBMDataException error) {
            Log.e(CLASS_NAME, "Exception : " + error.getMessage());
        }
    }

    private void sortItemsHistoryCard(List<HistoryCard> theList) {
        // Sort collection by case insensitive alphabetical order.
        Collections.sort(theList, new Comparator<HistoryCard>() {
            public int compare(HistoryCard lhs,
                               HistoryCard rhs) {
                String lhsName = lhs.getName();
                String rhsName = rhs.getName();
                return lhsName.compareToIgnoreCase(rhsName);
            }
        });
    }

}
