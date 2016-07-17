package com.example.ronylevari.callrecorder.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ronylevari.callrecorder.R;
import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class RecordingDataAdapter extends RecyclerView.Adapter<RecordingDataAdapter.RecordingViewHolder>   {

    private LayoutInflater mInflater;
    private ArrayList<ParentRecordingItem> mData = new ArrayList<>();
    private Context mContext;

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray();
    private boolean mIsInSelectionMode = false;

    public RecordingDataAdapter(Context context, ArrayList<ParentRecordingItem> data) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        if (data == null) {
            throw new IllegalArgumentException("model data should not be null");
        }
        this.mData = data;
    }

    public void setIsInSelectionMode(boolean isInSelectionMode) {
        this.mIsInSelectionMode = isInSelectionMode;
    }

    public boolean getIsInSelectionMode() {
        return mIsInSelectionMode;
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selectedItemsPos = new ArrayList<Integer>(mSelectedItems.size());
        for(int i = 0; i < mSelectedItems.size(); i++) {
            selectedItemsPos.add(mSelectedItems.keyAt(i));
        }
        return selectedItemsPos;
    }

    public void toggleSelection(int position) {
        if(mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selectedItemsPos = getSelectedItems();
        mSelectedItems.clear();
        for (int i = 0; i < selectedItemsPos.size(); i++) {
            notifyItemChanged(i);
        }
        setIsInSelectionMode(false);
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    @Override
    public RecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_parent_recording_data_row, parent, false);
        return new RecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordingViewHolder holder, int position) {
        ParentRecordingItem current = mData.get(position);
        holder.bindParentRecordingItem(current);
    }

    public void addItem(int position) {
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class RecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mListName;
        private TextView mNumberOfItems;
        private TextView mDate;
        private ImageView mAvatar;
        private ImageView mIsLocked;
        private CardView mCard;

        private ParentRecordingItem mParentRecording;

        public RecordingViewHolder(View itemView) {
            super(itemView);

            this.mListName = (TextView) itemView.findViewById(R.id.list_item_name);
            this.mDate = (TextView) itemView.findViewById(R.id.time);
            this.mNumberOfItems = (TextView) itemView.findViewById(R.id.number_of_items);
            this.mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            this.mIsLocked = (ImageView) itemView.findViewById(R.id.is_locked);
            this.mCard = (CardView) itemView.findViewById(R.id.card_view);

            mCard.setOnClickListener(this);
            mCard.setOnLongClickListener(this);
            mCard.setLongClickable(true);
        }

        @Override
        public void onClick(View v) {

            if(!getIsInSelectionMode()) {
                Toast.makeText(mContext, "Launch List Item " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            } else {
                if (isSelected(getAdapterPosition())) {
                    mCard.setCardBackgroundColor(R.color.cardview_light_background);
                    mAvatar.setImageResource(R.drawable.ic_parent_recording_avatar_unselected);
                } else {
                    setIsInSelectionMode(true);
                    mCard.setCardBackgroundColor(R.color.colorDivider);
                    mAvatar.setImageResource(R.drawable.ic_parent_recording_avatar_selected);
                }
                toggleSelection(getAdapterPosition());
                if (getSelectedItemCount() == 0) {
                    clearSelection();
                    Toast.makeText(mContext, "Notify Activity to close the context menu", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public boolean onLongClick(View v) {

            if(!getIsInSelectionMode()) {
                setIsInSelectionMode(true);
                Toast.makeText(mContext, "Launch context menu " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
            v.callOnClick();
            return true;
        }

        public void bindParentRecordingItem(ParentRecordingItem parentRecording) {
            mParentRecording = parentRecording;

            mListName.setText(mParentRecording.getRecordName());
            mNumberOfItems.setText(Integer.toString(mParentRecording.getNumberOfItemsInRecord()));
            mAvatar.setImageResource(R.drawable.ic_parent_recording_avatar_unselected);
            GregorianCalendar startDate = mParentRecording.getStartDate();
            CharSequence time = DateUtils.getRelativeDateTimeString(mDate.getContext(), startDate.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME);
            mDate.setText(time);

            if (mParentRecording.isClosed()) {
                mIsLocked.setImageResource(R.drawable.ic_lock_black_24dp);
            } else
                mIsLocked.setImageResource(R.drawable.ic_lock_open_black_24dp);
        }
    }

}
