package com.learn.yhviews.notepad.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learn.yhviews.notepad.entity.Cost;
import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.entity.DayCostHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yhviews on 2018/4/8.
 */

public class CostAdapter extends BaseAdapter {

    private List<Cost> list;
    private Context context;
    private int[] iconType = {R.drawable.eat_big, R.drawable.shop_big,R.drawable.daily_big,R.drawable.traff_big,R.drawable.party_big,
                    R.drawable.eat_big, R.drawable.room_big, R.drawable.bill_big,R.drawable.tour_big,
                    R.drawable.medical_big,R.drawable.edu_big, R.drawable.wage_big, R.drawable.redbag_big,
                    R.drawable.busy_big,R.drawable.littlem_big, R.drawable.twohands_big};
    private String[] typeStrings = {"餐饮", "购物", "日用", "交通", "聚会",
            "零食", "住房", "通讯", "旅行", "医疗", "教育", "工资", "红包", "外快", "零花钱","闲置"};
    private HashMap<String, Integer> typeMap;

    private List<DayCostHelper> dayCostList;

    public CostAdapter(List<Cost> list, Context context) {
        super();
        this.list = list;
        this.context = context;
        typeMap = new HashMap<>();
        initMap();
    }

    private void initMap() {
        for (int i = 0; i < iconType.length; i++) {
            typeMap.put(typeStrings[i], iconType[i]);
        }
    }

    public void setDayCostList(List<DayCostHelper> dayCostList){
        this.dayCostList = dayCostList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        Cost cost = (Cost) getItem(position);
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.tvMoneyLeft = view.findViewById(R.id.item_money2);
            holder.tvTypeLeft = view.findViewById(R.id.item_type2);
            holder.tvMoneyRight = view.findViewById(R.id.item_money);
            holder.tvTypeRight = view.findViewById(R.id.item_type);
            holder.ivType = view.findViewById(R.id.iv_type);

            holder.rlDay = view.findViewById(R.id.rl_day);
            holder.tvDay = view.findViewById(R.id.tv_day);
            holder.tvDayMoney = view.findViewById(R.id.tv_day_sumMoney);
            holder.verticalLine = view.findViewById(R.id.vertical_line);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tvTypeRight.setText(cost.getType());
        holder.tvMoneyRight.setText(cost.getMoney());
        holder.tvTypeLeft.setText(cost.getType());
        holder.tvMoneyLeft.setText(cost.getMoney());
        if (typeMap.get(cost.getType()) != null){
            holder.ivType.setImageResource(typeMap.get(cost.getType()));
        }
        if (cost.getPay().equals("1")){
            //支出，隐藏左边数据,显示右边数据
            holder.tvTypeLeft.setVisibility(View.GONE);
            holder.tvMoneyLeft.setVisibility(View.GONE);
            holder.tvTypeRight.setVisibility(View.VISIBLE);
            holder.tvMoneyRight.setVisibility(View.VISIBLE);
        } else if (cost.getPay().equals("0")){
            //收入，隐藏右边数据,显示左边数据
            holder.tvTypeRight.setVisibility(View.GONE);
            holder.tvMoneyRight.setVisibility(View.GONE);
            holder.tvTypeLeft.setVisibility(View.VISIBLE);
            holder.tvMoneyLeft.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < dayCostList.size(); i++) {
            int p = dayCostList.get(i).getPosition();
            Log.d("list", p + "position");
            if (position == p){
                holder.rlDay.setVisibility(View.VISIBLE);
                holder.tvDay.setText(dayCostList.get(i).getDay() + "日");
                holder.tvDayMoney.setText(dayCostList.get(i).getSumMoney() + "");
                holder.verticalLine.setVisibility(View.VISIBLE);
                break;
            } else {
                //一定记住这里啊啊啊!!!
                holder.rlDay.setVisibility(View.GONE);
                holder.verticalLine.setVisibility(View.GONE);
            }
        }
        return view;
    }

    static class ViewHolder{
        private TextView tvTypeLeft;
        private TextView tvTypeRight;
        private TextView tvMoneyLeft;
        private TextView tvMoneyRight;
        private ImageView ivType;

        private RelativeLayout rlDay;
        private TextView tvDay;
        private TextView tvDayMoney;
        private View verticalLine;
    }
}
