package com.example.e_culture_tool_a.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.R;

import java.util.List;


import com.example.e_culture_tool_a.Models.Zone;

public class ZonaAdapter extends RecyclerView.Adapter<ViewHolder>{
    List<Zone> itemList2;
    private Context context;

    public ZonaAdapter(List<Zone> itemList, Context context) {
        this.itemList2=itemList;
        this.context=context;

    }
    public void setFilteredList(List<Zone> filteredList){
        this.itemList2=filteredList;
        notifyDataSetChanged();
    };



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.zone_single_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.zonetxt.setText(itemList2.get(position).getNome());
        holder.zone_desc_txt.setText(itemList2.get(position).getDescrizione());
    }

    @Override
    public int getItemCount() {
        return itemList2.size();
    }
}
