package com.smartline.smartline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class Inspect2 extends Fragment
{
    private static final String TAG = "Inspect2";
    private LinkedList<String> operations= new LinkedList<>();
    private LinkedList<String> descriptions= new LinkedList<>();
    private RecyclerView listview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currentIndex;
    private String selectedCk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.inspect2, container, false);

        //reset vectors
        operations.clear();
        descriptions.clear();
        currentIndex=-1;

        listview=view.findViewById(R.id.recyclerView);
        layoutManager=new LinearLayoutManager(getContext());

        selectedCk=((MainActivity) getActivity()).selectedCk;
        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        Query queryOverall= database.getReference("checklist/"+selectedCk);

        ValueEventListener overallFetch=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                operations.clear();
                    for (DataSnapshot opSnapshot : dataSnapshot.getChildren())
                    {
                        operations.add(opSnapshot.getKey());
                        if(operations.indexOf(opSnapshot.getKey())>0)
                        {
                            if(opSnapshot.child(("descrizione")).getValue().equals(""))
                            {
                                descriptions.add("User: " + opSnapshot.child("utente").getValue().toString());
                            }
                            else
                            {
                                descriptions.add(opSnapshot.child("descrizione").getValue().toString() + "\n \nUser: " + opSnapshot.child("utente").getValue().toString());
                            }
                        }
                        else
                            descriptions.add(opSnapshot.getValue().toString());

                        if (((opSnapshot.getValue()).toString().contains("stato=1"))||(opSnapshot.getValue()).toString().contains("stato=2"))
                        {
                            currentIndex=operations.size()-1; //perchè parto da 0
                        }
                    }
                MyAdapter mAdapter= new MyAdapter(getContext(),operations,descriptions,currentIndex);
                listview.setAdapter(mAdapter);
                listview.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //Failed to read value
                Toast.makeText(getContext(), ""+databaseError ,Toast.LENGTH_SHORT).show();
            }
        };

        queryOverall.addValueEventListener(overallFetch);

        return view;
    }
}


