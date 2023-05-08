package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.databinding.ActivityMainBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            myStartActivity(SignUpActivity.class);
        }
        else {
            replaceFragment(new HomeFragement(),false);

            binding.menuBottomNavigation.setOnItemSelectedListener(item -> {
                switch ((item.getItemId())) {
                    case R.id.menu_home:
                        replaceFragment(new HomeFragement(),false);
                        break;
                    case R.id.menu_feed:
                        replaceFragment(new FeedFragment(),true);
                        break;
                    case R.id.menu_chat:
                        replaceFragment(new ChatFragment(),true );
                        break;
                    case R.id.menu_profile:
                        replaceFragment(new ProfileFragment(),true);
                        break;
                }
                return true;

            });
        }
    }

    private void replaceFragment(Fragment fragment,boolean addToBackStack){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu_frame_layout,fragment);
        if(addToBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}