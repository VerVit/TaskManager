package com.example.vitaliy.taskmanager.utils.Realm;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.example.vitaliy.taskmanager.task.Task;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Vitaliy on 23.06.2016.
 */

public class RealmController {

    private static RealmController mInstance;
    private final Realm mRealm;

    //ініціалізуємо Realm
    private RealmController(Application application) {
        mRealm = Realm.getDefaultInstance();
        mRealm.setAutoRefresh(true);
    }

    public static RealmController with(Activity activity) {

        if (mInstance == null) {
            mInstance = new RealmController(activity.getApplication());
        }
        return mInstance;
    }

    public static RealmController with(Application application) {

        if (mInstance == null) {
            mInstance = new RealmController(application);
        }
        return mInstance;
    }

    public static RealmController getmInstance() {

        return mInstance;
    }

    public Realm getmRealm() {

        return mRealm;
    }

    //Видалити все з бази даних
    public void clearAll() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.deleteAll();
            }
        });
    }

    //Отримати все з бази даних
    public RealmResults<Task> getTasks() {
        return mRealm.where(Task.class).findAll();
    }

    //Видаляємо завдання у зазначеній позиції
    public void deleteTaskByPosition(final int position) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults results = mRealm.where(Task.class).findAll();
                results.deleteFromRealm(position);
            }
        });
    }

    //додаємо завдання до бази даних
    public void addTask(final Task task) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task copyOfTask = mRealm.copyToRealm(task);
                Log.d("MLog", "copyOfTask " + copyOfTask.getId() + " " + copyOfTask.getmTaskName() + " " + copyOfTask.getmDescription());
            }
        });
    }

    //оновлюємо завдання в базі даних
    public void updateTask(final Task task) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task updateTask = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
                Log.d("MLog", "updateTask = " + updateTask + "\ntask = " + task);

                updateTask.setId(task.getId());
                updateTask.setmTaskName(task.getmTaskName());
                updateTask.setmDescription(task.getmDescription());
                updateTask.setmTaskBegin(task.getmTaskBegin());
                updateTask.setmTaskFinish(task.getmTaskFinish());
                updateTask.setmTaskColor(task.getmTaskColor());
                Log.d("MLog", "copyOfTask " + task.getId() + " " + task.getmTaskName() + " " + task.getmDescription());
            }
        });
    }
}