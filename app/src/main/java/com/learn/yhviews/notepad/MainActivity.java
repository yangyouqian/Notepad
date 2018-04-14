package com.learn.yhviews.notepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.learn.yhviews.notepad.activity.AddPayActivity;
import com.learn.yhviews.notepad.activity.ChartActivity;
import com.learn.yhviews.notepad.activity.UserCenterActivity;
import com.learn.yhviews.notepad.adapter.CostAdapter;
import com.learn.yhviews.notepad.db.MyOpenHelper;
import com.learn.yhviews.notepad.entity.Cost;
import com.learn.yhviews.notepad.entity.DayCostHelper;
import com.learn.yhviews.notepad.view.CircleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyOpenHelper dbHelper;
    private Button btnAddPay;//添加消费记录
    private CircleView circleView;
    private ListView listView;//显示消费记录
    private List<Cost> costList;
    private CostAdapter costAdapter;

    //左上角的按钮
    private Button btnUserCenter;
    //右上角的按钮
    private Button btnChart;

    //设置收入支出金额
    private TextView tvIncome;
    private TextView tvExpenditure;


    private GestureDetector gestureDetector;//手势检测
    private GestureDetector.OnGestureListener onSlideGestureListener = null;//左右滑动手势检测监听器

    private String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initData();

        //TODO 优化更新UI的逻辑
        updateData();
        //TODO 实现可折叠当日消费记录

    }

    //TODO 如果余额变成了负的圆环会显示数据错误
    /**
     * 更新圆弧
     * 更新收入与支出的数据
     * */
    private void updateData(){
        float income = dbHelper.getPayMoney(false, month);
        float pay = dbHelper.getPayMoney(true, month);
        tvIncome.setText(String.valueOf(income));
        tvExpenditure.setText(String.valueOf(pay));

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        //默认2000
        float allMoney = sp.getFloat("allMoney", 2000);
        float reMoney = allMoney + income - pay;
        circleView.setReMoney(String.valueOf(reMoney));
        circleView.drawReMoney(allMoney, reMoney);
    }

    private void initData() {
        //加载ListView的数据
        costList.clear();
        Cursor cursor = dbHelper.select();
        //获得当月的消费记录
        while (cursor.moveToNext()){
            String m = cursor.getString(cursor.getColumnIndex("date"));
            if (m.contains(month)){
                Cost cost = new Cost();
                cost.setType(cursor.getString(cursor.getColumnIndex("type")));
                cost.setMoney(cursor.getString(cursor.getColumnIndex("money")));
                cost.setPay(cursor.getString(cursor.getColumnIndex("pay")));
                costList.add(cost);
            }
        }
        cursor.close();
        costAdapter = new CostAdapter(costList, this);
        listView.setAdapter(costAdapter);
        costAdapter.setDayCostList(dbHelper.getEveryDayMoney(month));
        costAdapter.notifyDataSetChanged();
    }

    private void init() {
        dbHelper = new MyOpenHelper(this, "NoteMoney.db", null, 1);

        btnAddPay = findViewById(R.id.btn_add_pay);
        circleView = findViewById(R.id.circle_view);
        listView = findViewById(R.id.listview);
        btnUserCenter = findViewById(R.id.btn_userCenter);

        costList = new ArrayList<>();
        btnAddPay.setOnClickListener(this);
        btnUserCenter.setOnClickListener(this);

        tvIncome = findViewById(R.id.tv_income);
        tvExpenditure = findViewById(R.id.tv_expenditure);

        month = new SimpleDateFormat("MM").format(new Date());

        btnChart = findViewById(R.id.btn_chart);
        btnChart.setOnClickListener(this);

        onSlideGestureListener = new OnSlideGestureListener();
        gestureDetector = new GestureDetector(this, onSlideGestureListener);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    if (data.getBooleanExtra("haveData", true)){
                        //数据有添加，从AddPayActivity页面返回的时候更新数据
                        initData();
                        updateData();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK){
                    //改变了预算
                    if (data.getBooleanExtra("change", false)){
                        initData();
                        updateData();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_pay:
                Intent intent = new Intent(MainActivity.this, AddPayActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_userCenter:
                startActivityForResult(new Intent(MainActivity.this, UserCenterActivity.class), 2);
                overridePendingTransition(R.anim.move_left_in, R.anim.move_right_out);
                break;
            case R.id.btn_chart:
                startActivity(new Intent(MainActivity.this, ChartActivity.class));
                break;
        }
    }

    //左右滑动手势检测监听器
    private class OnSlideGestureListener implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //用户按下触摸屏，快速滑动后松开
            // e1：第1个ACTION_DOWN MotionEvent 手指刚接触屏幕
            // e2：最后一个ACTION_MOVE MotionEvent 手指在屏幕上滑动
            // velocityX：X轴上的移动速度，像素/秒
            // velocityY：Y轴上的移动速度，像素/秒
            if ((e1 == null) || (e2 == null)){
                return false;
            }
            int FLING_MIN_DISTANCE = 100;
            int FLING_MIN_VELOCITY = 100;
            // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)
            {
                // 向左滑动 设置需要接近屏幕左侧开始滑触发 以达到侧滑效果
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && e1.getX() < 150 ) {
                Log.d("slide", e1.getX() + "");
                // 向右滑动
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserCenterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.move_left_in, R.anim.move_right_out);
            }
            return false;
        }
    }

}
