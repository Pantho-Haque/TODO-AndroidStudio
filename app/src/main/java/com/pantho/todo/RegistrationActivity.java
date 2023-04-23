package com.pantho.todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {

    private ProgressDialog loader;

    private FirebaseAuth mAuth;

    private EditText regEmail, regPwd;
    private Button regBtn;
    private TextView gtlLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loader = new ProgressDialog(this);


        regEmail = findViewById(R.id.regEmail);
        regPwd = findViewById(R.id.regPassword);
        regBtn = findViewById(R.id.regBtn);
        gtlLayout = findViewById(R.id.gtlLayout);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String pwd = regPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    regEmail.setError("Email Required");
                    return;

                } else if (TextUtils.isEmpty(pwd)) {
                    regPwd.setError("Password required");
                    return;
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                            /*addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegistrationActivity.this, "success", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("error",e.getMessage());
                            Toast.makeText(RegistrationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
                    });*/
                    mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                Log.d("yee", "success");
                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Log.w("sad", "createUserWithEmail:failure", task.getException());
                                String err = "Error: " + task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, err, Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });


        gtlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

    }
}