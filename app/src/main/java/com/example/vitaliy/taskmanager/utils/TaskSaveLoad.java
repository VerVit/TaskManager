package com.example.vitaliy.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.vitaliy.taskmanager.task.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Vitaliy on 01.06.2016.
 */
public class TaskSaveLoad {

    private Context mContext;
    private String mFileName;
    private SharedPreferences mTaskSharedPreferences;
    private String mGSonTasksList;

    private final static String TASK_SHARED_PREFERENCE_KEY = "Shared preference";
    private final static String ITEM_ID_SHARED_PREFERENCE_KEY = "Item is checked";
    private final static String COLOR_CREATE_SHARED_PREFERENCE_KEY = "Color than task create";
    private final static String COLOR_BEGIN_SHARED_PREFERENCE_KEY = "Color than task begin";
    private final static String COLOR_FINISH_SHARED_PREFERENCE_KEY = "Color than task finish";

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
        //Сторюємо об’єкт Gson та серіалізуємо ArrayList<Task>
        mGSonTasksList = new Gson().toJson(taskArrayList);
        //Записуємо дані на файл
        boolean oK = taskEditor.putString(TASK_SHARED_PREFERENCE_KEY, mGSonTasksList).commit();
        Log.d("MLog", " All Tasks is save " + oK + "   " + mGSonTasksList);
    }

    //Завантажуємо дані про завдання із файла
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> taskArrayList;
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Отримуємо із файла серіалізований ArrayList<Task> та записуємо його до String
        mGSonTasksList = mTaskSharedPreferences.getString(TASK_SHARED_PREFERENCE_KEY, null);
        //Визначаэмо тип для дисерелызацыъ та зберігаємо дані до ArrayList<Task>
        Type listOfTasks = new TypeToken<ArrayList<Task>>() {
        }.getType();
        taskArrayList = new Gson().fromJson(mGSonTasksList, listOfTasks);
        Log.d("MLog", " All Tasks is load" + taskArrayList);
        return taskArrayList;
    }

    // Зберігаємо вибір сортування
    public synchronized void saveItemId(int id) {
        //Створюємо SharedPreferences та Editor
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor taskEditor = mTaskSharedPreferences.edit();

        //Записуємо дані на файл
        boolean oK = taskEditor.putInt(ITEM_ID_SHARED_PREFERENCE_KEY, id).commit();
        Log.d("MLog", " ItemId is save " + oK);
    }

    //Завантажуємо id Item із файла
    public int loadItemId() {

        int id;
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Отримуємо із файла серіалізований ArrayList<Task> та записуємо його до String
        id = mTaskSharedPreferences.getInt(ITEM_ID_SHARED_PREFERENCE_KEY, 0);
        Log.d("MLog", " All Tasks is load" + id);
        return id;
    }
    // Зберігаємо колір з налаштувань
    public synchronized void saveColor(int colorTaskCreate,int colorTaskBegin, int colorTaskFinish) {
        //Створюємо SharedPreferences та Editor
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor taskEditor = mTaskSharedPreferences.edit();

        //Записуємо дані на файл
        taskEditor.putInt(COLOR_CREATE_SHARED_PREFERENCE_KEY, colorTaskCreate);
        taskEditor.putInt(COLOR_BEGIN_SHARED_PREFERENCE_KEY, colorTaskBegin);
        taskEditor.putInt(COLOR_FINISH_SHARED_PREFERENCE_KEY, colorTaskFinish);
        boolean oK =taskEditor.commit();
        Log.d("MLog", " Color is save " + oK);
    }
    //Завантажуємо колір створено завдання
    public int loadColorCreate() {

        int color;
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Отримуємо із файла серіалізований ArrayList<Task> та записуємо його до String
        color = mTaskSharedPreferences.getInt(COLOR_CREATE_SHARED_PREFERENCE_KEY, 0);
        Log.d("MLog", " All Tasks is load" + color);
        return color;
    }
    //Завантажуємо колір розпочатого завдання
    public int loadColorBegin() {

        int color;
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Отримуємо із файла серіалізований ArrayList<Task> та записуємо його до String
        color = mTaskSharedPreferences.getInt(COLOR_BEGIN_SHARED_PREFERENCE_KEY, 0);
        Log.d("MLog", " All Tasks is load" + color);
        return color;
    }
    //Завантажуємо колір завершеного завдання
    public int loadColorFinish() {

        int color;
        //Отримуємо SharedPreference
        mTaskSharedPreferences = mContext.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        //Отримуємо із файла серіалізований ArrayList<Task> та записуємо його до String
        color = mTaskSharedPreferences.getInt(COLOR_FINISH_SHARED_PREFERENCE_KEY, 0);
        Log.d("MLog", " All Tasks is load" + color);
        return color;
    }
}
