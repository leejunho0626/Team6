package com.example.helloroutine;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    int mNumOfTabs; //탭의 갯수

    public FragmentAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.mNumOfTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentList tab1 = new FragmentList();
                return tab1;
            case 1:
                FragmentFav tab2 = new FragmentFav();
                return tab2;
            case 2:
                FragmentComplete tab3 = new FragmentComplete();
                return tab3;
            default:
                return null;
        }
        //return null;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
