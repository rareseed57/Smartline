package com.smartline.smartline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.LinkedList;

public class Home extends Fragment
{
    private View line;
    private Boolean token=false;
    private String userid;
    private ListView listviewAdmin;
    private ListView listViewUser;
    private CheckBox cbAdmin;
    private FloatingActionButton fab;
    private TextView noItemAdmin;
    private TextView noItemUser;
    private ImageView icUser;
    private static final  String TAG = "Home";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.home, container, false);
        token=true;
        cbAdmin = view.findViewById(R.id.cbAdmin);
        fab = view.findViewById(R.id.fab);
        line=view.findViewById(R.id.line);
        listviewAdmin=view.findViewById(R.id.listviewAdmin);
        listViewUser=view.findViewById(R.id.listviewUser);
        noItemAdmin=view.findViewById(R.id.noItemAdmin);
        noItemUser=view.findViewById(R.id.noItemUser);
        icUser=view.findViewById(R.id.icUser);
        fab.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                token = false;
                ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).idCreate);
            }
        });
        cbAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
              if (isChecked)
              {
                  icUser.setVisibility(View.INVISIBLE);
                  listViewUser.setVisibility(View.INVISIBLE);
                  line.setVisibility(View.INVISIBLE);
                  noItemUser.setVisibility(View.GONE);
              }
              else
                  {
                      icUser.setVisibility(View.VISIBLE);
                      listViewUser.setVisibility(View.VISIBLE);
                      line.setVisibility(View.VISIBLE);
              }
              Refresh();
            }
        });


        if(((MainActivity) getActivity()).getLogged())
        {
            Refresh();
        }

        return view;
    }

    public void Refresh()
    {
        final LinkedList<String> adminChecklists = new LinkedList<>();
        final LinkedList<String> userChecklists= new LinkedList<>();
        userid=((MainActivity) getActivity()).getUser().getEmail();

            DatabaseReference refChecklist=((MainActivity) getActivity()).refChecklist;

            ValueEventListener adminFetch=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    adminChecklists.clear();
                    for (DataSnapshot ckSnapshotA : dataSnapshot.getChildren())
                        adminChecklists.add(ckSnapshotA.getKey());

                    AdaptAdmin(adminChecklists);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    //Failed to read value
                    Toast.makeText(getContext(), ""+databaseError ,Toast.LENGTH_SHORT).show();
                }
            };
            ValueEventListener userFetch=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(!cbAdmin.isChecked()) {
                        userChecklists.clear();
                        for (DataSnapshot ckSnapshotU : dataSnapshot.getChildren()) {

                            if (ckSnapshotU.getValue().toString().contains("utente=" + userid))
                                userChecklists.add(ckSnapshotU.getKey());
                        }

                        AdaptUser(userChecklists);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    //Failed to read value
                    Toast.makeText(getContext(), ""+databaseError ,Toast.LENGTH_SHORT).show();
                }
            };

            Query queryAdmin=refChecklist.orderByChild("admin")
                    .equalTo(userid);

            Query queryUser=refChecklist;

            queryAdmin.addValueEventListener(adminFetch);
            queryUser.addValueEventListener(userFetch);
    }

    public void AdaptAdmin(LinkedList<String> cks)
    {
        if(token && getActivity()!=null)
        {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cks);
            listviewAdmin.setAdapter(arrayAdapter);
            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                    token = false;
                    ((MainActivity) getActivity()).selectionAdmin = true;
                    ((MainActivity) getActivity()).selectedCk = (listviewAdmin.getItemAtPosition(i)).toString();
                    ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).idInspect1);
                }
            };
            listviewAdmin.setOnItemClickListener(onItemClickListener);
        }

        if(cks.isEmpty())
            noItemAdmin.setVisibility(View.VISIBLE);
        else noItemAdmin.setVisibility(View.GONE);
    }

    public void AdaptUser(LinkedList<String> cks)
    {
        if(token && getActivity()!=null)
        {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cks);
            listViewUser.setAdapter(arrayAdapter);
            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                    token = false;
                    ((MainActivity) getActivity()).selectionAdmin = false;
                    ((MainActivity) getActivity()).selectedCk = (listViewUser.getItemAtPosition(i)).toString();
                    ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).idInspect1);
                }
            };
            listViewUser.setOnItemClickListener(onItemClickListener);

            if(cks.isEmpty())
                noItemUser.setVisibility(View.VISIBLE);
            else noItemUser.setVisibility(View.GONE);
        }
    }

    public LinkedList<String> merge(LinkedList<String> list1, LinkedList<String> list2){

        for (String element:list2)
        {
            list1.add(element);
        }
        return list1;
    }
}

