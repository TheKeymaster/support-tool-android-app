package com.example.philipp.supporttoolv3;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketDetailFragment extends Fragment implements View.OnClickListener{

    public int id;
    private MainActivity mainActivity;
    ListView myMessageList;
    EditText txtDMessage;
    Button btnSendMessage;
    ArrayList<MessageClass> messageArrayList = new ArrayList<MessageClass>();


    public TicketDetailFragment() {
        // Required empty public constructor
    }

    public class MessageClass {
        public String title;
        public String status;
        public String createdat;
        public String createdby;
        public String body;


        public MessageClass(String ti, int st, String dat, String by, String bo) {
            title=ti;
            status=getStatus(st);
            createdat=dat;
            createdby=by;
            body=bo;
        }

        public String getStatus(int intStatus) {
            switch (intStatus) {
                case 1: return status="offen";

                case 2: return status="in Bearbeitung";

                case 3: return status="geschlossen";

                default: return status="";
            }

        }
    }

    FancyAdapter aa = null;
    static ArrayList<String> resultRow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_detail, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Ticket Detail \uD83D\uDCAC");

        myMessageList = view.findViewById(R.id.messageList);
        txtDMessage = view.findViewById(R.id.txtDMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(this);


        //If the Detail Fragment is opened again -->Refresh
        mainActivity.ticketDetailFragment = new TicketDetailFragment();

        new TicketDetailFragment.DownloadTask().execute("http://10.0.2.2/src/api/Endpoints/get/messages.php?authkey=" + mainActivity.mAuthkey + "&ticketid=" + id);



        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnSendMessage) {
            new SendTask().execute("http://10.0.2.2/src/api/endpoints/post/createmessage.php");
            //mainActivity.setFragment(mainActivity.ticketListFragment  = new TicketListFragment());

        }


    }



    public void fillList(int id, String ak) {
        new TicketDetailFragment.DownloadTask().execute("http://10.0.2.2/src/api/Endpoints/get/messages.php?authkey=" + ak + "&ticketid=" + id);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                //LoginFragment loginFragment = new LoginFragment();
                return mainActivity.loginFragment.downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    MessageClass resultRow = new MessageClass(json_data.getString("title"), json_data.getInt("status"),
                            json_data.getString("createdat"), json_data.getString("createdby"), json_data.getString("body"));

                    messageArrayList.add(resultRow);
                }


                aa = new FancyAdapter();
                myMessageList.setAdapter(aa);
            }
            catch (JSONException e){
                mainActivity.myToast("JSON Error");
            }

        }
    }
    class FancyAdapter extends ArrayAdapter<MessageClass> {
        FancyAdapter(){
            super(mainActivity, android.R.layout.simple_list_item_1, messageArrayList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView==null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.ticket_detail_row, null);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.populateFrom(messageArrayList.get(position));

            return(convertView);
        }
    }

    class ViewHolder{
        public TextView txtTDName = null;
        public TextView txtTDTitel = null;
        public TextView txtTDDate = null;
        public TextView txtTDMessage = null;


        ViewHolder(View row){
            txtTDName = row.findViewById(R.id.txtTDName);
            txtTDTitel = row.findViewById(R.id.txtTDTitel);
            txtTDDate = row.findViewById(R.id.txtTDDate);
            txtTDMessage = row.findViewById(R.id.txtTDMessage);
        }

        void populateFrom(MessageClass mc){
            txtTDName.setText(mc.createdby.toString());
            txtTDTitel.setText(mc.title);
            txtTDDate.setText(mc.createdat);
            txtTDMessage.setText(mc.body);
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
                else if (result.contains("success")) {
                    JSONObject obj = new JSONObject(result);
                    result = obj.getString("status");

                    mainActivity.myToast("Message added successfully!");
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
        int length = 5000;
        String authkey = null;

        try {
            String urlParameters = "authkey=" + mainActivity.mAuthkey+ "&" + "ticketid=" + id + "&" + "body=" + txtDMessage.getText();
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

