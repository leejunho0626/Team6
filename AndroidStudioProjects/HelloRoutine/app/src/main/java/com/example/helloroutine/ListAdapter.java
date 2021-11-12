package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();


    public ListAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // item.xml 레이아웃을 inflate해서 참조획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.challenge_list, parent, false);
        }

        // item.xml 의 참조 획득
        TextView txt_title = (TextView)convertView.findViewById(R.id.title);
        TextView txt_sub = (TextView)convertView.findViewById(R.id.sub);
        ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.prg);
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.cbFav);

        ListItem listItem = listItems.get(position);

        // 가져온 데이터를 텍스트뷰에 입력
        txt_title.setText(listItem.getTitle());
        txt_sub.setText(listItem.getSub());
        progressBar.setProgress(listItem.getProgress());
        checkBox.setChecked(listItem.getCheck());



        return convertView;
    }

    public void addItem(String title, String sub, int progress){
        ListItem listItem = new ListItem();

        listItem.setTitle(title);
        listItem.setSub(sub);
        listItem.setProgress(progress);


        listItems.add(listItem);
    }
}