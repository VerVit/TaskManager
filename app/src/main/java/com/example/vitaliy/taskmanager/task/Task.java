package com.example.vitaliy.taskmanager.task;

import java.util.Comparator;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class Task {

    private String mTaskName;
    private String mDescription;
    private String mTaskBegin;
    private String mTaskFinish;
    private int mTaskColor;

    public Task(String mTaskName, String mDescription, String mTaskBegin, String mTaskFinish, int mTaskColor) {
        this.mTaskName = mTaskName;
        this.mDescription = mDescription;
        this.mTaskBegin = mTaskBegin;
        this.mTaskFinish = mTaskFinish;
        this.mTaskColor = mTaskColor;
    }

    public Task() {
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmTaskName() {
        return mTaskName;
    }

    public String getmTaskBegin() {
        return mTaskBegin;
    }

    public int getmTaskColor() {
        return mTaskColor;
    }

    public String getmTaskFinish() {
        return mTaskFinish;
    }

    public static class CompareByTaskNameA_Z implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            return lhs.getmTaskName().compareTo(rhs.getmTaskName());
        }
    }

    public static class CompareByTaskNameZ_A implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            return rhs.getmTaskName().compareTo(lhs.getmTaskName());
        }
    }

    public static class CompareByTaskTime0_9 implements Comparator<Task> {
        String tempCompare = "";

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getmTaskBegin() != null && rhs.getmTaskBegin() != null) {
                return lhs.getmTaskBegin().compareTo(rhs.getmTaskBegin());
            } else if (lhs.getmTaskBegin() == null && rhs.getmTaskBegin() != null) {
                return tempCompare.compareTo(rhs.getmTaskBegin());
            } else if (lhs.getmTaskBegin() != null) {
                return lhs.getmTaskBegin().compareTo(tempCompare);
            } else return 0;
        }
    }

    public static class CompareByTaskTime9_0 implements Comparator<Task> {
        String tempCompare = "";

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.getmTaskBegin() != null && rhs.getmTaskBegin() != null) {
                return rhs.getmTaskBegin().compareTo(lhs.getmTaskBegin());
            } else if (lhs.getmTaskBegin() == null && rhs.getmTaskBegin() != null) {
                return rhs.getmTaskBegin().compareTo(tempCompare);
            } else if (lhs.getmTaskBegin() != null) {
                return tempCompare.compareTo(lhs.getmTaskBegin());
            } else return 0;
        }
    }
}