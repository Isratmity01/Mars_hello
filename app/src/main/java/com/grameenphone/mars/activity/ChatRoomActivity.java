package com.grameenphone.mars.activity;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.mars.ApplicationChat;
import com.grameenphone.mars.R;
import com.grameenphone.mars.adapter.ChatRoomAdapter;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.fcm.FcmNotificationBuilder;
import com.grameenphone.mars.gcm.SinchService;
import com.grameenphone.mars.model.CallDetails;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.ChatRoom;
import com.grameenphone.mars.model.ChatSent;
import com.grameenphone.mars.model.FileModel;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.Constant;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.StickerGridView;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class ChatRoomActivity extends BaseActivity {


    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "";
    String firstDate, newDate;
    int timefirst, timesecond;
    int monthfirst, monthsecond;

    public int getMonthfirst() {
        return monthfirst;
    }

    public void setMonthfirst(int monthfirst) {
        this.monthfirst = monthfirst;
    }

    public int getMonthsecond() {
        return monthsecond;
    }

    public void setMonthsecond(int monthsecond) {
        this.monthsecond = monthsecond;
    }

    public int getTimefirst() {
        return timefirst;
    }

    public void setTimefirst(int timefirst) {
        this.timefirst = timefirst;
    }

    public int getTimesecond() {
        return timesecond;
    }

    public void setTimesecond(int timesecond) {
        this.timesecond = timesecond;
    }

    int year;
    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    EmojiconEditText emojiconEditText;
    private ImageView mAddMessageImageView;


    private User sender;
    private User receiver;

    private User me;

    private String receiverFirebaseToken;


    private DatabaseHelper dbHelper;

    private Button jumpToBottom;

    private View rootView;
    private ImageView emojiImageView;
    SharedPreferences preferences;
    private String[] Monthlist = {"জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"};

    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private ProgressDialog progressDialog;


    private static final int IMAGE_GALLERY_REQUEST = 101;
    private ImageView attachment;
    private ImageView pushToTalk;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String receiver_uid;


    private DatabaseReference mFirebaseDatabaseReference;


    SharedPreferences.Editor editor;
    public ChatRoomAdapter chatRoomAdapter;
    public ArrayList<Chat> chats = new ArrayList<Chat>();


    private BroadcastReceiver statusReceiver;
    private IntentFilter mIntent;

    private static Context context;
    Calendar calendar;
    Boolean IsSent=false;
    private static String sChatRoomName = "";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_chat_room);

        context = ChatRoomActivity.this;




        dbHelper = new DatabaseHelper(getApplicationContext());


        me = dbHelper.getMe();



        Bundle b = new Bundle();
        b = getIntent().getExtras();

        MESSAGES_CHILD = b.getString("room_uid");

        dbHelper.updateNotificationStateOfRoom(MESSAGES_CHILD, 0);


        sender = dbHelper.getMe();

        receiver_uid = (MESSAGES_CHILD.replace(sender.getUid(), "")).replace("_", "");

        receiver = dbHelper.getUser(receiver_uid);

        receiverFirebaseToken = receiver.getFirebaseToken();

        sChatRoomName = receiver.getName();




        jumpToBottom = (Button) findViewById(R.id.jump_bottom);
        jumpToBottom.setVisibility(View.GONE);

        rootView = (View) findViewById(R.id.root_view);
        calendar = Calendar.getInstance();
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoticon);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        popup.setBackgroundDrawable(null);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
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
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon.getEmoji() == null) {
                    if (emojicon.getEmojiId() > 0) {
                        long time = System.currentTimeMillis();
                        final Chat chat = new Chat(sender.getName(), receiver.getName(),
                                sender.getUid(), receiver.getUid(),
                                String.valueOf(emojicon.getEmojiId()),
                                time, "stk");


                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        //chat.setReadStatus(0);
                        //dbHelper.addMessage(chat, chat.getChatId());
                       // EventBus.getDefault().post(new ChatSent("yes"));
                        IsSent=true;
                        return;
                    } else
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
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });


        attachment = (ImageView) findViewById(R.id.attachment);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();
            }
        });

        pushToTalk = (ImageView) findViewById(R.id.push_to_talk);

        pushToTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });



        progressDialog = new ProgressDialog(ChatRoomActivity.this);


        getSupportActionBar().setHomeAsUpIndicator(null);
        getSupportActionBar().setTitle(receiver.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.chatroomRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //chats = dbHelper.getAllMsg(sender.getUid(),receiver.getUid());
        //System.out.println(chats.size() + " Here is the size ");

        chatRoomAdapter = new ChatRoomAdapter(ChatRoomActivity.this.getApplicationContext(), chats, sender, receiver, MESSAGES_CHILD);

        chatRoomAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = chatRoomAdapter.getItemCount();
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
        mMessageRecyclerView.setAdapter(chatRoomAdapter);


        mFirebaseDatabaseReference.child("chat_rooms").child(MESSAGES_CHILD)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            Chat c = dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());
                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                }

                                if (addFlag) {
                                    if (chats.size() == 0) {
                                        calendar.setTimeInMillis(c.getTimestamp());
                                        firstDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                                        firstDate = EToB(firstDate);
                                        setTimefirst(calendar.get(Calendar.DAY_OF_MONTH));
                                        setMonthfirst(calendar.get(Calendar.MONTH));
                                        String Month = Monthlist[getMonthfirst()];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));
                                    }


                                    calendar.setTimeInMillis(c.getTimestamp());
                                    //newDate=String.valueOf(mDay);
                                    setTimesecond(calendar.get(Calendar.DAY_OF_MONTH));
                                    setMonthsecond(calendar.get(Calendar.MONTH));
                                    if (getTimesecond() != getTimefirst()) {
                                        setTimefirst(getTimesecond());

                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String Month = Monthlist[calendar.get(Calendar.MONTH)];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));


                                    } else if (getMonthsecond() > getMonthfirst() && getTimesecond() == getTimefirst()) {
                                        String Month = Monthlist[getMonthsecond()];
                                        setMonthfirst(getMonthsecond());
                                        setTimefirst(getTimesecond());
                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));

                                    }
                                    chats.add(c);
                                }

                                if(IsSent)
                                {   IsSent=false;
                                    dbHelper.addMessage(c,c.getChatId());
                                    c.setReadStatus(0);
                                    sendPushNotificationToReceiver(c,c.getSender(), c.getMessage(),
                                            c.getSender(), me.getFirebaseToken(), receiverFirebaseToken, MESSAGES_CHILD);

                                }


                            }
                            if(c.getReceiverUid().equals(me.getUid()))
                            {
                              c.setReadStatus(1);
                                dbHelper.addMessage(c,c.getChatId());
                            }
                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);
                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            Chat c = dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());
                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                    if(data.getReadStatus() != c.getReadStatus()){
                                        data.setReadStatus(c.getReadStatus());

                                        chatRoomAdapter.notifyDataSetChanged();
                                        int lastPosition =
                                                mLinearLayoutManager.getItemCount();
                                        mMessageRecyclerView.scrollToPosition(lastPosition - 1);
                                    }

                                }

                                if (addFlag) {
                                    chats.add(c);
                                }


                            }

                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);

                        }


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int chatCount = chatRoomAdapter.getItemCount();

                if (lastVisiblePosition < (chatCount - 10) && (lastVisiblePosition != -1)) {
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
                mMessageRecyclerView.scrollToPosition(lastPosition - 1);
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

                    attachment.setVisibility(View.GONE);
                    pushToTalk.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(true);
                    mSendButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


                } else {
                    mSendButton.setVisibility(View.GONE);
                    attachment.setVisibility(View.VISIBLE);
                    pushToTalk.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(false);
                    mSendButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.disabled));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mSendButton = (ImageView) findViewById(R.id.send_button);
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long time = System.currentTimeMillis();
                final Chat chat = new Chat(sender.getName(), receiver.getName(),
                        sender.getUid(), receiver.getUid(),
                        emojiconEditText.getText().toString(),
                        time, "txt");


                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);

                if(receiver.getFirebaseToken()!=null) {
                    IsSent=true;
                  //  chat.setReadStatus(0);
                  //  dbHelper.addMessage(chat, chat.getChatId());

                }


                emojiconEditText.setText("");
            }
        });
    }

    @Override
    protected void onServiceConnected() {

        String get = null;
        try {
            get = getSinchServiceInterface().getUserName();
        } catch (Exception e) {

        }

        if (TextUtils.isEmpty(get)) {
            if (me.getUid() == null) return;
            else
                getSinchServiceInterface().startClient(me.getUid());


        }

        //else userName.setText(get);
        //   mCallButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Constant.Storage.STORAGE_URL).child(Constant.Storage.ATTACHMENT);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendPhotoFirebase(storageRef, selectedImageUri);
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
                        Log.e(TAG, "onFailure sendPhotoFirebase " + e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Log.i(TAG, "onSuccess sendPhotoFirebase");
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FileModel file = new FileModel("img", downloadUrl.toString(), name, "");
                        Chat chat = new Chat(sender.getName(), receiver.getName(),
                                sender.getUid(), receiver.getUid(), sender.getPhotoUrl(), "Image", time, file, file.getType());
                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                    //    chat.setReadStatus(0);
                      //  chat.setMessage("ছবি পাঠিয়েছেন");
                        //dbHelper.addMessage(chat, chat.getChatId());
                        //EventBus.getDefault().post(new ChatSent("yes"));
                      IsSent=true;
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred());
                        progressDialog.setMessage("Uploading ..");
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


    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ApplicationChat.setChatActivityOpen(true);
        sChatRoomName = receiver.getName();
        dbHelper.updateNotificationStateOfRoom(MESSAGES_CHILD, 0);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        sChatRoomName = receiver.getName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationChat.setChatActivityOpen(false);
        sChatRoomName = "";
        dbHelper.updateNotificationStateOfRoom(MESSAGES_CHILD, 1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sChatRoomName = "";
        dbHelper.updateNotificationStateOfRoom(MESSAGES_CHILD, 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.chat_info:
                Intent intent = new Intent(getApplicationContext(), UserProfileChatActivity.class);
                intent.putExtra("receiver_name", receiver.getName());
                intent.putExtra("receiver_uid", receiver.getUid());
                intent.putExtra("room_uid", MESSAGES_CHILD);
                intent.putExtra("receiver_phone", receiver.getPhone());
                intent.putExtra("receiver_photo_url", receiver.getPhotoUrl());
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.call_buddy:

                call();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void call() {
        if (receiver_uid.isEmpty()) {
            //Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = getSinchServiceInterface().callUser(receiver_uid);
            if (call == null) {
                //Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            final CallDetails user = new CallDetails(receiver.getName(), System.currentTimeMillis(), "outgoing", receiver.getUid(), receiver.getPhotoUrl());
            dbHelper.addUserLog(user);
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra("Photourl", sender.getPhotoUrl());
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    private String EToB(String english_number) {
        if (english_number.equals("null") || english_number.equals(""))
            return english_number;
        int v = english_number.length();
        String concatResult = "";
        for (int i = 0; i < v; i++) {
            if (english_number.charAt(i) == '1')
                concatResult = concatResult + "১";
            else if (english_number.charAt(i) == '2')
                concatResult = concatResult + "২";
            else if (english_number.charAt(i) == '3')
                concatResult = concatResult + "৩";
            else if (english_number.charAt(i) == '4')
                concatResult = concatResult + "৪";
            else if (english_number.charAt(i) == '5')
                concatResult = concatResult + "৫";
            else if (english_number.charAt(i) == '6')
                concatResult = concatResult + "৬";
            else if (english_number.charAt(i) == '7')
                concatResult = concatResult + "৭";
            else if (english_number.charAt(i) == '8')
                concatResult = concatResult + "৮";
            else if (english_number.charAt(i) == '9')
                concatResult = concatResult + "৯";
            else if (english_number.charAt(i) == '0')
                concatResult = concatResult + "০";

            else {
                return english_number;
            }

        }
        return concatResult;
    }


    private void sendPushNotificationToReceiver(Chat chat, String roomTitle,
                                                String message,
                                                String sender,
                                                String firebaseToken,
                                                String receiverFirebaseToken,
                                                String roomId) {
        EventBus.getDefault().post(new ChatSent("yes"));
        FcmNotificationBuilder.initialize()
                .title(roomTitle)
                .message(message)
                .sender(sender)
                .setReceived(chat)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .roomUid(roomId)
                .send();
    }





    public static String getChatRoomName() {
        return sChatRoomName;
    }

}
