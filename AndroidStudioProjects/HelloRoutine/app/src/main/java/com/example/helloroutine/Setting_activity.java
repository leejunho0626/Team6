package com.example.helloroutine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import java.util.Collection;

import static android.content.ContentValues.TAG;

public class Setting_activity extends AppCompatActivity {

    Button btnRemove;
    TextView txtPush, txtLock;
    Switch swPush, swLock;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences spref, spref2;
    SharedPreferences.Editor editor, editor2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        btnRemove = findViewById(R.id.btnRemove);
        swPush = findViewById(R.id.swPush);
        swLock = findViewById(R.id.swLock);
        txtPush = findViewById(R.id.txtPush);
        txtLock = findViewById(R.id.txtLock);

        spref = getSharedPreferences("gref", MODE_PRIVATE);
        spref2 = getSharedPreferences("gref2", MODE_PRIVATE);
        editor = spref.edit();
        editor2 = spref2.edit();


        String temp1 = spref.getString("push", "사용");
        boolean temp2 = spref.getBoolean("check",true);
        String temp3 = spref2.getString("push2", "사용");
        boolean temp4 = spref2.getBoolean("check2",true);
        txtPush.setText(temp1);
        swPush.setChecked(temp2);
        txtLock.setText(temp3);
        swLock.setChecked(temp4);


        if(temp4==true){
            Intent intent = new Intent(getApplicationContext(), ScreenService.class);
            startService(intent);
        }
        if(temp4==false){
            Intent intent = new Intent(getApplicationContext(), ScreenService.class);
            stopService(intent);
        }



        swPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked) {

                    txtPush.setText("사용");
                    editor.putString("push", "사용");
                    editor.putBoolean("check", true);
                    editor.commit();



                } else {

                    txtPush.setText("사용 안함");
                    editor.putString("push", "사용안함");
                    editor.putBoolean("check", false);
                    editor.commit();

                }
            }
        });

        swLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked) {
                    txtLock.setText("사용");
                    editor2.putString("push2", "사용");
                    editor2.putBoolean("check2", true);
                    editor2.commit();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent);



                } else {

                    txtLock.setText("사용 안함");
                    editor2.putString("push2", "사용안함");
                    editor2.putBoolean("check2", false);
                    editor2.commit();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    stopService(intent);


                }
            }
        });





        //회원탈퇴 버튼 클릭
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Setting_activity.this)
                        .setMessage("정말 탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                allDelete();
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        int result = errorResult.getErrorCode();

                                        if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                            Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //Toast.makeText(getApplicationContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        Toast.makeText(getApplicationContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Setting_activity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                     allDelete();

                                    }
                                });

                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public void allDelete(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseAuth.getInstance().signOut();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Setting_activity.this, Login.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }



}
