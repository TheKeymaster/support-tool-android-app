package com.example.philipp.supporttoolv3;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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


public class MessageAddFragment extends Fragment implements View.OnClickListener {

    MainActivity mainActivity;
    TextView txtAMAddMessage;
    Button btnAddMessage;
    ProgressBar progressBar;

    public MessageAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_add, container, false);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(R.string.Title_messageAdd);
        txtAMAddMessage  = view.findViewById(R.id.txtAMAddMessage);
        btnAddMessage = view.findViewById(R.id.btnAddMessage);
        progressBar = view.findViewById(R.id.progressBar);

        btnAddMessage.setOnClickListener(this);

        //String test = (String) mainActivity.mID;



        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnAddMessage) {

            new SendTask().execute("https://support-tool-backend.brader.co.at/src/api/Endpoints/post/createmessage.php");
            progressBar.setVisibility(View.VISIBLE);

        }
    }

    //##################################################################################
    //Send Task to add Message
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

                    mainActivity.myToast(getString(R.string.MessageAddedSucess));
                    //open TicketList so Customer could add Messages at other tickets
                    mainActivity.setFragment(mainActivity.ticketListFragment  = new TicketListFragment());

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
        int length = 500000;
        String authkey = null;

        try {
            String urlParameters = "authkey=" + mainActivity.mAuthkey+ "&" + "ticketid=" + mainActivity.mID + "&" + "body=" + txtAMAddMessage.getText();
            String responseText;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
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
