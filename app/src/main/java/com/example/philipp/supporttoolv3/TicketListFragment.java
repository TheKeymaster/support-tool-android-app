package com.example.philipp.supporttoolv3;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketListFragment extends Fragment {

    private TextView txtTicketList;
    private TextView txtAuthkey;
    MainActivity mainActivity;
    ListView myList;
    ArrayList<TicketsClass> ticketsArrayList = new ArrayList<TicketsClass>();

    public TicketListFragment() {
        // Required empty public constructor
    }

    public class TicketsClass {
        public int id;
        public String title;
        public String status;

        public TicketsClass(int ids, String titles, int statuses) {
            id=ids;
            title=titles;
            status=getStatus(statuses);
        }

        public String getStatus(int intStatus) {
            switch (intStatus) {
                case 1: return status="Offen";

                case 2: return status="Wartend";

                case 3: return status="Geschlossen";

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
        View TicketListView = inflater.inflate(R.layout.fragment_ticket_list, container, false);


        mainActivity = (MainActivity) getActivity();

        //if TicketList Fragment is opened again --> Refresh
        mainActivity.ticketListFragment = new TicketListFragment();

        myList = TicketListView.findViewById(R.id.myList);

        new DownloadTask().execute("http://10.0.2.2/src/api/Endpoints/get/tickets.php?authkey=" + mainActivity.mAuthkey);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TicketsClass tc = (TicketsClass) parent.getAdapter().getItem(position);
                mainActivity.ticketDetailFragment.id = tc.id;
                mainActivity.getSupportFragmentManager().beginTransaction().remove(mainActivity.ticketDetailFragment).commit();
                mainActivity.setFragment(mainActivity.ticketDetailFragment);
            }
        });


        return TicketListView;
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment.downloadContent(params[0]);
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
                    TicketsClass resultRow = new TicketsClass(json_data.getInt("id"), json_data.getString("title"), json_data.getInt("status"));
                    ticketsArrayList.add(resultRow);
                }


                aa = new FancyAdapter();
                myList.setAdapter(aa);
            }
            catch (JSONException e){
                Log.e("log_tag", "Fehler " + e.toString());
            }

        }
    }




    class FancyAdapter extends ArrayAdapter<TicketsClass>{
        FancyAdapter(){
            super(mainActivity, android.R.layout.simple_list_item_1, ticketsArrayList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView==null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row, null);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.populateFrom(ticketsArrayList.get(position));

            return(convertView);
        }
    }

    class ViewHolder{
        public TextView tvId = null;
        public TextView tvTitle = null;
        public TextView tvStatus = null;

        ViewHolder(View row){
            tvId = row.findViewById(R.id.FirstText);
            tvTitle = row.findViewById(R.id.SecondText);
            tvStatus = row.findViewById(R.id.ThirdText);
        }

        void populateFrom(TicketsClass tc){
            tvId.setText(Integer.toString(tc.id));
            tvTitle.setText(tc.title);
            tvStatus.setText(tc.status);
        }
    }


}
