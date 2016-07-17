package com.example.ronylevari.callrecorder.bl;

import java.util.Date;

/**
 * Java representation of single_recording_data_row to be displayed in RecyclerView
 **/
public class SingleRecordItem {

    private String mPhoneNumber;
    private Date mCallDate;
    private boolean mIsSMS;
    private String mMessage;

    public SingleRecordItem(String phoneNumber, Date callDate, boolean isSMS, String message) {
        this.mPhoneNumber = phoneNumber;
        this.mCallDate = callDate;
        this.mIsSMS = isSMS;
        this.mMessage = message;
    }

    public Date getmCallDate() {
        return mCallDate;
    }

    public boolean ismIsSMS() {
        return mIsSMS;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmCallDate(Date mCallDate) {
        this.mCallDate = mCallDate;
    }

    public void setmIsSMS(boolean mIsSMS) {
        this.mIsSMS = mIsSMS;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }
}
