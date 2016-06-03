package com.example.vitaliy.taskmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.task.Task;

import java.util.ArrayList;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class CustomAdapter extends ArrayAdapter<Task> {

    private final Context mContext;
    private final ArrayList<Task> mArrayTaskList;
    private final int mLayoutResource;

    public CustomAdapter(Context mContext, ArrayList<Task> mArrayTaskList, int mLayoutResourse) {
        super(mContext, mLayoutResourse, mArrayTaskList);
        this.mContext = mContext;
        this.mArrayTaskList = mArrayTaskList;
        this.mLayoutResource = mLayoutResourse;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder;
        if (row == null) {
            row = ((Activity) mContext).getLayoutInflater().inflate(mLayoutResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextViewTaskName = (TextView) row.findViewById(R.id.textView_item_task);
            viewHolder.mTextViewDescription = (TextView) row.findViewById(R.id.textView_item_description);
            viewHolder.mTextViewTaskBegin = (TextView) row.findViewById(R.id.textView_dateTime_start);
            viewHolder.mTextViewTaskFinish = (TextView) row.findViewById(R.id.textView_dateTime_finish);
            viewHolder.mItemLayout = (LinearLayout) row.findViewById(R.id.root_item_layout);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        Task task = mArrayTaskList.get(position);
        viewHolder.mTextViewTaskName.setText(task.getmTaskName());
        viewHolder.mTextViewDescription.setText(task.getmDescription());
        viewHolder.mTextViewTaskBegin.setText(task.getmTaskBegin());
        viewHolder.mTextViewTaskFinish.setText(task.getmTaskFinish());
        viewHolder.mItemLayout.setBackgroundColor(task.getmTaskColor());

        return row;

    }

    // Wrapper для TextView
    static class ViewHolder {
        TextView mTextViewTaskName;
        TextView mTextViewDescription;
        TextView mTextViewTaskBegin;
        TextView mTextViewTaskFinish;
        LinearLayout mItemLayout;
    }
}
