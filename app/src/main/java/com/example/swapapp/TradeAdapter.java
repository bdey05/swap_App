package com.example.swapapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class TradeAdapter extends BaseAdapter {
    Context context;
    ArrayList <String> names;
    ArrayList<String> ids;
    LayoutInflater inflter;

    public TradeAdapter(Context applicationContext, ArrayList<String> names, ArrayList<String> ids) {
        this.context = context;
        this.names = names;
        this.ids = ids;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list, null);
        TextView name = (TextView) view.findViewById(R.id.textViewName);
        TextView id = (TextView) view.findViewById(R.id.textViewID);
        CheckBox cb = (CheckBox) view.findViewById(R.id.cbBox);
        name.setText(names.get(i));
        id.setText(ids.get(i));
        id.setVisibility(View.GONE);
        cb.setChecked(false);
        return view;
    }


}
