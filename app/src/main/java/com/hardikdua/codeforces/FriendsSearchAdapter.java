package com.hardikdua.codeforces;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FriendsSearchAdapter extends RecyclerView.Adapter<FriendsSearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<FriendsInfo> arrayList;
    StorageReference storageReference;

    public FriendsSearchAdapter(Context context, ArrayList<FriendsInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView Image,addfriend;
        TextView Name,Email;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.friends_image);
            Name = (TextView) itemView.findViewById(R.id.friends_name);
            Email = (TextView) itemView.findViewById(R.id.friends_email);
            addfriend = (ImageView) itemView.findViewById(R.id.friends_addfriend);
        }
    }

    @NonNull
    @Override
    public FriendsSearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchfriends_layout,viewGroup,false);
        return new FriendsSearchAdapter.SearchViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final FriendsSearchAdapter.SearchViewHolder searchViewHolder, int i) {
        searchViewHolder.Name.setText(arrayList.get(i).getName());
        searchViewHolder.Email.setText(arrayList.get(i).getEmail());
        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.defaultpic);
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilepics/"+arrayList.get(i).getProfilepic()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).setDefaultRequestOptions(requestOptions)
                        .asBitmap().load(uri).into(searchViewHolder.Image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
