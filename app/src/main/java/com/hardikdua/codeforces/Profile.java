package com.hardikdua.codeforces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {
    private ImageView pic;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private TextView logout,coderating,codefriends,codecontests;
    private String codeforcesrating,codeforcesfriends,codeforcescontests;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dl = (DrawerLayout) findViewById(R.id.profile_dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
        }
        else {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        pic = (ImageView) findViewById(R.id.profile_profilepic);
        coderating = (TextView)findViewById(R.id.profile_codeforces_rating_num);
        codefriends = (TextView) findViewById(R.id.profile_codeforces_friends_num);
        codecontests = (TextView) findViewById(R.id.profile_codeforces_contest_num);
        Intent intent = getIntent();
        codeforcesrating = intent.getStringExtra("rating");
        codeforcesfriends = intent.getStringExtra("friends");
        codeforcescontests = intent.getStringExtra("contests");
        coderating.setText(codeforcesrating);
        Log.d("Rating","$$$$"+codeforcesrating);
        codefriends.setText(codeforcesfriends);
        codecontests.setText(codeforcescontests);
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        logout = (TextView) findViewById(R.id.navigation_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("MyData",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Email","");
                editor.putString("Password","");
                editor.commit();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.navigation_updateprofile)
                {
                    Toast.makeText(getApplicationContext(),"Update Profile",Toast.LENGTH_LONG).show();
                    startActivity( new Intent(getApplicationContext(),UpdateProfile.class));
                }
                else if(id == R.id.navigation_upcomingcontests)
                {
                    Toast.makeText(getApplicationContext(),"Upcoming Contests",Toast.LENGTH_LONG).show();
                    startActivity( new Intent(getApplicationContext(), UpcomingContests.class));
                }
                else if(id == R.id.navigation_searchfriend)
                {
                    Toast.makeText(getApplicationContext(),"Search Friend",Toast.LENGTH_LONG).show();
                    startActivity( new Intent(getApplicationContext(),SearchFriends.class));
                }
                else if(id == R.id.navigation_favourites)
                {
                    Toast.makeText(getApplicationContext(),"Favourites",Toast.LENGTH_LONG).show();
                }
                else if(id == R.id.navigation_myfriends)
                {
                    Toast.makeText(getApplicationContext(),"My Friends",Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
                storageReference.child("profilepics/"+auth.getCurrentUser().getUid()+".jpg")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try{
                            Glide.with(getApplicationContext())
                                    .load(uri).into(pic);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        }catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
