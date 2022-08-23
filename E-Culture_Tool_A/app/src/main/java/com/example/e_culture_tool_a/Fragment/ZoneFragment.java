package com.example.e_culture_tool_a.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culture_tool_a.R;
import com.example.e_culture_tool_a.RecyclerItemClickListener;
import com.example.e_culture_tool_a.ShowLuoghi;
import com.example.e_culture_tool_a.ShowZone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.e_culture_tool_a.Adapter.ZonaAdapter;

import com.example.e_culture_tool_a.Models.Zone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SearchView search;

    FirebaseFirestore fStore;

    String nomeZona;
    String descrZona;

    TextView list_my_zone;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ZoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZoneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoneFragment newInstance(String param1, String param2) {
        ZoneFragment fragment = new ZoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    RecyclerView recyclerView;
    List<Zone> ZoneList=new ArrayList<Zone>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        View view=inflater.inflate(R.layout.fragment_zone, container, false);
        ZonaAdapter adapter=new ZonaAdapter(ZoneList,getContext());
        search=view.findViewById(R.id.SearchZone);
        search.clearFocus();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                FilterList(s,adapter);
                return true;
            }
        });


        recyclerView=view.findViewById(R.id.firestore_list_zone);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Task<QuerySnapshot> query=fStore.collectionGroup("Zone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot doc:task.getResult()){
                        Zone zone=doc.toObject(Zone.class);
                        ZoneList.add(zone);
                    }
                }
                recyclerView.setAdapter(adapter);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                list_my_zone = view.findViewById(R.id.list_my_zone);
                nomeZona = list_my_zone.getText().toString();
                for(int i = 0; i < ZoneList.size(); i++){
                    if(nomeZona.equals(ZoneList.get(i).getNome())){
                        descrZona = ZoneList.get(i).getDescrizione();
                    }
                }
                Intent intent = new Intent(getActivity(), ShowZone.class);
                intent.putExtra("nomeZona", nomeZona);
                intent.putExtra("descrZona", descrZona);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return view;
    }
    public void FilterList(String Text,ZonaAdapter adapter){
        List<Zone> filteredList=new ArrayList<Zone>();
        for (Zone zone: ZoneList){
            if(zone.getNome().toLowerCase().contains(Text.toLowerCase())){
                filteredList.add(zone);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(getContext(), "Nessun Dato trovato", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList);
        }
    }
}