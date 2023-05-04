package com.app.searchdeal.Navigation_Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.app.searchdeal.R;
import com.app.searchdeal.databinding.NotificationFragmentBinding;

public class Notification_Fragment extends Fragment {

    NotificationFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.notification_fragment,container,false);
        View view = binding.getRoot();
        return view;
    }
}
