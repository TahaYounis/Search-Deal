package com.app.searchdeal.Navigation_Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.app.searchdeal.R;
import com.app.searchdeal.Welcome_Activity;
import com.app.searchdeal.databinding.NavHomeBinding;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Nav_Home extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient googleSignInClient;

    NavHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.nav_home);


        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //log out button
        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                googleSignInClient.signOut();

                Intent intent = new Intent(getApplicationContext() , Welcome_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(1,R.drawable.ic_home));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(2,R.drawable.ic_friends));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(3,R.drawable.ic_service));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(4,R.drawable.ic_offers));

        binding.bottomNavigationId.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

                Fragment fragment = null;
                switch (item.getId()){
                    case 1:
                        fragment = new Home_Fragment();
                        break;
                    case 2:
                        fragment = new Notification_Fragment();
                        break;
                    case 3:
                        fragment = new Account_Fragment();
                        break;
                    case 4:
                        fragment = new Chat_Fragment();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });
        binding.bottomNavigationId.show(1,true);
        binding.bottomNavigationId.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });
        binding.bottomNavigationId.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFirebaseAuth.signOut();
        googleSignInClient.signOut();

        Intent intent = new Intent(getApplicationContext() , Welcome_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}