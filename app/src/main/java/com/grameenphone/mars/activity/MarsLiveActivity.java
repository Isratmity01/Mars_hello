package com.grameenphone.mars.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.LiveUserListAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.navhelper.BottomNavigationViewHelper;
import com.grameenphone.mars.utility.DateTimeUtility;

import java.util.ArrayList;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.StickerGridView;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class MarsLiveActivity extends AppCompatActivity {



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


    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private ProgressDialog progressDialog;

    private View borderbottom;

    private ArrayList<User>userArrayList;



    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Chat, MarsLiveActivity.MessageViewHolder>
            chatFirebaseAdapter;

    private RecyclerView userrecylcer;
    private LiveUserListAdapter liveUserListAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_mars_live);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar ();
        final Drawable upArrow = ContextCompat.getDrawable(MarsLiveActivity.this,R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(MarsLiveActivity.this,R.color.icons), PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        actionbar.setDisplayHomeAsUpEnabled ( true );

        borderbottom = (View) findViewById(R.id.msgedittextborder);

        userrecylcer=(RecyclerView)findViewById(R.id.horizontallayoutholder);



        dbHelper = new DatabaseHelper(getApplicationContext());
        sender = dbHelper.getMe();
        userArrayList= dbHelper.getAllUser();
        try {

            liveUserListAdapter=new LiveUserListAdapter(MarsLiveActivity.this,userArrayList);
            userrecylcer.setAdapter(liveUserListAdapter);
            //userrecylcer.setOnClickListener((View.OnClickListener) getActivity().getApplicationContext());
        }catch (Exception e)
        {

        }
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.mars_live_chat_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        jumpToBottom = (Button) findViewById(R.id.jump_bottom);
        jumpToBottom.setVisibility(View.GONE);

        rootView = (View) findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoticon);
        emojiButton.setImageResource(R.drawable.emoji);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
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
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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






        progressDialog = new ProgressDialog(MarsLiveActivity.this);






        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        chatFirebaseAdapter = new FirebaseRecyclerAdapter<Chat,
                MarsLiveActivity.MessageViewHolder>(
                Chat.class,
                R.layout.item_live_messsage,
                MarsLiveActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MarsLiveActivity.MessageViewHolder viewHolder,
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
                        Glide.with(MarsLiveActivity.this.getApplicationContext()).load(path).into(viewHolder.sticker);

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





        jumpToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastPosition =
                        mLinearLayoutManager.getItemCount();
                mMessageRecyclerView.scrollToPosition( lastPosition -1);
            }
        });







        emojiconEditText = (EmojiconEditText) findViewById(R.id.messageEditText);
        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {

                    mSendButton.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(true);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getApplicationContext(),R.color.colorAccent)  );



                } else {
                    mSendButton.setVisibility(View.GONE);
                    mSendButton.setEnabled(false);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getApplicationContext(),R.color.disabled)  );
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



        mSendButton = (ImageView) findViewById(R.id.send_button);
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








        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

       // BottomNavigationViewHelper.increaseIconSize(getApplicationContext(),bottomNavigationView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setAnimation(null);
        bottomNavigationView.setSelectedItemId(R.id.action_live);
        Log.d("in live activity + ", String.valueOf(bottomNavigationView.getHeight()));
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_message:
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                                break;
                            case R.id.action_call:
                                Intent callintent = new Intent( getApplicationContext(), LogActivity.class );
                                startActivity(callintent);
                                overridePendingTransition(0,0);
                                break;
                            case R.id.action_live:
                                Intent intentAddaLive = new Intent(getApplicationContext(),MarsLiveActivity.class);
                                startActivity(intentAddaLive);
                                overridePendingTransition(0,0);
                                break;
                            case R.id.action_settings:
                                Intent intentSettings = new Intent(getApplicationContext(),UserProfileActivity.class);
                                startActivity(intentSettings);
                                overridePendingTransition(0,0);
                                break;
                        }
                        return false;
                    }
                });








        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 250) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    bottomNavigationView.setVisibility(View.GONE);
                    //mMessageRecyclerView.setVisibility(View.VISIBLE);
                    //messageArea.setVisibility(View.VISIBLE);

                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) borderbottom.getLayoutParams();
                    lp.bottomMargin = 0;



                }else{
                    //ok now we know the keyboard is down...
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    //mMessageRecyclerView.setVisibility(View.VISIBLE);
                    //messageArea.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) borderbottom.getLayoutParams();

                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (57 * scale + 0.5f);

                    lp.bottomMargin = pixels;

                }
            }
        });













    }
















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }






    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }



    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public void onClickCalled(int position) {
        // Call another acitivty here and pass some arguments to it
        User liveUser=userArrayList.get(position);
        Intent intent = new Intent(MarsLiveActivity.this, ChatRoomActivity.class);

        intent.putExtra("room_name", liveUser.getName());
        intent.putExtra("room_uid", liveUser.getUid());

        startActivity(intent);
    }








}
