package com.example.vitaliy.taskmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.adapter.CustomAdapter;
import com.example.vitaliy.taskmanager.task.Task;
import com.example.vitaliy.taskmanager.utils.RandomString;
import com.example.vitaliy.taskmanager.utils.TaskSaveLoad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskActivity extends Activity implements Runnable {

    private ArrayList<Task> mArrayListTasks;
    private CustomAdapter mAdapterTasks;
    private LinearLayout mLayoutMain;
    private ListView mViewTaskList;

    private final int REQUEST_CODE_ADD = 123;
    private final int REQUEST_CODE_SET = 456;
    private final String mFileName = "Tasks date";

    public final static String TASK_KEY = "Task";
    public final static String DESCRIPTION_KEY = "Description";
    public final static String ITEM_LIST_VIEW_POSITION_KEY = "Position";
    public final static String AL_OF_TASK_KEY = "ArrayList with Tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mViewTaskList = (ListView) findViewById(R.id.listView_task);
        mLayoutMain = (LinearLayout) findViewById(R.id.layout_main_1activity);

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        if (savedInstanceState != null) {
            mArrayListTasks = savedInstanceState.getParcelableArrayList(AL_OF_TASK_KEY);
        } else {
            mArrayListTasks = new ArrayList<>();
        }

        /*Створюємо адаптер для ListView та прикріплюємо його. Встановлюємо автоматичне оновлення
        ListView при добавленні нового об6єкта Task в ArrayList*/
        mAdapterTasks = new CustomAdapter(this, mArrayListTasks, R.layout.layout_item);
        mViewTaskList.setAdapter(mAdapterTasks);
        mAdapterTasks.setNotifyOnChange(true);
        //Реєструэмо на ListView контекстне миню
        registerForContextMenu(mViewTaskList);


        final Button buttonAdd = (Button) findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
            }
        });

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
                                            mTempDescription, null, null, getResources().getColor(R.color.colorGreen)));
                                    mAdapterTasks.notifyDataSetChanged();
                                }
                            }).show();
                }
                new Thread(TaskActivity.this).start();
            }
        });

        mViewTaskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String tempTaskName = mArrayListTasks.get(position).getmTaskName();
                String tempDescription = mArrayListTasks.get(position).getmDescription();

                Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
                intent.putExtra(TASK_KEY, tempTaskName);
                intent.putExtra(DESCRIPTION_KEY, tempDescription);
                intent.putExtra(ITEM_LIST_VIEW_POSITION_KEY, position);
                startActivityForResult(intent, REQUEST_CODE_SET);
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);

                return true;
            }
        });
    }

    //Виводить на view SnackBar
    private Snackbar runSnackBar(View view, String text, int duration) {

        Snackbar snackbar = Snackbar.make(view, text, duration);
        //змінюємо колір "Yes" в SnackBar
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        //змінюємо колір текстового сповіщення в SnackBar
        View snackBarView = snackbar.getView();
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorLGHTYellow));
        return snackbar;
    }

    //Обробляє час закінчення роботи завдання
    private void finishTask(int position) {
        Date mFinishDate = new Date();
        long mFinishTime = mFinishDate.getTime();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String mTempTask = mArrayListTasks.get(position).getmTaskName();
        String mTempDescription = mArrayListTasks.get(position).getmDescription();
        String mTempTaskBegin = mArrayListTasks.get(position).getmTaskBegin();
        String mTempTaskFinish = "-   " + getDateAndTime(mFinishDate) + "  "
                + getDifferenceTime(mFinishTime, mArrayListTasks.get(position).getmTaskBegin());
        mArrayListTasks.set(position, new Task(mTempTask, mTempDescription, mTempTaskBegin, mTempTaskFinish,
                getResources().getColor(R.color.colorRed)));
        mAdapterTasks.notifyDataSetChanged();
    }

    //Обробляє час початку роботи завдання
    private void beginTask(int position) {
        Date mStartDate = new Date();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String mTempTask = mArrayListTasks.get(position).getmTaskName();
        String mTempDescription = mArrayListTasks.get(position).getmDescription();
        mArrayListTasks.set(position, new Task(mTempTask, mTempDescription
                , getDateAndTime(mStartDate), null, getResources().getColor(R.color.colorLGHTYellow)));
        mAdapterTasks.notifyDataSetChanged();
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

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView_task) {
            String[] items = getResources().getStringArray(R.array.context_menu);
            for (int index = 0; index < items.length; index++) {
                menu.add(ContextMenu.NONE, index, index, items[index]);
            }
        }
    }*/

   /* @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = contextMenuInfo.position;
        switch (item.getItemId()) {
            //Запускає друге актівіті для редагування завдання
            case 0:
                String tempTaskName = mArrayListTasks.get(position).getmTaskName();
                String tempDescription = mArrayListTasks.get(position).getmDescription();

                Intent intent = new Intent(TaskActivity.this, TaskAddActivity.class);
                intent.putExtra(TASK_KEY, tempTaskName);
                intent.putExtra(DESCRIPTION_KEY, tempDescription);
                intent.putExtra(ITEM_LIST_VIEW_POSITION_KEY, position);
                startActivityForResult(intent, REQUEST_CODE_SET);
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                break;
            //Видаляє завдання
            case 1:
                mArrayListTasks.remove(position);
                mAdapterTasks.notifyDataSetChanged();
                new Thread(TaskActivity.this).start();
                break;
        }
        return super.onContextItemSelected(item);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //будує рандомний набір завдань
            case R.id.item_random:
                setRandomTasks();
                break;
            //завантажує збережені завдання
            case R.id.item_load_data:
                mArrayListTasks.clear();
                mArrayListTasks = new TaskSaveLoad(this, mFileName).loadTasks(mArrayListTasks);
                mAdapterTasks.notifyDataSetChanged();
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
            mArrayListTasks.add(new Task(RandomString.randomName(),
                    RandomString.randomDescription(), null, null, getResources().getColor(R.color.colorGreen)));
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
            String task;
            String description;
            //Виконується після додавання нового завдання
            switch (requestCode) {
                case REQUEST_CODE_ADD:
                    task = data.getStringExtra(TASK_KEY);
                    description = data.getStringExtra(DESCRIPTION_KEY);
                    mArrayListTasks.add(new Task(task, description, null, null, getResources().getColor(R.color.colorGreen)));
                    mAdapterTasks.notifyDataSetChanged();
                    break;
                //Виконується після коригування завдання
                case REQUEST_CODE_SET:
                    task = data.getStringExtra(TASK_KEY);
                    description = data.getStringExtra(DESCRIPTION_KEY);
                    int mPosition = data.getIntExtra(ITEM_LIST_VIEW_POSITION_KEY, 456);
                    String tempTaskBegin = mArrayListTasks.get(mPosition).getmTaskBegin();
                    String tempTaskFinish = mArrayListTasks.get(mPosition).getmTaskFinish();
                    int mColor = mArrayListTasks.get(mPosition).getmTaskColor();
                    mArrayListTasks.set(mPosition, new Task(task, description, tempTaskBegin,
                            tempTaskFinish, mColor));
                    mAdapterTasks.notifyDataSetChanged();
                    break;
            }
            new Thread(TaskActivity.this).start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //поміщаємо дані для збереження після знищення актівіті
        outState.putParcelableArrayList(AL_OF_TASK_KEY, mArrayListTasks);
    }

    @Override
    public void run() {
        //Зберігаємо дані
        new TaskSaveLoad(this, mFileName).saveTasks(mArrayListTasks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(TaskActivity.this).start();
    }
}
