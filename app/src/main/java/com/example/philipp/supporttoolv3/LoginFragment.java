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
public class LoginFragment extends Fragment {

    private Button btnLogin;
    private TextView txtLoginFragment;
    private TicketListFragment ticketListFragment;


    public LoginFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);


        btnLogin = view.findViewById(R.id.btnLogin);
        txtLoginFragment = view.findViewById(R.id.txtLoginFragment);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLoginFragment = v.findViewById(R.id.txtLoginFragment);
                txtLoginFragment.setText("Test");
            }
        });

        return inflater.inflate(R.layout.fragment_login, container, false);
    }



}
