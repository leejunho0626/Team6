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

public class ListAdapter extends BaseAdapter {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    CheckBox checkBox;
    private Context mContext;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
    private SharedPreferences pref1, pref2, pref3, pref4, pref5;
    private SharedPreferences.Editor editor1, editor2, editor3, editor4, editor5;
    private boolean checked, saveFav1, saveFav2, saveFav3, saveFav4, saveFav5;



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

        final int checkBoxPosition = position;

        // item.xml 레이아웃을 inflate해서 참조획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.challenge_list, parent, false);
        }

        // item.xml 의 참조 획득
        TextView txt_title = (TextView)convertView.findViewById(R.id.title);
        TextView txt_sub = (TextView)convertView.findViewById(R.id.sub);
        ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.prg);
        checkBox = (CheckBox)convertView.findViewById(R.id.cbFav);



        ListItem listItem = listItems.get(position);

        // 가져온 데이터를 텍스트뷰에 입력
        checkBox.setChecked(listItem.getCheck());
        txt_title.setText(listItem.getTitle());
        txt_sub.setText(listItem.getSub());
        progressBar.setProgress(listItem.getProgress());


        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    addFav1(Integer.toString(position),listItem.getTitle());
                }
                else {
                    deleteFav1(Integer.toString(position));
                }

                Toast.makeText(mContext, "click switch view "+position, Toast.LENGTH_SHORT).show();}
        });




        return convertView;
    }
    //즐겨찾기 추가1
    public void addFav1(String position, String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(position).set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext.getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(mContext.getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
        }
    }

    //즐겨찾기 해제1
    public void deleteFav1(String position){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //첫번째 즐겨찾기 DB 검색
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(position)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기를 해제했습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext.getApplicationContext(), "즐겨찾기 해제를 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                });


    }


    public void addItem(String title, String sub, int progress ,boolean check){
        ListItem listItem = new ListItem();

        listItem.setCheck(check);
        listItem.setTitle(title);
        listItem.setSub(sub);
        listItem.setProgress(progress);



        listItems.add(listItem);


    }
}