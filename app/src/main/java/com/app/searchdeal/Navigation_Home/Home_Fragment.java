package com.app.searchdeal.Navigation_Home;

import static android.app.Activity.RESULT_OK;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.app.searchdeal.MapsActivity;
import com.app.searchdeal.R;
import com.app.searchdeal.Register.Sign_in;
import com.app.searchdeal.Utils.Common;
import com.app.searchdeal.databinding.HomeFragmentBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home_Fragment extends Fragment {

    HomeFragmentBinding binding;
    DatabaseReference userInformation;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mFirebaseAuth;
    GoogleSignInClient googleSignInClient;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment,container,false);
        View view = binding.getRoot();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        userInformation = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        binding.btnFindPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
        /* قدم (M)Android 6.0 Marshmallow نموذج أذونات جديدا
        يتيح للتطبيقات طلب الأذونات من المستخدم في وقت التشغيل ، وليس قبل التثبيت.
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                // Permission taken
                getCurrentLocation();

            }else{
                // Permission not taken
                String [] permission = { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(),permission,REQUEST_CHECK_SETTINGS);
            }
        }
        return view;
    }
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Init location manager
        LocationManager locationManager =(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            // when location is enable
            // get last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener <Location>() {
                @Override
                public void onComplete(@NonNull Task <Location> task) {
                    //Init location
                    Location location = task.getResult();
                    //check condition
                    if (location != null){
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        userInformation.child(mFirebaseAuth.getUid()).child("latitude").setValue(latitude);
                        userInformation.child(mFirebaseAuth.getUid()).child("longitude").setValue(longitude);
                    }else {
                        // when location result is null
                        // Init location result

                        // خاص بـ location
                        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                                .setWaitForAccurateLocation(false)//إذا تم التعيين على "صحيح" وكان هذا الطلب "ذي أولوية". PRIORITY_HIGH_ACCURACY ، فسيؤدي ذلك إلى تأخير تسليم المواقع منخفضة الدقة الأولية لفترة زمنية قصيرة في حالة إمكانية تسليم موقع عالي الدقة بدلاً من ذلك.
                                .setMinUpdateDistanceMeters(100f)// يضبط الحد الأدنى للمسافة المطلوبة بين تحديثات الموقع المتتالية. إذا لم يكن تحديث الموقع المشتق على الأقل المسافة المحددة بعيدًا عن تحديث الموقع السابق الذي تم تسليمه إلى العميل ، فلن يتم تسليمه. قد يسمح هذا أيضًا بتوفير إضافي للطاقة في ظل بعض الظروف
                                .setMinUpdateIntervalMillis(2000)//يضبط الفاصل الزمني الأسرع المسموح به لتحديثات الموقع. قد تصل تحديثات الموقع بشكل أسرع من الفاصل الزمني المطلوب (setIntervalMillis (طويل)) ، ولكنها لن تصل أبدًا أسرع مما هو محدد هنا.
                                .setIntervalMillis(4000)//يضبط الفاصل الزمني المطلوب لتحديثات الموقع. قد تصل تحديثات الموقع بشكل أسرع من هذا الفاصل الزمني (ولكن ليس أسرع من المحدد بواسطة setMinUpdateIntervalMillis (طويل)) أو أبطأ من هذا الفاصل الزمني (إذا تم تقييد الطلب على سبيل المثال).
                                .build();

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location = locationResult.getLastLocation();
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                userInformation.child(mFirebaseAuth.getUid()).child("latitude").setValue(latitude);
                                userInformation.child(mFirebaseAuth.getUid()).child("longitude").setValue(longitude);

                            }
                        };
                        //request location update
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK){
            }else {
                mFirebaseAuth.signOut();
                googleSignInClient.signOut();

                Intent intent = new Intent(getActivity() , Sign_in.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getActivity(), "يجب السماح للتطبيق بالوصول لموقعك", Toast.LENGTH_LONG).show();
            }
        }
    }
}
