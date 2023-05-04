package com.app.searchdeal.Register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.searchdeal.Models.UserModel;
import com.app.searchdeal.Nav_Home;
import com.app.searchdeal.R;
import com.app.searchdeal.Utils.Common;
import com.app.searchdeal.databinding.SignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Sign_up extends AppCompatActivity {

    private FirebaseAuth mAuth;
    SignUpBinding binding;
    ProgressDialog pDialog;
    DatabaseReference user_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.sign_up);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("يرجي التاكد من اتصال الانترنت ...");
        pDialog.setCancelable(true);

        mAuth = FirebaseAuth.getInstance();
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.nameEt.length()==0){
                    binding.nameEt.setError("ادخل الاسم");
                }else if (binding.emailEt.length()==0){
                    binding.emailEt.setError("ادخل الايميل");
                }else if (binding.passwordEt.length() < 6 ){
                    binding.passwordEt.setError("كلمة المرور يجب ان تكون اكبر من 6 حروف او ارقام");
                } else if (binding.phoneEt.length()==0){
                    binding.phoneEt.setError("ادخل رقم التليفون");
                }else {
                    pDialog.show();
                    mAuth.createUserWithEmailAndPassword(binding.emailEt.getText().toString(), binding.passwordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                                @Override
                                public void onComplete(Task <AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserModel userModel = new UserModel(mAuth.getUid(),mAuth.getCurrentUser().getEmail(),
                                                mAuth.getCurrentUser().getDisplayName(),binding.phoneEt.getText().toString(),
                                                "default");
                                        user_information.child(mAuth.getUid()).setValue(userModel);

                                        pDialog.dismiss();
                                        startActivity(new Intent(Sign_up.this, Nav_Home.class));
                                        Toast.makeText(Sign_up.this, "تم انشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {
                                        Toast.makeText(Sign_up.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        pDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

    }
}