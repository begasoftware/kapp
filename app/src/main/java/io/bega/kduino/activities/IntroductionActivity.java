package io.bega.kduino.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viewpagerindicator.CirclePageIndicator;

import io.bega.kduino.R;
import io.bega.kduino.fragments.IntroductionFragmentPageAdapter;
import io.bega.kduino.fragments.IntroductionImageFragmentPageAdapter;
import io.bega.kduino.fragments.IntroductionTextFragmentPageAdapter;

public class IntroductionActivity extends AppCompatActivity {
    ViewPager viewPager;

    ViewPager viewTextPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introduction);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        viewPager = (ViewPager)findViewById(R.id.pager);

        viewTextPager= (ViewPager)findViewById(R.id.text_pager);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        FragmentManager fragmentManager =  getSupportFragmentManager();
        IntroductionImageFragmentPageAdapter introductionImageFragmentPageAdapter =
                new IntroductionImageFragmentPageAdapter(fragmentManager);
        int count = introductionImageFragmentPageAdapter.getCount();
        IntroductionTextFragmentPageAdapter introductionTextFragmentPageAdapter =
                new IntroductionTextFragmentPageAdapter(fragmentManager, count);



        viewPager.setAdapter(introductionImageFragmentPageAdapter);
        viewTextPager.setAdapter(introductionTextFragmentPageAdapter);
        CirclePageIndicator mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                viewTextPager.scrollTo(viewPager.getScrollX(), viewTextPager.getScrollY());
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewTextPager.setCurrentItem(viewPager.getCurrentItem(), false);
                }
            }
        });

        viewTextPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                viewPager.scrollTo(viewTextPager.getScrollX(), viewPager.getScrollY());
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewPager.setCurrentItem(viewTextPager.getCurrentItem(), false);
                }
            }
        });
    }

}
