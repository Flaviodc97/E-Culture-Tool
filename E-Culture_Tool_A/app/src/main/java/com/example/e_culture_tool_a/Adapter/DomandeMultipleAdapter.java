package com.example.e_culture_tool_a.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.example.e_culture_tool_a.R;

import java.util.List;

public class DomandeMultipleAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<DomandeMultiple> itemlist4;
    private Context context;

    public DomandeMultipleAdapter(List<DomandeMultiple> itemlist4,Context context){
        this.itemlist4=itemlist4;
        this.context=context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sbagliata_single_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sbagliatatxt.setText(itemlist4.get(position).getRisposte_errate().toString());
    }

    @Override
    public int getItemCount() {
        return itemlist4.size();
    }
}
