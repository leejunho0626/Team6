package com.example.helloroutine;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter
{
    ArrayList<GridItem>mItem = new ArrayList<GridItem>();
    LayoutInflater inflater;
    Context mContext;

    GridAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void add(GridItem item)
    {
        mItem.add(item);
    }

    public void clear()
    {
        mItem.clear();
    }

    @Override
    public int getCount()
    {
        return mItem.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        Long id = Long.parseLong(mItem.get(position).day());
        return id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v;
        if (convertView == null)
        {
            v = inflater.inflate(R.layout.item_cal, null);
        }
        else
        {
            v = convertView;
        }

        TextView day = (TextView)v.findViewById(R.id.day);
        day.setText(mItem.get(position).day());

        int text = mItem.get(position).text();
        if (text == 1)
            day.setTextColor(mContext.getResources().getColor(R.color.red));
        else if (text == 7)
            day.setTextColor(mContext.getResources().getColor(R.color.blue));

        day.setVisibility(View.VISIBLE);

        day.setVisibility(View.VISIBLE);

        TextView label = (TextView)v.findViewById(R.id.label);
        if (mItem.get(position).img())
            label.setVisibility(View.VISIBLE);

        return v;
    }

}