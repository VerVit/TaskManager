package com.example.vitaliy.taskmanager.adapter;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.vitaliy.taskmanager.R;
import com.example.vitaliy.taskmanager.activity.TaskActivity;
import com.example.vitaliy.taskmanager.activity.TaskAddActivity;
import com.example.vitaliy.taskmanager.task.Task;
import com.example.vitaliy.taskmanager.utils.Realm.RealmController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vitaliy on 27.05.2016.
 */
public class CustomAdapter extends RecyclerSwipeAdapter<CustomAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Task> mArrayTaskList;
    private final int NOTIFICATION_ID = 1;

    public CustomAdapter(Context mContext, ArrayList<Task> mArrayTaskList) {
        this.mContext = mContext;
        this.mArrayTaskList = mArrayTaskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(mContext).inflate(R.layout.swipe_layout, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("MLog", "mPosition =" + position);

        holder.mTextViewTaskName.setText(mArrayTaskList.get(position).getmTaskName());
        holder.mTextViewDescription.setText(mArrayTaskList.get(position).getmDescription());
        holder.mTextViewTaskBegin.setText(mArrayTaskList.get(position).getmTaskBegin());
        holder.mTextViewTaskFinish.setText(mArrayTaskList.get(position).getmTaskFinish());
        holder.mItemLayout.setBackgroundColor(mArrayTaskList.get(position).getmTaskColor());

        // лістенер на кнопку старт
        if (holder.mTextViewTaskBegin.getText().toString().equals("")) {
            holder.mBtnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.button_start) {
                        beginTask(position);
                    }
                }
            });
        }
        // лістенер на кнопку фініш
        if (!(holder.mTextViewTaskBegin.getText().toString().equals(""))
                && holder.mTextViewTaskFinish.getText().toString().equals("")) {
            holder.mBtnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.button_finish) {
                        finishTask(position);
                    }
                }
            });
        }
        // Свайп меню налаштування
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        // Витягуємо з ліва свайп меню
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        // Витягуємо з права свайп меню
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));
        //лістенер на кнопку Edit
        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.imageView_edit) {
                    String tempTaskName = mArrayTaskList.get(position).getmTaskName();
                    String tempDescription = mArrayTaskList.get(position).getmDescription();

                    Intent intent = new Intent(mContext, TaskAddActivity.class);
                    intent.putExtra(TaskActivity.TASK_KEY, tempTaskName);
                    intent.putExtra(TaskActivity.DESCRIPTION_KEY, tempDescription);
                    intent.putExtra(TaskActivity.ITEM_LIST_VIEW_POSITION_KEY, position);
                    ((AppCompatActivity) mContext).startActivityForResult(intent, TaskActivity.getREQUEST_CODE_SET_TASK());
                    //Анімація переходів між актівіті
                    ((AppCompatActivity) mContext).overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                }
            }
        });
        //лістенер на кнопку Delete
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.imageView_delete) {
                    mArrayTaskList.remove(position);
                    notifyItemRemoved(position);
                    //Зберігаємо дані в базу даних
                    RealmController.with((Activity) mContext).deleteTaskByPosition(position);
                }
            }
        });
        //лістенер на кнопку Reset
        holder.mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textView_resetTask) {
                    if (!(holder.mTextViewTaskBegin.getText().toString().equals(""))
                            && holder.mTextViewTaskFinish.getText().toString().equals("")) {
                        //Записали тимчасові дані
                        String taskName = mArrayTaskList.get(position).getmTaskName();
                        String taskDescription = mArrayTaskList.get(position).getmDescription();
                        String id = mArrayTaskList.get(position).getId();
                        //перезаписали об’єкт TASK
                        Task task = new Task(taskName, taskDescription, null, null, TaskActivity.getmColorCreate(),id);
                        mArrayTaskList.set(position, task);
                        notifyItemChanged(position);
                        //Зберігаємо дані в базу даних
                        RealmController.with((Activity) mContext).updateTask(task);
                    }
                }
            }
        });
        //лістенер на кнопку ResetEnd
        holder.mResetEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textView_resetTaskEnd) {
                    if (!(holder.mTextViewTaskBegin.getText().toString().equals(""))
                            && !(holder.mTextViewTaskFinish.getText().toString().equals(""))) {
                        //Записали тимчасові дані
                        String taskName = mArrayTaskList.get(position).getmTaskName();
                        String taskDescription = mArrayTaskList.get(position).getmDescription();
                        String taskBegin = mArrayTaskList.get(position).getmTaskBegin();
                        String id = mArrayTaskList.get(position).getId();
                        //перезаписали об’єкт TASK
                        Task task = new Task(taskName, taskDescription, taskBegin, null, TaskActivity.getmColorBegin(),id);
                        mArrayTaskList.set(position, task);
                        notifyItemChanged(position);
                        //Зберігаємо дані в базу даних
                        RealmController.with((Activity) mContext).updateTask(task);
                        //Зберігаємо метод для автозавершення завдання
                        autoFinishedTaskAfterReset(getDateAndTime(new Date()), taskBegin, taskName, position);
                    }
                }
            }
        });
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    @Override
    public int getItemCount() {
        return mArrayTaskList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Wrapper
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTaskName;
        TextView mTextViewDescription;
        TextView mTextViewTaskBegin;
        TextView mTextViewTaskFinish;
        LinearLayout mItemLayout;
        Button mBtnStart;
        Button mBtnFinish;
        //Swipe menu
        SwipeLayout swipeLayout;
        ImageView mDelete;
        ImageView mEdit;
        TextView mReset;
        TextView mResetEnd;

        ViewHolder(View itemView) {
            super(itemView);
            mTextViewTaskName = (TextView) itemView.findViewById(R.id.textView_item_task);
            mTextViewDescription = (TextView) itemView.findViewById(R.id.textView_item_description);
            mTextViewTaskBegin = (TextView) itemView.findViewById(R.id.textView_dateTime_start);
            mTextViewTaskFinish = (TextView) itemView.findViewById(R.id.textView_dateTime_finish);
            mItemLayout = (LinearLayout) itemView.findViewById(R.id.iclude_item_layout);
            mBtnStart = (Button) itemView.findViewById(R.id.button_start);
            mBtnFinish = (Button) itemView.findViewById(R.id.button_finish);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
            mDelete = (ImageView) itemView.findViewById(R.id.imageView_delete);
            mEdit = (ImageView) itemView.findViewById(R.id.imageView_edit);
            mReset = (TextView) itemView.findViewById(R.id.textView_resetTask);
            mResetEnd = (TextView) itemView.findViewById(R.id.textView_resetTaskEnd);
        }
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
        Log.d("MLog", "Start time = " + (mStarTime / 1000) + "сек, FinishTime = " + (mFinishTime / 1000) + "сек\nDifference = " +
                (mDifferenceTime / 1000));
        //Розбиваємо міліскунди на хвилини і години
        long mDifHours = (mDifferenceTime / 1000) / 3600;
        long mDifMinutes = ((mDifferenceTime / 1000) % 3600) / 60;

        Log.d("MLog", "DifHours = " + mDifHours + ", DifMinutes " + mDifMinutes);
        return String.format("%02d:%02d", mDifHours, mDifMinutes);
    }

    //Обробляє час закінчення роботи завдання
    private void finishTask(int position) {
        Date mFinishDate = new Date();
        long mFinishTime = mFinishDate.getTime();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String tempTask = mArrayTaskList.get(position).getmTaskName();
        String tempDescription = mArrayTaskList.get(position).getmDescription();
        String tempTaskBegin = mArrayTaskList.get(position).getmTaskBegin();
        String id = mArrayTaskList.get(position).getId();
        String tempTaskFinish = "-   " + getDateAndTime(mFinishDate) + "  "
                + getDifferenceTime(mFinishTime, mArrayTaskList.get(position).getmTaskBegin());
        Task task = new Task(tempTask, tempDescription, tempTaskBegin, tempTaskFinish,
                TaskActivity.getmColorFinish(),id);
        mArrayTaskList.set(position, task);
        notifyItemChanged(position);
        //Зберігаємо дані в базу даних
        RealmController.with((Activity) mContext).updateTask(task);
    }

    private void finishTaskAfterReset(int position, String oldTextTaskBegin) {
        Date mFinishDate = new Date();
        long mFinishTime = mFinishDate.getTime();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String tempTask = mArrayTaskList.get(position).getmTaskName();
        String tempDescription = mArrayTaskList.get(position).getmDescription();
        String tempTaskBegin = mArrayTaskList.get(position).getmTaskBegin();
        String id = mArrayTaskList.get(position).getId();
        String tempTaskFinish = "-   " + getDateAndTime(mFinishDate) + "  "
                + getDifferenceTime(mFinishTime, oldTextTaskBegin);
        Task task = new Task(tempTask, tempDescription, tempTaskBegin, tempTaskFinish,
                TaskActivity.getmColorFinish(),id);
        mArrayTaskList.set(position, task);
        notifyItemChanged(position);
        //Зберігаємо дані в базу даних
        RealmController.with((Activity) mContext).updateTask(task);
    }

    private void beginTask(int position) {
        Date mStartDate = new Date();
        //перезаписуємо об’єкт Task в mArrayListTasks додаючи часові дані та обновляємо адаптер
        String tempTask = mArrayTaskList.get(position).getmTaskName();
        String tempDescription = mArrayTaskList.get(position).getmDescription();
        String id = mArrayTaskList.get(position).getId();
        Task task = new Task(tempTask, tempDescription
                , getDateAndTime(mStartDate), null, TaskActivity.getmColorBegin(),id);
        mArrayTaskList.set(position, task);
        notifyItemChanged(position);
        //Зберігаємо дані в базу даних
        RealmController.with((Activity) mContext).updateTask(task);
       //запускаємо метод дял автозавершення завдання
        autoFinishedTask(getDateAndTime(mStartDate), tempTask, position);
    }
    //Отримуємо строку з датою та часом
    private String getDateAndTime(Date mCurrentDate) {
        Log.d("MLog", "mCurrentDate = " + mCurrentDate);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(mCurrentDate);
    }
    // Отримуємо час в мілісекундах закінчення виконная завдання
    private int getTaskFinished(String mTextStartTask) {
        long starTime = 0;
        Date mFinishDate = new Date();
        long finishTime = mFinishDate.getTime();
        // Отримуємо час в мілісекундах закінчення виконная завдання
        try {
            starTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).parse(mTextStartTask).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // різниця часу закінчення і початку в мілісекундах
        long differenceTime = finishTime - starTime;
        return (int) differenceTime;
    }

    private void autoFinishedTask(final String mTextStartTask, final String taskName, final int position) {

        final Handler handler = new Handler();
        Log.d("MLog", "mTextStartTask = " + mTextStartTask + "\nFinish auto time = " + TaskActivity.getmAutoFinish() * 60000 +
                "\nFinish time = " + getTaskFinished(mTextStartTask));
        handler.post(new Runnable() {
                         @Override
                         public void run() {
                             //Провіряємо умову чи завдання не завершене користувачем
                             if (mArrayTaskList.get(position).getmTaskFinish() == null) {
                                 //Провіряємо умову чи не вийшов максимальний час на виконання завдання
                                 if (TaskActivity.getmAutoFinish() * 60000 <= getTaskFinished(mTextStartTask)) {
                                     finishTask(position);
                                     getNotification(taskName);
                                 } else handler.postDelayed(this, 60000);
                             }
                         }
                     }
        );
    }

    private void autoFinishedTaskAfterReset(final String textStartTask, final String oldTextStartTask, final String taskName, final int position) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Провіряємо умову чи завдання не завершене користувачем
                if (mArrayTaskList.get(position).getmTaskFinish() == null) {
                    //Провіряємо умову чи не вийшов максимальний час на виконання завдання
                    if ((TaskActivity.getmAutoFinish() * 60000) <= getTaskFinished(textStartTask)) {
                        finishTaskAfterReset(position, oldTextStartTask);
                        getNotification(taskName);
                    } else handler.postDelayed(this, 60000);
                }
            }
        });
    }
    // Створює локальне сповіщення
    private void getNotification(String taskName) {

        Intent intent = new Intent(mContext, TaskActivity.class);
        intent.setAction("android.intent.action.TaskActivity");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.startActivity(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle("Task automatically finished")
                .setContentText("Task " + taskName + " is finished.\nTime on task ended")
                .setSmallIcon(R.drawable.ic_default)
                .setAutoCancel(true)
                .setColor(mContext.getResources().getColor(R.color.colorPrimary));
        builder.setContentIntent(pendingIntent);
        //Призначаємо звуковий сигнал сповіщення
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        //Менеджер сповіщення
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }
}
