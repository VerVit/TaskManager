package com.example.vitaliy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.vitaliy.taskmanager.R;

public class TaskAddActivity extends Activity implements View.OnClickListener {

    private EditText mEditTextTaskName;
    private EditText mEditTextDescription;
    private int mItemPosition;
    private RelativeLayout mMainLayout;

    private final static String NAME_TASK_KEY = "Task name";
    private final static String DESCRIPTION_TASK_KEY = "Description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        mEditTextTaskName = (EditText) findViewById(R.id.editText_taskName);
        mEditTextDescription = (EditText) findViewById(R.id.editText_taskDescription);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout_2activity);

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            mEditTextTaskName.setText(savedInstanceState.getString(NAME_TASK_KEY));
            mEditTextDescription.setText(savedInstanceState.getString(DESCRIPTION_TASK_KEY));
        }

        Button btn_save = (Button) findViewById(R.id.button_save);
        Button btn_exit = (Button) findViewById(R.id.button_exit);
        btn_save.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        //заповнюємо EditText для редагування завдань
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
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
            case R.id.button_exit:
                finish();
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_TASK_KEY, getTextFromEditText(mEditTextTaskName));
        outState.putString(DESCRIPTION_TASK_KEY, getTextFromEditText(mEditTextDescription));
    }
}

