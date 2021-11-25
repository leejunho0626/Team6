package com.example.helloroutine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Challenge_Adapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<String> arrayList;
    ArrayList<Integer> arrayList2;
    ArrayList<Boolean> arrayList3;
    Context mContext;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public Challenge_Adapter() {
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_challenge, parent, false);
        ViewHolder viewholder = new ViewHolder(context, view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = arrayList.get(position);
        int value = arrayList2.get(position);
        boolean fav = arrayList3.get(position);
        holder.txtFav.setText(text);
        holder.txtSub.setText(value+"%");
        holder.prgFav.setProgress(value);
        holder.fav.setChecked(fav);

        holder.fav.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    Log.d(TAG, "=> 등록"+arrayList.get(position));
                    addFav1(arrayList.get(position));
                }
                else {
                    Log.d(TAG, "=> 해제");
                    deleteFav1(arrayList.get(position));
                }

            }
        });


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public void setArrayData(String txtList, int value, boolean fav) {


        arrayList.add( txtList);
        arrayList2.add(value);
        arrayList3.add(fav);


    }
    //즐겨찾기 추가1
    public void addFav1(String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(txt).set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{
            Toast.makeText(mContext.getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
        }
    }

    //즐겨찾기 해제1
    public void deleteFav1(String txt){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //첫번째 즐겨찾기 DB 검색
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(txt)
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

                    }
                });
    }
}