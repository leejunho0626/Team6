package com.example.helloroutine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentUser extends Fragment {

    TextView txtID, txtUid;
    Button btnFriend;
    FirebaseAuth firebaseAuth;
    String strEmail;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        txtID = view.findViewById(R.id.txtUserID);
        txtUid = view.findViewById(R.id.txtUID);
        btnFriend = view.findViewById(R.id.btnFriend);

        Intent intent = getActivity().getIntent();
        strEmail = intent.getStringExtra("email");

        if(firebaseAuth.getCurrentUser() != null){
            txtID.setText(firebaseAuth.getCurrentUser().getEmail());
        }
        txtUid.setText(firebaseAuth.getUid());

        //회원가입 버튼 클릭
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Friend.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
