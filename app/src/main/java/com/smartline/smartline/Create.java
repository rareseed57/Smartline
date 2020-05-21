package com.smartline.smartline;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class Create extends Fragment {
    private static final String TAG = "Create";

    private TextView lCreate;
    private LinkedList<TextView> stepNumbers=new LinkedList<>();
    private LinkedList<EditText> indici=new LinkedList<>();
    private LinkedList<TableRow> rows = new LinkedList<>();
    private TableLayout tableLayout;
    private boolean empty=true;
    private boolean spec=false;
    private boolean invalid=true;
    private int state=0;
    private int conta=1;
    private LinkedList<String> availableUsers=new LinkedList<>();
    private LinkedList<String> availableUsersemail=new LinkedList<>();
    private LinkedList<EditText> steps = new LinkedList<>();
    private LinkedList<EditText> descriptions = new LinkedList<>();
    private LinkedList<Spinner> users= new LinkedList<>();
    private FloatingActionButton bAdd;
    private LinearLayout stepsLayout;
    private EditText e;
    public ProgressBar progressBar;
    public AlertDialog alertDialog;
    public FirebaseDatabase database;
    public Boolean ok=false;
    public String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create, container, false);
        FloatingActionButton bCreate;
        //reset view
        descriptions.clear();
        stepNumbers.clear();
        indici.clear();
        rows.clear();
        availableUsers.clear();
        steps.clear();
        users.clear();
        state=0;
        empty=true;
        spec=false;
        invalid=true;

        database= FirebaseDatabase.getInstance();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        //find views
        lCreate=view.findViewById(R.id.lCreate);
        tableLayout=view.findViewById(R.id.tableLayout);
        stepsLayout=view.findViewById(R.id.stepsLayout);
        stepsLayout.removeAllViews();
        bAdd=view.findViewById(R.id.bAdd);
        bCreate=view.findViewById(R.id.bCreate);
        e = new EditText(getContext());
        e.setHint("Step 1");
        e.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        e.setMaxLines(1);
        e.setLines(1);
        e.setSingleLine();
        conta=1;
        tableLayout.setVisibility(View.GONE);
        progressBar = new ProgressBar(getContext());
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(96, 96));

        steps.add(e);
        availableUsers.add("*user*");
        stepsLayout.addView(e);

        bAdd.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if(state==0)
                {
                    steps.add(conta, new EditText(getActivity()));
                    steps.get(conta).setHint("Step " + (conta + 1));
                    steps.get(conta).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    steps.get(conta).setMaxLines(1);
                    steps.get(conta).setLines(1);
                    steps.get(conta).setSingleLine();
                    stepsLayout.addView(steps.get(conta));
                    conta++;
                }
                else
                {
                    state=0;
                    lCreate.setText("Create new pipeline");
                    for(TableRow element:rows)
                    {
                        element.removeAllViews();
                    }
                    tableLayout.removeAllViews();
                    tableLayout.setVisibility(View.GONE);
                    for(EditText element:steps)
                    {
                        stepsLayout.addView(element);
                    }

                    bAdd.setImageResource(R.drawable.ic_playlist_add_white_24dp);
                }
            }
        });

        bCreate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                int i;
                int k;
                email= ((MainActivity) getActivity()).user.getEmail();
                if (state == 0)
                {
                    spec=false;
                    empty=true;
                    for(EditText element:steps)
                    {
                        if(!((((element).getText()).toString()).equals(""))) empty=false;
                        if(element.getText().toString().contains("=")) {spec=true;}
                    }

                    if (empty)
                    {
                        Toast.makeText(getContext(), "Write at least a step.", Toast.LENGTH_SHORT).show();
                    }
                    else if (spec)
                    {
                        Toast.makeText(getContext(), "Please avoid special characters.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        state = 1;
                        lCreate.setText("Assign users");
                        bAdd.setImageResource(R.drawable.ic_undo_white_24dp);
                        stepsLayout.removeAllViews();


                        //trim vector
                        for(EditText element:steps)
                        {
                            if(((((element).getText()).toString()).equals("")) && (steps.indexOf(element))<(conta-1))
                            {
                                i=steps.indexOf(element);
                                k=i+1;
                                while(((((steps.get(k)).getText()).toString()).equals("")) && (k<(conta-1)))
                                {
                                    k++;
                                }
                                steps.set(i, steps.get(k));
                                steps.set(k, new EditText(getActivity()));
                            }
                        }
                        indici.clear();
                        //delete empty EditText
                        for(EditText element:steps)
                        {
                            if((((element).getText()).toString()).equals(""))
                            {
                                indici.add(element);
                            }
                        }
                        conta = conta - (indici.size());
                        steps.removeAll(indici);
                        indici.clear();
                        stepNumbers.clear();

                        //show table
                        for(EditText element:steps)
                        {
                            Query queryUsers=((MainActivity) getActivity()).refUsers;
                            queryUsers.addValueEventListener(userFetch);
                            String txt;
                            int j=steps.indexOf(element);
                            rows.add(new TableRow(getActivity()));
                            stepNumbers.add(new TextView(getActivity()));
                            users.add(new Spinner((getActivity())));
                            descriptions.add(new EditText(getContext()));



                            tableLayout.addView((rows.get(j)));
                            tableLayout.addView(descriptions.get(j));

                            txt=(j+1)+". "+ ((element.getText()).toString());
                            descriptions.get(j).setHint("Type further information here");

                            stepNumbers.get(j).setTextColor(Color.parseColor("#FFFF5722"));
                            stepNumbers.get(j).setText(txt);
                            stepNumbers.get(j).setMaxLines(1);
                            stepNumbers.get(j).setLines(1);
                            stepNumbers.get(j).setSingleLine();
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, availableUsers);
                            users.get(j).setAdapter(arrayAdapter);

                            (rows.get(j)).addView((stepNumbers.get(j)),0);
                            (rows.get(j)).addView((users.get(j)),1);
                            (stepNumbers.get(j)).setLayoutParams(new TableRow.LayoutParams(440, ViewGroup.LayoutParams.WRAP_CONTENT));
                            stepNumbers.get(j).setMaxWidth(440);
                            (rows.get(j)).setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            (users.get(j)).setLayoutParams(new TableRow.LayoutParams(500, 200));
                            descriptions.get(j).setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        tableLayout.setVisibility(View.VISIBLE);
                        stepsLayout.addView(tableLayout);
                    }
                }
                else
                    {
                        invalid=false;
                        empty=false;
                        for (Spinner element:users)
                        {
                            int c=users.indexOf(element);
                            if(!element.equals(users.getLast())) {if(element.getSelectedItem().equals(users.get(c+1).getSelectedItem())) invalid=true;}

                            if(element.getSelectedItem()!=null)
                            if(((element.getSelectedItem()).toString()).equals("*user*")) empty=true;
                        }

                        if(empty)
                        {
                            Toast.makeText(getContext(), "Be sure to assign an user to each step.", Toast.LENGTH_SHORT).show();
                        }
                        else if(invalid)
                        {
                            Toast.makeText(getContext(), "Be sure not to assign the same user to consecutive steps.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            database=FirebaseDatabase.getInstance();
                            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                            final EditText edittext = new EditText(getContext());
                            edittext.setMaxLines(1);
                            edittext.setLines(1);
                            edittext.setSingleLine();
                            edittext.setLayoutParams(new ViewGroup.LayoutParams(736, ViewGroup.LayoutParams.WRAP_CONTENT));

                            edittext.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count)
                                {
                                    progressBar.setVisibility(View.VISIBLE);
                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                }

                                @Override
                                public void afterTextChanged(Editable s)
                                {
                                    Query checkName= database.getReference("checklist");

                                    ValueEventListener fetchNames = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            ok=true;
                                            for (DataSnapshot ckSnapshot:dataSnapshot.getChildren())
                                            {
                                                if((ckSnapshot.getKey()).equals(edittext.getText().toString().trim()))
                                                {
                                                    ok = false;
                                                }
                                            }
                                            if(!edittext.getText().toString().equals("")) {
                                                if(!edittext.getText().toString().contains("=")) {
                                                    if (ok) {
                                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                                        alertDialog.setMessage("This name can be used.");
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                        alertDialog.setMessage("This checklist exists already. Choose another name.");
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                                else
                                                    {
                                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    alertDialog.setMessage("The name can't contain some special symbols.");
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                            else
                                            {
                                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                alertDialog.setMessage("The name can't be empty.");
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    };

                                    checkName.addValueEventListener(fetchNames);

                                    progressBar.setVisibility(View.VISIBLE);
                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                                }
                            });

                            progressBar.setVisibility(View.GONE);
                            final LinearLayout container = new LinearLayout(getContext());
                            container.addView(new Space(getContext()),new LinearLayout.LayoutParams(64, ViewGroup.LayoutParams.MATCH_PARENT));
                            container.addView(edittext);
                            container.addView(progressBar);

                            alertBuilder.setMessage("The name can't be empty.");
                            alertBuilder.setTitle("Insert checklist name");

                            alertBuilder.setView(container);
                            alertBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    String ckName = edittext.getText().toString().trim();
                                    DatabaseReference ckreference=database.getReference("checklist/"+ckName);
                                    ckreference.child("admin").setValue(email);
                                    for (EditText element: steps)
                                    {
                                        int i=steps.indexOf(element);
                                        ckreference.child(element.getText().toString()).child("descrizione").setValue(descriptions.get(i).getText().toString());
                                        ckreference.child(element.getText().toString()).child("i").setValue(Integer.toString(i+1));
                                        ckreference.child(element.getText().toString()).child("utente").setValue(availableUsersemail.get((users.get(i).getSelectedItemPosition())-1));
                                        if(i==0)
                                        {
                                            ckreference.child(element.getText().toString()).child("stato").setValue("1");
                                        }
                                        else
                                        {
                                            ckreference.child(element.getText().toString()).child("stato").setValue("0");
                                        }
                                    }
                                    Toast.makeText(getContext(),"Succesfully created checklist: "+ckName,Toast.LENGTH_SHORT).show();
                                    ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).idHome);
                                }
                            });

                            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    container.removeAllViews();
                                    dialog.dismiss();
                                }
                            });

                            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog)
                                {
                                    container.removeAllViews();
                                }
                            });

                            alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    container.removeAllViews();
                                }
                            });
                            alertDialog = alertBuilder.create();
                            alertDialog.show();
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
            }
        });
        return view;
    }


    ValueEventListener userFetch=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            availableUsers.clear();
            availableUsers.add("*user*");
            for (DataSnapshot ckSnapshotU : dataSnapshot.getChildren())
            {
                if(!ckSnapshotU.getKey().replace("H","@").replace("P",".").equals(email))
                {
                    availableUsers.add(((ckSnapshotU.getKey().replace("P",".")).split("H"))[0]);
                    availableUsersemail.add(ckSnapshotU.getKey().replace("H", "@").replace("P", "."));
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {
            //Failed to read value
            Toast.makeText(getContext(), "Can't download users. Error code: "+databaseError ,Toast.LENGTH_SHORT).show();
        }
    };

}
