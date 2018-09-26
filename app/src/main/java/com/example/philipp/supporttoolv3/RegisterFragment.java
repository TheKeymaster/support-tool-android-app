package com.example.philipp.supporttoolv3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private TextView txtMail, txtFirstname, txtLastname, txtPassword;
    private Button btnRegisterUser;

    private TextView txtRegisterFragment;



    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RegisterView = inflater.inflate(R.layout.fragment_register, container, false);

        //######
        //Felder initialisieren
        txtMail = RegisterView.findViewById(R.id.txtMail);
        txtFirstname = RegisterView.findViewById(R.id.txtFirstname);
        txtLastname = RegisterView.findViewById(R.id.txtLastname);
        txtPassword = RegisterView.findViewById(R.id.txtPassword);

        btnRegisterUser = RegisterView.findViewById(R.id.btnRegisterUser);
        //#######
        txtRegisterFragment = RegisterView.findViewById(R.id.txtRegisterFragment);
        //#######

        btnRegisterUser.setOnClickListener(this);


        return RegisterView;
    }


    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.btnRegisterUser) {
            txtRegisterFragment.setText("Button tut etwas!");
        }

    }
}
