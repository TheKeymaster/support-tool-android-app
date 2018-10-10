package com.example.philipp.supporttoolv3;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
public class TicketCreateFragment extends Fragment implements View.OnClickListener {

    private EditText txtCTCreateMessage, txtCTTitle;
    private Button btnCreateTicket;
    MainActivity mainActivity;

    public TicketCreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_create, container, false);


        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(R.string.createTicket);

        txtCTCreateMessage = view.findViewById(R.id.txtCTCreateMessage);
        txtCTTitle = view.findViewById(R.id.txtCTTitle);
        btnCreateTicket = view.findViewById(R.id.btnCreateTicket);

        btnCreateTicket.setOnClickListener(this);


        return view;
    }



    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnCreateTicket) {
            if (!(txtCTTitle.getText().toString().isEmpty() || txtCTCreateMessage.getText().toString().isEmpty())) {
                new SendTask().execute("http://10.0.2.2/src/api/endpoints/post/createticket.php");
            }
            else {
                mainActivity.myToast(getText(R.string.checkEnteredData).toString());
            }

        }
    }

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

                    mainActivity.myToast(getString(R.string.TicketAddedSuccess));
                    //open TicketList so Customer could add Messages at other tickets
                    mainActivity.setFragment(mainActivity.ticketListFragment  = new TicketListFragment());

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
            String urlParameters = "authkey=" + mainActivity.mAuthkey+ "&" + "title=" + txtCTTitle.getText()+ "&" + "body=" + txtCTCreateMessage.getText();
            String responseText;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
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
