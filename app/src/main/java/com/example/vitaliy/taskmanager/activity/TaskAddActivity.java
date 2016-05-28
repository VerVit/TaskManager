package com.example.vitaliy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vitaliy.taskmanager.R;

public class TaskAddActivity extends Activity implements View.OnClickListener {

    private EditText mEditText_taskName;
    private EditText mEditText_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        mEditText_taskName = (EditText) findViewById(R.id.editText_taskName);
        mEditText_description = (EditText) findViewById(R.id.editText_taskDescription);
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        // Анымація для загрузки RelativeLayout
        final Animation layoutAppearance = AnimationUtils.loadAnimation(this, R.anim.from_right_to_left);
        mainLayout.setAnimation(layoutAppearance);

        Button btn_save = (Button) findViewById(R.id.button_save);
        Button btn_exit = (Button) findViewById(R.id.button_exit);
        btn_save.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

    }

    private String getTextFromEditText(EditText editTextName) {
        return editTextName.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if (mEditText_taskName.getText().toString().equals("") ||
                        mEditText_description.getText().toString().equals("")) {
                    Toast.makeText(this, "Please write TaskName and  description for him", Toast.LENGTH_LONG).show();
                } else {
                    String mTask = getTextFromEditText(mEditText_taskName);
                    String mDescription = getTextFromEditText(mEditText_description);

                    Intent intent = new Intent(this, TaskAddActivity.class);
                    intent.putExtra(TaskActivity.TASK_KEY, mTask);
                    intent.putExtra(TaskActivity.DESCRIPTION_KEY, mDescription);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.button_exit:
                finish();
                break;
        }
    }
}

