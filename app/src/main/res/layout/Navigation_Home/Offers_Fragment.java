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
import com.app.searchdeal.databinding.OffersFragmentBinding;

public class Offers_Fragment extends Fragment {

    OffersFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.offers_fragment,container,false);
        View view = binding.getRoot();
        return view;
    }
}
