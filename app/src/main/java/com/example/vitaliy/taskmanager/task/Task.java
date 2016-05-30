package com.example.vitaliy.taskmanager.task;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class Task implements Parcelable{

    private String mTaskName;
    private String mDescription;

    public Task(String mTaskName, String mDescription) {
        this.mTaskName = mTaskName;
        this.mDescription = mDescription;
    }

    public Task() {
    }

    protected Task(Parcel in) {
        mTaskName = in.readString();
        mDescription = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getmDescription() {
        return mDescription;
    }

    public String getmTaskName() {
        return mTaskName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mTaskName);
    dest.writeString(mDescription);
    }
}
