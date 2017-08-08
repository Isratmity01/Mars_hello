package com.grameenphone.mars.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.User;

import java.util.List;


public class CongratulationsActivity extends Activity {


    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User user;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;
    private Button profile;
    private ImageView succ;
    private Boolean KeyboardEnabled=false;
    private static Boolean KeyboardChosen=false;
    private ProgressBar mProgressBar;
    InputMethodManager imeManager;
    public Boolean getKeyboardEnabled() {
        return KeyboardEnabled;
    }

    public void setKeyboardEnabled(Boolean keyboardEnabled) {
        KeyboardEnabled = keyboardEnabled;
    }

    private static final String TAG = "CongratulatinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_congratulations);
    /*    imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        Boolean checked=IfEnabled();
    if (!checked)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(CongratulationsActivity.this);
        alert.setTitle("");
        alert.setMessage("এখন একটি সেটিংস পেজ আসবে। সেটিংস পেজ থেকে “হ্যালো কিবোর্ড” টি এনাবল করে নিন।\n“হ্যালো কিবোর্ড” আপনার “হ্যালো মেসেঞ্জার” ব্যবহার এর অভিজ্ঞতা কে আরো সমৃদ্ধ করবে।\n\nপরবর্তিতে যে কোন সময় আপনি কি বোর্ড পরিবর্তন করে নিতে পারবেন।");
        alert.setCancelable(false);
        alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(intent);


            }
        });

        alert.show();
    }
    else {
        Toast.makeText(getApplicationContext(),"Your Keyboard Enabled already",Toast.LENGTH_SHORT).show();
      //  chooseKeyboard();
    }
     /*
*/      succ=(ImageView)findViewById(R.id.success_icon);
        succ.setBackgroundResource(R.drawable.successful);
        profile = (Button) findViewById(R.id.profile);
        profile.setBackgroundResource(R.drawable.profile_button_shape);
        profile.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarCongratulation);



        dbHelper = new DatabaseHelper(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();



        if(mFirebaseUser != null){
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        user = dataSnapshot.getValue(User.class);
                        if(user!=null){

                            user.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());
                            mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).setValue(user);


                            profile.setText("ম্যাসেজ স্ক্রিনে যান");
                        }

                    }
                    mProgressBar.setVisibility(View.GONE);
                    profile.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user != null){

                    dbHelper.addMe(user);

                    Intent intent = new Intent(CongratulationsActivity.this, MainActivityHolder.class);
                    startActivity(intent);
                    finish();


                } else {
                    Intent intent = new Intent(CongratulationsActivity.this, ProfileEditActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(),"on onResume",Toast.LENGTH_SHORT).show();
        boolean check=IfEnabled();
       // if(check) chooseKeyboard();


    }
    @Override
    protected void onStart() {
        super.onStart();
       // Toast.makeText(getApplicationContext(),"on start",Toast.LENGTH_SHORT).show();
       boolean check=IfEnabled();
        if(check) chooseKeyboard();


    }
    @Override
    protected void onRestart() {
        super.onRestart();
      //  Toast.makeText(getApplicationContext(),"on Restart",Toast.LENGTH_SHORT).show();


    }
    private void chooseKeyboard()
    {

       imeManager.showInputMethodPicker();
       // Toast.makeText(getApplicationContext(),"Your Keyboard Enabled",Toast.LENGTH_SHORT).show();
    }
    private Boolean IfEnabled()
    {
        String packageLocal = getPackageName();
        boolean isInputDeviceEnabled = false;
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        // check if our keyboard is enabled as input method
        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            if (packageName.equals(packageLocal)) {
               // Toast.makeText(getApplicationContext(),"Your Keyboard Enable",Toast.LENGTH_SHORT).show();
                KeyboardEnabled=true;
                setKeyboardEnabled(true);

            }
            else {



            }
        }
        return  KeyboardEnabled;
    }

}
