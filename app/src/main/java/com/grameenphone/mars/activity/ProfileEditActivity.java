package com.grameenphone.mars.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageView profilePic;
    private EditText name;
    private Button done;
    String photo;
    private String profilepicUrl = null;


    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final String STORAGE_URL = "gs://mars-e7047.appspot.com";
    public static final String ATTACHMENT = "attachments";

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    String photoUrl, Name;
    TextView NameTitle, BarTitle;
    private ImageButton Back, Save;
    public static final String USERS_CHILD = "users";
    private DatabaseReference mFirebaseDatabaseReference;

    private ProgressDialog progressDialog;
    private static final int IMAGE_GALLERY_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_profile_edit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        photoUrl = intent.getStringExtra("photoUrl");
        Name = intent.getStringExtra("name");

        profilePic = (ImageView) findViewById(R.id.user_pic);
        profilePic.setImageResource(R.drawable.user_pic);
        name = (EditText) findViewById(R.id.name_field);
        NameTitle = (TextView) findViewById(R.id.name_field_title);
        BarTitle = (TextView) findViewById(R.id.bartitle);
        done = (Button) findViewById(R.id.profile_done);
        done.setBackgroundResource(R.drawable.profile_button_shape);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mFirebaseUser = mAuth.getCurrentUser();
        }


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo(view);
            }
        });


    }

    public void saveInfo(View v) {
        String user_name = name.getText().toString();

        if ((!user_name.isEmpty() && photoUrl != null) || (!user_name.isEmpty() && profilepicUrl != null)) {


            String phone = mFirebaseUser.getPhoneNumber();
            if (profilepicUrl != null) {
                photo = profilepicUrl;
            } else photo = photoUrl;

            String uid = mFirebaseUser.getUid();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileEditActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LoginName", uid);
            editor.apply();

            final User user = new User(uid, user_name, phone, photo);

            user.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());


            boolean success = false;
            try {
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabaseReference.child(USERS_CHILD).child(mFirebaseUser.getUid())
                        .setValue(user);


                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.addMe(user);


                success = true;
            } catch (Error ignored) {

            }

            if (success) {
                Intent intent = new Intent(ProfileEditActivity.this, MainActivityHolder.class);
                startActivity(intent);
                finish();
            }


        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ProfileEditActivity.this);
            alert.setTitle("");
            alert.setMessage("দয়া করে ছবি আপলোড করুন");
            alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    photoGalleryIntent();
                }
            });

            alert.show();
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
                        profilePic.setImageDrawable(d);
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

                        profilepicUrl = taskSnapshot.getDownloadUrl().toString();


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


}
