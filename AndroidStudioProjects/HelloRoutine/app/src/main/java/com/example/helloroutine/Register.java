package com.example.helloroutine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    TextView txtError1, txtError2, txtError3, txtError4;
    EditText edtID, edtPw, edtPW2, pickEmail, edtNick;
    Button btOk, btCheckId;
    String[] items = {"선택","naver.com","daum.net","nate.com","yahoo.com","hanmail.com","직접입력"}; //이메일 선택
    private FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.register);

        txtError1 = findViewById(R.id.txtError1);
        txtError2 = findViewById(R.id.txtError2);
        txtError3 = findViewById(R.id.txtError3);
        txtError4 = findViewById(R.id.txtError4);
        edtID = findViewById(R.id.edtID);
        edtPw = findViewById(R.id.edtPW);
        edtPW2 = findViewById(R.id.edtPW2);
        edtNick = findViewById(R.id.edtNick);
        pickEmail = findViewById(R.id.edtEmail);
        btOk = findViewById(R.id.btnOk);
        btCheckId = findViewById(R.id.btnCheckID);
        firebaseAuth = FirebaseAuth.getInstance();
        Spinner spinner = findViewById(R.id.spinner);

        //이메일 선택 스피너
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pickEmail.setText(items[i]);
                pickEmail.setClickable(false);
                pickEmail.setFocusable(false);

                if(i==6){
                    pickEmail.setFocusableInTouchMode(true);
                    pickEmail.setFocusable(true);
                    pickEmail.setText(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //아이디 중복확인(계정 생성 후 로그아웃)
        btCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = edtID.getText().toString().trim()+"@"+pickEmail.getText().toString().trim();
                final String adminPW = "adminPW";
                firebaseAuth.createUserWithEmailAndPassword(id, adminPW).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            txtError1.setText("사용 가능한 아이디입니다.");
                            txtError1.setTextColor(Color.parseColor("#4CAF50"));
                            edtID.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                            firebaseAuth.signOut();
                        }
                        else {
                            txtError1.setText("이미 등록된 아이디입니다.");
                            txtError1.setTextColor(Color.RED);
                            edtID.setBackgroundResource(R.drawable.red_edittext);  //테투리 흰색으로 변경

                        }
                    }
                });
            }
        });

        //닉네입 입력 중복 시
        edtNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String nick = edtNick.getText().toString();
                if (nick.length()>0) {
                    txtError4.setText("이미 존재한 닉네임입니다.");
                    txtError4.setTextColor(Color.RED);
                    edtNick.setBackgroundResource(R.drawable.red_edittext);  //테투리 흰색으로 변경
                }
                else{
                    txtError4.setText("사용할 수 있습니다.");    // 경고 메세지
                    txtError4.setTextColor(Color.parseColor("#4CAF50"));
                    edtNick.setBackgroundResource(R.drawable.white_edittext);  // 적색 테두리 적용
                }
            }
        });


        //비밀번호 입력 오류 시
        edtPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtPw.length()>5) {
                    txtError2.setText("사용할 수 있습니다.");
                    txtError2.setTextColor(Color.parseColor("#4CAF50"));
                    edtPw.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                }
                else{
                    txtError2.setText("6자리 이상으로 해주세요.");    // 경고 메세지
                    txtError2.setTextColor(Color.RED);
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
                else{
                    txtError3.setText("동일합니다.");
                    txtError3.setTextColor(Color.parseColor("#4CAF50"));
                    edtPW2.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                }
            }
        });

        //회원가입 버튼 클릭
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    //최종 회원가입(로그인후 비밀번호 변경)
    public void register(){
        final String id = edtID.getText().toString().trim()+"@"+pickEmail.getText().toString().trim();
        final String adminPW= "adminPW";
        if(id.length()>0 && adminPW.length()>0){
            firebaseAuth.signInWithEmailAndPassword(id, adminPW)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String pw = edtPw.getText().toString().trim();
                                user.updatePassword(pw);
                                Toast.makeText(Register.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.(회원가입)",Toast.LENGTH_SHORT).show();
        }
    }

    //뒤로 가기 버튼 클릭 시(계정삭제)
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        final String id = edtID.getText().toString().trim()+"@"+pickEmail.getText().toString().trim();
        final String adminPW= "adminPW";
        if(id.length()>0 && adminPW.length()>0){
            firebaseAuth.signInWithEmailAndPassword(id, adminPW)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"뒤로가기 버튼 에러",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),"뒤로가기 버튼 에러",Toast.LENGTH_SHORT).show();
        }

    }

}
