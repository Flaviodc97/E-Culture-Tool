package com.example.e_culture_tool_a.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.R;

public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView itemtxt;
    public ImageView luoghi_img;

    public TextView zonetxt;
    public TextView zone_desc_txt;

    public TextView oggettotxt;
    public TextView oggetto_desc_txt;
    public ImageView oggetto_img;

    public TextView sbagliatatxt;



    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemtxt=itemView.findViewById(R.id.list_luoghi);
        luoghi_img=itemView.findViewById(R.id.Image_Luoghi);

        zonetxt=itemView.findViewById(R.id.list_my_zone);
        zone_desc_txt=itemView.findViewById(R.id.list_descrizione);

        oggettotxt=itemView.findViewById(R.id.list_nome_oggetto);
        oggetto_desc_txt=itemView.findViewById(R.id.list_descrizione);
        oggetto_img=itemView.findViewById(R.id.imageOggetti);

        sbagliatatxt=itemView.findViewById(R.id.sbagliata);
    }
}
