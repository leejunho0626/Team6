package com.example.helloroutine;

import android.content.Context;
import android.graphics.Color;
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



        FragmentView(Fragment_1);



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

            case 3:
                FragmentComplete fragmentComplete = new FragmentComplete();
                transaction.replace(R.id.fragment_challenge, fragmentComplete);
                transaction.commit();
                break;
        }

    }


}
