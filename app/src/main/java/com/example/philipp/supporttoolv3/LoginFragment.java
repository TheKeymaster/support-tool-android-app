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
        mainActivity.setTitle(R.string.title_login);

        //EditText fields
        txtMail = LoginView.findViewById(R.id.txtMail);
        txtPassword = LoginView.findViewById(R.id.txtPassword);

        //Login Button
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
            //Send POST Request to validate user and get authkey
            new SendTask().execute("https://support-tool-backend.brader.co.at/src/api/Endpoints/post/uservalidate.php");

        }
        else if (v.getId()==R.id.btnRegister) {
            //go to Register Fragment
            mainActivity.setFragment(mainActivity.registerFragment);

        }

    }
    //Convert InputStream to String
    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    //try to connect to php file at server and build a String
    public String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000 /* milliseconds */);
            conn.setConnectTimeout(5000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();


            StringBuilder sb = new StringBuilder();
            for (int c; (c = is.read()) >= 0;) {
                sb.append((char) c);
            }
            String contentAsString = sb.toString();

            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }




    //class for GET Requests
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
    //class for POST Requests
    public class SendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

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
                if (result.contains(getText(R.string.LoginFailureNoConn))) {
                    mainActivity.setAlert(getText(R.string.LoginFailureNoConn).toString());
                    mainActivity.mAuthkey = "";
                }
                //if authkey is in result
                else if (result.contains("authkey")) {
                    JSONObject obj = new JSONObject(result);
                    result = obj.getString("authkey");
                    mainActivity.mAuthkey=result;
                    //change to List of Tickets
                    mainActivity.setFragment(mainActivity.ticketListFragment);

                }
                else {
                    mainActivity.setAlert(getString(R.string.Login_incorrect));
                    mainActivity.mAuthkey = "";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String sendContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 5000;  //max length of return String
        String authkey = null;

        try {
            //add params to post requests
            String urlParameters = "email=" + txtMail.getText()+ "&" + "password=" + txtPassword.getText() + "&ismobile=true";
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
