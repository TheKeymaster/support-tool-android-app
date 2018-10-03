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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public Button btnLogin, btnRegister, btnChangeToTicketlist;
    private TextView txtMail, txtPassword;
    MainActivity mainActivity = (MainActivity) getActivity();
    TicketListFragment ticketListFragment  = new TicketListFragment();
    //zu Testzwecken aufrufen können
    TicketCreateFragment ticketCreateFragment = new TicketCreateFragment();
    private View LoginView;
    String responseString="";


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LoginView = inflater.inflate(R.layout.fragment_login, container, false);

        // MainActivity
        mainActivity = (MainActivity)getActivity();
        mainActivity.setTitle("Login");

        //EditText fields
        txtMail = LoginView.findViewById(R.id.txtMail);
        txtPassword = LoginView.findViewById(R.id.txtPassword);

        //Shared Prefs for Testing
        //txtMail.setText(mainActivity.mEmail);
        //txtPassword.setText(mainActivity.mPassword);

        //login Button
        btnLogin = (Button) LoginView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        //Register Button
        btnRegister = LoginView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);


        return LoginView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnLogin) {
            new SendTask().execute("http://10.0.2.2/src/api/endpoints/post/uservalidate.php");


        }
        else if (v.getId()==R.id.btnRegister) {

            mainActivity.setFragment(mainActivity.registerFragment);

        }

    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    public String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 5000;

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



    public class DownloadTask extends AsyncTask<String, Void, String> {

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

        }
    }

    public class SendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {

                return sendContent(params[0]);

            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task

            try {

                //If Conn. to Server is dead
                if (result.contains("Unable")) {
                    mainActivity.myToast(result);
                }
                else if (result.contains("authkey")) {
                    JSONObject obj = new JSONObject(result);
                    result = obj.getString("authkey");
                    mainActivity.setUserData(txtMail.getText().toString(), txtPassword.getText().toString(),result);
                    mainActivity.setFragment(mainActivity.ticketListFragment);

                }
                else {
                    mainActivity.myToast("Falsche Zugangsdaten");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String sendContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 5000;
        String authkey = null;

        try {
            String urlParameters = "email=" + txtMail.getText()+ "&" + "password=" + txtPassword.getText();
            String responseText;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000 /* milliseconds */);
            conn.setConnectTimeout(3000 /* milliseconds */);
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setUseCaches( false );

            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write(postData);

            }

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            //convert Inputstream to String
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;) {
                sb.append((char) c);
            }
            String response = sb.toString();

            return response;
            
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }
}
