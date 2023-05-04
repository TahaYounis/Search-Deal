package com.app.searchdeal.Navigation_Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.searchdeal.Adapters.People_Adapter;
import com.app.searchdeal.Adapters.RecyclerView_InterFace;
import com.app.searchdeal.MessagesActivity;
import com.app.searchdeal.Models.ChatListModel;
import com.app.searchdeal.Models.ChatModel;
import com.app.searchdeal.Models.UserModel;
import com.app.searchdeal.People_List;
import com.app.searchdeal.R;
import com.app.searchdeal.databinding.ChatFragmentBinding;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat_Fragment extends Fragment implements RecyclerView_InterFace {

    ChatFragmentBinding binding;
    People_Adapter people_adapter;
    List<UserModel> mUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    List<ChatListModel> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment,container,false);
        View view = binding.getRoot();

        binding.recyclerViewId.setHasFixedSize(true);
        binding.recyclerViewId.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseAuth = FirebaseAuth.getInstance();

        userList = new ArrayList <>();
        reference = FirebaseDatabase.getInstance().getReference("chatList").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            userList.clear();
            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                ChatListModel chatList = dataSnapshot.getValue(ChatListModel.class);
                userList.add(chatList);
            }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnPeopleList.setOnClickListener(v -> startActivity(new Intent(getActivity(), People_List.class)));
        return view;
    }

    private void chatList() {
        mUser = new ArrayList <>();
        reference = FirebaseDatabase.getInstance().getReference("UserInformation");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        for (ChatListModel chatList: userList) {
                        if (userModel.getUid().equals(chatList.getId()))
                            mUser.add(userModel);
                    }
                }
                people_adapter = new People_Adapter(getActivity(),mUser,Chat_Fragment.this);
                binding.recyclerViewId.setAdapter(people_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}
