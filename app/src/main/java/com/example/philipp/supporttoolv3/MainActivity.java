package com.example.philipp.supporttoolv3;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    public LoginFragment loginFragment;
    public RegisterFragment registerFragment;
    public TicketCreateFragment ticketCreateFragment;
    public TicketDetailFragment ticketDetailFragment;
    public TicketListFragment ticketListFragment;
    public UserFragment userFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainNav = findViewById(R.id.main_nav);
        mMainFrame = findViewById(R.id.main_frame);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        ticketCreateFragment = new TicketCreateFragment();
        ticketDetailFragment = new TicketDetailFragment();
        ticketListFragment  = new TicketListFragment();
        userFragment  = new UserFragment();

        setFragment(loginFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_User:
                        setFragment(userFragment);
                        return true;

                    case R.id.nav_Ticketlist:
                        setFragment(ticketListFragment);
                        return true;

                    case R.id.nav_Exit:
                        clickOnExit();
                        return true;

                        default:return false;


                }


            }
        });

    }

    private void clickOnExit() {
        finishAffinity();

    }


    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }







}
