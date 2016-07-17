package com.example.ronylevari.callrecorder.bl;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Java representation of parent_recording_data_row to be displayed in RecyclerView
 **/
public class ParentRecordingItem {

    private String mRecordName;
    private int mNumberOfItemsInRecord;
    private GregorianCalendar mEndDate;
    private GregorianCalendar mStartDate;
    private boolean mIsClosed = false;
    private ArrayList<SingleRecordItem> mItems = new ArrayList<>();


    public ParentRecordingItem(String mRecordName, int mNumberOfItemsInRecord, GregorianCalendar startDate) {
        this.mRecordName = mRecordName;
        this.mNumberOfItemsInRecord = mNumberOfItemsInRecord;
        this.mStartDate = startDate;
    }

    public int getNumberOfItemsInRecord() {
        return mNumberOfItemsInRecord;
    }

    public String getRecordName() {
        return mRecordName;
    }

    public GregorianCalendar getEndDate() {
        return mEndDate;
    }

    public boolean isClosed() {
        return mIsClosed;
    }

    public GregorianCalendar getStartDate() {
        return mStartDate;
    }

    public void setNumberOfItemsInRecord(int mNumberOfItemsInRecord) {
        this.mNumberOfItemsInRecord = mNumberOfItemsInRecord;
    }

    public void setRecordName(String mRecordName) {
        this.mRecordName = mRecordName;
    }

    public void closeRecording() {
        if (!mIsClosed) {
            this.mIsClosed = true;
            this.mEndDate = new GregorianCalendar();
        }
    }

    public void addItem(SingleRecordItem newIten) {
        mItems.add(newIten);
    }

    public SingleRecordItem getItemAt(int index) {
        if (mItems.size() != 0)
            return mItems.get(index);
        else
            return null;
    }

    public ArrayList<SingleRecordItem> getItems() {
        // TODO Make this method return a copy of the item list for memory leak reasons
        return mItems;
    }
}
