package com.grameenphone.mars.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.activity.LogActivity;
import com.grameenphone.mars.activity.MainActivity;
import com.grameenphone.mars.activity.MainActivityHolder;
import com.grameenphone.mars.activity.MarsLiveActivity;
import com.grameenphone.mars.adapter.LiveUserListAdapter;
import com.grameenphone.mars.adapter.UserAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.DateTimeUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.StickerGridView;
import github.ankushsachdeva.emojicon.emoji.Emojicon;


/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_Live extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "mars_live";

    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EmojiconEditText mMessageEditText;


    private Button jumpToBottom;

    private View rootView;
    EmojiconEditText emojiconEditText;

    private User sender;

    private DatabaseHelper dbHelper;
    private Toolbar toolbar;
    private ImageButton Back;


    private static final String TAG = Fragment_Live.class.getSimpleName();
    private ProgressDialog progressDialog;

    private View borderbottom;

    private ArrayList<ChatRoom>userArrayList;



    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Chat, MessageViewHolder>
            chatFirebaseAdapter;

    private RecyclerView userrecylcer;
    private LiveUserListAdapter liveUserListAdapter;
private View fragmentView;
    public Fragment_Live() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        android.support.v7.app.ActionBar ab =  ((AppCompatActivity)getActivity()).getSupportActionBar();

        final Drawable upArrow = ContextCompat.getDrawable(getActivity(),R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(),R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator ( R.drawable.ic_backiconsmall );
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("লাইভ");

        //  setActionBarTitle("নোটিফিকেশন সেটিংস");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.activity_mars_live,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }
    private void bindViews(View view) {

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);



        borderbottom = (View) view.findViewById(R.id.msgedittextborder);

        userrecylcer=(RecyclerView)view.findViewById(R.id.horizontallayoutholder);



        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        sender = dbHelper.getMe();
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.mars_live_chat_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        jumpToBottom = (Button) view.findViewById(R.id.jump_bottom);
        jumpToBottom.setVisibility(View.GONE);

        rootView = (View) view.findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) view.findViewById(R.id.emoticon);
        emojiButton.setImageResource(R.drawable.emoji);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());
        popup.setBackgroundDrawable(null);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.emoji);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon.getEmoji() == null) {
                    if(emojicon.getEmojiId()>0)
                    {
                        long time = System.currentTimeMillis();
                        final Chat chat = new Chat(sender.getName(), sender.getUid(),
                                String.valueOf( emojicon.getEmojiId()),
                                time, sender.getPhotoUrl(), "stk");




                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        return;
                    }
                    else
                        return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });
        popup.setEmojiClickListener(new StickerGridView.OnStickerClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });
        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if(!popup.isShowing()){

                    //If keyboard is visible, simply show the emoji popup
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else{
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });
        emojiconEditText = (EmojiconEditText) view.findViewById(R.id.messageEditText);
        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {

                    mSendButton.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(true);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent)  );



                } else {
                    mSendButton.setVisibility(View.GONE);
                    mSendButton.setEnabled(false);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getActivity().getApplicationContext(),R.color.disabled)  );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emojiconEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (popup.isShowing()) {
                    popup.dismiss();
                    //   popup.showAtBottom();
                    changeEmojiKeyboardIcon(emojiButton, R.drawable.emoji);
                    //If keyboard is visible, simply show the emoji popup
                  /*  if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {

                    }*/
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });



        mSendButton = (ImageView) view.findViewById(R.id.send_button);
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatText = emojiconEditText.getText().toString();

                long time = System.currentTimeMillis();
                Chat chat = new Chat(sender.getName(),
                        sender.getUid(),
                        chatText,
                        time, sender.getPhotoUrl(), "txt");


                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD)
                        .push().setValue(chat);
                emojiconEditText.setText("");
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
        userArrayList= dbHelper.getAllRoombytype("p2p");
        try {

            liveUserListAdapter=new LiveUserListAdapter(getActivity(),userArrayList,dbHelper);
            userrecylcer.setAdapter(liveUserListAdapter);
            //userrecylcer.setOnClickListener((View.OnClickListener) getActivity().getApplicationContext());
        }catch (Exception e)
        {

        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        chatFirebaseAdapter = new FirebaseRecyclerAdapter<Chat,
                Fragment_Live.MessageViewHolder>(
                Chat.class,
                R.layout.item_live_messsage,
                Fragment_Live.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).limitToLast(200)) {

            @Override
            protected void populateViewHolder(Fragment_Live.MessageViewHolder viewHolder,
                                              Chat chat, int position) {



                if ( chat.getMessageType().equals("txt")) {

                    if ( chat.getMessage() != null ) {
                        viewHolder.sticker.setVisibility(View.GONE);
                        String name =  "<font color='#26BEFF'>"+ chat.getSender() +": </font>";
                        String message = chat.getMessage();
                        viewHolder.liveuser.setVisibility(View.GONE);
                        viewHolder.livestickertime.setVisibility(View.GONE);
                        viewHolder.liveMessage.setText(Html.fromHtml(name + message));
                        viewHolder.liveMessage.setVisibility(View.VISIBLE);

                        viewHolder.liveMessageTime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));
                        viewHolder.liveMessageTime.setVisibility(View.VISIBLE);


                    }



                }
                if ( chat.getMessageType().equals("stk")) {

                    if ( chat.getMessage() != null ) {
                        viewHolder.sticker.setVisibility(View.VISIBLE);

                        viewHolder.liveMessageTime.setVisibility(View.GONE);
                        viewHolder.liveuser.setVisibility(View.VISIBLE);
                        String name =   chat.getSender() +":";
                        String message = chat.getMessage();

                        viewHolder.liveuser.setText(name);
                        viewHolder.liveMessage.setVisibility(View.GONE);
                        int path = Integer.parseInt(chat.getMessage());
                        Glide.with(getActivity()).load(path).into(viewHolder.sticker);

                        viewHolder.livestickertime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));
                        viewHolder.livestickertime.setVisibility(View.VISIBLE);


                    }



                }




            }
        };




        chatFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = chatFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });






        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(chatFirebaseAdapter);



        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int chatCount = chatFirebaseAdapter.getItemCount();



                if( lastVisiblePosition < (chatCount - 10) && (lastVisiblePosition != -1) ){
                    jumpToBottom.setVisibility(View.VISIBLE);
                } else {
                    jumpToBottom.setVisibility(View.GONE);

                }
            }
        });








    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView liveMessage;
        public TextView liveMessageTime;
        public TextView liveuser;
        public ImageView sticker;
        public TextView livestickertime;


        public MessageViewHolder(View v) {
            super(v);
            liveMessage = (TextView) itemView.findViewById(R.id.live_message);
            liveMessageTime = (TextView) itemView.findViewById(R.id.time_stamp_live_message);
            livestickertime = (TextView) itemView.findViewById(R.id.time_stamp_live_sticker);
            liveuser = (TextView) itemView.findViewById(R.id.liveusername);
            sticker = (ImageView) itemView.findViewById(R.id.livesticker);

        }
    }

}
