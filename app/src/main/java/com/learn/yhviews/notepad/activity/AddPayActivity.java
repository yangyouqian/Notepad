package com.learn.yhviews.notepad.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.yhviews.notepad.MainActivity;
import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.adapter.MyGridAdapter;
import com.learn.yhviews.notepad.db.MyOpenHelper;
import com.learn.yhviews.notepad.entity.Cost;
import com.learn.yhviews.notepad.entity.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPayActivity extends AppCompatActivity implements View.OnClickListener {
    private GridView gridView;
    private ImageView etImageType;
    private TextView tvType;
    private Button btnPayBack;
    private TextView tvSavePay;
    private EditText etPayMoney;

    private TextView pageIncome;
    private TextView pagePay;

    private MyGridAdapter adapter;
    List<Type> typeList;

    //支出页面的数据
    private int[] images = {R.drawable.eat, R.drawable.shop, R.drawable.daily, R.drawable.traff,
            R.drawable.party, R.drawable.eat, R.drawable.room, R.drawable.bill,
            R.drawable.tour, R.drawable.medical, R.drawable.edu};
    private int[] iconBig = {R.drawable.eat_big, R.drawable.shop_big,R.drawable.daily_big,R.drawable.traff_big,R.drawable.party_big,
            R.drawable.eat_big, R.drawable.room_big, R.drawable.bill_big,R.drawable.tour_big,
            R.drawable.medical_big,R.drawable.edu_big};
    private String[] typePayStrings = {"餐饮", "购物", "日用", "交通", "聚会",
                                    "零食", "住房", "通讯", "旅行", "医疗", "教育"};

    //收入页面的数据
    private int[] imagesIncome = {R.drawable.wage, R.drawable.redbag, R.drawable.busy,
            R.drawable.littlem, R.drawable.twohands};
    private int[] iconBigIncome = {R.drawable.wage_big, R.drawable.redbag_big,
            R.drawable.busy_big,R.drawable.littlem_big, R.drawable.twohands_big};
    private String[] typeIncomeStrings = {"工资", "红包", "外快", "零花钱","闲置"};

    //标记当前是收入还是支出页面，默认是收入
    private String isPay = "1";
    private boolean haveData = false;//是否添加了数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pay);

        init();
        initData();
        adapter = new MyGridAdapter(this, typeList);
        gridView.setAdapter(adapter);
        adapter.setSelection(0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelection(position);
                adapter.notifyDataSetChanged();

                if (isPay.equals("1")){
                    etImageType.setImageResource(iconBig[position]);
                    tvType.setText(typePayStrings[position]);
                } else {
                    etImageType.setImageResource(iconBigIncome[position]);
                    tvType.setText(typeIncomeStrings[position]);
                }

            }
        });
    }

    private void init() {
        gridView = findViewById(R.id.grid_view);
        etImageType = findViewById(R.id.iv_pay_type);
        tvType = findViewById(R.id.tv_pay_type);

        tvSavePay = findViewById(R.id.tv_save);
        btnPayBack = findViewById(R.id.btn_payBack);

        etPayMoney = findViewById(R.id.et_payMoney);

        pageIncome = findViewById(R.id.page_income);
        pagePay = findViewById(R.id.page_pay);

        typeList = new ArrayList<>();

        btnPayBack.setOnClickListener(this);
        tvSavePay.setOnClickListener(this);
        pagePay.setOnClickListener(this);
        pageIncome.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (!haveData)
            intent.putExtra("haveData", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initData(){
        typeList.clear();
        for (int i = 0; i < images.length; i++) {
            typeList.add(new Type(typePayStrings[i], images[i]));
        }
    }

    private void changeData(boolean flagPay){
        typeList.clear();
        if (flagPay){//true是支出
            for (int i = 0; i < images.length; i++) {
                typeList.add(new Type(typePayStrings[i], images[i]));
            }
            adapter.refreshData(typeList, iconBig);
        } else {
            for (int i = 0; i < imagesIncome.length; i++) {
                typeList.add(new Type(typeIncomeStrings[i], imagesIncome[i]));
            }
            adapter.refreshData(typeList, iconBigIncome);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_payBack:
                Intent intent = new Intent();
                if (!haveData)
                    intent.putExtra("haveData", false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_save:
                String money = etPayMoney.getText().toString();
                if (!"".equals(money)){
                    haveData = true;
                    MyOpenHelper helper = new MyOpenHelper(AddPayActivity.this, "NoteMoney.db", null, 1);
                    String type = tvType.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String date = format.format(new Date());
                    helper.add(type, money, date, isPay, "");
                    String to = "又花钱啦～";
                    if (isPay.equals("0"))
                        to = "努力赚小钱钱！";
                    Toast.makeText(AddPayActivity.this, to, Toast.LENGTH_SHORT).show();
                    //清空输入框数据
                    etPayMoney.setText("");
                } else {
                    Toast.makeText(AddPayActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.page_income:
                pageIncome.setTextColor(getResources().getColor(R.color.colorMain));
                pageIncome.setTextSize(24);
                pagePay.setTextColor(getResources().getColor(R.color.normalTvColor));
                pagePay.setTextSize(16);
                isPay = "0";
                //切换默认选中
                etImageType.setImageResource(imagesIncome[0]);
                tvType.setText(typeIncomeStrings[0]);
                adapter.setSelection(0);
                changeData(false);
                break;
            case R.id.page_pay:
                pagePay.setTextColor(getResources().getColor(R.color.colorMain));
                pagePay.setTextSize(24);
                pageIncome.setTextColor(getResources().getColor(R.color.normalTvColor));
                pageIncome.setTextSize(16);
                isPay = "1";
                etImageType.setImageResource(images[0]);
                tvType.setText(typePayStrings[0]);
                adapter.setSelection(0);
                changeData(true);
                break;
        }
    }
}
