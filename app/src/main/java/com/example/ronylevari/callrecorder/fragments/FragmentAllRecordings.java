package com.example.ronylevari.callrecorder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ronylevari.callrecorder.R;
import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;
import com.example.ronylevari.callrecorder.adapter.RecordingDataAdapter;
import com.example.ronylevari.callrecorder.communicators.ItemListClickListeners;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class FragmentAllRecordings extends Fragment implements ItemListClickListeners {

    public static final String TAG = "FragmentAllRecordingList";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecordingDataAdapter mAdapter;
    private boolean mInnActionMode;

    private ArrayList<ParentRecordingItem> mData = new ArrayList<>();

    public FragmentAllRecordings() {}

    public static FragmentAllRecordings newInstance() {
        FragmentAllRecordings f = new FragmentAllRecordings();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataSet();
    }

    private void initDataSet() {
        // TODO get content through content provider or remote server or database, maybe use loader

        for (int i = 0; i < 50; i++) {
            ParentRecordingItem prItem = new ParentRecordingItem("List " + i, i, new GregorianCalendar());
            if (i % 2 == 1)
                prItem.closeRecording();
            mData.add(prItem);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_recordings, container, false);
        view.setTag(TAG);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecordingDataAdapter(getActivity(), mData);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getActivity().getMenuInflater().inflate(R.menu.paren_recording_data_context, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDefaultClick(int position) {

    }

    @Override
    public void onSelectionModeClick() {

    }
}
