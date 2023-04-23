package com.pantho.todo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ProgressDialog loader=new ProgressDialog(this);

        FirebaseAuth auth=FirebaseAuth.getInstance();


        EditText loginEmail = findViewById(R.id.loginEmail);
        EditText loginPwd = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView gtrLayout = findViewById(R.id.gtrLayout);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String pwd = loginPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email Required");
                } else if (TextUtils.isEmpty(pwd)) {
                    loginPwd.setError("Password Required");
                }else {
                    loader.setMessage("Login in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                finish();
                            }else{
                                String err="Error: "+ task.getException().toString();
                                Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });


        gtrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

    }
}