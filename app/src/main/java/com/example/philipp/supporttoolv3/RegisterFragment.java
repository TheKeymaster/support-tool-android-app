package com.example.philipp.supporttoolv3;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private TextView txtRMail, txtRFirstname, txtRLastname, txtRPassword, txtRPassword2;
    private Button btnRegisterUser;

    MainActivity mainActivity;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RegisterView = inflater.inflate(R.layout.fragment_register, container, false);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(R.string.title_registration);
        //######
        //Felder initialisieren
        txtRMail = RegisterView.findViewById(R.id.txtRMail);
        txtRFirstname = RegisterView.findViewById(R.id.txtRFirstname);
        txtRLastname = RegisterView.findViewById(R.id.txtRLastname);
        txtRPassword = RegisterView.findViewById(R.id.txtRPassword);
        txtRPassword2 = RegisterView.findViewById(R.id.txtRPassword2);

        btnRegisterUser = RegisterView.findViewById(R.id.btnRegisterUser);


        btnRegisterUser.setOnClickListener(this);


        return RegisterView;
    }

    //method to validate typed in Email Address
    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.btnRegisterUser) {

            //check if all txtFields are filled
            if (!(txtRMail.getText().toString().isEmpty() || txtRFirstname.getText().toString().isEmpty()
                    || txtRLastname.getText().toString().isEmpty() || txtRPassword.getText().toString().isEmpty()
                    || txtRPassword2.getText().toString().isEmpty())) {

                //check if typed in Mail is correct
                if (isEmailValid(txtRMail.getText().toString())) {

                    //check if both password field contain the same
                    if (txtRPassword.getText().toString().equals(txtRPassword2.getText().toString())) {

                        // SEND USER CREATE TO SERVER
                        new SendTask().execute("http://10.0.2.2/src/api/endpoints/post/createuser.php");
                        mainActivity.myToast(getString(R.string.UserCreatedSuccess));
                        mainActivity.setFragment(mainActivity.loginFragment);
                    }
                    else {
                        mainActivity.myToast(getString(R.string.PasswordNotMatching));
                    }
                }
                else {
                    mainActivity.myToast(getString(R.string.EmailWrongSyntax));
                }
            }
            else {
                mainActivity.myToast(getString(R.string.checkEnteredData));
            }



        }

    }

    //Send Task to Create user

    public class SendTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {

                return sendContent(params[0]);

            } catch (IOException e) {
                return getText(R.string.LoginFailureNoConn).toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task

            try {

                //If Conn. to Server is dead
                if (result.contains(getText(R.string.LoginFailureNoConn))) {
                    mainActivity.setAlert(getText(R.string.LoginFailureNoConn).toString());
                }
                else if (result.contains("success")) {
                    JSONObject obj = new JSONObject(result);
                    result = obj.getString("status");

                }
                /*else {
                    mainActivity.myToast("Falsche Zugangsdaten");
                }*/

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
            String urlParameters = "email=" + txtRMail.getText()+ "&" + "firstname=" + txtRFirstname.getText()+ "&" + "lastname=" + txtRLastname.getText()+ "&" + "password=" + txtRPassword.getText();
            String responseText;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
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
