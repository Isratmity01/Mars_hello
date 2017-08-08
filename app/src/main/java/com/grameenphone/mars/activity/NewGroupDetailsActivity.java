package com.grameenphone.mars.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.SelectedFriendListAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.fragment.MessageFragment;
import com.grameenphone.mars.model.Group;
import com.grameenphone.mars.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroupDetailsActivity extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final String STORAGE_URL = "gs://mars-e7047.appspot.com";
    public static final String ATTACHMENT = "attachments";

    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;


    private String GROUP_CHILD = "group_details";


    private static final int IMAGE_GALLERY_REQUEST = 101;

    private RecyclerView selectedFriend;
    private static SelectedFriendListAdapter selectedFriendAdapter;
Context context;
    private ProgressDialog progressDialog;
    private EditText groupName;
    private CircleImageView groupPic;


    private String groupPicUrl;
    public static ArrayList<User> finalSelectedMember = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_new_group_details);
context=NewGroupDetailsActivity.this;
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        dbHelper = new DatabaseHelper(getApplicationContext());


        progressDialog = new ProgressDialog(this);
        groupName = (EditText) findViewById(R.id.name_field);
        groupPic = (CircleImageView) findViewById(R.id.group_pic);

        if (finalSelectedMember.size() > 0) {
            finalSelectedMember.clear();
        }

        Intent i = getIntent();
        ArrayList<String> selectedmember = i.getStringArrayListExtra("users_uid");


        for (String uid : selectedmember) {
            User u = dbHelper.getUser(uid);
            finalSelectedMember.add(u);
        }


        selectedFriend = (RecyclerView) findViewById(R.id.selectedFriend);
        selectedFriendAdapter = new SelectedFriendListAdapter(this, finalSelectedMember);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        selectedFriend.setAdapter(selectedFriendAdapter);
        selectedFriend.setLayoutManager(llm);


        groupPic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoGalleryIntent();
                    }
                });

    }

    public static boolean addSelectedMember(User u) {
        boolean alReadySelected = false;
        String uid = u.getUid();
        for (int i = 0; finalSelectedMember.size() > i; i++) {
            if (uid.equals(finalSelectedMember.get(i).getUid())) {
                alReadySelected = true;
                finalSelectedMember.remove(i);
            }
        }

        if (!alReadySelected) {
            finalSelectedMember.add(u);
        }

        selectedFriendAdapter.notifyDataSetChanged();
        return !alReadySelected;
    }


    private void createGroup() {


        String name = groupName.getText().toString();

        if (name.length() > 0 && finalSelectedMember.size() > 1 && groupPicUrl != null) {
            User me = dbHelper.getMe();
            long time = System.currentTimeMillis();

            Map<String, Boolean> admin = new HashMap<String, Boolean>();
            admin.put(me.getUid(), true);

            Map<String, Boolean> members = new HashMap<String, Boolean>();
            members.put(me.getUid(), true);

            for (int i = 0; finalSelectedMember.size() > i; i++) {
                members.put(finalSelectedMember.get(i).getUid(), true);
            }


            String groupId = me.getUid() + "_" + time;
            Group group = new Group();


            group.setName(name);
            group.setAdmin(admin);
            group.setOwner(me.getUid());
            group.setMember(members);
            group.setGroupId(groupId);
            group.setGroupPic(groupPicUrl);


            mFirebaseDatabaseReference.child(GROUP_CHILD).child(groupId)
                    .setValue(group);
            dbHelper.addGroup(group);
            Intent intent = new Intent(this,MainActivityHolder.class);
            intent.putExtra("room_uid",groupId); //for example
            intent.putExtra("room_name",name); //for example
            intent.putExtra("room_type","grp");
            startActivity(intent);
            finish();


        } else {

            if (name.trim().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please Give a name", Toast.LENGTH_SHORT).show();
            }

            if (finalSelectedMember.size() < 2) {
                Toast.makeText(getApplicationContext(), "Please Select more than 1 member", Toast.LENGTH_SHORT).show();
            }

            if (groupPicUrl == null) {
                Toast.makeText(getApplicationContext(), "Please Select Image for Group", Toast.LENGTH_SHORT).show();
            }

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_URL).child(ATTACHMENT);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendPhotoFirebase(storageRef, selectedImageUri);

                    try {
                        InputStream is = getContentResolver().openInputStream(selectedImageUri);

                        Drawable d = Drawable.createFromStream(is, selectedImageUri.toString());
                        groupPic.setImageDrawable(d);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    //FIX ME
                }
            }
        }
    }

    private void sendPhotoFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            final long time = System.currentTimeMillis();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");


            try {

                InputStream image_stream = getContentResolver().openInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(image_stream);

                int size = (bitmap.getByteCount()) / 1000;

                int imageReductionRate = 100;
                if (size < 2000 && size > 1000) {
                    imageReductionRate = 60;
                } else if (size < 3000 && size > 2000) {
                    imageReductionRate = 50;
                } else if (size < 5000 && size > 3000) {
                    imageReductionRate = 30;
                } else if (size > 5000) {
                    imageReductionRate = 15;
                }


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, imageReductionRate, outputStream);
                byte[] data = outputStream.toByteArray();
                UploadTask uploadTask = imageGalleryRef.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        groupPicUrl = taskSnapshot.getDownloadUrl().toString();


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred());
                        progressDialog.setMessage("Uploading ..");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else {
            //FIXME
        }

    }


    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_next:
                createGroup();
                return true;

            case R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), GroupAddActivity.class);
        startActivity(intent);
        finish();
    }


}
