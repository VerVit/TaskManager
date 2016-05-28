package com.example.vitaliy.taskmanager.task;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class Task {

    private String mTaskName;
    private String mDescription;

    public Task(String mTaskName, String mDescription) {
        this.mTaskName = mTaskName;
        this.mDescription = mDescription;
    }

    public Task() {
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmTaskName() {
        return mTaskName;
    }


}
