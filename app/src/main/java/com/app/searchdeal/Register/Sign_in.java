package com.app.searchdeal.Register;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.app.searchdeal.Models.UserModel;
import com.app.searchdeal.Nav_Home;
import com.app.searchdeal.R;
import com.app.searchdeal.Utils.Common;
import com.app.searchdeal.databinding.SignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.reactivex.annotations.NonNull;

public class Sign_in extends AppCompatActivity {

    private FirebaseAuth mAuth;
    GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog pDialog;
    SignInBinding binding;
    DatabaseReference user_information;
    Dialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Intent intent= new Intent(Sign_in.this, Nav_Home.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.sign_in);

        dialog = new Dialog(this);
        // خاص بـ progressBar
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("يرجي التاكد من اتصال الانترنت ...");
        pDialog.setCancelable(true);

        mAuth = FirebaseAuth.getInstance();
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        createGoogleRequest();

        //زرار التسجيل بجوجل
        binding.btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                someActivityResultLauncher.launch(signInIntent);
            }
        });
        // create new normal account
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(Sign_in.this,Sign_up.class));
            }
        });
        //normal sign in
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalSignIn();
            }
        });
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback <ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Toast.makeText(Sign_in.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });

    // خاص بتسجيل جوجل
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener <AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // If sign in succeeds the auth state listener will be notified and logic to
                        // handle the signed in user can be handled in the listener.
                        user_information.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    if (!snapshot.child(mAuth.getUid()).exists()){
                                        openDialog();
                                    }else{
                                        Common.loggedUser = snapshot.child(mAuth.getUid()).getValue(UserModel.class);
                                        startActivity(new Intent(Sign_in.this,Nav_Home.class));
                                        finish();
                                    }
                                }
                                Toast.makeText(Sign_in.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                Toast.makeText(Sign_in.this, error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Sign_in.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // خاص بتسجيل جوجل
    private void createGoogleRequest() {
        // Configure Google Sign In
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }
    // التسجيل العادي
    private void normalSignIn(){
        String email = binding.emailEt.getText().toString();
        String password = binding.passwordEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.emailEt.setError("ادخل الايميل");
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordEt.setError("ادخل كلمة المرور");
        }else {
            pDialog.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                        @Override
                        public void onComplete(Task <AuthResult> task) {
                            if (task.isSuccessful()) {
                                    pDialog.dismiss();
                                    Toast.makeText(Sign_in.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Nav_Home.class);
                                    startActivity(intent);
                                    finish();
                            }else{
                                pDialog.dismiss();
                                Toast.makeText(Sign_in.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void openDialog() {
        dialog.setContentView(R.layout.dialog_comolete_google_sign_in_data);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_createAccount = dialog.findViewById(R.id.btn_sign_up_google);
        EditText phone_et = dialog.findViewById(R.id.phone_et);
        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_et.getText().equals("")){
                    phone_et.setError("ادخل رقم الهاتف");
                }else {
                    Common.loggedUser = new UserModel(mAuth.getUid(),mAuth.getCurrentUser().getEmail(),
                            mAuth.getCurrentUser().getDisplayName(),phone_et.getText().toString(),"default");

                    UserModel userModel = new UserModel(mAuth.getUid(),mAuth.getCurrentUser().getEmail(),
                            mAuth.getCurrentUser().getDisplayName(),phone_et.getText().toString(),"default");
                    user_information.child(mAuth.getUid()).setValue(userModel);
                    startActivity(new Intent(Sign_in.this, Nav_Home.class));
                    dialog.dismiss();
                }
            }
        });
    dialog.show();
    }
}