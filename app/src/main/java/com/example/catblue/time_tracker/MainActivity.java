package com.example.catblue.time_tracker;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ActivityDatabase db;
    UserActivityDao dao;
    TextView activityLogTextView;
    AutoCompleteTextView activityNameEditText;
    Boolean currentActivityExists;
    Button activityButton;
    ArrayAdapter<String> adapter;
    ArrayList<String> userActivityNames;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:H:m:s");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityLogTextView = findViewById(R.id.activityLogTextView);
        activityNameEditText = findViewById(R.id.activityNameEditText);
        activityButton = findViewById(R.id.activityButton);

        activityLogTextView.setMovementMethod(new ScrollingMovementMethod());

        db = Room.databaseBuilder(getApplicationContext(), ActivityDatabase.class, "test-db").allowMainThreadQueries().build();
        dao = db.userActivityDao();
        activityButton.setEnabled(false);

        userActivityNames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, userActivityNames);
        activityNameEditText.setAdapter(adapter);
        activityNameEditText.setThreshold(1);

        new doDatabaseTask(new TaskHolder() {
            @Override
            public void task() {
                String entireLog = "";
                List<ActivityLog> activityLogList = dao.getActivityLog();
                for (int i=0;i<activityLogList.size();i++) {
                    entireLog += activityLogList.get(i).getText();
                }

                currentActivityExists = (dao.getCurrentActivity() != null);

                userActivityNames = (ArrayList<String>) dao.getAllUserActivityNames();
                adapter.addAll(userActivityNames);

                final String eL = entireLog;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityLogTextView.setText(eL);
                        updateActivityButton();
                        activityButton.setEnabled(true);
                    }
                });
            }
        }).execute();

    }

    public void updateAdapter() {

    }

    public void setCurrentActivity() {
        String name = String.valueOf(activityNameEditText.getText());
        CurrentActivity currentActivity = new CurrentActivity();
        currentActivity.setActivityName(String.valueOf(name));
        currentActivity.setStartTime(System.currentTimeMillis());
        dao.insertCurrentActivity(currentActivity);


        if (!userActivityNames.contains(currentActivity.getActivityName())) {
            userActivityNames.add(name);
            adapter.add(name);

            UserActivity userActivity = new UserActivity();
            userActivity.setName(name);
            dao.insertUserActivity(userActivity);
        }
    }

    public String millisToTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    public void updateActivityButton() {
        if (currentActivityExists) {
            activityButton.setText("Stop Activity");
        }
        else {
            activityButton.setText("Start Activity");
        }
    }

    public void doActivity(View view) throws ExecutionException, InterruptedException {
        if (currentActivityExists) {
            currentActivityExists = false;
            activityButton.setEnabled(false);
            updateActivityButton();
            new doDatabaseTask(new TaskHolder() {
                @Override
                public void task() {
                    long startTime = dao.getCurrentActivity().getStartTime();
                    long totalTime = (System.currentTimeMillis() - startTime);
                    CurrentActivity currentActivity = dao.getCurrentActivity();
                    Calendar calendarStart = Calendar.getInstance();
                    calendarStart.setTimeInMillis(currentActivity.getStartTime());

                    Calendar calendarEnd = Calendar.getInstance();

                    ActivityLog activityLog = new ActivityLog();
                    activityLog.setText(
                            currentActivity.getActivityName()
                            + " took you "
                            + millisToTime(totalTime)
                            + "\n"
                            + "(started at "
                            + simpleDateFormat.format(calendarStart.getTime())
                            + " and ended at "
                            + simpleDateFormat.format(calendarEnd.getTime())
                            + ")\n\n");

                    dao.insertActivityLog(activityLog);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            activityLogTextView.setText(activityLogTextView.getText() + activityLog.getText());
                            activityButton.setEnabled(true);
                        }
                    });
                    dao.deleteCurrentActivity();
                }
            }).execute();
        }

        else {
            currentActivityExists = true;
            updateActivityButton();
            new doDatabaseTask(new TaskHolder() {
                @Override
                public void task() {
                    setCurrentActivity();
                }
            }).execute().get();
        }
    }

    public void deleteActivityLog(View view) {
        activityLogTextView.setText("");
        new doDatabaseTask(new TaskHolder() {
            @Override
            public void task() {
                dao.deleterActivityLog();
            }
        }).execute();
    }
}

class doDatabaseTask extends AsyncTask<Void, Void, Void> {
    private TaskHolder taskHolder;

    public doDatabaseTask(TaskHolder taskHolder) {
       this.taskHolder = taskHolder;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        taskHolder.task();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        taskHolder.postTask();
    }
}

interface TaskHolder {
    void task();
    default void postTask() {};
}
