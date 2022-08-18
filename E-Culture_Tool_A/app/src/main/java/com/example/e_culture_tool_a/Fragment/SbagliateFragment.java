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

import com.example.e_culture_tool_a.Adapter.DomandeMultipleAdapter;
import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.example.e_culture_tool_a.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SbagliateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SbagliateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String oggettoID,zonaID, luogoID,domandaID;
    DomandeMultipleAdapter adapter1;


    RecyclerView sbagliate_rec;
    List<DomandeMultiple> sbagliate_list=new ArrayList<DomandeMultiple>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SbagliateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SbagliateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SbagliateFragment newInstance(String param1, String param2) {
        SbagliateFragment fragment = new SbagliateFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fStore = FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        Intent intent=getActivity().getIntent();

        Bundle extras = getActivity().getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");

        oggettoID = extras.getString("oggettoID");
        zonaID = extras.getString("zonaID");
        luogoID= extras.getString("luogoID");
        domandaID = extras.getString("id");;

        String user_id = fAuth.getCurrentUser().getUid();



        View view=inflater.inflate(R.layout.fragment_sbagliate, container, false);
        DomandeMultipleAdapter domAdapter=new DomandeMultipleAdapter(sbagliate_list,getContext());

        DocumentReference docRef= fStore.collection("utenti")
                .document(user_id)
                .collection("Luoghi")
                .document(luogoID)
                .collection("Zone")
                .document(zonaID)
                .collection("Oggetti")
                .document(oggettoID)
                .collection("DomandeMultiple")
                .document(domandaID);

        adapter1=new DomandeMultipleAdapter(sbagliate_list,getContext());
        sbagliate_rec=view.findViewById(R.id.firestore_list_sbagliate);
        sbagliate_rec.setHasFixedSize(true);
        sbagliate_rec.setLayoutManager(new LinearLayoutManager(getContext()));

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DomandeMultiple domMulti = documentSnapshot.toObject(DomandeMultiple.class);
                sbagliate_list.add(domMulti);
                sbagliate_rec.setAdapter(adapter1);
            }
        });

        return view;
    }
}