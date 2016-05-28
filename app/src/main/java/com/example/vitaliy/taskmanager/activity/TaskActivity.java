package com.example.vitaliy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.adapter.CustomAdapter;
import com.example.vitaliy.taskmanager.task.Task;

import java.util.ArrayList;

public class TaskActivity extends Activity {

    private ArrayList<String> mArrayListTaskName;
    private ArrayList<String> mArrayListDescription;
    private ArrayList<Task> mArrayListTasks;
    private CustomAdapter mAdapter;

    final int REQUEST_CODE = 123;
    public final static String TASK_KEY = "Task";
    public final static String DESCRIPTION_KEY = "Description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        final ListView mViewTaskList = (ListView) findViewById(R.id.listView_task);

        //Створюємо ArrayList для об’єктів Task
        mArrayListTasks = new ArrayList<>();

        //Відновлюємо втрачені дані після знищення актівітіс(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            //отримуємо ArrayList із масиву TaskName
            mArrayListTaskName = savedInstanceState.getStringArrayList(TASK_KEY);
            //Log.d("MLog", "Ліст з тасків " + mArrayListTaskName);
            //отримуємо ArrayList із масиву Description
            mArrayListDescription = savedInstanceState.getStringArrayList(DESCRIPTION_KEY);
            //Log.d("MLog", "Ліст з описів " + mArrayListDescription);
            //добавляємо за допомогою методу setTaskNameAndDescription TaskName і Description
            // в ArrayList, що містить об’єкти Task
            mArrayListTasks.addAll(setTaskNameAndDescription(mArrayListTaskName, mArrayListDescription));
            // Log.d("MLog", "Обєкти таск в лісті " + mArrayListTasks);
        }

        //Створюємо адаптер для ListView та прикріплюємо його. Встановлюємо автоматичне оновлення
        //ListView при добавленні нового об6єкта Task в ArrayList
        mAdapter = new CustomAdapter(this, mArrayListTasks, R.layout.layout_item);
        mViewTaskList.setAdapter(mAdapter);
        mAdapter.setNotifyOnChange(true);


        final Button buttonAdd = (Button) findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String task = data.getStringExtra(TASK_KEY);
            String description = data.getStringExtra(DESCRIPTION_KEY);
            mArrayListTasks.add(new Task(task, description));
            //Log.d("MLog", "Task " + mArrayListTaskName + "   Description " + mArrayListDescription);
            mAdapter.notifyDataSetChanged();
        }
    }

    // метод отримання ArrayList<String> з TaskName із ArrayList<Task>
    private ArrayList<String> getTaskNameFromArrayList(ArrayList<Task> arrayListTaskList) {
        ArrayList<String> taskNameList = new ArrayList<>();
        for (Task task : arrayListTaskList) {
            taskNameList.add(task.getmTaskName());
        }
        return taskNameList;
    }

    // метод отримання ArrayList<String> з Description із ArrayList<Task>
    private ArrayList<String> getDescriptionFromArrayList(ArrayList<Task> arrayListTaskList) {
        ArrayList<String> descriptionList = new ArrayList<>();
        for (Task task : arrayListTaskList) {
            descriptionList.add(task.getmDescription());
        }
        return descriptionList;
    }

    // поміщаємо дані з двох ArrayList із TaskName та Description в ArrayList <Task>
    private ArrayList<Task> setTaskNameAndDescription(ArrayList<String> mArrayListTaskName,
                                                      ArrayList<String> mArrayListDescription) {
        ArrayList<Task> arrayListTask = new ArrayList<>();
        for (int index = 0; index < mArrayListTaskName.size(); index++) {
            arrayListTask.add(new Task(mArrayListTaskName.get(index), mArrayListDescription.get(index)));
            Log.d("MLog", "В лысты пысля повороту, в циклы " + arrayListTask);
        }
        return arrayListTask;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mArrayListTaskName = getTaskNameFromArrayList(mArrayListTasks);
        mArrayListDescription = getDescriptionFromArrayList(mArrayListTasks);

        outState.putStringArrayList(TASK_KEY, mArrayListTaskName);
        outState.putStringArrayList(DESCRIPTION_KEY, mArrayListDescription);
    }
}
