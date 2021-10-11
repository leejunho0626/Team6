package com.example.helloroutine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentUser extends Fragment {

    TextView txtID, txtUid;
    FirebaseAuth firebaseAuth;
    String strEmail, strUid;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        txtID = view.findViewById(R.id.txtUserID);
        txtUid = view.findViewById(R.id.txtUID);

        Intent intent = getActivity().getIntent();
        strEmail = intent.getStringExtra("email");

        txtID.setText(strEmail);


        return view;
    }
}
