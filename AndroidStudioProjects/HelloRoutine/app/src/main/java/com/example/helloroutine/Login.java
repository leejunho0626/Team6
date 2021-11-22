package com.example.helloroutine;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import static android.content.ContentValues.TAG;

public class Login extends AppCompatActivity {

    EditText edtID, edtPw;
    Button btnLogin, btnRegister;
    ProgressDialog customProgressDialog;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SessionCallback sessionCallback;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    static SignInButton signInButton;
    LoginButton loginButton;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제
        setContentView(R.layout.login);
        //참조
        edtID = findViewById(R.id.loginID);
        edtPw = findViewById(R.id.loginPW);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        loginButton = findViewById(R.id.btnKakao);
        signInButton = findViewById(R.id.signInButton);

        firebaseAuth = FirebaseAuth.getInstance();

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //로딩창을 투명하게

        //자동로그인(로그아웃을 하지 않고 종료한 경우)
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class); //메인화면으로 이동
            startActivity(intent);
            finish();
        }

        //카카오 로그인 콜백 초기화
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);

        //회원가입 버튼 클릭
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class); //회원가입 화면으로 이동
                startActivity(intent);
            }
        });

        //일반 로그인 버튼 클릭
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalLogin(edtID.getText().toString(), edtPw.getText().toString()); //일반로그인(입력한 아이디, 비밀번호)
            }
        });

        //카카오 로그인 버튼 클릭
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, Login.this); //카카오 로그인 화면으로 이동

            }
        });

        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("구글계정으로 로그인");
        textView.setTextSize(18);
        textView.setPadding(240,0,0,0);

        //구글 로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //구글 로그인 버튼 클릭
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    //뒤로가기 버튼(종료)
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
    public void generalLogin(String id, String pw){
        id = edtID.getText().toString().trim(); //아이디
        pw = edtPw.getText().toString().trim(); //비밀번호
        //입력한 아이디, 비밀번호가 있는 경우
        if(id.length()>0 && pw.length()>0){
            firebaseAuth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(Login.this, MainActivity.class); //메인화면으로 이동
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
    
    //카카오 로그인 후 Firestore에 아이디 저장
    public void kakaoLogin(String id, String pw){
        //입력한 아이디, 비밀번호가 있는 경우
        if(id.length()>0 && pw.length()>0){
            firebaseAuth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Intent intent = new Intent(Login.this, MainActivity.class); //메인화면으로 이동
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                //Firestore에 아이디(Eamil) 저장
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                UserWrite userWrite = new UserWrite(user.getEmail());
                                db.collection("DB").document("User").collection(user.getUid()).document("ID").set(userWrite)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void avoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error.(getEmail)", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                saveFriend();
                                saveID();
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

    //카카오 로그인
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
                    // 로딩창 보여주기
                    customProgressDialog.show();
                    //카카오 로그인 시 파이어베이스에 계정 생성
                    final String id =result.getKakaoAccount().getEmail().toString().trim();
                    final String adminPW = "tempPW"; //임시 비밀번호
                    //FirebaseAuth에 등록
                    firebaseAuth.createUserWithEmailAndPassword(id, adminPW).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //FirebaseAuth에 등록된 계정이 없을 때
                            if (task.isSuccessful()) {
                                //Firestore에 아이디(Eamil) 저장
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                UserWrite userWrite = new UserWrite(user.getEmail());
                                db.collection("DB").document("User").collection(user.getUid()).document("ID").set(userWrite)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void avoid) {
                                                Log.d(TAG, "Add success(kakao)");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Add fail(kakao)");
                                            }
                                        });
                                saveFriend();
                                saveScore();
                                saveID();
                                Intent intent = new Intent(Login.this, MainActivity.class); //메인화면으로 이동
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
                            }
                            //FirebaseAuth에 등록된 계정이 있을 때
                            else {
                                kakaoLogin(id,adminPW); //로그인 후 Firestore에 아이디 저장
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
        //카카오 로그인 액티비티에서 넘어온 경우일 때 실행
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        //GoogleSignInApi.getSignInIntent에서 인텐트를 실행하여 반환된 결과
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google 로그인에 성공한 후 Firebase로 인증
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    //구글 계정으로 파이어베이스에 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로딩창 보여주기
                            customProgressDialog.show();
                            //로그인 성공, 로그인한 사용자 정보로 UI 업데이트
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            //로그인에 실패하면 사용자에게 메시지를 표시
                            updateUI(null);
                        }
                    }
                });
    }

    //구글 사용자 정보
    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class); //메인화면으로 이동
            startActivity(intent);
            //Firestore에 아이디(Eamil) 저장
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(user.getEmail());
            db.collection("DB").document("User").collection(user.getUid()).document("ID").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Log.d(TAG, "Add success(google)");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Add fail(google)");
                        }
                    });
            saveFriend();
            saveID();
            finish();
        }
    }

    public void saveFriend(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(user.getUid());
        db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid").document(user.getUid()).set(userWrite) //경로
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

    public void saveScore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserRank userRank = new UserRank(user.getEmail(),"0");
        db.collection("DB").document("User").collection(user.getUid()).document("Score").set(userRank) //경로
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

    public void saveID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(user.getUid());
        db.collection("DB").document("ID").collection(user.getEmail()).document("uid").set(userWrite) //경로
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

}
