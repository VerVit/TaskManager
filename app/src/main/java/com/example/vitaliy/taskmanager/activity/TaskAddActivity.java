package com.example.vitaliy.taskmanager.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vitaliy.taskmanager.R;

public class TaskAddActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditTextTaskName;
    private EditText mEditTextDescription;
    private TextInputLayout mInputLayoutName;
    private TextInputLayout mInputLayoutDescription;
    private RelativeLayout mLayoutMain;
    private int mItemPosition;
    private final int REQUEST_CODE_VOICE_NAME = 987;
    private final int REQUEST_CODE_VOICE_DESCRIPTION = 123;

    private final static String NAME_TASK_KEY = "Task name";
    private final static String DESCRIPTION_TASK_KEY = "Description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayoutMain = (RelativeLayout) findViewById(R.id.main_layout_2activity);
        mEditTextTaskName = (EditText) findViewById(R.id.editText_taskName);
        mEditTextDescription = (EditText) findViewById(R.id.editText_taskDescription);
        mInputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        mInputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        // Кнопки запису в EditText за допомогою голосу
        ImageView launchVoiceName = (ImageView) findViewById(R.id.imageView_name_speak);
        ImageView launchVoiceDescription = (ImageView) findViewById(R.id.imageView_description_speak);

        if (launchVoiceName != null) {
            launchVoiceName.setOnClickListener(this);
        }
        if (launchVoiceDescription != null) {
            launchVoiceDescription.setOnClickListener(this);
        }

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            mEditTextTaskName.setText(savedInstanceState.getString(NAME_TASK_KEY));
            mEditTextDescription.setText(savedInstanceState.getString(DESCRIPTION_TASK_KEY));
        }

        //заповнюємо EditText даними отриманими з TaskActivity для редагування завдання
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
    // отримуємо строку з EditText
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
                //Виконується коли EditTextName містить менше 5 символів
                if (mEditTextTaskName.getText().toString().length() < 5) {
                    mInputLayoutName.setError("Enter your task name (min 5 letters)");
                    mInputLayoutName.setErrorEnabled(true);
                //Виконується коли EditTextDescription мзовсім не заповнений
                } else if (mEditTextDescription.getText().toString().length() == 0) {
                    mInputLayoutDescription.setError("Your task description is empty");
                    mInputLayoutDescription.setErrorEnabled(true);
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
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
    }

    @Override
    public void onClick(View v) {
        //Створюємо обробчика голосових команд
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        switch (v.getId()) {
            case R.id.imageView_name_speak:
                try {
                    //Запускаємо обробчика голосових команд
                    startActivityForResult(intent, REQUEST_CODE_VOICE_NAME);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(mLayoutMain, "Your device doesn't have Voice Recognition", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.imageView_description_speak:
                try {
                    //Запускаємо обробчика голосових команд
                    startActivityForResult(intent, REQUEST_CODE_VOICE_DESCRIPTION);
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(mLayoutMain, "Your device doesn't have Voice Recognition", Snackbar.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultWord;
        //Присвоюємо отримані оброблені голосові команди EditText ам
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_VOICE_NAME:
                    resultWord = data.getStringExtra(RecognizerIntent.EXTRA_RESULTS);
                    mEditTextTaskName.setText(resultWord);
                    break;
                case REQUEST_CODE_VOICE_DESCRIPTION:
                    resultWord = data.getStringExtra(RecognizerIntent.EXTRA_RESULTS);
                    mEditTextDescription.setText(resultWord);
                    break;
            }
        }
    }
}

