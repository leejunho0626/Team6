package com.example.helloroutine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class RequestList extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView, recyclerView2;
    RequestAdapter requestAdapter;
    ReceiveAdapter receiveAdapter;
    SwipeRefreshLayout refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rq);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        recyclerView = (RecyclerView)findViewById(R.id.recyceler_rqList);
        recyclerView2 = (RecyclerView)findViewById(R.id.recyceler_rvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        requestAdapter = new RequestAdapter();
        receiveAdapter = new ReceiveAdapter();
        refresh_layout = findViewById(R.id.refresh_layout_rq);

        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAdapter.arrayList.clear();
                receiveAdapter.arrayList.clear();

                // 새로고침 코드를 작성
                rqList();
                rvList();
                requestAdapter.notifyDataSetChanged();
                receiveAdapter.notifyDataSetChanged();

                // 새로고침 완료시,
                // 새로고침 아이콘이 사라질 수 있게 isRefreshing = false
                refresh_layout.setRefreshing(false);
            }
        });

        rqList();
        rvList();

    }
    //보낸 요청
    public void rqList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Request")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str = document.getId();

                            requestAdapter.setArrayData(str);
                            recyclerView.setAdapter(requestAdapter);

                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //받은 요청
    public void rvList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Receive")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str = document.getId().toString(); //아이디

                            receiveAdapter.setArrayData(str);
                            recyclerView2.setAdapter(receiveAdapter);
                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //뒤로 가기 버튼 클릭 시
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, Friend.class); //메인화면으로 이동
        startActivity(intent);


    }
}
