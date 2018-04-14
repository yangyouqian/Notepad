package com.learn.yhviews.notepad.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.learn.yhviews.notepad.AlarmReceiver;
import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.adapter.NotifyAdapter;
import com.learn.yhviews.notepad.db.MyOpenHelper;
import com.learn.yhviews.notepad.entity.Notify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class NotificationActivity extends AppCompatActivity {

    private ListView notifyListView;
    private Button addNotifiBtn;
    private List<Notify> notifyList;
    private MyOpenHelper dbHelper;
    private NotifyAdapter adapter;
    private int hour;
    private int mMinute;
    private String[] repeat;
    private int[] repeatTime;

    private Calendar mCalendar;

    AlarmManager manager;
    PendingIntent sender;
    private Switch mSwitchNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        init();
        showNotify();
        //TODO 设置修改提醒事项
        //TODO 删除提醒事项
        notifyListView.setAdapter(adapter);
        addNotifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.checkNitifyMaxNum()) {
                    //创建自定义对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                    final View view = LayoutInflater.from(NotificationActivity.this).inflate(R.layout.dialog_add_notify, null);
                    builder.setView(view);
                    Button btnConfirmNotify = view.findViewById(R.id.btn_confirmNotify);
                    Spinner spinner = view.findViewById(R.id.spinner);
                    mSwitchNotify = view.findViewById(R.id.switch_openNotify);
                    final TimePicker timePicker = view.findViewById(R.id.timePicker);
                    timePicker.setIs24HourView(true);//设置为24小时制..省去麻烦

                    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            hour = hourOfDay;
                            mMinute = minute;
                        }
                    });

                    final String[] repeatCycle = new String[1];
                    final boolean[] open = {false};

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            repeatCycle[0] = repeat[position];//选中的重复周期
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            String[] repeat = getResources().getStringArray(R.array.spinner_data);
                            repeatCycle[0] = repeat[0];//默认选中
                        }
                    });

                    mSwitchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.d("switch", mSwitchNotify.isChecked() + "");
                            open[0] = isChecked;
                        }
                    });

                    final AlertDialog dialog = builder.create();

                    btnConfirmNotify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //添加提醒事项到数据库
                            String time;
                            if (mMinute < 10){
                                time = hour + ":0" + mMinute;
                            } else {
                                time = hour + ":" + mMinute;
                            }
                            Notify notify = new Notify(time, open[0], repeatCycle[0]);
                            dbHelper.addNotifyToDB(notify);
                            notifyList.add(notify);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    //超过了5条提醒不允许添加
                    Toast.makeText(NotificationActivity.this, "最多添加5条", Toast.LENGTH_SHORT).show();
                }

                //TODO 设置定时推送
                setAlarmManagerForNotify();
            }
        });

    }

    //设置AlarmManager
    private void setAlarmManagerForNotify(){
        Intent intent = new Intent(NotificationActivity.this, AlarmReceiver.class);
        for (int i = 0; i < notifyList.size(); i++) {
            Notify notify = notifyList.get(i);
            if (notify.isOpen()){
                int nHour = Integer.parseInt(notify.getTime().split(":")[0]);
                int nMinute = Integer.parseInt(notify.getTime().split(":")[1]);
                setCalendar(nHour, nMinute);
                //使用id来设置PendingIntent,防止重复
                PendingIntent pi = PendingIntent.getBroadcast(NotificationActivity.this,
                        notify.getId(), intent, 0);
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),
                        getRepeat(notify.getRepeatCycle()), pi);
            } else {
                //关闭
                Intent intent2 = new Intent(NotificationActivity.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(NotificationActivity.this, notify.getId(),
                        intent2, 0);
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                //取消警报
                if (am != null)
                    am.cancel(pi);
            }
        }
    }


    private int getRepeat(String repeatCycle){
        if (repeatCycle.equals(repeat[0])){
            return repeatTime[0];
        }
        return repeatTime[1];
    }

    /***
     *
     * @param hour
     * @param minute
     */
    private void setCalendar(int hour, int minute){
        long systemTime = System.currentTimeMillis();
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(systemTime);
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //设置在几点提醒
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        //设置在几分提醒
        mCalendar.set(Calendar.MINUTE, minute);
        long selectTime = mCalendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }


    private void showNotify() {
        //从数据库中查询添加的提醒事项，显示在listView中
        Cursor cursor = dbHelper.selectNotifyFormDB();
        if (cursor != null){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String open = cursor.getString(cursor.getColumnIndex("open"));
                String repeat = cursor.getString(cursor.getColumnIndex("repeat"));
                boolean isOpen = open.equals("1");
                notifyList.add(new Notify(time, isOpen, repeat, id));
            }
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }


    private void init() {
        notifyListView = findViewById(R.id.notifi_list);
        addNotifiBtn = findViewById(R.id.btn_add_notifi);
        notifyList = new ArrayList<>();
        repeat = getResources().getStringArray(R.array.spinner_data);
        int day = 1000*60*60*24;
        repeatTime = new int[]{day, day*7};

        dbHelper = new MyOpenHelper(this, "NoteMoney.db", null, 1);
        adapter = new NotifyAdapter(notifyList, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("notification", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("notification", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("notification", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("notification", "onStop");
    }
}
