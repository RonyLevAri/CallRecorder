package com.example.ronylevari.callrecorder.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ronylevari.callrecorder.R;
import com.example.ronylevari.callrecorder.constants.AppConstants;

public class FragmentNoContent extends Fragment {

    public static String TAG = "Fragment No Content";
    private String title;

    public FragmentNoContent() {}

    public static FragmentNoContent newInstance(String notification) {
        FragmentNoContent f = new FragmentNoContent();
        Bundle args = new Bundle();
        args.putString(AppConstants.NO_CONTENT_TITLE, notification);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_content_yet, container, false);

        // update text view according to text in bundle
        TextView textView = (TextView) view.findViewById(R.id.no_content_text);
        textView.setText(getArguments().getString(AppConstants.NO_CONTENT_TITLE));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = (savedInstanceState != null) ? savedInstanceState.getString(AppConstants.NO_CONTENT_TITLE) : AppConstants.NO_CONTENT;
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
    public void onDestroy() {
        super.onDestroy();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
