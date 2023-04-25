package com.example.view_pager_test;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private ViewTreeObserver viewTreeObserver;
    private ViewPager2 viewPager2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments = new ArrayList<>();
        fragments.add(new View_Pager_1());
        fragments.add(new View_Pager_2());
        fragments.add(new View_Pager_3());

        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabLayout);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this, fragments);
        viewPager2.setAdapter(myPagerAdapter);
        viewPager2.setOffscreenPageLimit(1);

        //tabLayout 밑에 fragment 화면에 나타나도록 margin 설정
        viewTreeObserver = tabLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //tabLayout Height 값 받기
                int tabLayoutHeight = tabLayout.getMeasuredHeight();
                ViewTreeObserver obs = tabLayout.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                //tabLayout Height 크기만큼 viewPager의 margin 설정
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager2.getLayoutParams();
                params.topMargin = tabLayoutHeight;
                viewPager2.setLayoutParams(params);
            }
        });

        //tabLayout 설정
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("지도");
                    break;
                case 1:
                    tab.setText("최신 게시글");
                    break;
                case 2:
                    tab.setText("팔로우한 게시글");
            }
        }).attach();
    }

    //view Pager에 맞게 화면 설정
    private class MyPagerAdapter extends FragmentStateAdapter{
        private List<Fragment> fragments;
        private static final int num_pages = 3;

        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments=fragments;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return num_pages;
        }
    }
}