package com.hardikdua.codeforces;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    Context context;
    private ArrayList<ContestsInfo> arrayList;
    private StorageReference myref;
    private FirebaseAuth auth;


    public SearchAdapter(Context context, ArrayList<ContestsInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView name,duration,starttime,date;
        ImageView share,notify;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.contest_name);
            duration = (TextView) itemView.findViewById(R.id.contest_duration);
            starttime = (TextView) itemView.findViewById(R.id.contest_starttime);
            date = (TextView)itemView.findViewById(R.id.contest_date);
            notify = (ImageView) itemView.findViewById(R.id.contest_notify);
            share = (ImageView) itemView.findViewById(R.id.contest_share);
        }
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.contestslayout,viewGroup,false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.SearchViewHolder searchViewHolder, int i) {
        searchViewHolder.name.setText(arrayList.get(i).getName());
        searchViewHolder.starttime.setText(arrayList.get(i).getStarttime());
        searchViewHolder.duration.setText(arrayList.get(i).getDuration());
        searchViewHolder.date.setText(arrayList.get(i).getDate());
        auth = FirebaseAuth.getInstance();
        final String id = arrayList.get(i).getId();
        final String d = arrayList.get(i).getDate();
        final String n = arrayList.get(i).getName();

        searchViewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, auth.getCurrentUser().getEmail());
                String res = "Don't forget to register at " + n + "@codeforces before "
                                    + d +
                                    " at https://codeforces.com/contest/contestRegistration/"+ id;
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contest Reminder");
                intent.putExtra(Intent.EXTRA_TEXT, res);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


        searchViewHolder.notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);

//                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, n);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
