package com.example.helloroutine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
public class Login extends AppCompatActivity {

    EditText edtID, edtPw;
    Button btnLogin, btnRegister, btnKakao;
    CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    SignInButton signInButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor, editor2;
    private boolean saveID;
    String sID;
    private SessionCallback sessionCallback;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.login);
        edtID = findViewById(R.id.loginID);
        edtPw = findViewById(R.id.loginPW);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        checkBox = findViewById(R.id.saveLogin);
        btnKakao = findViewById(R.id.btnKakao);
        firebaseAuth = FirebaseAuth.getInstance();
        
        //일반 계정 자동 로그인
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        //카카오 로그인 콜백 초기화
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        //앱 실행 시 로그인 토큰이 있으면 자동으로 로그인 수행
        Session.getCurrentSession().checkAndImplicitOpen();


        //일반 로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(edtID.getText().toString(), edtPw.getText().toString());
            }
        });

        //회원가입 버튼
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        //카카오 로그인
        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, Login.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }

    //일반 로그인(파이어베이스)
    public void login(String id, String pw){
        id = edtID.getText().toString().trim();
        pw = edtPw.getText().toString().trim();
        if(id.length()>0 && pw.length()>0){
            firebaseAuth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
        }
    }

    //카카오 로그인
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {

                    }
                }
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }
                @Override
                public void onSuccess(MeV2Response result) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);

                    final String id = "kk"+result.getKakaoAccount().getEmail().toString().trim();
                    final String adminPW = "adminPW";
                    firebaseAuth.createUserWithEmailAndPassword(id, adminPW).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);

                                //카카오 계정의 이메일과 이름 가져오기
                                intent.putExtra("name", result.getNickname());
                                if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
                                    intent.putExtra("email", result.getKakaoAccount().getEmail());
                                else
                                    intent.putExtra("email", "none");
                            }
                            else {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);

                                //카카오 계정의 이메일과 이름 가져오기
                                intent.putExtra("name", result.getNickname());
                                if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
                                    intent.putExtra("email", result.getKakaoAccount().getEmail());
                                else
                                    intent.putExtra("email", "none");
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) { //카카오 로그인 액티비티에서 넘어온 경우일 때 실행
            super.onActivityResult(requestCode, resultCode, data);

            return;
        }
    }

}
