package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.CameraActivity;
import com.example.myapplication.GalleryActivity;
import com.example.myapplication.MemberInfo;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MemberInitActivity extends AppCompatActivity {
    //private FirebaseAuth mAuth;

    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        profileImageView = findViewById(R.id.profileimageView);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.galleryButton).setOnClickListener(onClickListener);
        findViewById(R.id.pictureButton).setOnClickListener(onClickListener);
    }

    //뒤로가기로 꺼짐 방지
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0 : {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    profileUpdate();
                    break;
                case R.id.profileimageView:
                    CardView cardView = findViewById(R.id.buttonsCardView);
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.pictureButton:
                    myStartActivty(CameraActivity.class);
                    break;
                case R.id.galleryButton:
                    // Here, thisActivity is the current activity\
                    if (ContextCompat.checkSelfPermission(MemberInitActivity.this,
                            Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                Manifest.permission.READ_MEDIA_IMAGES)) {
                            ActivityCompat.requestPermissions(MemberInitActivity.this,
                                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                    1);
                        } else {
                            ActivityCompat.requestPermissions(MemberInitActivity.this,
                                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                    1);
                            startToast("권한을 허용해 주세요.");
                        }
                    } else {
                        //허용 되어있는 경우
                        myStartActivty(GalleryActivity.class);
                    }
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivty(GalleryActivity.class);
                } else {
                    startToast("권한을 허용해 주세요.");
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void profileUpdate() {
        final String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText)findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText)findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText)findViewById(R.id.addressEditText)).getText().toString();

        if(name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            // 유저 아이디에 따라 다른 디렉토리에 파일 저장
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if(profilePath == null) {
                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address); // 오버로드를 사용해 변수를 4개만 사용
                uploader(memberInfo);
            }else{
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));

                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask((task) ->  {
                        if (!task.isSuccessful()) {
                            Log.e("실패1", "실패");
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();

                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, downloadUri.toString());
                            uploader(memberInfo);
                        } else {
                            startToast("회원정보를 등록하는데 실패했습니다.");
                            Log.e("로그", "실패");
                        }
                    });
                }catch (FileNotFoundException e){
                    Log.e("로그 : ","에러 : " + e.toString());
                }
            }

        }else{
            startToast("회원정보를 입력해 주세요.");
        }
    }

    private void uploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();// Cloud Firestore 초기화

        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(aVoid -> {
                    startToast("회원정보 등록을 성공하였습니다.");
                    finish();
                })
                .addOnFailureListener(e -> {
                    startToast("회원정보 등록을 실패하였습니다.");
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    //c 액티비티로 이동
    private void myStartActivty(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}