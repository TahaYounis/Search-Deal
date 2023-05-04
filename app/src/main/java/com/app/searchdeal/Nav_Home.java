package com.app.searchdeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.app.searchdeal.Navigation_Home.Account_Fragment;
import com.app.searchdeal.Navigation_Home.Home_Fragment;
import com.app.searchdeal.Navigation_Home.Chat_Fragment;
import com.app.searchdeal.Navigation_Home.Notification_Fragment;
import com.app.searchdeal.Register.Sign_in;
import com.app.searchdeal.Utils.Common;
import com.app.searchdeal.databinding.NavHomeBinding;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Nav_Home extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient googleSignInClient;
    NavHomeBinding binding;
    DatabaseReference userInformation;
    LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.nav_home);

        userInformation = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // فتح GPS
        turnOnGPS();

        //log out button
        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            logOutMethod();
            }
        });

        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(1,R.drawable.ic_home));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(2,R.drawable.ic_notification));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(3,R.drawable.ic_chat));
        binding.bottomNavigationId.add(new MeowBottomNavigation.Model(4,R.drawable.ic_profile2));

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
                        fragment = new Chat_Fragment();
                        break;
                    case 4:
                        fragment = new Account_Fragment();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });
        binding.bottomNavigationId.show(1,true);
        Bundle extra = getIntent().getExtras();
        if (extra != null){
            binding.bottomNavigationId.show(extra.getInt("chatId"),true);
        }
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
        logOutMethod();
    }

    private void turnOnGPS(){
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)//إذا تم التعيين على "صحيح" وكان هذا الطلب "ذي أولوية". PRIORITY_HIGH_ACCURACY ، فسيؤدي ذلك إلى تأخير تسليم المواقع منخفضة الدقة الأولية لفترة زمنية قصيرة في حالة إمكانية تسليم موقع عالي الدقة بدلاً من ذلك.
                .setMinUpdateDistanceMeters(100f)// يضبط الحد الأدنى للمسافة المطلوبة بين تحديثات الموقع المتتالية. إذا لم يكن تحديث الموقع المشتق على الأقل المسافة المحددة بعيدًا عن تحديث الموقع السابق الذي تم تسليمه إلى العميل ، فلن يتم تسليمه. قد يسمح هذا أيضًا بتوفير إضافي للطاقة في ظل بعض الظروف
                .setMinUpdateIntervalMillis(2000)//يضبط الفاصل الزمني الأسرع المسموح به لتحديثات الموقع. قد تصل تحديثات الموقع بشكل أسرع من الفاصل الزمني المطلوب (setIntervalMillis (طويل)) ، ولكنها لن تصل أبدًا أسرع مما هو محدد هنا.
                .setIntervalMillis(4000)//يضبط الفاصل الزمني المطلوب لتحديثات الموقع. قد تصل تحديثات الموقع بشكل أسرع من هذا الفاصل الزمني (ولكن ليس أسرع من المحدد بواسطة setMinUpdateIntervalMillis (طويل)) أو أبطأ من هذا الفاصل الزمني (إذا تم تقييد الطلب على سبيل المثال).
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(Nav_Home.this,REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "تم تشغيل GPS", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Nav_Home.this, "التسجيل يتطلب تشغيل GPS", Toast.LENGTH_LONG).show();
                logOutMethod();
            }
        }
    }

    private void logOutMethod() {
        mFirebaseAuth.signOut();
        googleSignInClient.signOut();

        Intent intent = new Intent(Nav_Home.this , Sign_in.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}