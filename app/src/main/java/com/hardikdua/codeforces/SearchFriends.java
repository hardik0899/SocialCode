package com.hardikdua.codeforces;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFriends extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<FriendsInfo> arrayList;
    EditText searchbar;
    String res = "";
    private DatabaseReference myref;
    private FriendsSearchAdapter friendsSearchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        getSupportActionBar().setTitle("Friends");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
        }
        else {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        recyclerView = (RecyclerView) findViewById(R.id.searchfriends_recycler);
        searchbar =(EditText) findViewById(R.id.searchfriends_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        arrayList = new ArrayList<>();
        myref = FirebaseDatabase.getInstance().getReference("Users");

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable st) {
                if(!st.toString().isEmpty())
                {
                    setAdapter(st.toString());
                }
                else
                {
                    arrayList.clear();
                }
            }
        });

    }

    private void setAdapter(final String searchstring)
    {
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                int counter = 0;
                for( DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                        String uid = snapshot.getKey();
                        UserInfo userInfo =snapshot.child("Info").getValue(UserInfo.class);
                        String profilepic = uid;
                        String name = userInfo.getName();
                        String email = userInfo.getEmail();
                        if(name.toLowerCase().contains(searchstring.toLowerCase()))
                        {
                            FriendsInfo friendsInfo = new FriendsInfo(name,email,profilepic);
                            arrayList.add(friendsInfo);
                            counter+=1;
                        }
                        else if(email.toLowerCase().contains(searchstring.toLowerCase()))
                        {
                            FriendsInfo friendsInfo = new FriendsInfo(name,email,profilepic);
                            arrayList.add(friendsInfo);
                            counter+=1;
                        }
                        if(counter == 10)
                            break;
                }

                friendsSearchAdapter = new FriendsSearchAdapter(getApplicationContext(),arrayList);
                recyclerView.setAdapter(friendsSearchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
