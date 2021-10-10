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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.login);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        checkBox = findViewById(R.id.saveLogin);
        btnKakao = findViewById(R.id.btnKakao);
        firebaseAuth = FirebaseAuth.getInstance();

        //카카오 로그인 콜백 초기화
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        //앱 실행 시 로그인 토큰이 있으면 자동으로 로그인 수행
        Session.getCurrentSession().checkAndImplicitOpen();

        //로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
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
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {

                    final String id = "k"+result.getKakaoAccount().getEmail().toString().trim();
                    final String adminPW = "adminPW";
                    firebaseAuth.createUserWithEmailAndPassword(id, adminPW).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("name", result.getNickname());

                                if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
                                    intent.putExtra("email", result.getKakaoAccount().getEmail());
                                else
                                    intent.putExtra("email", "none");
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("name", result.getNickname());

                                if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE)
                                    intent.putExtra("email", result.getKakaoAccount().getEmail());
                                else
                                    intent.putExtra("email", "none");
                                startActivity(intent);
                                finish();

                            }
                        }
                    });


                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
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
