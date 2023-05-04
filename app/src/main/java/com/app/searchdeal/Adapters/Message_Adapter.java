package com.app.searchdeal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.searchdeal.Models.ChatModel;
import com.app.searchdeal.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class Message_Adapter extends RecyclerView.Adapter <Message_Adapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT= 0;
    public static final int MSG_TYPE_Right= 1;
    private Context context;
    private List <ChatModel> list;
    FirebaseAuth firebaseAuth;

    public Message_Adapter(Context context, List <ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Message_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_Right)
        return new Message_Adapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_right, parent, false));
        else
            return new Message_Adapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Message_Adapter.ViewHolder holder, int position) {
        ChatModel chatModel = list.get(position);
        holder.showMessage.setText(chatModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message_tv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (list.get(position).getSender().equals(firebaseAuth.getUid()))
            return MSG_TYPE_Right;
        else
            return MSG_TYPE_LEFT;

    }
}
