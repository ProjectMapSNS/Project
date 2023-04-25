package com.example.bottomnavigationbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.bottomnavigationbar.databinding.ActivityMainBinding;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Mainmenu_Home_Fragment());

        binding.menuBottomNavigation.setOnItemSelectedListener(item -> {

            switch ((item.getItemId())){
                case R.id.menu_home:
                    replaceFragment(new Mainmenu_Home_Fragment());
                    break;
                case R.id.menu_chat:
                    replaceFragment(new Mainmenu_Chat_Fragment());
                    break;
                case R.id.menu_profile:
                    replaceFragment(new Mainmenu_Profile_Fragment());
                    break;
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu_frame_layout,fragment);
        fragmentTransaction.commit();
    }
}