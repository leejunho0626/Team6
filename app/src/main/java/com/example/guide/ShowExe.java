package com.example.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

public class ShowExe extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;


    //이미지
    private int[] images = new int[]{
            R.drawable.abdominalcrunch, R.drawable.abdominalcrunch2};
    private int[] images2 = new int[]{
            R.drawable.bicyclecrunch, R.drawable.bicyclecrunch};
    private int[] images3 = new int[]{
            R.drawable.russianstwist, R.drawable.russiantwitst};
    private int[] images4 = new int[]{
            R.drawable.ratpulldown1, R.drawable.ratpulldown2};
    private int[] images5 = new int[]{
            R.drawable.packdeckfly1, R.drawable.packdeckfly2};
    private int[] images6 = new int[]{
            R.drawable.packdecklateral1, R.drawable.packdecklateral2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_exe);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String date = bundle.getString("date");
        if (date.equals("1")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images));
        } else if (date.equals("2")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images2));
        }  else if (date.equals("3")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images3));
        } else if (date.equals("4")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images4));
        } else if (date.equals("5")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images5));
        } else {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images6));
        }

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        setupIndicators(images.length);
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }
}