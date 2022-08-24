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

import com.example.e_culture_tool_a.Models.Luogo;

public class LuogoAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<Luogo> itemList1;
    private Context context;

    public LuogoAdapter(List<Luogo> itemList,Context context) {
        this.itemList1=itemList;
        this.context=context;

    }
    public void setFilteredList(List<Luogo> filteredList){
        this.itemList1=filteredList;
        notifyDataSetChanged();
    };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.luoghi_single_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemtxt.setText(itemList1.get(position).getNome());
        holder.luoghi_options.setVisibility(View.INVISIBLE);

        if(itemList1.get(position).getPhoto()!= null) {
            Picasso.get().load(itemList1.get(position).getPhoto()).into(holder.luoghi_img);
        }
    }

    @Override
    public int getItemCount() {
        return itemList1.size();
    }

}
