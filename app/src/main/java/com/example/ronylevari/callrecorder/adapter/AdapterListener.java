package com.example.ronylevari.callrecorder.adapter;

import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;

public interface AdapterListener {

    void onContextMenuState();
    void onDeselectingAllItems();
    void onChildrenRequest(ParentRecordingItem parent, boolean trashedChildren);

}
