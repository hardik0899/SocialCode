package com.hardikdua.codeforces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import javax.net.ssl.HttpsURLConnection;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference myref;
    private String codeforces;
    private FirebaseDatabase database;
    static int splashtime = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final String user,pass;
        SharedPreferences sharedPref = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        user = sharedPref.getString("Email","");
        pass = sharedPref.getString("Password","");
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        if(!user.isEmpty() && !pass.isEmpty())
        {
            auth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        myref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserInfo userInfo = dataSnapshot.child(auth.getCurrentUser().getUid())
                                        .child("Info").getValue(UserInfo.class);
//                                Toast.makeText(getApplicationContext(),"Database",Toast.LENGTH_SHORT).show();
                                codeforces = userInfo.getCodeforces();
                                Log.d("Database",codeforces);
                                fetchdata process = new fetchdata(codeforces);
                                process.execute();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                        Log.d("DatabaseOut","$$"+codeforces);

                    }
                    else
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(),Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        },splashtime);
                    }
                }
            });
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            },splashtime);
        }
    }
    private class fetchdata extends AsyncTask<Void,Void,String> {
        String result;

        String codeforces,rating,friends,contests;
        public fetchdata()
        {
            super();

        }
        public fetchdata(String a)
        {
            this.codeforces = a;
            this.rating = "0";
            this.friends = "0";
            this.contests = "0";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(getApplicationContext(),Profile.class);
            intent.putExtra("rating",rating);
            intent.putExtra("friends",friends);
            intent.putExtra("contests",contests);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{


                //// THIS IS FOR CODEFORCES RATING AND FRIENDS
                Log.d("InsideBackgroundId","$$$"+this.codeforces);
                URL url = new URL("http://codeforces.com/api/user.info?handles="+this.codeforces+";");
                HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                result = "";
                while (line!= null)
                {
                    line = bufferedReader.readLine();
                    result = result + line;
                }
                Log.d("Hey","$$$$"+result+"$$$");
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("OK"))
                {
//                    Toast.makeText(getApplicationContext(),"Valid",Toast.LENGTH_LONG).show();
                    Log.d("Valid","Jsonobject");
                    JSONArray temp = jsonObject.getJSONArray("result");
                    JSONObject c = temp.getJSONObject(0);
                     rating = c.getString("rating");
                     Log.d("Rating","$$$$"+rating);
                     friends = c.getString("friendOfCount");
                     Log.d("Friends","$$$"+friends);
                }
                else{
                    return null;
                }



                ////THIS IS FOR CODEFORCES CONTESTS

                result = "";
                String spec;
                URL url1 = new URL("https://codeforces.com/api/user.rating?handle="+this.codeforces);
                HttpURLConnection httpURLConnection1 = (HttpsURLConnection) url1.openConnection();
                InputStream inputStream1 = httpURLConnection1.getInputStream();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));
                line = "";
                while (line!=null)
                {
                    line = bufferedReader1.readLine();
                    result = result + line;
                }

                JSONObject jsonObject1 = new JSONObject(result);
                if(jsonObject1.getString("status").equals("OK"))
                {
                    JSONArray temp = jsonObject1.getJSONArray("result");
                    int l = temp.length();
                    contests = String.valueOf(l);
                }
                else
                {
                    return null;
                }

            }catch(MalformedURLException e) {
                Log.d("MALFORMED",e.getMessage());
            }catch (IOException e){
                Log.d("IOEXCEPTION",e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}