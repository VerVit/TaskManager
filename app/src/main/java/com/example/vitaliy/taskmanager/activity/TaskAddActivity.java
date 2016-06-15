package com.example.vitaliy.taskmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vitaliy.taskmanager.R;

public class TaskAddActivity extends AppCompatActivity {

    private EditText mEditTextTaskName;
    private EditText mEditTextDescription;
    private int mItemPosition;
    private LinearLayout mMainLayout;
    private Snackbar mSnackBarExit;

    private final static String NAME_TASK_KEY = "Task name";
    private final static String DESCRIPTION_TASK_KEY = "Description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTextTaskName = (EditText) findViewById(R.id.editText_taskName);
        mEditTextDescription = (EditText) findViewById(R.id.editText_taskDescription);
        mMainLayout = (LinearLayout) findViewById(R.id.main_layout_2activity);

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            mEditTextTaskName.setText(savedInstanceState.getString(NAME_TASK_KEY));
            mEditTextDescription.setText(savedInstanceState.getString(DESCRIPTION_TASK_KEY));
        }

        //заповнюємо EditText для редагування завдань
        fillEditText();

    }

    private void fillEditText() {
        Intent intent = getIntent();
        if (intent != null) {
            mEditTextTaskName.setText(intent.getStringExtra(TaskActivity.TASK_KEY));
            mEditTextDescription.setText(intent.getStringExtra(TaskActivity.DESCRIPTION_KEY));
            //Передаєм номер позиції Item в ListView
            mItemPosition = intent.getIntExtra(TaskActivity.ITEM_LIST_VIEW_POSITION_KEY, 456);
        }
    }

    private String getTextFromEditText(EditText editTextName) {
        return editTextName.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_ok:
                //Виконується коли не заповнене одне із EditText
                if (mEditTextTaskName.getText().toString().equals("") ||
                        mEditTextDescription.getText().toString().equals("")) {
                    Snackbar.make(mMainLayout, "Please write TaskName and  description for him", Snackbar.LENGTH_LONG).show();
                } else {
                    //передаємо дані з EditText через Intent першому актівіті
                    String mTask = getTextFromEditText(mEditTextTaskName);
                    String mDescription = getTextFromEditText(mEditTextDescription);

                    Intent intent = new Intent(this, TaskAddActivity.class);
                    intent.putExtra(TaskActivity.TASK_KEY, mTask);
                    intent.putExtra(TaskActivity.DESCRIPTION_KEY, mDescription);
                    intent.putExtra(TaskActivity.ITEM_LIST_VIEW_POSITION_KEY, mItemPosition);
                    setResult(RESULT_OK, intent);
                    finish();
                    //Анімація переходу між актівіті
                    overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                }
                break;
            case R.id.item_cancel:
                finish();
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_TASK_KEY, getTextFromEditText(mEditTextTaskName));
        outState.putString(DESCRIPTION_TASK_KEY, getTextFromEditText(mEditTextDescription));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_left_to_right,R.anim.exit_left_to_right);
    }
}

