package com.grameenphone.mars.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.LogActivity;
import com.grameenphone.mars.adapter.UserAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;

import com.grameenphone.mars.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_PlaceCall extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mCallButton;
    private EditText mCallName;
    RecyclerView allusers;
    ArrayList<User> userArrayList;
    UserAdapter userAdapter;
    EventBus myEventBus;
    public Fragment_PlaceCall() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);
        myEventBus = EventBus.getDefault();
        EventBus.getDefault().register(this);
       ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator ( R.drawable.ic_backiconsmall );

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("নতুন কল");
      //  setActionBarTitle("নোটিফিকেশন সেটিংস");

    }

    public boolean onSupportNavigateUp(){
        getActivity().getFragmentManager().popBackStack();
        return true;
    }
    public void setActionBarTitle(String title) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_placecall, container, false);

        bindViews(fragmentView);
        return fragmentView;
    }
    private void bindViews(View view) {

        allusers = (RecyclerView) view.findViewById(R.id.allusers);


    }
    @Subscribe
    public void onEvent(User event){
        // your implementation
        ((LogActivity)getActivity()).callButtonClicked(event.getName(),event.getPhotoUrl(),event.getUid());
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }
    public void init()
    {
        populateList();
    }
    public void populateList()
    {

        final DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        userArrayList=dbHelper.getAllUser();
        try {

            userAdapter=new UserAdapter(getActivity(),userArrayList,true);
            allusers.setAdapter(userAdapter);
            allusers.setOnClickListener((View.OnClickListener) getActivity().getApplicationContext());
        }catch (Exception e)
        {

        }

     /*   for(int i=0;i<userCallsArrayList.size();i++)
        {
            names.add(userCallsArrayList.get(i).getCallingTo() + userCallsArrayList.get(i).getCallType());
        }
        if(names.size()>0)linearLayout.setVisibility(View.VISIBLE);
        arrayAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                names );
*/
        //listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here
                ((LogActivity)getActivity()).findViewById(R.id.tabs).setVisibility(View.VISIBLE);

                ((LogActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.logohdpi);
                ((LogActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((LogActivity)getActivity()).getSupportActionBar().setTitle("");
                ((LogActivity)getActivity()).findViewById(R.id.newcall).setVisibility(View.VISIBLE);
                ((LogActivity)getActivity()).findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                getFragmentManager().popBackStack();
                return true;
        }
        return false;
    }
    @Subscribe
    public void onEvent(String s){
        // your implementation
        refreshlistview();
    }
    public void refreshlistview()
    {

        final DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        userArrayList.clear();
        userArrayList=dbHelper.getAllUser();

        userAdapter.updateList(userArrayList);

    }


}
