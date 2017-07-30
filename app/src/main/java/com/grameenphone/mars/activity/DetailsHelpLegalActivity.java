package com.grameenphone.mars.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.mars.R;

public class DetailsHelpLegalActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_details_help_legal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String title = extras.getString("title");
        String text = extras.getString("text");
        getSupportActionBar().setTitle(title);

        imageView = (ImageView) findViewById(R.id.image);

        switch (title){
            case "টার্মস":
                imageView.setImageResource(R.drawable.ic_error_black_24dp);
                break;
            case "প্রাইভেসি পলিসি":
                imageView.setImageResource(R.drawable.ic_security_black_24dp);
                break;
            case "প্রশ্ন":
                imageView.setImageResource(R.drawable.ic_live_help_black_24dp);
                break;
            case "লাইসেন্স":
                imageView.setImageResource(R.drawable.ic_license);
                break;
            case "লিগ্যাল":
                imageView.setImageResource(R.drawable.ic_legal);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_error_black_24dp);
                break;
        }

        textView = (TextView) findViewById(R.id.text);
        textView.setText(text);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
