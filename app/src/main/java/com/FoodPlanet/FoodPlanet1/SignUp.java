package com.FoodPlanet.FoodPlanet1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {

    public FirebaseAuth mAuth;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        Button signup= findViewById(R.id.signup);
        Button backToLogin= findViewById(R.id.signup_to_login);
        // sign up case
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                registerNewUser();
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showProgressDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.progress_dialog);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    private void hideProgressDialog(){  alertDialog.dismiss(); }

    public void registerNewUser(){
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        EditText email=findViewById(R.id.txtemail);
        String person_email=email.getText().toString();

        EditText pass=findViewById(R.id.pass);
        String person_pass=email.getText().toString();

        mAuth.createUserWithEmailAndPassword(person_email, person_pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(SignUp.this, "registered successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else {
                    hideProgressDialog();
                    Toast.makeText(SignUp.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
