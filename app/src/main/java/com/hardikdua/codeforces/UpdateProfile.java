package com.hardikdua.codeforces;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {
    private EditText Name,College,Codechef,Codeforces,Hackerrank;
    private TextView Email;
    private ImageView profilepic;
    private Button save;
    private String profileImageurl;
    private Uri uriprofileimage;
    private static final int choose_Image =101;
    private FirebaseAuth auth;
    private DatabaseReference myref;
    private FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
        }
        else {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        Name = (EditText) findViewById(R.id.updateprofile_name);
        College = (EditText) findViewById(R.id.updateprofile_college);
        Codechef = (EditText) findViewById(R.id.updateprofile_codechef);
        Codeforces = (EditText) findViewById(R.id.updateprofile_codeforces);
        Hackerrank = (EditText) findViewById(R.id.updateprofile_hackerrank);
        profilepic = (ImageView) findViewById(R.id.updateprofile_img);
        save = (Button) findViewById(R.id.updateprofile_save);
        Email = (TextView) findViewById(R.id.updateprofile_email);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        myref = FirebaseDatabase.getInstance().getReference("Users");
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                UserInfo newInfo = new UserInfo(Name.getText().toString(),College.getText().toString(),
                                                Email.getText().toString(),Codeforces.getText().toString(),
                                                Codechef.getText().toString(),Hackerrank.getText().toString());
                myref.child(auth.getCurrentUser().getUid()).child("Info").setValue(newInfo);
            }
        });

    }

    private void saveUserInfo()
    {
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null && profileImageurl!=null)
        {
            UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder()
                    .setDisplayName(Name.getText().toString())
                    .setPhotoUri(Uri.parse(profileImageurl))
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Toast.makeText(getApplicationContext(),"Hey Vasu",Toast.LENGTH_SHORT).show();
        if(requestCode == choose_Image && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
//            Toast.makeText(getApplicationContext(),"Hey Dhammi",Toast.LENGTH_SHORT).show();

            uriprofileimage =data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriprofileimage);
                profilepic.setImageBitmap(bitmap);
                uploadImagetoFirebase();
            }
            catch (IOException e)
            {
                    e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo userInfo =dataSnapshot.child(auth.getCurrentUser().getUid()).child("Info").getValue(UserInfo.class);
                Name.setText(userInfo.getName());
                College.setText(userInfo.getCollege());
                Codechef.setText(userInfo.getCodechef());
                Codeforces.setText(userInfo.getCodeforces());
                Hackerrank.setText(userInfo.getHackerrank());
                Email.setText(userInfo.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Some error occurred",Toast.LENGTH_LONG).show();
            }
        });

        try {
            firebaseStorage.getReference().child("profilepics/"+auth.getCurrentUser().getUid()+".jpg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(profilepic);
//                            profileImageurl = uri.toString();
//                            Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
        }catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }


    private void showImageChooser()
    {
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Profile image"),choose_Image);
    }

    private void uploadImagetoFirebase()
    {
        final StorageReference profileImageref = FirebaseStorage.getInstance().getReference("profilepics/" +
                auth.getCurrentUser().getUid()+".jpg");
        if(uriprofileimage!=null)
        {
            profileImageref.putFile(uriprofileimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageurl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                    Toast.makeText(getApplicationContext(),profileImageurl,Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
