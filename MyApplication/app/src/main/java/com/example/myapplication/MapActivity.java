package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;

    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(myId);
    private DatabaseReference userRefs = FirebaseDatabase.getInstance().getReference("users");
    private List<Marker> markers = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myRef.child("email").setValue(email);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();

        //FusedLocationProviderClient 초기화
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //내 위치 업데이트
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build();

        //
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    updateLocationUI();
                }
            }
        };
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivty(SignUpActivity.class);
                    break;
            }
        }
    };



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        startLocationUpdates();
    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        }
    }

    //권한 요청 시 호출
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            //거부하면 나오는 텍스트
            else {
                Toast.makeText(this, "권한을 허용해야 지도를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        updateLocationUI();
    }

    //현재 위치 정보에 대한 UI
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }



                userRefs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(Marker marker:markers){
                            marker.remove();
                        }
                        markers.clear();

                        for(DataSnapshot userSnapshot:snapshot.getChildren()){
                            String userId = userSnapshot.getKey();
                            double latitude = userSnapshot.child("latitude").getValue(double.class);
                            double longtitude = userSnapshot.child("longtitude").getValue(double.class);
                            LatLng location = new LatLng(latitude,longtitude);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(userId);
                            Marker marker = mMap.addMarker(markerOptions);
                            markers.add(marker);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //내 현재 위치 지도에 표시하는 기능
                mMap.setMyLocationEnabled(true);
                //내 위치를 중심으로 하는 버튼 활성화
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                //currentLatLng에 저장된 내 마지막 위치 갱신
                if(mLastKnownLocation!=null){
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    myRef.child("latitude").setValue(mLastKnownLocation.getLatitude());
                    myRef.child("longtitude").setValue(mLastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
                }
            }
            else{
                //기능들 비활성화
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();;
            }
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
    }

    //주기적으로 위치 업데이트
    private void startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.getMainLooper());
        }
        else{
            getLocationPermission();
        }
    }
    private void myStartActivty(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}