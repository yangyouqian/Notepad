package com.learn.yhviews.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learn.yhviews.notepad.R;
import com.learn.yhviews.notepad.entity.Cost;

import java.util.List;

/**
 * Created by yhviews on 2018/9/27.
 */

public class ShowPreAdapter extends BaseAdapter {
    private List<Cost> list;
    private Context context;

    public ShowPreAdapter(List<Cost> list, Context context) {
        this.list = list;
        this.context = context;
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
        Cost cost = (Cost) getItem(position);
        View view = LayoutInflater.from(context).inflate(R.layout.pre_item, null);
        TextView tvType = view.findViewById(R.id.tv_type);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvMoney = view.findViewById(R.id.tv_money);
        if (cost.getPay().equals("0")){
            tvType.setText("收入");
        } else {
            tvType.setText("支出");
        }
        tvName.setText(cost.getType());
        tvMoney.setText(cost.getMoney());
        return view;
    }
}
