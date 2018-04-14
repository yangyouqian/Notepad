package com.learn.yhviews.notepad.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.learn.yhviews.notepad.AlarmReceiver;
import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.activity.NotificationActivity;
import com.learn.yhviews.notepad.db.MyOpenHelper;
import com.learn.yhviews.notepad.entity.Notify;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by yhviews on 2018/4/10.
 */

//TODO 代码中有重复的东西,希望写个工具类提取出来
public class NotifyAdapter extends BaseAdapter {
    private List<Notify> list;
    private Context context;
    private MyOpenHelper dbHelper;
    private int hour;
    private int mMinute;
    private String[] repeat;
    private int[] repeatTime;

    public NotifyAdapter(List<Notify> list, Context context) {
        this.list = list;
        this.context = context;
        dbHelper = new MyOpenHelper(context, "NoteMoney.db", null, 1);
        repeat = context.getResources().getStringArray(R.array.spinner_data);
        int day = 1000*60*60*24;
        repeatTime = new int[]{day, day*7};
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.notifi_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTime = view.findViewById(R.id.tv_timeNotifi);
            viewHolder.aSwitch = view.findViewById(R.id.switchNotifi);
            viewHolder.imageButton = view.findViewById(R.id.btn_settingNotifi);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final Notify notifi = list.get(position);
        viewHolder.tvTime.setText(notifi.getTime());
        viewHolder.aSwitch.setChecked(notifi.isOpen());
        viewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbHelper.updateNotifyOpenState(isChecked, notifi.getId());
            }
        });
        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "btn", Toast.LENGTH_SHORT).show();
                //弹出popupwindow
                showPopupWindow(viewHolder.imageButton, position);
            }
        });
        return view;
    }

    private void showPopupWindow(View imageButton, final int position){
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View ppw = LayoutInflater.from(context).inflate(R.layout.layout_popupwindow, null);
        TextView tvDelete = ppw.findViewById(R.id.tv_delete);
        TextView tvUpdate = ppw.findViewById(R.id.tv_update);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ppw", "delete");
                Notify notify = list.get(position);
                //删除该提醒事项
                list.remove(position);
                notifyDataSetChanged();
                //根据id删除数据库中该条数据
                dbHelper.delete(notify.getId());
                //如果当前是open状态,还要删除设置定时提醒
                if (notify.isOpen()){
                    cancelNotification(context, notify.getId());
                }
                popupWindow.dismiss();
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ppw", "update");
                final Notify notifyOld = list.get(position);
                //修改提醒
                //创建自定义对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_notify, null);
                builder.setView(view);

                Button btnConfirmNotify = view.findViewById(R.id.btn_confirmNotify);
                Spinner spinner = view.findViewById(R.id.spinner);
                final TimePicker timePicker = view.findViewById(R.id.timePicker);
                LinearLayout linearLayout = view.findViewById(R.id.ll_open);
                linearLayout.setVisibility(View.GONE);//不显示是否开启提醒这一项
                timePicker.setIs24HourView(true);//设置为24小时制..省去麻烦
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        hour = hourOfDay;
                        mMinute = minute;
                    }
                });
                final String[] repeatCycle = new String[1];
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        repeatCycle[0] = repeat[position];//选中的重复周期
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        String[] repeat = context.getResources().getStringArray(R.array.spinner_data);
                        repeatCycle[0] = repeat[0];//默认选中
                    }
                });
                final AlertDialog dialog = builder.create();
                btnConfirmNotify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //修改提醒事项
                        String time;
                        if (mMinute < 10){
                            time = hour + ":0" + mMinute;
                        } else {
                            time = hour + ":" + mMinute;
                        }
                        Notify notify = new Notify();
                        notify.setTime(time);
                        notify.setRepeatCycle(repeatCycle[0]);
                        notify.setId(notifyOld.getId());
                        notify.setOpen(notifyOld.isOpen());
                        list.set(position, notify);
                        notifyDataSetChanged();
                        dbHelper.updateNotify(notify);
                        dialog.dismiss();
                        //修改了数据库中的时间和重复周期后,下一次进入该界面时,会进行重置通知
                    }
                });
                dialog.show();
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(ppw);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(imageButton);
    }

    private void cancelNotification(Context context, int id){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, id,
                intent, 0);
        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        //取消警报
        if (am != null)
            am.cancel(pi);
    }

    static class ViewHolder{
        private TextView tvTime;
        private Switch aSwitch;
        private ImageButton imageButton;
    }
}
