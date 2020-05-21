package com.smartline.smartline;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class Inspect1 extends Fragment
{
    private Button countdownButton;
    private TextView countdownText;
    private CountDownTimer countdownTimer;
    private long timeLeftInMilliseconds= 60000; //1 min
    private boolean timerRunning=false;
    private static final String TAG = "Inspect1";
    private String currentTask;
    private String myTask;
    private String nextTask;
    private String path;
    private String currentState;
    private TextView lOperation;
    private TextView lState;
    private LinkedList<DataSnapshot> operations= new LinkedList<>();
    private TextView lDescription;
    public String email;
    public Boolean flag=true;
    public Boolean selectionAdmin;
    private int nextIndex;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.inspect1, container, false);

            flag=true;
            operations.clear();
            currentState="-1";
            nextTask="-1";
            path=("checklist/"+((MainActivity) getActivity()).selectedCk);
            selectionAdmin=((MainActivity) getActivity()).selectionAdmin;

            final FirebaseDatabase database= FirebaseDatabase.getInstance();
            final DatabaseReference overall = database.getReference(path);
            email=((MainActivity) getActivity()).user.getEmail();

            lOperation= view.findViewById(R.id.lOp);
            lDescription= view.findViewById(R.id.ldescription);
            lState= view.findViewById(R.id.lstate);
            countdownButton = view.findViewById(R.id.button);
            countdownButton.setEnabled(false);
            countdownButton.setAlpha((float) 0.3);
            countdownText = view.findViewById(R.id.ltime);
            countdownButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //notifica
                    //startStop();
                    DatabaseReference storeState = database.getReference(path + "/" + currentTask);
                    DatabaseReference storenextState = database.getReference(path + "/" + nextTask);
                    if(currentState.equals("2"))
                    {
                        storeState.child("stato").setValue("3");
                        if(!nextTask.equals("-1")) storenextState.child("stato").setValue("1");
                    }
                    else if(currentState.equals("1"))
                    {
                        storeState.child("stato").setValue("2");
                    }
                }
            });


            ValueEventListener overallFetch=new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    operations.clear();
                    flag=true;
                    currentState="-1";
                    for (DataSnapshot opSnapshot : dataSnapshot.getChildren())
                    {
                        operations.add(opSnapshot);
                    }

                    for (DataSnapshot opSnapshot:operations)
                    {

                        if (opSnapshot.getValue().toString().contains(email))
                        {
                            myTask = opSnapshot.getKey();
                            nextIndex = operations.indexOf(opSnapshot)+1;
                            //Toast.makeText(getContext(),"next index: "+nextIndex,Toast.LENGTH_LONG).show();
                        }

                        if ((opSnapshot.getValue()).toString().contains("stato=1"))
                        {
                            currentTask = opSnapshot.getKey();
                            currentState = "1";
                        }

                        if ((opSnapshot.getValue()).toString().contains("stato=2"))
                        {
                            currentTask = opSnapshot.getKey();
                            currentState = "2";
                        }

                        if((opSnapshot.getValue()).toString().contains("i="+nextIndex))
                            nextTask= opSnapshot.getKey(); //Toast.makeText(getContext(),"next: "+nextTask,Toast.LENGTH_LONG).show();
                    }



                    if(!currentState.equals("-1")) lOperation.setText(currentTask);

                    if(!(myTask==null)) {
                        if (myTask.equals("admin"))
                        {
                            //Toast.makeText(getContext(),"sei admin",Toast.LENGTH_LONG).show();
                            //sei admin della ck
                            countdownButton.setEnabled(false);
                            countdownButton.setAlpha((float) 0.3);
                            //((MainActivity) getActivity()).tabLayout.getTabAt(0).setText("CURRENT TASK");
                            fillTxt(database, true);
                        }

                        else
                            {
                            //non sei admin
                            //((MainActivity) getActivity()).tabLayout.getTabAt(0).setText("MY TASK");
                            lOperation.setText(myTask);
                            fillTxt(database, false);
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    //Failed to read value
                    Toast.makeText(getContext(), ""+databaseError ,Toast.LENGTH_SHORT).show();
                }
            };
            Query queryOverall= overall;
            queryOverall.addValueEventListener(overallFetch);
            return view;
        }

    public void fillTxt(FirebaseDatabase database, final boolean admin) {
        if (currentState.equals("-1") && admin)
        {
            //Checklist completed
            lOperation.setText("Last operation: ");
            lDescription.setText(operations.getLast().getKey());
            lState.setText("Checklist completed.");
            countdownButton.setText("");
            countdownButton.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp);
            lOperation.setTextColor(Color.parseColor("#BA000000"));
            countdownButton.getBackground().setColorFilter((Color.parseColor("#00c0a8")), PorterDuff.Mode.MULTIPLY);
            countdownButton.setEnabled(false);
            countdownButton.setAlpha((float) 0.3);
            countdownButton.setScaleX((float) 1.2);
            countdownButton.setScaleY((float) 1.2);
        }
        else
            {
            //Checklist still running
                Query state;
            if(admin) state = database.getReference(path + "/" + currentTask); else state = database.getReference(path + "/" + myTask);

            ValueEventListener stateFetch = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot opSnapshot : dataSnapshot.getChildren()) {
                        if (((opSnapshot.getKey()).equals("stato")) && !admin)
                            currentState = (opSnapshot.getValue().toString());

                        if ((opSnapshot.getKey()).equals("descrizione"))
                            lDescription.setText((opSnapshot.getValue()).toString());

                        if (((opSnapshot.getKey()).equals("utente")) && admin) {
                            lState.setText("");
                            lOperation.setTextColor(Color.parseColor("#FFFF5722"));
                            switch (currentState)
                            {
                                case "1":
                                    lState.setText("It's " + (opSnapshot.getValue()).toString().split("@")[0] + "'s turn to start his activity.");
                                    countdownButton.setText("START");
                                    break;
                                case "2":
                                    lState.setText((opSnapshot.getValue()).toString().split("@")[0] + " is currently working on his task.");
                                    countdownButton.setText("FINISH");
                                    break;
                            }
                        }

                        if (!admin && currentState != null)
                        {
                            switch (currentState)
                            {
                                case "0":
                                    lState.setText("Waiting for your turn...");
                                    countdownButton.setText("WAITING");
                                    countdownButton.setBackgroundResource(R.drawable.roundedbutton);
                                    lOperation.setTextColor(Color.parseColor("#BA000000"));
                                    countdownButton.setEnabled(false);
                                    countdownButton.setAlpha((float) 0.3);
                                    break;
                                case "1":
                                    //Toast.makeText(getActivity(), "Your turn!", Toast.LENGTH_SHORT).show();
                                    lState.setText("It's your turn. Press START when you are ready.");
                                    countdownButton.setText("START");
                                    countdownButton.setBackgroundResource(R.drawable.roundedbutton);
                                    lOperation.setTextColor(Color.parseColor("#FFFF5722"));
                                    countdownButton.setAlpha(1);
                                    countdownButton.setEnabled(true);
                                    break;
                                case "2":
                                    lState.setText("You're up! Press FINISH when you are done.");
                                    countdownButton.setText("FINISH");
                                    countdownButton.setBackgroundResource(R.drawable.roundedbutton);
                                    lOperation.setTextColor(Color.parseColor("#FFFF5722"));
                                    countdownButton.setAlpha(1);
                                    countdownButton.setEnabled(true);
                                    break;
                                case "3":
                                    lState.setText("Done!");
                                    countdownButton.setText("");
                                    countdownButton.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp);
                                    lOperation.setTextColor(Color.parseColor("#BA000000"));
                                    countdownButton.getBackground().setColorFilter((Color.parseColor("#00c0a8")), PorterDuff.Mode.MULTIPLY);
                                    countdownButton.setEnabled(false);
                                    countdownButton.setAlpha((float) 0.3);
                                    countdownButton.setScaleX((float) 1.2);
                                    countdownButton.setScaleY((float) 1.2);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Failed to read value
                    Toast.makeText(getContext(), "" + databaseError, Toast.LENGTH_SHORT).show();
                }
            };
            state.addValueEventListener(stateFetch);
        }
    }

    private void startStop()
    {
        if (!timerRunning)
        {
            startTimer();
            countdownButton.setText("Stop");
        }
        else
        {
            stopTimer();
            countdownButton.setText("Start");
        }
    }

    private void startTimer()
    {
        countdownTimer= new CountDownTimer(timeLeftInMilliseconds, 1000)
        {
            @Override
            public void onTick(long l)
            {
                timeLeftInMilliseconds=l;
                updateTimer();
            }

            @Override
            public void onFinish()
            {
            }
        }.start();
        timerRunning=true;
    }

    private void stopTimer()
    {
        countdownTimer.cancel();
        timerRunning=false;
    }

    private void updateTimer()
    {
        int minutes = (int) timeLeftInMilliseconds/60000;
        int seconds = (int) ((timeLeftInMilliseconds/1000)-(minutes*60) );

        String timeLeftText;

        timeLeftText= ""+minutes;
        timeLeftText+= ":";
        if(seconds<10) timeLeftText+="0";
        timeLeftText+= seconds;

        countdownText.setText(timeLeftText);
    }
}

