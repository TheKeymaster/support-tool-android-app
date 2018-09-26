package com.example.philipp.supporttoolv3;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button btnLogin, btnRegister, btnChangeToTicketlist;
    private TextView txtLoginFragment, txtMail, txtPassword;
    MainActivity mainActivity = (MainActivity) getActivity();
    TicketListFragment ticketListFragment  = new TicketListFragment();
    //zu Testzwecken aufrufen können
    TicketCreateFragment ticketCreateFragment = new TicketCreateFragment();


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
            new DownloadTask().execute("http://10.0.2.2/src/api/Endpoints/get/tickets.php?authkey=31166d-85d82e-4ea258-3bfa60-c903f5");
        }
        else if (v.getId()==R.id.btnRegister) {
            txtLoginFragment.setText("Registerversuch: "  + txtMail.getText() + " " + txtPassword.getText());
        }
        else if (v.getId()==R.id.btnChangeToTicketList) {

            //open Fragment
            Fragment ticketListFragment = new TicketListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, ticketListFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }


    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, length);


            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            txtLoginFragment.setText(result);
        }
    }
}
