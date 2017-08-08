package com.grameenphone.mars.fragment;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.ContactListAdapter;
import com.grameenphone.mars.model.SelectUser;
import com.sinch.android.rtc.MissingPermissionException;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Contacts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    ContactListAdapter adapter;
    Cursor phones, email;
    // ArrayList
    ArrayList<SelectUser> selectUsers=new ArrayList<SelectUser>();
    ArrayList<SelectUser> distinctselectUsers=new ArrayList<SelectUser>();
    List<SelectUser> temp;
    View fragmentView;
    // Pop up
    ContentResolver resolver;
    ListView listView;

    public Fragment_Contacts() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.fragment_blank2,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }
    private void bindViews(View view) {

        listView=(ListView)view.findViewById(R.id.contacts_list);
        MatrixCursor mc = new MatrixCursor(new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        });
        try {
            resolver = getActivity().getApplicationContext().getContentResolver();
            phones = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if(phones.getCount()>0)
            {
                LoadContact loadContact = new LoadContact();
                loadContact.execute();
            }
            else Toast.makeText(getActivity().getApplicationContext(),"আপনার মোবাইলে কোন কন্টাক্ট সেভ করা নেই",Toast.LENGTH_SHORT).show();
        }catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "contact permission is granted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }
    public void init()
    {

    }
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                    return null;
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));


                    SelectUser selectUser = new SelectUser();
                    selectUser.setThumb(image_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser. setEmail(id);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }
        private void removeDuplicates(ArrayList<SelectUser> list)
        {
            int count = list.size();

            for (int i = 0; i < count; i++)
            {
                for (int j = i + 1; j < count; j++)
                {
                    if (list.get(i).getName().equals(list.get(j).getName()))
                    {
                        list.remove(j--);
                        count--;
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           // HashSet<SelectUser> hashSet = new HashSet<SelectUser>();
            //hashSet.addAll(selectUsers);
            //selectUsers.clear();
            //selectUsers.addAll(hashSet);
        //   Collections.sort(selectUsers);
            removeDuplicates(selectUsers);
            adapter = new ContactListAdapter(selectUsers, getActivity().getApplicationContext());
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectUser data = selectUsers.get(i);
                }
            });

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        phones.close();
    }

}
