package com.muraliyashu.hellomessenger;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verifyNumber extends AppCompatActivity {

    private String mVerificationId;
    private FirebaseAuth firebaseAuth;

    final sqlite_database db = new sqlite_database(verifyNumber.this);
    String getNumber;
    private EditText otp;
    LinearLayout resendDisable;
    private TextView otpTime, title1, title, resendColor, title2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);
        db.open();
        Bundle extras = getIntent().getExtras();
        getNumber = (String)extras.get("number");
        otpTime = (TextView) findViewById(R.id.otpTimer);
        otp = (EditText) findViewById(R.id.otp);
        title = (TextView)findViewById(R.id.title);
        title1 = (TextView)findViewById(R.id.title1);
        title2 = (TextView)findViewById(R.id.title2);
        resendColor = (TextView)findViewById(R.id.resendColor);
        resendDisable = (LinearLayout)findViewById(R.id.resendDisable);
        resendDisable.setEnabled(false);
        title.setText("Verify +91 "+getNumber);
        title1.setText("You've tried to register +91 "+getNumber+" recently. Wait before requesting an SMS with your code");

        title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (otp.getText().toString().length() == 6)
                    {
                        final String getCode = otp.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, getCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            });

            resendDisable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendOTP();
                    resendDisable.setEnabled(false);
                    resendColor.setTextColor(Color.parseColor("#C5C6C8"));
                }
            });
            sendOTP();
        }
        catch (Exception e)
        {
            String r = e.getMessage();
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(verifyNumber.this, "Phone number verification successsfull", Toast.LENGTH_SHORT).show();
                            try
                            {
                                db.DeleteAll("NUMBER");
                                ContentValues valuesDOB = new ContentValues();
                                valuesDOB.put("MOBILE", getNumber);
                                db.Insert(valuesDOB, "NUMBER");

                                Intent intent = new Intent(verifyNumber.this, initialProfile.class);
                                intent.putExtra("number",getNumber);
                                startActivity(intent);
                                MainActivity.fa.finish();
                                finish();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(verifyNumber.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(verifyNumber.this,"Invalid verification code",Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                });
    }
    public class MyCountDownTimer extends CountDownTimer
    {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            otpTime.setText("00:"+(int)millisUntilFinished / 1000);
        }
        @Override
        public void onFinish()
        {
            otpTime.setText("");
            resendColor.setTextColor(Color.parseColor("#000000"));
            resendDisable.setEnabled(true);
        }
    }

    private void sendOTP()
    {
        try{
            firebaseAuth = FirebaseAuth.getInstance();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    getNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    verifyNumber.this,new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential)
                        {
                            signInWithPhoneAuthCredential(credential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e)
                        {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(verifyNumber.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(verifyNumber.this, "Too many requests sent", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        @Override
                        public void onCodeSent(String verificationId,PhoneAuthProvider.ForceResendingToken token) {
                            mVerificationId = verificationId;
                            Toast.makeText(verifyNumber.this, "OTP has been sent to "+getNumber, Toast.LENGTH_SHORT).show();
                            MyCountDownTimer mine = new MyCountDownTimer(60000,1000);
                            mine.start();
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(verifyNumber.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
    }
}
