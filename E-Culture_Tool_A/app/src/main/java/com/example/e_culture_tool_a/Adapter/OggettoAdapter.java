package com.example.e_culture_tool_a.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.example.e_culture_tool_a.Models.Oggetti;

public class OggettoAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<Oggetti> itemList3;
        private Context context;

        public OggettoAdapter(List<Oggetti> itemList, Context context) {
            this.itemList3=itemList;
            this.context=context;

        }
    public void setFilteredList(List<Oggetti> filteredList){
        this.itemList3=filteredList;
        notifyDataSetChanged();
    };
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.oggetti_single_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.oggettotxt.setText(itemList3.get(position).getNome());
        holder.oggetti_options.setVisibility(View.INVISIBLE);
        if(itemList3.get(position).getPhoto()!= null) {
            Picasso.get().load(itemList3.get(position).getPhoto()).into(holder.oggetto_img);
        }
    }

    @Override
    public int getItemCount() {
        return itemList3.size();
    }

}
