package com.example.helloroutine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.api.UserApi;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
public class Login extends AppCompatActivity {

    TextView google;
    EditText edtID, edtPw;
    Button btnLogin, btnRegister;
    ImageButton btnKakao;
    ImageView btnGoogle;
    CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    SignInButton signInButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean autoLogin;
    private SessionCallback sessionCallback;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        autoLogin = pref.getBoolean("AutoLogin",false);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setClipToOutline(true);



        //이전에 체크박스 설정을 저장시킨 기록이 있으면
        if(autoLogin)
            checkBox.setChecked(true);

        //체크박스가 클릭되어있을 때 자동로그인
        if(checkBox.isChecked()){
            if(firebaseAuth.getCurrentUser() != null){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        //체크박스 클릭
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()==true){
                    editor.putBoolean("AutoLogin",checkBox.isChecked());
                }
                else {
                    editor.clear();
                }
                editor.commit();
            }
        });


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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //구글 로그인
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
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
                                String value = "a";
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
    //카카오 로그인(파이어베이스)
    public void kLogin(String id, String pw){

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
                //카카오 로그인 성공
                @Override
                public void onSuccess(MeV2Response result) {

                    //카카오 로그인 시 파이어베이스에 계정 생성
                    final String id =result.getKakaoAccount().getEmail().toString().trim();
                    final String adminPW = "adminPW"; //임의 비밀번호
                    firebaseAuth.createUserWithEmailAndPassword(id, adminPW).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //등록된 계정이 없을 때
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                //카카오 계정의 이메일 가져오기
                                if(result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE){
                                    intent.putExtra("email", result.getKakaoAccount().getEmail());

                                }

                                else{
                                    intent.putExtra("email", "none");

                                }
                                startActivity(intent);
                            }
                            //등록된 계정이 있을 때
                            else {
                                kLogin(id,adminPW);
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

    //구글 로그인
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) { //카카오 로그인 액티비티에서 넘어온 경우일 때 실행
            super.onActivityResult(requestCode, resultCode, data);

            return;
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
