package com.example.ronylevari.callrecorder.bl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Java representation of single_recording_data_row to be displayed in RecyclerView
 **/
public class ChildRecordItem implements DatabaseObject, Parcelable {

    private long mID;
    private long mParent;
    private String mCaller;
    private String mPhoneNumber;
    private long mCallTime;
    private boolean mIsSMS;
    private String mMessage;
    private boolean mIsTrashed;

    public ChildRecordItem() {}

    public  ChildRecordItem(Parcel input) {
        mID = input.readLong();
        mParent = input.readLong();
        mCaller = input.readString();
        mPhoneNumber = input.readString();
        mCallTime = input.readLong();
        mIsSMS = input.readInt() != 0;
        mMessage = input.readString();
        mIsTrashed = input.readInt() != 0;
    }

    public ChildRecordItem(long mID, long mParent, long mCallTime, String mCaller, String mPhoneNumber, boolean mIsSMS, boolean mIsTrashed, String mMessage) {
        this.mID = mID;
        this.mParent = mParent;
        this.mCallTime = mCallTime;
        this.mCaller = mCaller;
        this.mPhoneNumber = mPhoneNumber;
        this.mIsSMS = mIsSMS;
        this.mIsTrashed = mIsTrashed;
        this.mMessage = mMessage;
    }

    public ChildRecordItem(long mID, long mParent, long mCallTime, String mCaller, String mPhoneNumber, boolean mIsSMS, boolean mIsTrashed) {
        this.mID = mID;
        this.mParent = mParent;
        this.mCallTime = mCallTime;
        this.mCaller = mCaller;
        this.mPhoneNumber = mPhoneNumber;
        this.mIsSMS = mIsSMS;
        this.mIsTrashed = mIsTrashed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
        dest.writeLong(mParent);
        dest.writeString(mCaller);
        dest.writeString(mPhoneNumber);
        dest.writeLong(mCallTime);
        dest.writeInt(mIsSMS ? 1 : 0);
        dest.writeString(mMessage);
        dest.writeInt(mIsTrashed ? 1 : 0);
    }

    public static final Parcelable.Creator<ChildRecordItem> CREATOR = new Parcelable.Creator<ChildRecordItem>() {

        @Override
        public ChildRecordItem createFromParcel(Parcel source) {
            return new ChildRecordItem(source);
        }

        @Override
        public ChildRecordItem[] newArray(int size) {
            return new ChildRecordItem[size];
        }
    };

    public long getID() {
        return mID;
    }

    public void setID(long id) {
        this.mID = id;
    }

    public long getParent() {
        return mParent;
    }

    public void setParent(long id) {
        this.mParent = id;
    }

    public boolean getIsTrashed() {
        return mIsTrashed;
    }

    public void setIsTrashed(boolean trashed) {
        this.mIsTrashed = trashed;
    }

    public long getCallTime() {
        return mCallTime;
    }

    public void setCallTime(long mCallDate) {
        this.mCallTime = mCallDate;
    }

    public boolean getIsSMS() {
        return mIsSMS;
    }

    public void setIsSMS(boolean mIsSMS) {
        this.mIsSMS = mIsSMS;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getCaller() {
        return mCaller;
    }

    public void setCaller(String mCaller) {
        this.mCaller = mCaller;
    }

    public String getPhoneNumber() { return this.mPhoneNumber; }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        boolean res = false;
        if (o instanceof ChildRecordItem) {
            ChildRecordItem other = (ChildRecordItem) o;
            res = (this.getID() == other.getID());
        }
        return res;
    }

    @Override
    public String toString() {
        return "ChildRecordItem{" +
                "mCaller='" + mCaller + '\'' +
                ", mID=" + mID +
                ", mParent=" + mParent +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mCallTime=" + mCallTime +
                ", mIsSMS=" + mIsSMS +
                ", mMessage='" + mMessage + '\'' +
                ", mIsTrashed=" + mIsTrashed +
                '}';
    }
}
