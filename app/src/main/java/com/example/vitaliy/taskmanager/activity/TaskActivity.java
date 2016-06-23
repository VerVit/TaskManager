package com.example.vitaliy.taskmanager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.adapter.CustomAdapter;
import com.example.vitaliy.taskmanager.task.Task;
import com.example.vitaliy.taskmanager.utils.RandomString;
import com.example.vitaliy.taskmanager.utils.Realm.RealmController;
import com.example.vitaliy.taskmanager.utils.SharedPreference.TaskSaveLoad;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import io.realm.RealmResults;

public class TaskActivity extends AppCompatActivity implements Runnable {

    private ArrayList<Task> mArrayListTasks;
    private CustomAdapter mAdapterTasks;
    private LinearLayout mLayoutMain;
    private RecyclerView mViewTaskList;
    private Snackbar mSnackBarExit;
    private LinearLayoutManager mLayoutManager;
    private static int mColorCreate;
    private static int mColorBegin;
    private static int mColorFinish;
    private static int mAutoFinish;

    private final static int REQUEST_CODE_ADD_TASK = 123;
    private final static int REQUEST_CODE_SET_TASK = 456;
    private final static int REQUEST_CODE_COLOR_TASK = 789;

    final static String FILE_NAME = "Tasks date";
    public final static String TASK_KEY = "Task";
    public final static String DESCRIPTION_KEY = "Description";
    public final static String ITEM_LIST_VIEW_POSITION_KEY = "Position";
    final static String COLOR_TASK_CREATE = "Task create";
    final static String COLOR_TASK_BEGIN = "Task begin";
    final static String COLOR_TASK_FINISH = "Task finish";
    final static String TASK_AUTO_FINISH = "Task auto finish";

    private int mItemSelectedId = -1;
    private static boolean mShowDialog;
    private AlertDialog.Builder mDialog;


    public static int getmAutoFinish() {
        return mAutoFinish;
    }

    public static int getREQUEST_CODE_SET_TASK() {
        return REQUEST_CODE_SET_TASK;
    }

    public static int getmColorCreate() {
        return mColorCreate;
    }

    public static int getmColorBegin() {
        return mColorBegin;
    }

    public static int getmColorFinish() {
        return mColorFinish;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewTaskList = (RecyclerView) findViewById(R.id.listView_task);
        mLayoutMain = (LinearLayout) findViewById(R.id.layout_main_1activity);

        //Відновлюємо втрачені дані після знищення актівіті(зміни орієнтації екрану)
        savedInstanceState();
        //Свторюємо FAB
        createFAB();

        //Ініціалізуємо AlertDialog
        if (mShowDialog) {
            createDialogToDeleteTask();
            mDialog.show();
        } else createDialogToDeleteTask();
    }

