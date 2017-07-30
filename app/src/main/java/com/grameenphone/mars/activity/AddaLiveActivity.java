package com.grameenphone.mars.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.mars.R;
import com.grameenphone.mars.dbhelper.DatabaseHelper;
import com.grameenphone.mars.model.Chat;
import com.grameenphone.mars.model.FileModel;
import com.grameenphone.mars.model.User;
import com.grameenphone.mars.utility.Constant;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class AddaLiveActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView receiversMessage;
        public TextView receiversName;
        public CircleImageView receiversImage;
        public ImageView receiversImageView;


        public TextView sendersMessage;
        public TextView senderName;
        public ImageView senderImageView;

        public TextView systemMessage;

        public MessageViewHolder(View v) {
            super(v);
            receiversMessage = (TextView) itemView.findViewById(R.id.receiversMessage);
            receiversName = (TextView) itemView.findViewById(R.id.receiversName);
            receiversImage = (CircleImageView) itemView.findViewById(R.id.receiversImage);
            receiversImageView = (ImageView) itemView.findViewById(R.id.receivers_image_view);

            sendersMessage = (TextView) itemView.findViewById(R.id.sendersMessage);
            senderName = (TextView) itemView.findViewById(R.id.senderName);
            senderImageView = (ImageView) itemView.findViewById(R.id.senders_image_view);

            systemMessage = (TextView) itemView.findViewById(R.id.system_message);
        }
    }



    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "";

    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EmojiconEditText mMessageEditText;
    private ImageView mAddMessageImageView;


    private Button jumpToBottom;

    private View rootView;
    private ImageView emojiImageView;
    private EmojIconActions emojIconActions;

    private User sender;
    private User receiver;

    private DatabaseHelper dbHelper;

    private Bundle bundle = new Bundle();



    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private ProgressDialog progressDialog;


    private static final int IMAGE_GALLERY_REQUEST = 101;
    private ImageView attachment;
    private FirebaseStorage storage = FirebaseStorage.getInstance();





    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Chat, AddaLiveActivity.MessageViewHolder>
            chatFirebaseAdapter;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_chat_room);

        dbHelper = new DatabaseHelper(getApplicationContext());
        sender = dbHelper.getMe();

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.chatroomRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


        jumpToBottom = (Button) findViewById(R.id.jump_bottom);
        jumpToBottom.setVisibility(View.GONE);

        rootView = (View) findViewById(R.id.root_view);
        emojiImageView = (ImageView) findViewById(R.id.emoticon);



        attachment = (ImageView) findViewById(R.id.attachment);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();
            }
        });


        progressDialog = new ProgressDialog(AddaLiveActivity.this);







        bundle = getIntent().getExtras();

        MESSAGES_CHILD = bundle.getString("roomId");
        String name = bundle.getString("roomName");
        getSupportActionBar().setTitle(name);


        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        chatFirebaseAdapter = new FirebaseRecyclerAdapter<Chat,
                AddaLiveActivity.MessageViewHolder>(
                Chat.class,
                R.layout.item_message,
                AddaLiveActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(AddaLiveActivity.MessageViewHolder viewHolder,
                                              Chat chat, int position) {



                if ( chat.getMessageType().equals("txt")) {

                    if (chat.getMessage() != null && !chat.getSenderUid().equals(sender.getUid()) ) {
                        viewHolder.receiversMessage.setText(chat.getMessage());
                        viewHolder.receiversMessage.setVisibility(TextView.VISIBLE);

                        long milliseconds = chat.getTimestamp();
                        int minutes = (int) ((milliseconds / (1000*60)) % 60);
                        int hours   = (int) ((milliseconds / (1000*60*60)) % 24) - 6;

                        viewHolder.receiversName.setText( hours+":"+minutes );
                        viewHolder.receiversName.setVisibility(TextView.VISIBLE);

                        if (chat.getPhotoUrl() == null) {
                            viewHolder.receiversImage.setImageDrawable(ContextCompat.getDrawable(AddaLiveActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
                            viewHolder.receiversImage.setVisibility(CircleImageView.VISIBLE);
                        } else {
                            Glide.with(AddaLiveActivity.this)
                                    .load(chat.getPhotoUrl())
                                    .into(viewHolder.receiversImage);
                            viewHolder.receiversImage.setVisibility(CircleImageView.VISIBLE);
                        }



                        viewHolder.senderName.setVisibility(View.GONE);
                        viewHolder.sendersMessage.setVisibility(View.GONE);

                        viewHolder.senderImageView.setVisibility(View.GONE);
                        viewHolder.receiversImageView.setVisibility(View.GONE);

                        viewHolder.systemMessage.setVisibility(View.GONE);



                    } else {

                        viewHolder.sendersMessage.setText(chat.getMessage());
                        viewHolder.sendersMessage.setVisibility(TextView.VISIBLE);

                        long milliseconds = chat.getTimestamp();
                        int minutes = (int) ((milliseconds / (1000*60)) % 60);
                        int hours   = (int) ((milliseconds / (1000*60*60)) % 24) - 6;


                        viewHolder.senderName.setText( hours+":"+minutes );
                        viewHolder.senderName.setVisibility(TextView.VISIBLE);





                        viewHolder.receiversMessage.setVisibility(View.GONE);
                        viewHolder.receiversName.setVisibility(View.GONE);
                        viewHolder.receiversImage.setVisibility(View.GONE);

                        viewHolder.senderImageView.setVisibility(View.GONE);
                        viewHolder.receiversImageView.setVisibility(View.GONE);

                        viewHolder.systemMessage.setVisibility(View.GONE);


                    }



                } else if (chat.getMessageType().equals("system")){

                    if( chat.getSenderUid().equals(sender.getUid())) {
                        String message = chat.getMessage() + "You";
                        viewHolder.systemMessage.setText(message);
                        viewHolder.systemMessage.setVisibility(TextView.VISIBLE);
                    } else {
                        String message = chat.getMessage();
                        if( chat.getSender() != null){
                            message += chat.getSender();
                        }
                        viewHolder.systemMessage.setText( message );
                        viewHolder.systemMessage.setVisibility(TextView.VISIBLE);
                    }



                    viewHolder.sendersMessage.setVisibility(View.GONE);
                    viewHolder.senderName.setVisibility(View.GONE);

                    viewHolder.receiversMessage.setVisibility(View.GONE);
                    viewHolder.receiversName.setVisibility(View.GONE);
                    viewHolder.receiversImage.setVisibility(View.GONE);

                    viewHolder.senderImageView.setVisibility(View.GONE);
                    viewHolder.receiversImageView.setVisibility(View.GONE);



                } else {


                    if( !chat.getSenderUid().equals(sender.getUid()) ){

                        long milliseconds = chat.getTimestamp();
                        int minutes = (int) ((milliseconds / (1000*60)) % 60);
                        int hours   = (int) ((milliseconds / (1000*60*60)) % 24) - 6;


                        viewHolder.receiversName.setText( hours+":"+minutes );
                        viewHolder.receiversName.setVisibility(TextView.VISIBLE);

                        if (chat.getPhotoUrl() == null) {
                            viewHolder.receiversImage.setImageDrawable(ContextCompat.getDrawable(AddaLiveActivity.this,
                                    R.drawable.hello1));
                            viewHolder.receiversImage.setVisibility(CircleImageView.VISIBLE);
                        } else {
                            Glide.with(AddaLiveActivity.this)
                                    .load(chat.getPhotoUrl())
                                    .into(viewHolder.receiversImage);
                            viewHolder.receiversImage.setVisibility(CircleImageView.VISIBLE);
                        }


                        Glide.with(AddaLiveActivity.this)
                                .load(chat.getFile().getUrl_file())
                                .into(viewHolder.receiversImageView);
                        viewHolder.receiversImageView.setVisibility(View.VISIBLE);


                        viewHolder.receiversMessage.setVisibility(View.GONE);

                        viewHolder.senderName.setVisibility(View.GONE);
                        viewHolder.sendersMessage.setVisibility(View.GONE);
                        viewHolder.senderImageView.setVisibility(View.GONE);

                        viewHolder.systemMessage.setVisibility(View.GONE);



                    } else {

                        long milliseconds = chat.getTimestamp();
                        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
                        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24) - 6;


                        viewHolder.senderName.setText( hours+":"+minutes );
                        viewHolder.senderName.setVisibility(TextView.VISIBLE);


                        Glide.with(AddaLiveActivity.this)
                                .load(chat.getFile().getUrl_file())
                                .into(viewHolder.senderImageView);
                        viewHolder.senderImageView.setVisibility(View.VISIBLE);



                        viewHolder.sendersMessage.setVisibility(View.GONE);

                        viewHolder.receiversMessage.setVisibility(View.GONE);
                        viewHolder.receiversName.setVisibility(View.GONE);
                        viewHolder.receiversImage.setVisibility(View.GONE);
                        viewHolder.receiversImageView.setVisibility(View.GONE);

                        viewHolder.systemMessage.setVisibility(View.GONE);


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






        mMessageEditText = (EmojiconEditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getApplicationContext(),R.color.colorAccent)  );
                } else {
                    mSendButton.setEnabled(false);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getApplicationContext(),R.color.disabled)  );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int len = editable.length();


            }
        });



        emojIconActions = new EmojIconActions(this, rootView, mMessageEditText, emojiImageView);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);


        mSendButton = (ImageView) findViewById(R.id.send_button);
        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long time = System.currentTimeMillis();
                Chat chat = new Chat(sender.getName(),
                        sender.getUid(),
                        mMessageEditText.getText().toString(),
                        time, sender.getPhotoUrl(), "txt");


                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD)
                        .push().setValue(chat);
                mMessageEditText.setText("");
            }
        });
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Constant.Storage.STORAGE_URL).child(Constant.Storage.ATTACHMENT);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    sendPhotoFirebase(storageRef,selectedImageUri);
                } else{
                    //FIX ME
                }
            }
        }
    }



    private void sendPhotoFirebase(StorageReference storageReference, final Uri file){
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            final long time = System.currentTimeMillis();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendPhotoFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Log.i(TAG,"onSuccess sendPhotoFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel file = new FileModel("img",downloadUrl.toString(),name,"");
                    Chat chat = new Chat(sender.getName(), null,
                            sender.getUid(), null, sender.getPhotoUrl(), "Image", time ,file , file.getType());
                    mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
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
        } else {
            //FIXME
        }

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!bundle.getString("roomId").equals("live_adda")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.group_menu, menu);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            case R.id.group_info:
                Intent intent = new Intent(getApplicationContext(), EditGroupActivity.class);
                intent.putExtra("roomId", bundle.getString("roomId"));
                intent.putExtra("roomName", bundle.getString("roomName"));
                startActivity(intent);
                finish();
                return true;
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






    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType ("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }




}
