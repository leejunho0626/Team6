package com.example.helloroutine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FragmentUser extends Fragment {

    TextView txtID, txtUid;
    Button btnFriend, btnCopy;
    FirebaseAuth firebaseAuth;
    String strEmail;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        txtID = view.findViewById(R.id.txtUserID);
        txtUid = view.findViewById(R.id.txtUID);
        btnFriend = view.findViewById(R.id.btnFriend);
        btnCopy = view.findViewById(R.id.btnCopy);

        Intent intent = getActivity().getIntent();
        strEmail = intent.getStringExtra("email");

        if(firebaseAuth.getCurrentUser() != null){
            txtID.setText(firebaseAuth.getCurrentUser().getEmail());
        }
        txtUid.setText(firebaseAuth.getUid());

        //친구목록
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Friend.class);
                startActivity(intent);
            }
        });

        btnCopy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("uid", firebaseAuth.getUid());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity().getApplicationContext(), "UID가 복사되었습니다.", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }
}
