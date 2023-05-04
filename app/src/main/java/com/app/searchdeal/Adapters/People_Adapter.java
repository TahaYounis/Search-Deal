package com.app.searchdeal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.searchdeal.Models.UserModel;
import com.app.searchdeal.R;
import java.util.ArrayList;
import java.util.List;

public class People_Adapter extends RecyclerView.Adapter <People_Adapter.ViewHolder> implements Filterable {
    private Context context;
    private List <UserModel> list;
    private List<UserModel> menuModelListFilter;
    private RecyclerView_InterFace recyclerView_interFace;

    public People_Adapter(Context context, List <UserModel> list, RecyclerView_InterFace recyclerView_interFace) {
        this.context = context;
        this.list = list;
        this.recyclerView_interFace = recyclerView_interFace;
        menuModelListFilter = new ArrayList <>(list);
    }

    @NonNull
    @Override
    public People_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new People_Adapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull People_Adapter.ViewHolder holder, int position) {
        UserModel menuModel = list.get(position);
        holder.userName.setText(menuModel.getName());
        holder.userEmail.setText(menuModel.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName_tv);
            userEmail = itemView.findViewById(R.id.userEmail_tv);
            itemView.setOnClickListener(view -> recyclerView_interFace.onItemClick(getAdapterPosition()));
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (menuModelListFilter.size() == 0) menuModelListFilter = new ArrayList<>(list);

            List<UserModel> filterList = new ArrayList <>();
            if (charSequence ==null || charSequence.length()==0){
                // if search text is empty add all original list values to filterlist
                filterList.addAll(menuModelListFilter);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                // Perform search on whole original list
                for (UserModel menuu : menuModelListFilter){
                    if (menuu.getName().toLowerCase().contains(filterPattern)){
                        filterList.add(menuu);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }
        //run on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}