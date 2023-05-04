package com.app.searchdeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.searchdeal.Adapters.Message_Adapter;
import com.app.searchdeal.Models.ChatModel;
import com.app.searchdeal.databinding.ActivityMessagesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    String uid, userName;
    ActivityMessagesBinding binding;
    FirebaseAuth firebaseAuth;
    Message_Adapter message_adapter;
    List<ChatModel> list;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_messages);

        binding.recyclerViewChat.setHasFixedSize(true);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));

        firebaseAuth = FirebaseAuth.getInstance();
        Bundle extra = getIntent().getExtras();
        if ( extra != null){
            uid = extra.getString("uid");
            userName = extra.getString("name");
            binding.nameTv.setText(userName);
        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.sendEt.equals(""))
                    binding.sendEt.setError("فارغ!");
                else
                    sendMessage(firebaseAuth.getUid(), uid, binding.sendEt.getText().toString());
                binding.sendEt.setText("");
            }
        });
        readMessages(firebaseAuth.getUid(), uid);
    }
    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap <>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("chat").push().setValue(hashMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatList")
                .child(firebaseAuth.getUid()).child(uid);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    chatRef.child("id").setValue(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void readMessages(String myId, String userId){
        list = new ArrayList <>();
        reference = FirebaseDatabase.getInstance().getReference("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getReceiver().equals(myId) && chatModel.getSender().equals(userId)
                            || chatModel.getReceiver().equals(userId) && chatModel.getSender().equals(myId))
                        list.add(chatModel);
                    message_adapter = new Message_Adapter(MessagesActivity.this,list);
                    binding.recyclerViewChat.setAdapter(message_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}