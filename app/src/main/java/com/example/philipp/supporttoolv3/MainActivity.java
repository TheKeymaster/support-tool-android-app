package com.example.philipp.supporttoolv3;


import android.app.Activity;
import android.content.SharedPreferences;
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
    public UserFragment userFragment;
    public String mAuthkey= "", mEmail = "", mPassword = "";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Shared Preferences
        SharedPreferences prefs = getSharedPreferences("myfile", 0);
        //mEmail = prefs.getString("email", "");
        //mPassword = prefs.getString("password", "");
        



        mMainNav = findViewById(R.id.main_nav);
        mMainFrame = findViewById(R.id.main_frame);

        //create all Fragments
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        ticketCreateFragment = new TicketCreateFragment();
        ticketDetailFragment = new TicketDetailFragment();
        ticketListFragment  = new TicketListFragment();
        userFragment  = new UserFragment();

        //Set Start Fragment
        setFragment(loginFragment);


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_User:
                        if (!mAuthkey.isEmpty()) {
                            myToast("You're already logged in!");
                        }
                        else {
                            setFragment(loginFragment);
                        }

                        return true;

                    case R.id.nav_Ticketlist:
                        if (mAuthkey.isEmpty()) {
                            myToast("You're not logged in!");
                            //TODO
                            //SET FOCUS ON previous NAV ITEM....
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
                            myToast("You're logged out!");
                        }

                        return true;

                    default:
                        return false;

                }
            }
        });

    }

    //Method for Shared Preferences
    public void setUserData(String e, String p, String a) {
        mEmail = e;
        mPassword = p;
        mAuthkey = a;
    }

    //ONSTOP for Shared Preferences
    @Override
    public void onStop()
    {
        super.onStop();
        SharedPreferences myPrefs = this.getSharedPreferences("myfile", 0);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("email", mEmail);
        editor.putString("password", mPassword);
        editor.apply();
    }

    //Navigation Bar Click on Exit
    private void clickOnExit() {
        finishAffinity();

    }

    //Change Fragment
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //set on main_frame
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    //modified Toast to toast more simplier
    public void myToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

}
