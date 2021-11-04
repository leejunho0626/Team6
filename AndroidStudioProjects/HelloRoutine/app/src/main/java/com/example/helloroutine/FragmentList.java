package com.example.helloroutine;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentList extends Fragment {

    CheckBox btnFav1, btnFav2, btnFav3, btnFav4, btnFav5;
    TextView txtCha1;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        btnFav1 = view.findViewById(R.id.fav1);
        btnFav2 = view.findViewById(R.id.fav2);
        btnFav3 = view.findViewById(R.id.fav3);
        btnFav4 = view.findViewById(R.id.fav4);
        btnFav5 = view.findViewById(R.id.fav5);
        txtCha1 = view.findViewById(R.id.challenge1);

        firebaseAuth = FirebaseAuth.getInstance();

        txtCha1.setText("운동 일정 10개 추가");

        btnFav1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnFav1.isChecked()){
                    addFav(txtCha1.getText().toString());
                }
                else{

                }
            }
        });
        btnFav2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnFav3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnFav4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnFav5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    //즐겨찾기 추가1
    public void addFav(String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기로 설정했습니다.", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "UID을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }
}
