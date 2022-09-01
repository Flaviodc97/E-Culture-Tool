package com.example.e_culture_tool_a.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.R;
    /*Classe utilizzata dagli Adapter  Luoghi,Zone e Oggetti */
public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView itemtxt;
    public ImageView luoghi_img;
    public ImageView luoghi_options;

    public TextView zonetxt;
    public TextView zone_desc_txt;
    public ImageView zone_options;

    public TextView oggettotxt;
    public TextView oggetto_desc_txt;
    public ImageView oggetto_img;
    public ImageView oggetti_options;

    public TextView sbagliatatxt;



    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemtxt=itemView.findViewById(R.id.list_luoghi);
        luoghi_img=itemView.findViewById(R.id.Image_Luoghi);
        luoghi_options=itemView.findViewById(R.id.LuoghiOptions);

        zonetxt=itemView.findViewById(R.id.list_my_zone);

        zone_options=itemView.findViewById(R.id.ZoneOptions);

        oggettotxt=itemView.findViewById(R.id.list_nome_oggetto);

        oggetto_img=itemView.findViewById(R.id.imageOggetti);
        oggetti_options=itemView.findViewById(R.id.options);
    }
}
