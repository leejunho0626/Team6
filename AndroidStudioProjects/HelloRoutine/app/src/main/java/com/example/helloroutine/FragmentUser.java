package com.example.helloroutine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentUser extends Fragment {

    TextView txtID, txtUid;
    Button btnFriend, btnCopy, btnLogout, btnRemove;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    LinearLayout layouty_fr;
    private GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        //구글 로그인한 정보 가져오기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        txtID = view.findViewById(R.id.txtUserID);
        txtUid = view.findViewById(R.id.txtUID);
        btnCopy = view.findViewById(R.id.btnCopy);
        btnLogout = view.findViewById(R.id.btnLogout);
        layouty_fr = view.findViewById(R.id.user_fr);

        if(firebaseAuth.getCurrentUser() != null){
            txtID.setText(firebaseAuth.getCurrentUser().getEmail());
        }
        txtUid.setText(firebaseAuth.getUid());

        btnCopy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("uid", firebaseAuth.getUid());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity().getApplicationContext(), "UID가 복사되었습니다.", Toast.LENGTH_LONG).show();

            }
        });

        layouty_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Friend.class);
                startActivity(intent);

            }
        });

        //로그아웃 버튼 클릭
        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); //파이어베이스 로그아웃
                signOut(); //구글 로그아웃
                //카카오 로그아웃
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                    }
                });
                Toast.makeText(getActivity().getApplicationContext(), "로그아웃이 되었습니다", Toast.LENGTH_SHORT).show();

                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                getActivity().finish();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

            }
        });




        return view;
    }

    //구글 로그아웃
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}
