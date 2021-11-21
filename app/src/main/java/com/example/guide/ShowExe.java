package com.example.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

public class ShowExe extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;


    //상체
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

    //하체
    private int[] images7 = new int[]{
            R.drawable.backlunge, R.drawable.backlunge2};
    private int[] images8 = new int[]{
            R.drawable.highknees, R.drawable.highknees};
    private int[] images9 = new int[]{
            R.drawable.squart, R.drawable.squart2};
    private int[] images10 = new int[]{
            R.drawable.anglelegpress1, R.drawable.anglelegpres3};
    private int[] images11 = new int[]{
            R.drawable.hacksquart1, R.drawable.hacksquart2};
    private int[] images12 = new int[]{
            R.drawable.leg1, R.drawable.leg2};

    //전신
    private int[] images13 = new int[]{
            R.drawable.flank, R.drawable.flank};
    private int[] images14 = new int[]{
            R.drawable.flankjack, R.drawable.flankjack2};
    private int[] images15 = new int[]{
            R.drawable.mountainclimber, R.drawable.mountainclimber};
    private int[] images16 = new int[]{
            R.drawable.sideflankwalk, R.drawable.sideflankwalk2};
    private int[] images17 = new int[]{
            R.drawable.bantoverowing1, R.drawable.bantoverrowing2};
    private int[] images18 = new int[]{
            R.drawable.deadlift1, R.drawable.deadlift3};
    private int[] images19 = new int[]{
            R.drawable.hangingleg, R.drawable.hangingleg2};
    private int[] images20 = new int[]{
            R.drawable.sittedrowing, R.drawable.sittedrowing2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_exe);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        TextView txt=findViewById(R.id.txt1);


        layoutIndicator = findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String date = bundle.getString("date");
        if (date.equals("1")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images));
            txt.setText(R.string.top_body_1);
        } else if (date.equals("2")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images2));
        }  else if (date.equals("3")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images3));
        } else if (date.equals("4")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images4));
        } else if (date.equals("5")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images5));
        } else if (date.equals("6")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images6));
        } else if (date.equals("7")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images7));
        }else if (date.equals("8")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images8));
        }else if (date.equals("9")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images9));
        }else if (date.equals("10")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images10));
        }else if (date.equals("11")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images11));
        }else if (date.equals("12")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images12));
        }else if (date.equals("13")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images13));
        }else if (date.equals("14")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images14));
        }else if (date.equals("15")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images15));
        }else if (date.equals("16")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images16));
        }else if (date.equals("17")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images17));
        }else if (date.equals("18")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images18));
        }else if (date.equals("19")) {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images19));
        }else {
            sliderViewPager.setAdapter(new ImageSliderAdapter(this, images20));
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