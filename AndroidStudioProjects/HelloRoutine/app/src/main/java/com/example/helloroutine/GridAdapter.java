package com.example.helloroutine;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class GridAdapter extends BaseAdapter
{
    ArrayList<GridItem>mItem = new ArrayList<GridItem>();
    LayoutInflater inflater;
    Context mContext;
    private Calendar mCal;
    TextView label;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


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
        mCal = Calendar.getInstance();

        if (convertView == null)
        {
            v = inflater.inflate(R.layout.item_cal, null);
        }
        else
        {
            v = convertView;
        }

        TextView day = (TextView)v.findViewById(R.id.day);
        label = (TextView)v.findViewById(R.id.label);
        day.setText(mItem.get(position).day());

        int text = mItem.get(position).text();
        if (text == 1)
            day.setTextColor(mContext.getResources().getColor(R.color.red));
        else if (text == 7)
            day.setTextColor(mContext.getResources().getColor(R.color.blue));
        //오늘 날짜
        Integer today = mCal.get(Calendar.DAY_OF_MONTH);
        String sToday = mItem.get(position).month()+String.valueOf(today);

        if(sToday.equals(mItem.get(position).month()+mItem.get(position).day())){
            day.setBackground(mContext.getResources().getDrawable(R.drawable.button_round));

        }

        day.setVisibility(View.VISIBLE);
        




        return v;
    }


}