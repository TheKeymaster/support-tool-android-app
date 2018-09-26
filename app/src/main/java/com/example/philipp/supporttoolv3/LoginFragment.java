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

    private Button btnLogin, btnRegister, btnChangeToTicketlist;
    private TextView txtLoginFragment, txtMail, txtPassword;
    MainActivity mainActivity = (MainActivity) getActivity();
    TicketListFragment ticketListFragment  = new TicketListFragment();


    public LoginFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_login, container, false);



        //EditText fields
        txtMail = myView.findViewById(R.id.txtMail);
        txtPassword = myView.findViewById(R.id.txtPassword);

        //login Button
        btnLogin = (Button) myView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        //Register Button
        btnRegister = myView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        //Demo
        txtLoginFragment = myView.findViewById(R.id.txtLoginFragment);
        btnChangeToTicketlist = myView.findViewById(R.id.btnChangeToTicketList);
        btnChangeToTicketlist.setOnClickListener(this);


        return myView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnLogin) {
            txtLoginFragment.setText("Loginversuch: " + txtMail.getText() + " " + txtPassword.getText());
            }
        else if (v.getId()==R.id.btnRegister) {
            txtLoginFragment.setText("Registerversuch: "  + txtMail.getText() + " " + txtPassword.getText());
        }
        else if (v.getId()==R.id.btnChangeToTicketList) {
            Fragment ticketListFragment = new TicketListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, ticketListFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }
    }
}
