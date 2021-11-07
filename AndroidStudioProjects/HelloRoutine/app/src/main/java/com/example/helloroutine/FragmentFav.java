package com.example.helloroutine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentFav extends Fragment {

    TextView txtFav1, txtFav2, txtFav3;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_fav, container, false);

        txtFav1 = view.findViewById(R.id.favList1);
        txtFav2 = view.findViewById(R.id.favList2);
        txtFav3 = view.findViewById(R.id.favList3);

        showFav1();
        showFav2();
        showFav3();


        return view;
    }

    //즐겨찾기 표시1
    public void showFav1(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));
                        txtFav1.setText(" 1. "+x);
                    } else {
                        txtFav1.setText(" 즐겨찾기를 설정하세요.");

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    //즐겨찾기 표시2
    public void showFav2(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));
                        txtFav2.setText(" 2. "+x);
                    } else {
                        txtFav2.setText(" 즐겨찾기를 설정하세요.");

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    //즐겨찾기 표시1
    public void showFav3(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));
                        txtFav3.setText(" 3. "+x);
                    } else {
                        txtFav3.setText(" 즐겨찾기를 설정하세요.");

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
