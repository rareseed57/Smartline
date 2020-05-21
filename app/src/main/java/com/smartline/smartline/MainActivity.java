package com.smartline.smartline;

import android.content.DialogInterface;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity //implements View.OnClickListener
{
    public Boolean flagTab=true;
    public String selectedCk;
    public Boolean selectionAdmin;
    public FirebaseDatabase database;
    DatabaseReference refChecklist;
    DatabaseReference refUsers;
    public int tabSelected=0;
    public boolean logged=false;
    public FirebaseUser user;
    public int page;
    public int idLogin=0;
    public int idHome=1;
    public int idCreate=2;
    public int idInspect1=3;
    public int idInspect2=4;
    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private NonSwipeableViewPager mViewPager;
    public ConstraintLayout screen3;
    public TabLayout tabLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database=FirebaseDatabase.getInstance();
        refChecklist=database.getReference("checklist");
        refUsers=database.getReference("users");
        tabLayout=findViewById(R.id.tabLayout);
        screen3=findViewById(R.id.screen3);
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager= findViewById(R.id.container);
        mViewPager.setAllowedSwipeDirection(SwipeDirection.none);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position)
            {

                if(position==idCreate && screen3.getVisibility()==View.VISIBLE)
                {
                    mViewPager.setCurrentItem(idInspect1);
                    mViewPager.setAllowedSwipeDirection(SwipeDirection.right); //right
                    if(tabSelected!=0) {flagTab=false; selectTab(0);}
                }
                else {

                    if (position == idInspect1) {
                        mViewPager.setAllowedSwipeDirection(SwipeDirection.right); //right
                        if (tabSelected != 0) {
                            flagTab = false;
                            selectTab(0);
                        }
                    } else {
                        mViewPager.refresh();
                        mViewPager.setAllowedSwipeDirection(SwipeDirection.left); //left
                        if (tabSelected != 1) {
                            flagTab = false;
                            selectTab(1);
                        }
                    }
                }
            }
        });

        setupViewPager(mViewPager);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mViewPager.setOffscreenPageLimit(1);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(flagTab)
                {
                    if (tab.getPosition() == 0)
                    {
                        setViewPager(idInspect1);
                        tabSelected = 0;
                    }

                    else if (tab.getPosition() == 1)
                    {
                        setViewPager(idInspect2);
                        tabSelected = 1;
                    }
                }

                flagTab=true;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            if(logged)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure to log out?");
                builder.setIcon(R.drawable.ic_logout_black2_24dp);
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else
            {Toast.makeText(getBaseContext(), "You are not logged in yet.", Toast.LENGTH_SHORT).show();}
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(NonSwipeableViewPager viewPager)
    {
        SectionsStatePagerAdapter adapter= new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Login(), "Login");
        adapter.addFragment(new Home(), "Home");
        adapter.addFragment(new Create(), "Create");
        adapter.addFragment(new Inspect1(), "Inspect1");
        adapter.addFragment(new Inspect2(), "Inspect2");
        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber)
    {
        //if(fragmentNumber==idHome && (mViewPager.getCurrentItem()!=idCreate || mViewPager.getCurrentItem()!= idInspect1)) {mViewPager.refresh();}
        if(mViewPager.getCurrentItem()==idLogin) setupViewPager(mViewPager);
        if(mViewPager.getCurrentItem()==idCreate) mViewPager.refresh();

         mViewPager.setCurrentItem(fragmentNumber);
         page= fragmentNumber;

        if(fragmentNumber==idInspect1 || fragmentNumber==idInspect2)
         {
             screen3.setVisibility(View.VISIBLE);
             if(fragmentNumber==idInspect1)
             {
                 mViewPager.setAllowedSwipeDirection(SwipeDirection.right); //right
                 if(tabSelected!=0) {flagTab=false; selectTab(0);}
             }
             else
             {
                 mViewPager.setAllowedSwipeDirection(SwipeDirection.left); //left
                 if(tabSelected!=1) {flagTab=false; selectTab(1);}
             }
         }
         else
         {
             screen3.setVisibility(View.INVISIBLE);
             mViewPager.setAllowedSwipeDirection(SwipeDirection.none);
         }
    }

    @Override
    public void onBackPressed()
    {
        switch(mViewPager.getCurrentItem())
        {
            case 0:
                super.onBackPressed();
                break;
        case 1:
            super.onBackPressed();
            break;
        case 2:
            setViewPager(idHome);
            break;
         case 3:
             setViewPager(idHome);
             break;
            case 4:
                selectTab(0);
                setViewPager(idHome);
                break;
        }
    }

    public void setUser(FirebaseUser u)
    {
        user=u;
    }

    public boolean getLogged()
    {
        return(logged);
    }

    public void setLogged(boolean lg)
    {
        logged = lg;
    }

    public void selectTab(int index)
    {
        tabLayout.getTabAt(index).select();
        tabSelected=index;
    }
    public void logOut()
    {
        //Toast.makeText(this, "Successfully signed out.", Toast.LENGTH_SHORT).show();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Successfully signed out.", Toast.LENGTH_SHORT).show();
                        setLogged(false);
                        setViewPager(idCreate);
                        setViewPager(idLogin);
                    }
                });
    }

    public FirebaseUser getUser() {
        return user;
    }
}

