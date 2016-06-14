package com.example.vitaliy.taskmanager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.adapter.CustomAdapter;
import com.example.vitaliy.taskmanager.task.Task;
import com.example.vitaliy.taskmanager.utils.RandomString;
import com.example.vitaliy.taskmanager.utils.TaskSaveLoad;
import com.melnykov.fab.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity implements Runnable {

    private ArrayList<Task> mArrayListTasks;
    private CustomAdapter mAdapterTasks;
    private LinearLayout mLayoutMain;
    private ListView mViewTaskList;
    private Snackbar mSnackBarExit;
    private int mColorCreate;
    private int mColorBegin;
    private int mColorFinish;

    private final int REQUEST_CODE_ADD_TASK = 123;
    private final int REQUEST_CODE_SET_TASK = 456;
    private final int REQUEST_CODE_COLOR_TASK = 789;

    public final static String FILE_NAME = "Tasks date";
    public final static String TASK_KEY = "Task";
    public final static String DESCRIPTION_KEY = "Description";
    public final static String ITEM_LIST_VIEW_POSITION_KEY = "Position";
    public final static String ALL_OF_TASK_KEY = "ArrayList with Tasks";
    public final static String ITEM_SELECTED_KEY = "Item selected";
    public final static String COLOR_TASK_CREATE = "Task create";
    public final static String COLOR_TASK_BEGIN = "Task begin";
    public final static String COLOR_TASK_FINISH = "Task finish";

    private int mItemSelectedId = -1;
    private static boolean mShowDialog;
    private AlertDialog.Builder mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewTaskList = (ListView) findViewById(R.id.listView_task);
        mLayoutMain = (LinearLayout) findViewById(R.id.layout_main_1activity);

        //Встановлюємо стандартні кольори якщо вони не були попередньо змінені в налаштуваннях
        setDefaultColor();

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        savedInstanceState();

        //Свторюємо FAB
        createFAB();

        //Ініціалізуємо AlertDialog
        if (mShowDialog) {
            createDialog();
            mDialog.show();
        } else createDialog();

        // Ставимо слухача при кліку на ListView
        mViewTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //Знаходимо TextView в поточному item listView
                final TextView mTVDateAndTimeStart = (TextView) view.findViewById(R.id.textView_dateTime_start);
                final TextView mTVDateAndTimeFinish = (TextView) view.findViewById(R.id.textView_dateTime_finish);

                //Виконується якщо завдання не активне
                if (mTVDateAndTimeStart.getText().toString().equals("")) {
                    beginTask(position);
                    //Виконується коли завдання активне
                } else if (!(mTVDateAndTimeStart.getText().toString().equals("")) &&
                        mTVDateAndTimeFinish.getText().toString().equals("")) {
                    finishTask(position);
                    //Спрацьовує коли завдання виконано і ви хочете його знову розпочати
                } else {
                    runSnackBar(mLayoutMain, "Do you want restart this Task", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Скидається час виконання завдання
                                    String mTempTask = mArrayListTasks.get(position).getmTaskName();
                                    String mTempDescription = mArrayListTasks.get(position).getmDescription();
                                    mArrayListTasks.set(position, new Task(mTempTask,
                                            mTempDescription, null, null, mColorCreate));
                                    mAdapterTasks.notifyDataSetChanged();
                                }
                            }).show();
                }
                //Зберігаємо дані в новому потоці
                new Thread(TaskActivity.this).start();
            }
        });
        // Створюэмо слухача на LongClick
        mViewTaskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String tempTaskName = mArrayListTasks.get(position).getmTaskName();
                String tempDescription = mArrayListTasks.get(position).getmDescription();

                Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
                intent.putExtra(TASK_KEY, tempTaskName);
                intent.putExtra(DESCRIPTION_KEY, tempDescription);
                intent.putExtra(ITEM_LIST_VIEW_POSITION_KEY, position);
                startActivityForResult(intent, REQUEST_CODE_SET_TASK);
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                return true;
            }
        });

    }

    //перевіряємо в методі чи кольори не були змінені користувачем
    private void setDefaultColor() {
        if (mColorCreate == 0) {
            mColorCreate = getColorFromID(R.color.colorGreen);
        }
        if (mColorBegin == 0) {
            mColorBegin = getColorFromID(R.color.colorLGHTYellow);
        }
        if (mColorFinish == 0) {
            mColorFinish = getColorFromID(R.color.colorRed);
        }
    }

    private void createDialog() {
        mDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setCancelable(false)
                .setIcon(R.drawable.ic_delete)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mShowDialog = false;
                        mArrayListTasks.clear();
                        mAdapterTasks.notifyDataSetChanged();
                        new Thread(TaskActivity.this).start();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mShowDialog = false;
                    }
                });
    }

    //Створюємо кастумний FAB
    private void createFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.attachToListView(mViewTaskList);
            fab.show(true);
            fab.hide(true);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchAddTaskActivity();
                }
            });
        }
    }

    //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
    private void savedInstanceState() {
        //Створюэмо ArrayList для завдань та заповнюємо даними загруженими із файла
        mArrayListTasks = new ArrayList<>();
        mArrayListTasks.addAll(new TaskSaveLoad(this, FILE_NAME).loadTasks());
        mItemSelectedId = new TaskSaveLoad(this, FILE_NAME).loadItemId();

        /*Створюємо адаптер для ListView та прикріплюємо його. Встановлюємо автоматичне оновлення
        ListView при добавленні нового об6єкта до адаптеру*/
        mAdapterTasks = new CustomAdapter(this, mArrayListTasks, R.layout.layout_item);
        mViewTaskList.setAdapter(mAdapterTasks);
        mAdapterTasks.setNotifyOnChange(true);

    }

    //Запускаємо TaskAddActivity
    private void launchAddTaskActivity() {
        Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
        //Анімація переходів між актівіті
        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
    }

    //Виводить на view SnackBar
    private Snackbar runSnackBar(View view, String text, int duration) {

        Snackbar snackbar = Snackbar.make(view, text, duration);
        //змінюємо колір "Yes" в SnackBar
        snackbar.setActionTextColor(getColorFromID(R.color.colorAccent));
        //змінюємо колір текстового сповіщення в SnackBar
        View snackBarView = snackbar.getView();
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getColorFromID(R.color.colorLGHTYellow));
        return snackbar;
    }

    //Обробляє час закінчення роботи завдання
    private void finishTask(int position) {
        Date mFinishDate = new Date();
        long mFinishTime = mFinishDate.getTime();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String tempTask = mArrayListTasks.get(position).getmTaskName();
        String tempDescription = mArrayListTasks.get(position).getmDescription();
        String tempTaskBegin = mArrayListTasks.get(position).getmTaskBegin();
        String tempTaskFinish = "-   " + getDateAndTime(mFinishDate) + "  "
                + getDifferenceTime(mFinishTime, mArrayListTasks.get(position).getmTaskBegin());

        mArrayListTasks.set(position, new Task(tempTask, tempDescription, tempTaskBegin, tempTaskFinish,
                mColorFinish));
        mAdapterTasks.notifyDataSetChanged();
    }

    //Обробляє час початку роботи завдання
    private void beginTask(int position) {
        Date mStartDate = new Date();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String tempTask = mArrayListTasks.get(position).getmTaskName();
        String tempDescription = mArrayListTasks.get(position).getmDescription();
        mArrayListTasks.set(position, new Task(tempTask, tempDescription
                , getDateAndTime(mStartDate), null, mColorBegin));
        mAdapterTasks.notifyDataSetChanged();
    }

    private int getColorFromID(int colorId) {
        return getResources().getColor(colorId);
    }

    //Повертає стрінг з датою та часом вказаною в аргументі
    private String getDateAndTime(Date mCurrentDate) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(mCurrentDate);
    }

    //Повертає стрінг з часом різниць 2 аргументів
    private String getDifferenceTime(long mFinishTime, String mTextStartTask) {
        long mStarTime = 0;
        // Отримуємо час в мілісекундах закінчення виконная завдання
        try {
            mStarTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).parse(mTextStartTask).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // різниця часу закінчення і початку в мілісекундах
        long mDifferenceTime = mFinishTime - mStarTime;
        //Розбиваємо міліскунди на хвилини і години
        long mDifMinutes = ((mDifferenceTime / 1000) % 3600) / 60;
        long mDifHours = mDifMinutes / 3600;
        Log.d("MLog", "mDifMinutes " + mDifMinutes + ", mDifHours " + mDifHours + " mDifferenceTime " + mDifferenceTime);
        return String.format("%02d:%02d", mDifHours, mDifMinutes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mItem;
        if (mItemSelectedId == -1) {
            return true;
        }
        //відмічаємо список сортування, що був обраний користувачем останній раз
        switch (mItemSelectedId) {
            case R.id.menuSortA_Z:
                mItem = menu.findItem(R.id.menuSortA_Z);
                mItem.setChecked(true);
                break;
            case R.id.menuSortZ_A:
                mItem = menu.findItem(R.id.menuSortZ_A);
                mItem.setChecked(true);
                break;
            case R.id.menuSort0_9:
                mItem = menu.findItem(R.id.menuSort0_9);
                mItem.setChecked(true);
                break;
            case R.id.menuSort9_0:
                mItem = menu.findItem(R.id.menuSort9_0);
                mItem.setChecked(true);
                break;
        }
        return true;
    }

    // Метод для сортування завдань
    private void sortBy(ArrayList<Task> list, Comparator<Task> comparator) {
        Collections.sort(list, comparator);
        mAdapterTasks.notifyDataSetChanged();
        new Thread(this).start();
    }

    //Запускаємо TaskSetting
    private void launchSettingActivity() {
        Intent intent = new Intent(this, TaskSetting.class);
        startActivityForResult(intent, REQUEST_CODE_COLOR_TASK);
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.menuSortA_Z:
                mItemSelectedId = id;
                item.setChecked(true);
                sortBy(mArrayListTasks, new Task.CompareByTaskNameA_Z());
                break;
            case R.id.menuSortZ_A:
                mItemSelectedId = id;
                item.setChecked(true);
                sortBy(mArrayListTasks, new Task.CompareByTaskNameZ_A());
                break;
            case R.id.menuSort0_9:
                mItemSelectedId = id;
                item.setChecked(true);
                sortBy(mArrayListTasks, new Task.CompareByTaskTime0_9());
                break;
            case R.id.menuSort9_0:
                mItemSelectedId = id;
                item.setChecked(true);
                sortBy(mArrayListTasks, new Task.CompareByTaskTime9_0());
                break;
            //Перехід до меню налаштувань
            case R.id.item_settings:
                launchSettingActivity();
                break;
            //Перехід до меню додавання завдань
            case R.id.item_add:
                launchAddTaskActivity();
                break;
            //будує рандомний набір завдань
            case R.id.item_random:
                setRandomTasks();
                break;
            //видалити всі завдання
            case R.id.item_clean_list:
                mShowDialog = true;
                mDialog.show();
                break;
            //виходить з додатку
            case R.id.item_exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Створює рандомні завдання
    private void setRandomTasks() {
        int count = 0;
        for (int index = 0; index <= count; index++) {
           /* додаємо перше завдання для визначення параметра висоти Item та ListView.
            Далі змінюємо count і створюмо решту завдань*/
            mArrayListTasks.add(new Task(RandomString.getRandom(10),
                    RandomString.getRandom(30), null, null, mColorCreate));
            mAdapterTasks.notifyDataSetChanged();
            if (count > 0) continue;
            //обраховуємо необхідну кількість завдань (в 3 рази більшу ніж вміщаєтьсяна дисплею)
            count = (mViewTaskList.getHeight() / calculateListViewHeight(mViewTaskList)) * 3;
            Log.d("MLog", "довжина item " + calculateListViewHeight(mViewTaskList) +
                    "\nдовжина listView " + mViewTaskList.getHeight()
                    + "\nРізниця " + count);
        }
        new Thread(this).start();
    }

    //Обраховуємо довжину Item ListView
    private int calculateListViewHeight(ListView list) {
        int height = 0;
        //Отримуємо item ListView та визначаємо висоту, що вміщається в видиму область дисплея
        View childView = list.getAdapter().getView(0, null, list);
        childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        height += childView.getMeasuredHeight();

        //Висота розділювача
        height += list.getDividerHeight();

        return height;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //Створюємо тимчасові змінні
            String taskName;
            String taskDescription;
            String taskBegin;
            String taskFinish;
            int color;
            //Виконується після додавання нового завдання
            switch (requestCode) {
                case REQUEST_CODE_ADD_TASK:
                    taskName = data.getStringExtra(TASK_KEY);
                    taskDescription = data.getStringExtra(DESCRIPTION_KEY);
                    mArrayListTasks.add(new Task(taskName, taskDescription, null, null, mColorCreate));
                    mAdapterTasks.notifyDataSetChanged();
                    break;
                //Виконується після коригування завдання
                case REQUEST_CODE_SET_TASK:
                    taskName = data.getStringExtra(TASK_KEY);
                    taskDescription = data.getStringExtra(DESCRIPTION_KEY);
                    int mPosition = data.getIntExtra(ITEM_LIST_VIEW_POSITION_KEY, 456);
                    taskBegin = mArrayListTasks.get(mPosition).getmTaskBegin();
                    taskFinish = mArrayListTasks.get(mPosition).getmTaskFinish();
                    color = mArrayListTasks.get(mPosition).getmTaskColor();
                    mArrayListTasks.set(mPosition, new Task(taskName, taskDescription, taskBegin,
                            taskFinish, color));
                    mAdapterTasks.notifyDataSetChanged();
                    break;
                //Виконується при зміні кольору в налаштуваннях
                case REQUEST_CODE_COLOR_TASK:

                    mColorCreate = data.getIntExtra(COLOR_TASK_CREATE, 0);
                    mColorBegin = data.getIntExtra(COLOR_TASK_BEGIN, 0);
                    mColorFinish = data.getIntExtra(COLOR_TASK_FINISH, 0);

                    for (int index = 0; index < mArrayListTasks.size(); index++) {
                        taskName = mArrayListTasks.get(index).getmTaskName();
                        taskDescription = mArrayListTasks.get(index).getmDescription();
                        taskBegin = mArrayListTasks.get(index).getmTaskBegin();
                        taskFinish = mArrayListTasks.get(index).getmTaskFinish();

                        if (taskBegin == null) {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, null, null, mColorCreate));
                        } else if (taskFinish == null) {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, taskBegin, null, mColorBegin));
                        } else {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, taskBegin, taskFinish, mColorFinish));
                        }
                    }
                    mAdapterTasks.notifyDataSetChanged();
                    break;
            }
            new Thread(TaskActivity.this).start();
        }
    }

    @Override
    public void onBackPressed() {
        if (mSnackBarExit != null && mSnackBarExit.isShown()) {
            finish();
        } else {
            mSnackBarExit = runSnackBar(mLayoutMain, "Press \"Back\" to exit", Snackbar.LENGTH_LONG);
            mSnackBarExit.show();
        }
    }

    @Override
    public void run() {
        //Зберігаємо дані
        TaskSaveLoad taskSaveLoad = new TaskSaveLoad(this, FILE_NAME);
        taskSaveLoad.saveTasks(mArrayListTasks);
        taskSaveLoad.saveItemId(mItemSelectedId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(TaskActivity.this).start();
    }
}
