package com.example.helloroutine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentCalendar extends Fragment {

    GridView grid;
    GridAdapter adt;
    Calendar cal;
    TextView date;
    ImageButton pre, next;
    boolean img;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        adt = new GridAdapter(getActivity()); //어댑터 객체 생성
        grid = view.findViewById(R.id.grid); //그리드뷰 객체 참조
        date = view.findViewById(R.id.date);
        pre = view.findViewById(R.id.pre);
        next = view.findViewById(R.id.next);

        //달력표시
        cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m - 1, 1);
        show();

        pre.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
              pre();
            }
        });
        next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        return view;
    }
    //달력 표시
    private void show()
    {
        adt.clear();
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)+1;
        date.setText(y+"-"+m);
        // 1일의 요일
        int fText = cal.get(Calendar.DAY_OF_WEEK);
        // 빈 날짜 넣기
        for (int i = 1; i<fText; i++)
        {
            boolean img = false;
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m), img);
            adt.add(item);
        }
        // 이번 달 마지막 날
        int lDay = getLastDay(y, m);
        for (int i=1; i<=lDay; i++)
        {
            img = false;
            cal.set(y, cal.get(Calendar.MONTH), i);
            int text = cal.get(Calendar.DAY_OF_WEEK);
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m), Integer.toString(i), text, img);
            adt.add(item);
        }
        grid.setAdapter(adt);
    }

    // 이전 달
    public void pre()
    {
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)-1;
        cal.set(y, m, 1);
        show();
    }
    // 다음 달
    public void next()
    {
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)+1;
        cal.set(y, m, 1);
        show();
    }
    // 특정월의 마지막 날짜
    private int getLastDay(int year, int month)
    {
        Date d = new Date(year, month, 1);
        d.setHours(d.getDay()-1*24);
        SimpleDateFormat f = new SimpleDateFormat("dd");
        return Integer.parseInt(f.format(d));
    }
}
