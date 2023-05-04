package com.app.searchdeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.app.searchdeal.Adapters.People_Adapter;
import com.app.searchdeal.Adapters.RecyclerView_InterFace;
import com.app.searchdeal.Models.UserModel;
import com.app.searchdeal.databinding.ActivityPeopleListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class People_List extends AppCompatActivity implements RecyclerView_InterFace {

    ActivityPeopleListBinding binding;
    private FirebaseDatabase db;
    private ArrayList <UserModel> list;
    private People_Adapter adapter;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_people_list);

        db = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.recyclerViewId.setHasFixedSize(true);
        binding.recyclerViewId.setLayoutManager(new LinearLayoutManager(People_List.this));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        getList();
    }
    private void  getList() {
        db.getReference("UserInformation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list=new ArrayList <>();
                adapter = new People_Adapter(People_List.this,list, People_List.this);
                binding.recyclerViewId.setAdapter(adapter);
                for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (!userModel.getEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                        list.add(userModel);
                        adapter.notifyItemInserted(list.size() - 1);
                        adapter.getItemCount();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(People_List.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        UserModel userModel = list.get(position);
        Intent intent = new Intent(People_List.this, MessagesActivity.class);
        intent.putExtra("name",userModel.getName());
        intent.putExtra("uid",userModel.getUid());
        startActivity(intent);
    }
}