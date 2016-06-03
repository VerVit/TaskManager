package com.example.vitaliy.taskmanager.task;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class Task implements Parcelable {

    private String mTaskName;
    private String mDescription;
    private String mTaskBegin;
    private String mTaskFinish;
    private int mTaskColor;

    private final static String TASK_NAME_KEY = "Task name";
    private final static String DESCRIPTION_KEY = "Description";
    private final static String TASK_BEGIN_KEY = "Begin task";
    private final static String TASK_FINISH_KEY = "Finish task";
    private final static String TASK_COLOR_KEY = "Task color";

    public Task(String mTaskName, String mDescription, String mTaskBegin, String mTaskFinish, int mTaskColor) {
        this.mTaskName = mTaskName;
        this.mDescription = mDescription;
        this.mTaskBegin = mTaskBegin;
        this.mTaskFinish = mTaskFinish;
        this.mTaskColor = mTaskColor;
    }

    public Task() {
    }

    //Конструктор для загрузки
    protected Task(Parcel in) {
        mTaskName = in.readString();
        mDescription = in.readString();
        mTaskBegin = in.readString();
        mTaskFinish = in.readString();
        mTaskColor = in.readInt();
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

    public String getmTaskBegin() {
        return mTaskBegin;
    }

    public int getmTaskColor() {
        return mTaskColor;
    }

    public String getmTaskFinish() {
        return mTaskFinish;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTaskName);
        dest.writeString(mDescription);
        dest.writeString(mTaskBegin);
        dest.writeString(mTaskFinish);
        dest.writeInt(mTaskColor);
    }

    //Десеріалізація
    public Task(JSONObject taskJsonObject) throws JSONException {
        //Виконується якщо завдання закінчене
        if (taskJsonObject.has(TASK_FINISH_KEY)) {
            mTaskName = taskJsonObject.getString(TASK_NAME_KEY);
            mDescription = taskJsonObject.getString(DESCRIPTION_KEY);
            mTaskBegin = taskJsonObject.getString(TASK_BEGIN_KEY);
            mTaskFinish = taskJsonObject.getString(TASK_FINISH_KEY);
            mTaskColor = taskJsonObject.getInt(TASK_COLOR_KEY);
            //Виконується якщо завдання розпочате
        } else if (taskJsonObject.has(TASK_BEGIN_KEY)) {
            mTaskName = taskJsonObject.getString(TASK_NAME_KEY);
            mDescription = taskJsonObject.getString(DESCRIPTION_KEY);
            mTaskBegin = taskJsonObject.getString(TASK_BEGIN_KEY);
            mTaskColor = taskJsonObject.getInt(TASK_COLOR_KEY);
            //Виконується якщо завдання ще не розпочиналось
        } else {
            mTaskName = taskJsonObject.getString(TASK_NAME_KEY);
            mDescription = taskJsonObject.getString(DESCRIPTION_KEY);
            mTaskColor = taskJsonObject.getInt(TASK_COLOR_KEY);
        }
    }

    //Серіалізація
    public JSONObject convertToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TASK_NAME_KEY, mTaskName);
        jsonObject.put(DESCRIPTION_KEY, mDescription);
        jsonObject.put(TASK_BEGIN_KEY, mTaskBegin);
        jsonObject.put(TASK_FINISH_KEY, mTaskFinish);
        jsonObject.put(TASK_COLOR_KEY, mTaskColor);
        return jsonObject;
    }

}
