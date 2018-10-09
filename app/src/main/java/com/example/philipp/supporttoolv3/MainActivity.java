package com.example.philipp.supporttoolv3;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Optional;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    public LoginFragment loginFragment;
    public RegisterFragment registerFragment;
    public TicketCreateFragment ticketCreateFragment;
    public TicketDetailFragment ticketDetailFragment;
    public TicketListFragment ticketListFragment;
    public MessageAddFragment messageAddFragment;
    public String mAuthkey= "", mEmail = "", mPassword = "";
    public String mID = "";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        



        mMainNav = findViewById(R.id.main_nav);
        mMainFrame = findViewById(R.id.main_frame);

        //create all Fragments
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        ticketCreateFragment = new TicketCreateFragment();
        ticketDetailFragment = new TicketDetailFragment();
        ticketListFragment  = new TicketListFragment();
        messageAddFragment = new MessageAddFragment();


        //Set Start Fragment
        setFragment(loginFragment);


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_User:
                        if (!mAuthkey.isEmpty()) {
                            setAlert(getString(R.string.AlreadyLoggedIn));

                        }
                        else {
                            setFragment(loginFragment);
                        }

                        return true;

                    case R.id.nav_Ticketlist:
                        if (mAuthkey.isEmpty()) {
                            setAlert(getString(R.string.NotLoggedIn));
                        }
                        else {
                            setFragment(ticketListFragment);
                        }

                        return true;

                    case R.id.nav_Logout:

                        setFragment(loginFragment);
                        if (mAuthkey.isEmpty()) {

                        }
                        else {
                            mAuthkey = "";
                            setAlert(getString(R.string.LoggedOut));
                        }

                        return true;

                    default:
                        return false;

                }
            }
        });

    }



    //ONSTOP for Shared Preferences
    @Override
    public void onStop()
    {
        super.onStop();
    }

    //Change Fragment
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //set on main_frame
        //EXTRA: Add to Back Stack --> now Android Return does work!! CAUSES thats Lists are filled twice or more
        fragmentTransaction.replace(R.id.main_frame, fragment).addToBackStack("tag");
        fragmentTransaction.commit();

    }

    //Alert Builder
    public void setAlert (String textToSee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(textToSee)
                .setTitle("Information")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //modified Toast to toast more simplier
    public void myToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

}
