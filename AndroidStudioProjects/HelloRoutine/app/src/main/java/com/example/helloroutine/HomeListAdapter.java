package com.example.helloroutine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class HomeListAdapter extends BaseAdapter {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Context mContext;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
    private boolean checked, saveFav1, saveFav2, saveFav3, saveFav4, saveFav5;




    public HomeListAdapter(Context context){
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

        final int checkBoxPosition = position;

        // item.xml 레이아웃을 inflate해서 참조획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.home_challenge_list, parent, false);
        }

        // item.xml 의 참조 획득
        TextView txt_title = (TextView)convertView.findViewById(R.id.home_title);
        TextView txt_sub = (TextView)convertView.findViewById(R.id.home_sub);
        ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.home_prg);



        ListItem listItem = listItems.get(position);

        // 가져온 데이터를 텍스트뷰에 입력
        txt_title.setText(listItem.getTitle());
        txt_sub.setText(listItem.getSub());
        progressBar.setProgress(listItem.getProgress());




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