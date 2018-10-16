package com.example.philipp.supporttoolv3;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketDetailFragment extends Fragment implements View.OnClickListener{

    public int id;
    private MainActivity mainActivity;
    ListView myMessageList;
    FloatingActionButton faBtnAddMessage;
    ArrayList<MessageClass> messageArrayList = new ArrayList<MessageClass>();


    public TicketDetailFragment() {
        // Required empty public constructor
    }

    //class to set rows

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
        mainActivity.setTitle(getString(R.string.Title_Messages) + " #" + id + " \uD83D\uDCAC");

        myMessageList = view.findViewById(R.id.messageList);
        faBtnAddMessage = view.findViewById(R.id.faBtnAddMessage);

        faBtnAddMessage.setOnClickListener(this);


        //If the Detail Fragment is opened again -->Refresh
        mainActivity.ticketDetailFragment = new TicketDetailFragment();

        new TicketDetailFragment.DownloadTask().execute("https://support-tool-backend.brader.co.at/src/api/Endpoints/get/messages.php?authkey=" + mainActivity.mAuthkey + "&ticketid=" + id);

        //Clear Array so details are not listed more than one time by hitting back button
        messageArrayList.clear();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.faBtnAddMessage) {
            mainActivity.setFragment(mainActivity.messageAddFragment = new MessageAddFragment());
        }
    }



    /*public void fillList(int id, String ak) {
        new TicketDetailFragment.DownloadTask().execute("https://support-tool-backend.brader.co.at/src/api/Endpoints/get/messages.php?authkey=" + ak + "&ticketid=" + id);
    }*/

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return mainActivity.loginFragment.downloadContent(params[0]);
            } catch (IOException e) {
                return getText(R.string.LoginFailureNoConn).toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{

                if (result.contains(getText(R.string.LoginFailureNoConn))) {
                    mainActivity.setAlert(getText(R.string.LoginFailureNoConn).toString());
                }
                else {

                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        MessageClass resultRow = new MessageClass(json_data.getString("title"), json_data.getInt("status"),
                                json_data.getString("createdat"), json_data.getString("createdby"), json_data.getString("body"));

                        messageArrayList.add(resultRow);
                    }
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

}

