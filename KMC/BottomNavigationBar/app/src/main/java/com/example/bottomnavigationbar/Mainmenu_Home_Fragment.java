package com.example.bottomnavigationbar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class Mainmenu_Home_Fragment extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public Mainmenu_Home_Fragment() {
        // Required empty public constructor
    }
    public static Mainmenu_Home_Fragment newInstance(String param1, String param2) {
        Mainmenu_Home_Fragment fragment = new Mainmenu_Home_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private List<Fragment> fragments;
    private TabLayout tabLayout;
    private ViewTreeObserver viewTreeObserver;
    private ViewPager2 viewPager2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mainmenu__home_, container, false);

        fragments = new ArrayList<>();
        fragments.add(new View_Pager_1());
        fragments.add(new View_Pager_2());
        fragments.add(new View_Pager_3());

        viewPager2 = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabLayout);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getActivity(), fragments);
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

        return view;
    }

    private class MyPagerAdapter extends FragmentStateAdapter {
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