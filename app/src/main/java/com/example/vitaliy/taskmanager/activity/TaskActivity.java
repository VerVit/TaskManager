package com.example.vitaliy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    private ArrayList<Task> mArrayListTasks;
    private CustomAdapter mAdapter;

    final int REQUEST_CODE = 123;
    public final static String TASK_KEY = "Task";
    public final static String DESCRIPTION_KEY = "Description";
    public final static String AL_OF_TASK_KEY = "ArrayList with Tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        final ListView mViewTaskList = (ListView) findViewById(R.id.listView_task);

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            mArrayListTasks=savedInstanceState.getParcelableArrayList(AL_OF_TASK_KEY);
        }else {
            mArrayListTasks = new ArrayList<>();
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
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //поміщаємо дані для збереження після знищення актівіті
        outState.putParcelableArrayList(AL_OF_TASK_KEY,mArrayListTasks);
    }
}
