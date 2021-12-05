package com.example.helloroutine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.usermgmt.response.model.User;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {

    TextView txtError1, txtError2, txtError3; //경고메시지 텍스트
    EditText edtID, edtPw, edtPW2; //아이디, 비밀번호, 이메일 입력텍스트
    Button btnRegister, btnCheckId; //중복확인, 최종 회원가입 버튼
    private FirebaseAuth firebaseAuth; //FirebaseAuth 선언
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제
        setContentView(R.layout.register);
        //참조
        txtError1 = findViewById(R.id.txtError1);
        txtError2 = findViewById(R.id.txtError2);
        txtError3 = findViewById(R.id.txtError3);
        edtID = findViewById(R.id.edtID); //아이디
        edtPw = findViewById(R.id.edtPW); //비번
        edtPW2 = findViewById(R.id.edtPW2);
        btnRegister = findViewById(R.id.btnSignUp);
        btnCheckId = findViewById(R.id.btnCheckID);

        firebaseAuth = FirebaseAuth.getInstance();

        //아이디 중복확인
        /*
        입력한 계정과 임시 비밀번호를 통해 FirebaseAuth로 계정 생성
        */
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = edtID.getText().toString().trim(); //입력한 아이디와 선택한 이메일
                final String tempPW = "tempPW"; //임시 비밀번호
                if(id.length()==0){
                    txtError1.setText("아이디를 입력하세요."); //경고 메시지
                    txtError1.setTextColor(Color.RED); //경고 메시지 색상
                    edtID.setBackgroundResource(R.drawable.red_edittext);  //테투리 빨간색으로 변경
                }
                else{
                    //계정 생성
                    firebaseAuth.createUserWithEmailAndPassword(id, tempPW).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //계정 생성 성공
                            if (task.isSuccessful()) {
                                txtError1.setText("사용 가능한 아이디입니다."); //경고 메시지
                                txtError1.setTextColor(Color.parseColor("#4CAF50"));
                                edtID.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                               /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserRank userRank = new UserRank("테스트", "test");
                                databaseReference.child("User").child(user.getUid()).setValue(userRank);*/
                                Toast.makeText(Register.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();


                            }
                            //계정 생성 실패
                            else {
                                //입력한 아이디가 없음
                                if(edtID.getText().toString().length()==0){
                                    txtError1.setText("아이디를 입력하세요."); //경고 메시지
                                }
                                //입력한 아이디가 있지만 이메일을 선택하지 않음
                                else if(!edtID.getText().toString().contains("@")){
                                    txtError1.setText("올바른 이메일 형식으로 입력하세요."); //경고 메시지
                                }
                                //아이디 중복
                                else {
                                    txtError1.setText("사용할 수 없습니다."); //경고 메시지
                                }
                                txtError1.setTextColor(Color.RED); //경고 메시지 색상
                                edtID.setBackgroundResource(R.drawable.red_edittext);  //테투리 빨간색으로 변경
                            }
                        }
                    });
                }

            }
        });

        //비밀번호 입력
         /*
         입력한 비밀번호 값에 따라 경고메시지 변화
         비밀번호는 6자리 이상 입력해야함
        */
        edtPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //입력한 비밀번호가 6자리 이상인 경우
                if (edtPw.length()>5) {
                    txtError2.setText("사용할 수 있습니다."); // 경고 메세지
                    txtError2.setTextColor(Color.parseColor("#4CAF50")); //경고메시지 색상
                    edtPw.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                }
                //입력한 비밀번호가 6자리 이상이 아닌 경우
                else{
                    txtError2.setText("6자리 이상으로 해주세요."); // 경고 메세지
                    txtError2.setTextColor(Color.RED); //경고메시지 색상
                    edtPw.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                }
            }
        });

        //비밀번호 재입력 오류 시
        edtPW2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtPW2.getText().toString().equals(edtPw.getText().toString())) {
                    txtError3.setText("비밀번호를 다시 입력해주세요.");    // 경고 메세지
                    txtError3.setTextColor(Color.RED);
                    edtPW2.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                }
                else if(edtPW2.getText().toString().length()<6){
                    txtError3.setText("올바른 비밀번호를 입력해주세요.");    // 경고 메세지
                    txtError3.setTextColor(Color.RED);
                    edtPW2.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                }
                else{
                    txtError3.setText("동일합니다.");
                    txtError3.setTextColor(Color.parseColor("#4CAF50"));
                    edtPW2.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                }
            }
        });

        //회원가입 버튼 클릭
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(); //최종 회원가입
            }
        });
    }

    public void authLogin(String id, String pw){
        //1. firebaseAuth에 로그인
        firebaseAuth.signInWithEmailAndPassword(id, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserRank userRank = new UserRank("테스트", "test");
                            databaseReference.child("User").child(user.getUid()).setValue(userRank);
                            Toast.makeText(Register.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();


                        }
                        else {
                            Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.(회원가입)",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //최종 회원가입
     /*
         1. 계정 중복 확인을 한 아이디와 임시 비밀번호로 로그인
         2. 로그인한 계정의 비밀번호를 사용자가 입력한 비밀번호로 변경
         3. 회원가입 후 Firestore에 계정 정보 저장
    */
    public void register(){
        final String id = edtID.getText().toString().trim(); //입력한 아이디와 선택한 이메일
        final String adminPW= "tempPW";
        //입력한 아이디가 있을 경우
        if(id.length()>0&&adminPW.length()>0){
            //1. firebaseAuth에 로그인
            firebaseAuth.signInWithEmailAndPassword(id, adminPW)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String pw = edtPw.getText().toString().trim(); //사용자가 입력한 비밀번호
                                //2. 비밀번호 변경
                                user.updatePassword(pw);
                                //3. 아이디 DB에 저장
                                saveFriend();
                                saveID();
                                firebaseAuth.signOut(); //로그아웃
                                finish();
                                Toast.makeText(Register.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.(회원가입)",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.(회원가입)",Toast.LENGTH_SHORT).show();
        }
    }
    public void saveID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(user.getEmail());
        db.collection("DB").document("UID").collection(user.getUid()).document("id").set(userWrite) //경로
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d(TAG, "save success(id)");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "save fail(id)");
                    }
                });
    }


    public void saveFriend(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(user.getEmail());
        db.collection("DB").document(user.getEmail()).collection("Friend").document(user.getEmail()).set(userWrite) //경로
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d(TAG, "save success(score)");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "save fail(score)");
                    }
                });
    }


    //뒤로 가기 버튼 클릭 시(계정삭제)
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(firebaseAuth.getCurrentUser() != null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //
                    Log.d(TAG, "delete");
                }
            });
        }
        else{
            finish();
        }

    }
}
