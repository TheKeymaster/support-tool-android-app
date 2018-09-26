package com.example.philipp.supporttoolv3;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button btnLogin;
    private TextView txtLoginFragment;
    private TicketListFragment ticketListFragment;


    public LoginFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) myView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        txtLoginFragment = myView.findViewById(R.id.txtLoginFragment);
        return myView;
    }

    @Override
    public void onClick(View v) {
        txtLoginFragment.setText("Test");
    }
}
