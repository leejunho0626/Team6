package com.example.helloroutine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentChallenge extends Fragment {

    Button btnList, btnFav, btnComplete;
    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;
    private final int Fragment_3 = 3;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_challenge, container, false);

        btnList = (Button) view.findViewById(R.id.btnList);
        btnFav = (Button) view.findViewById(R.id.btnFavorite);
        btnComplete = (Button) view.findViewById(R.id.btnComplete);

        FragmentView(Fragment_1);

        btnList.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(Fragment_1);
            }
        });

        btnFav.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(Fragment_2);
            }
        });

        btnComplete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentView(Fragment_3);
            }
        });



        return view;
    }

    private void FragmentView(int fragment){

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                FragmentList fragmentList = new FragmentList();
                transaction.replace(R.id.fragment_challenge, fragmentList);
                transaction.commit();
                break;

            case 2:
                FragmentFav fragmentFav = new FragmentFav();
                transaction.replace(R.id.fragment_challenge, fragmentFav);
                transaction.commit();
                break;

            case 3:
                FragmentComplete fragmentComplete = new FragmentComplete();
                transaction.replace(R.id.fragment_challenge, fragmentComplete);
                transaction.commit();
                break;
        }

    }


}
