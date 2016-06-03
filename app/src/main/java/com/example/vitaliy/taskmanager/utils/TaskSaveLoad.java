package com.example.vitaliy.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.vitaliy.taskmanager.task.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Vitaliy on 01.06.2016.
 */
public class TaskSaveLoad {

    private Context mContext;
    private String mFileName;
    private SharedPreferences mTaskSharedPreferences;

    private final static String TASK_SHARED_PREFERENCE_KEY = "Shared preference";

    //Конструктоор для отримання поточної силки на контекст та назви файлу
    public TaskSaveLoad(Context mContext, String mFileName) {
        this.mContext = mContext;
        this.mFileName = mFileName;
    }

    // Зберігаємо завдання
    public synchronized void saveTasks(ArrayList<Task> taskArrayList) {
        //Створюємо SharedPreferences та Editor
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor taskEditor = mTaskSharedPreferences.edit();
        //Сторюємо Set та поміщаємо туди серіалізовані об'єкти завдань
        Set<String> jsonSetString = new LinkedHashSet<>();
        for (Task task : taskArrayList) {
            try {
                jsonSetString.add(task.convertToJSON().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Записуємо дані на файл
        boolean oK = taskEditor.putStringSet(TASK_SHARED_PREFERENCE_KEY, jsonSetString).commit();
        Log.d("MLog", " All Tasks is save " + oK + "   " + jsonSetString);
    }

    //Завантажуємо дані із файла
    public ArrayList<Task> loadTasks(ArrayList<Task> taskArrayList) {
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Зберігаємо в Set JSONObject як стрінг
        Set<String> taskSet = mTaskSharedPreferences.getStringSet(TASK_SHARED_PREFERENCE_KEY, null);
        //Конвертуємо кожне завдання в JSONObject і формуємо ArrayList з завдань
        if (taskSet != null) {
            for (String task : taskSet) {
                try {
                    JSONObject taskJSJsonObject = new JSONObject(task);
                    taskArrayList.add(new Task(taskJSJsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("MLog", " All Tasks is load" + taskArrayList);
        return taskArrayList;
    }
}
