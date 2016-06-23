package com.example.vitaliy.taskmanager.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.utils.SharedPreference.TaskSaveLoad;

public class TaskSetting extends AppCompatActivity implements View.OnClickListener, Runnable {

    private ColorPickerDialog mColorPickerDialog;
    private int mColorButtonTaskCreate;
    private int mColorButtonTaskBegin;
    private int mColorButtonTaskFinish;
    private int mOldColorButtonTaskCreate;
    private int mOldColorButtonTaskBegin;
    private int mOldColorButtonTaskFinish;
    private ImageButton mIButtonTaskCreate;
    private ImageButton mIButtonTaskBegin;
    private ImageButton mIButtonTaskFinish;
    private EditText mAutoFinish;
    private static int mButtonId;
    private static boolean mColorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);

        // робимо видимою кновку "назад" в ToolBar
        enabledHomeButton();

        mIButtonTaskCreate = (ImageButton) findViewById(R.id.imageButton_taskCreate);
        mIButtonTaskBegin = (ImageButton) findViewById(R.id.imageButton_taskBegin);
        mIButtonTaskFinish = (ImageButton) findViewById(R.id.imageButton_taskFinish);
        mAutoFinish = (EditText) findViewById(R.id.editText_autoFinish);

        mIButtonTaskCreate.setOnClickListener(this);
        mIButtonTaskBegin.setOnClickListener(this);
        mIButtonTaskFinish.setOnClickListener(this);


        //Відновлюємо втрачені дані після повороту екрану телефона
        savedInstanceState();

        //Ініціалізуємо колор пікет
        createColorPicker();
    }

    private void savedInstanceState() {

        //завантажуємо кольори вибрані останній раз
        TaskSaveLoad load = new TaskSaveLoad(this, TaskActivity.FILE_NAME);
        mColorButtonTaskCreate = load.loadColorCreate();
        mColorButtonTaskBegin = load.loadColorBegin();
        mColorButtonTaskFinish = load.loadColorFinish();

        //Відновлюємо кольори вибрані останній раз
        mIButtonTaskCreate.setImageDrawable(createShape(mColorButtonTaskCreate));
        mIButtonTaskBegin.setImageDrawable(createShape(mColorButtonTaskBegin));
        mIButtonTaskFinish.setImageDrawable(createShape(mColorButtonTaskFinish));

        //Записуємо поточні кольори до старих, щоб пізніше прослідкувати чи
        //користувач змінював кольори чи ні
        mOldColorButtonTaskCreate = mColorButtonTaskCreate;
        mOldColorButtonTaskBegin = mColorButtonTaskBegin;
        mOldColorButtonTaskFinish = mColorButtonTaskFinish;
    }

    private void createColorPicker() {

        //Викликаємо колор пікет
        mColorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);
        mColorPickerDialog.hideColorComponentsInfo();
        mColorPickerDialog.setCancelable(false);
        mColorPickerDialog.setOnClosedListener(new ColorPickerDialog.OnClosedListener() {
            @Override
            public void onClosed() {
                mColorPicker = false;
            }
        });
        mColorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                //Зберігаємо  вибвр кольору
                switch (mButtonId) {
                    case R.id.imageButton_taskCreate:
                        mIButtonTaskCreate.setImageDrawable(createShape(color));
                        mColorButtonTaskCreate = color;
                        mColorPicker = false;
                        break;
                    case R.id.imageButton_taskBegin:
                        mIButtonTaskBegin.setImageDrawable(createShape(color));
                        mColorButtonTaskBegin = color;
                        mColorPicker = false;
                        break;
                    case R.id.imageButton_taskFinish:
                        mIButtonTaskFinish.setImageDrawable(createShape(color));
                        mColorButtonTaskFinish = color;
                        mColorPicker = false;
                        break;
                }
                new Thread(TaskSetting.this).start();
            }
        });
        if (mColorPicker) {
            mColorPickerDialog.show();
        }
    }
    // отримуємо int змінну з EditText, по дефолту 10
    private int getIntFromET(EditText editText) {
        int autoFinish;
        if (editText.getText().length() != 0) {
            autoFinish = Integer.parseInt(editText.getText().toString());
        } else return 10;
        return autoFinish;
    }
    //Робимо видимою HomeButton
    private void enabledHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Призначаємо слухача на кнопку "назад"
            case android.R.id.home:
                startIntent();
                break;
            case R.id.item_default:
                // Записуємо дефолтні кольори до відповідних змінних
                mColorButtonTaskCreate = getResources().getColor(R.color.colorGreen);
                mColorButtonTaskBegin = getResources().getColor(R.color.colorLGHTYellow);
                mColorButtonTaskFinish = getResources().getColor(R.color.colorRed);
                //Призначаємо дефолтні кольори кнопкам
                mIButtonTaskCreate.setImageDrawable(createShape(mColorButtonTaskCreate));
                mIButtonTaskBegin.setImageDrawable(createShape(mColorButtonTaskBegin));
                mIButtonTaskFinish.setImageDrawable(createShape(mColorButtonTaskFinish));
                mAutoFinish.setText("10");
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageButton_taskCreate:
                mButtonId = R.id.imageButton_taskCreate;
                mColorPicker = true;
                mColorPickerDialog.show();
                break;
            case R.id.imageButton_taskBegin:
                mButtonId = R.id.imageButton_taskBegin;
                mColorPicker = true;
                mColorPickerDialog.show();
                break;
            case R.id.imageButton_taskFinish:
                mButtonId = R.id.imageButton_taskFinish;
                mColorPicker = true;
                mColorPickerDialog.show();
                break;
        }
    }

    //Передаємо дані зміни кольорів на головне актівіті
    private void startIntent() {
        if (mOldColorButtonTaskCreate == mColorButtonTaskCreate &&
                mOldColorButtonTaskBegin == mColorButtonTaskBegin &&
                mOldColorButtonTaskFinish == mColorButtonTaskFinish) {
            finish();
            // Анімація переходу між актівіті
            overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
        } else {
            Intent intent = new Intent(this, TaskActivity.class);
            intent.putExtra(TaskActivity.COLOR_TASK_CREATE, mColorButtonTaskCreate);
            intent.putExtra(TaskActivity.COLOR_TASK_BEGIN, mColorButtonTaskBegin);
            intent.putExtra(TaskActivity.COLOR_TASK_FINISH, mColorButtonTaskFinish);
            intent.putExtra(TaskActivity.TASK_AUTO_FINISH, getIntFromET(mAutoFinish));
            setResult(RESULT_OK,intent);
            finish();
            // Анімація переходу між актівіті
            overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
        }
    }

    // Надаємо круглу форму для кнопки та записуємо вибраний користувачем колір
    private GradientDrawable createShape(int color) {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    @Override
    public void run() {
        TaskSaveLoad save = new TaskSaveLoad(this, TaskActivity.FILE_NAME);
        save.saveColor(mColorButtonTaskCreate, mColorButtonTaskBegin, mColorButtonTaskFinish);
        save.saveAutoTimeFinish(getIntFromET(mAutoFinish));
    }

    @Override
    public void onBackPressed() {
        startIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(this).start();
    }
}
