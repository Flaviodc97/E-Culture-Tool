package com.example.e_culture_tool_a.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culture_tool_a.R;
import com.example.e_culture_tool_a.RecyclerItemClickListener;
import com.example.e_culture_tool_a.ShowLuoghi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.e_culture_tool_a.Adapter.LuogoAdapter;
import com.example.e_culture_tool_a.Models.Luogo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LuoghiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LuoghiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SearchView search;
    FirebaseFirestore fStore;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String nomeLuogo;
    String fotoLuogo;
    String descrLuogo;

    TextView list_luoghi;

    public LuoghiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LuoghiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LuoghiFragment newInstance(String param1, String param2) {
        LuoghiFragment fragment = new LuoghiFragment();
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
    List<Luogo> itemList=new ArrayList<Luogo>();

    String TAG="prova";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fStore = FirebaseFirestore.getInstance();
        View view=inflater.inflate(R.layout.fragment_luoghi, container, false);
        LuogoAdapter adapter=new LuogoAdapter(itemList,getContext());


        search=view.findViewById(R.id.Search);
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


        recyclerView=view.findViewById(R.id.firestore_list_luoghi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        

            Task<QuerySnapshot> query=fStore.collectionGroup("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot doc:task.getResult()){
                            Luogo luog=doc.toObject(Luogo.class);
                            itemList.add(luog);
                        }
                    }
                    recyclerView.setAdapter(adapter);
                }
            });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                list_luoghi = view.findViewById(R.id.list_luoghi);
                nomeLuogo = list_luoghi.getText().toString();
                for(int i = 0; i < itemList.size(); i++){
                    if(nomeLuogo.equals(itemList.get(i).getNome())){
                        descrLuogo = itemList.get(i).getDescrizione();
                        fotoLuogo = itemList.get(i).getPhoto();
                    }
                }
                Intent intent = new Intent(getActivity(), ShowLuoghi.class);
                intent.putExtra("nomeLuogo", nomeLuogo);
                intent.putExtra("descrLuogo", descrLuogo);
                intent.putExtra("fotoLuogo", fotoLuogo);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }
    public void FilterList(String Text,LuogoAdapter adapter){
        List<Luogo> filteredList=new ArrayList<Luogo>();
        for (Luogo luogo: itemList){
            if(luogo.getNome().toLowerCase().contains(Text.toLowerCase())){
                filteredList.add(luogo);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(getContext(), "Nessun Dato trovato", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(filteredList );
        }
    }

}