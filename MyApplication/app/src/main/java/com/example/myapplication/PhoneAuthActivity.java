package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    EditText inputPhoneNumber, inputCheckNum;
    Button sendSmsButton, checkButton;
    String verificationId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        mAuth = FirebaseAuth.getInstance();
        inputPhoneNumber = findViewById(R.id.input_phone_num);
        inputCheckNum = findViewById(R.id.input_check_num);
        sendSmsButton = findViewById(R.id.send_sms_button);
        checkButton = findViewById(R.id.check_button);

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = inputPhoneNumber.getText().toString().trim();
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(PhoneAuthActivity.this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    sendVerificationCode(phoneNumber);
                }
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = inputCheckNum.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(PhoneAuthActivity.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(code);
                }
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            inputCheckNum.setText(code);
                            verifyCode(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(PhoneAuthActivity.this, "전화번호 인증 실패", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(PhoneAuthActivity.this, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(PhoneAuthActivity.this, "인증 성공", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneAuthActivity.this, SignUpActivity.class));
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneAuthActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}