    private void createDialogToDeleteTask() {
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
            fab.attachToRecyclerView(mViewTaskList);
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
        /*Завантажуємо дані щодо автозавершення завдань та Id вибраного пункту сортування завдань
        кольори тасків. Якщо завантажене значення null то записуємо дефолтне значення*/
        TaskSaveLoad taskSaveLoad = new TaskSaveLoad(this, FILE_NAME);
        if (taskSaveLoad.loadItem_Id() != 0) {
            mItemSelectedId = taskSaveLoad.loadItem_Id();
        }
        if (taskSaveLoad.loadAutoTimeFinish() != 0) {
            mAutoFinish = taskSaveLoad.loadAutoTimeFinish();
        } else mAutoFinish = 10;
        if (taskSaveLoad.loadColorBegin() != 0) {
            mColorBegin = taskSaveLoad.loadColorBegin();
        } else {
            mColorBegin = getColorFromID(R.color.colorLGHTYellow);
        }
        if (taskSaveLoad.loadColorCreate() != 0) {
            mColorCreate = taskSaveLoad.loadColorCreate();
        } else {
            mColorCreate = getColorFromID(R.color.colorGreen);
        }
        if (taskSaveLoad.loadColorFinish() != 0) {
            mColorFinish = taskSaveLoad.loadColorFinish();
        } else {
            mColorFinish = getColorFromID(R.color.colorRed);
        }
        //Завантажуємо з Realm дані завдань та записуємо до ArrayList
        RealmResults<Task> realmResults = RealmController.with(this).getTasks();
        for (Task task : realmResults) {
            if (task != null) {
                mArrayListTasks.add(task);
            }
        }
        /*Створюємо адаптер для ListView та прикріплюємо його. Встановлюємо автоматичне оновлення
        ListView при добавленні нового об6єкта до адаптеру*/
        mAdapterTasks = new CustomAdapter(this, mArrayListTasks);
        mLayoutManager = new LinearLayoutManager(this);
        mViewTaskList.setLayoutManager(mLayoutManager);

        mViewTaskList.setAdapter(mAdapterTasks);
        // Може витягуватись декілька свайп меню
        mAdapterTasks.setMode(Attributes.Mode.Multiple);
        mAdapterTasks.notifyDataSetChanged();

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

    //Отримуємо колір з ресурсів
    private int getColorFromID(int colorId) {
        return getResources().getColor(colorId);
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

    //Запускаємо актівіті TaskSetting
    private void launchSettingActivity() {
        Intent intent = new Intent(this, TaskSetting.class);
        startActivityForResult(intent, REQUEST_CODE_COLOR_TASK);
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //пункти меню сортування
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
                RealmController.with(this).clearAll();
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
            Task task = new Task(RandomString.getRandom(10),
                    RandomString.getRandom(30), null, null, mColorCreate, UUID.randomUUID().toString());
            mArrayListTasks.add(task);
            mAdapterTasks.notifyDataSetChanged();
            //Зберігаємо дані до бази даних
            RealmController.with(this).addTask(task);
            if (count > 0) continue;
            //обраховуємо необхідну кількість завдань (в 3 рази більшу ніж вміщаєтьсяна дисплею)
            count = (mViewTaskList.getHeight() / calculateListViewHeight()) * 3;
            Log.d("MLog", "довжина item " + calculateListViewHeight() +
                    "\nдовжина listView " + mViewTaskList.getHeight()
                    + "\nРізниця " + count);
        }
        new Thread(this).start();
    }

    //Обраховуємо довжину Item ListView
    private int calculateListViewHeight() {
        int height = 0;
        //Отримуємо item ListView та визначаємо висоту, що вміщається в видиму область дисплея

        // View childView = mLayoutManager.findViewByPosition(0);
        View childView = mViewTaskList.getChildAt(0);
        Log.d("MLog", "childView = " + childView);
        childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        height += childView.getMeasuredHeight();
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
            String id;
            int color;
            //Виконується після додавання нового завдання
            switch (requestCode) {
                case REQUEST_CODE_ADD_TASK:
                    taskName = data.getStringExtra(TASK_KEY);
                    taskDescription = data.getStringExtra(DESCRIPTION_KEY);
                    Task tempTask = new Task(taskName, taskDescription, null, null, mColorCreate,UUID.randomUUID().toString());
                    mArrayListTasks.add(tempTask);
                    mAdapterTasks.notifyDataSetChanged();
                    //Зберігаємо дані до бази даних
                    RealmController.with(this).addTask(tempTask);
                    break;
                //Виконується після коригування завдання
                case REQUEST_CODE_SET_TASK:
                    taskName = data.getStringExtra(TASK_KEY);
                    taskDescription = data.getStringExtra(DESCRIPTION_KEY);
                    int mPosition = data.getIntExtra(ITEM_LIST_VIEW_POSITION_KEY, 456);
                    taskBegin = mArrayListTasks.get(mPosition).getmTaskBegin();
                    taskFinish = mArrayListTasks.get(mPosition).getmTaskFinish();
                    color = mArrayListTasks.get(mPosition).getmTaskColor();
                    id = mArrayListTasks.get(mPosition).getId();
                    Task tempTask2 = new Task(taskName, taskDescription, taskBegin, taskFinish, color,id);
                    mArrayListTasks.set(mPosition, tempTask2);
                    mAdapterTasks.notifyItemChanged(mPosition);
                    //Зберігаємо дані до бази даних
                    RealmController.with(this).updateTask(tempTask2);
                    break;
                //Виконується при зміні кольору в налаштуваннях
                case REQUEST_CODE_COLOR_TASK:

                    mColorCreate = data.getIntExtra(COLOR_TASK_CREATE, 0);
                    mColorBegin = data.getIntExtra(COLOR_TASK_BEGIN, 0);
                    mColorFinish = data.getIntExtra(COLOR_TASK_FINISH, 0);
                    mAutoFinish = data.getIntExtra(TASK_AUTO_FINISH, 0);

                    for (int index = 0; index < mArrayListTasks.size(); index++) {
                        taskName = mArrayListTasks.get(index).getmTaskName();
                        taskDescription = mArrayListTasks.get(index).getmDescription();
                        taskBegin = mArrayListTasks.get(index).getmTaskBegin();
                        taskFinish = mArrayListTasks.get(index).getmTaskFinish();
                        id=mArrayListTasks.get(index).getId();
                        if (taskBegin == null) {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, null, null, mColorCreate,id));
                        } else if (taskFinish == null) {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, taskBegin, null, mColorBegin,id));
                        } else {
                            mArrayListTasks.set(index, new Task(taskName, taskDescription, taskBegin, taskFinish, mColorFinish,id));
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
        //Зберігаємо в Shared Preference Id меню сортування завдань та кольори
        TaskSaveLoad taskSaveLoad = new TaskSaveLoad(this, FILE_NAME);
        taskSaveLoad.saveItemId(mItemSelectedId);
        taskSaveLoad.saveColor(mColorCreate, mColorBegin, mColorFinish);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(TaskActivity.this).start();
    }
}
