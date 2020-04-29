package com.hardikdua.codeforces;

import android.os.AsyncTask;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpcomingContests extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<ContestsInfo> arrayList;
    String rest = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_contests);
        getSupportActionBar().setTitle("Contests");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
        }
        else {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        recyclerView = (RecyclerView) findViewById(R.id.contest_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        arrayList = new ArrayList<>();
        Log.d("Dekhte hain","####");
        fetchdata process = new fetchdata();
        process.execute();
        Log.d("Dekhte hain Bhakkk bsdk","####"+rest);

    }

    class fetchdata extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            SearchAdapter searchAdapter = new SearchAdapter(getApplicationContext(),arrayList);
            recyclerView.setAdapter(searchAdapter);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL("https://codeforces.com/api/contest.list?");
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                String result = "";
                while (line!= null)
                {
                    line = bufferedReader.readLine();
                    result = result + line;
                }
                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("status").equals("OK"))
                {
                    JSONArray temp = jsonObject.getJSONArray("result");
                    for(int i=0;i<temp.length();i++)
                    {
                        JSONObject ob = temp.getJSONObject(i);
                        if(!ob.getString("phase").equals("BEFORE"))
                        {
                            break;
                        }
                        ContestsInfo contestsInfo = new ContestsInfo(ob.getString("name"),
                                gettime(ob.getInt("durationSeconds")),
                                getstarttime(ob.getInt("startTimeSeconds")),
                                getstartdate(ob.getInt("startTimeSeconds")),
                                String.valueOf(ob.getInt("id")));
                        arrayList.add(contestsInfo);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Connection Failed...",Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public String gettime(int st)
    {
        String res = "";
        int hours = st/3600;
        st = st%3600;
        int min = st/60;
        int sec = st%60;
        if(hours<10)
        {
            res+="0"+String.valueOf(hours)+":";
        }
        else
        {
            res+=String.valueOf(hours)+":";
        }

        if(min<10)
            res+="0"+String.valueOf(min);
        else
            res+=String.valueOf(min);
        return res;
    }

    public String getstarttime(int st)
    {
        String res="";
        Calendar cal = Calendar.getInstance();
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        Date line = new Date(st*1000);
        String [] units = String.valueOf(line).split(" ");
        String[] temp = units[3].split(":");
        res = temp[0]+":"+temp[1];
        return res;
    }

    public String getstartdate(int st)
    {
        String res="";
        Calendar cal = Calendar.getInstance();
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        Date line = new Date(st*1000);
        String [] units = String.valueOf(line).split(" ");
        res = units[0]+"/"+units[1]+"/"+units[2];
        return res;
    }
}
