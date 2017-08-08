package com.grameenphone.mars.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.LogActivity;
import com.grameenphone.mars.activity.MainActivityHolder;
import com.grameenphone.mars.adapter.UserCallAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.CallEnded;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class Fragment_RecentCalls extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView;
    LinearLayout linearLayout;
    RecyclerView mRecyclerViewAllUserListing;
    private ArrayList<CallDetails> userCallsArrayList=new ArrayList<>();
    private ArrayList<String >names=new ArrayList<>();
    UserCallAdapter adapter;
    EventBus myEventBus;
    TextView history;
    Button newCall;
    FrameLayout real,fake;
    View fragmentView;
    LinearLayout linearLayout1;
    public Fragment_RecentCalls() {

        // Required empty public constructor
    }
    public static Fragment_RecentCalls newInstance() {
        Fragment_RecentCalls f = new Fragment_RecentCalls();
        return f;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);
        myEventBus = EventBus.getDefault();
        EventBus.getDefault().register(this);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator ( R.drawable.ic_backiconsmall );
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("হ্যালো ভয়েস কল");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.fragment_recentcalls,
                    container, false);
        bindViews(fragmentView);
        }


        return fragmentView;
}

    private void bindViews(View view) {
        real=(FrameLayout)view.findViewById(R.id.realholder);

        fake=(FrameLayout)view.findViewById(R.id.emptyholder);
        linearLayout=(LinearLayout)view.findViewById(R.id.listholder);
        linearLayout1=(LinearLayout)view.findViewById(R.id.nocalls);
        linearLayout1.setBackgroundResource(R.drawable.ic_page_1);
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
        history=(TextView)view.findViewById(R.id.noHistory);
        newCall=(Button)view.findViewById(R.id.new_call_fromRecent);
        newCall.setBackgroundResource(R.drawable.profile_button_shape);
        newCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newanother();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }
    public void init()
    {
        populatelistview();
    }
    @Subscribe
    public void onEvent(CallDetails event){
        // your implementation
        ((LogActivity)getActivity()).callButtonClicked(event.getCallingTo(),event.getImgUrl(),event.getUid());
    }
    public void newanother(){
        // your implementation
        ((MainActivityHolder)getActivity()).placeNewCallActivity();
    }
    @Subscribe
    public void onEvent(CallEnded event){
        // your implementation
       refreshlistview();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_call, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_newCall:
                newanother();
                return true;

            case android.R.id.home:
                getActivity().onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void populatelistview()
    {

        final DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        userCallsArrayList=dbHelper.getAllUserLog();
        try {
            if(userCallsArrayList.size()>0)fake.setVisibility(View.GONE);
            adapter=new UserCallAdapter(getActivity(),userCallsArrayList);
            mRecyclerViewAllUserListing.setAdapter(adapter);
            mRecyclerViewAllUserListing.setOnClickListener((View.OnClickListener) getActivity().getApplicationContext());
        }catch (Exception e)
        {
            if(userCallsArrayList.size()==0)fake.setVisibility(View.VISIBLE);
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

    public void refreshlistview()
    {

        final DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        userCallsArrayList.clear();
        userCallsArrayList=dbHelper.getAllUserLog();
        if(userCallsArrayList.size()>0)
        {fake.setVisibility(View.GONE);

        }

        adapter.updateList(userCallsArrayList);



    }


}
