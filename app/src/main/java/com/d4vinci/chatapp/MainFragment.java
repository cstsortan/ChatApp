package com.d4vinci.chatapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static final String TAG  = "TAG - MainFragment";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private MainActivity mainActivity;

    MyUser myUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    StorageReference reference = FirebaseStorage.getInstance().getReference();

    private static DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    EditText etMsg;
    ImageView btSend;
    ImageView btAddPhoto;
    private int messagesShown;
    private Query messagesQuery;
    private String messagesPath = "messages-test";

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //async task to upload new userdata in case the're changed
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                myUser = new MyUser(
                        user.getUid(),
                        user.getDisplayName(),
                        user.getPhotoUrl()!=null?user.getPhotoUrl().toString():"");
                mRef.child("users").child(user.getUid()).setValue(myUser);
                return null;
            }
        }.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mainActivity =(MainActivity) getActivity();

        Log.d(TAG, "onCreateView: my pic is" + user.getPhotoUrl());

        etMsg = (EditText) v.findViewById(R.id.et_message);

        btSend = (ImageView) v.findViewById(R.id.bt_send);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                if(!etMsg.getText().toString().equals("")) {
                    Message message = new Message(myUser.getUid(), etMsg.getText().toString(), now.getTime().toString(), "");
                    mRef.child(messagesPath)
                            .push()
                            .setValue(message);
                }
                etMsg.setText("");
                autoScroll();
            }
        });

        btAddPhoto = (ImageView) v.findViewById(R.id.bt_add_photo);
        btAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_messages);
        adapter = initializeRecyclerViewAdapter(25);
        recyclerView.setAdapter(adapter);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        return v;
    }



    private FirebaseRecyclerAdapter initializeRecyclerViewAdapter(final int messagesShown) {
        messagesQuery = mRef.child(messagesPath).limitToLast(messagesShown);
        this.messagesShown = messagesShown;
        return new FirebaseRecyclerAdapter<Message, ChatHolder>
                (Message.class,
                R.layout.viewholder, ChatHolder.class,
                messagesQuery) {

            @Override
            public void populateViewHolder(final ChatHolder chatMessageViewHolder, final Message chatMessage, int position) {

                DatabaseReference userRef = mRef.child("users").child(chatMessage.getUid());
                final MyUser[] user = new MyUser[1];
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user[0] = dataSnapshot.getValue(MyUser.class);

                        chatMessageViewHolder.updateUI(user[0], chatMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: failed to get this user" + chatMessage.getUid());
                    }
                });
            }

            @Override
            public void onChildChanged(EventType type, DataSnapshot snapshot, int index, int oldIndex) {
                super.onChildChanged(type, snapshot, index, oldIndex);
                autoScroll();
            }
        };

    }

    private void autoScroll() {
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }


    public static class ChatHolder extends RecyclerView.ViewHolder {

        private TextView tv_msg;
        private TextView tv_name;
        private TextView tv_time;
        private ImageView ivPic;  //the profile pic
        private ImageView ivPhoto; //the message one

        public ChatHolder(View itemView) {
            super(itemView);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_message);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_prof);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
        }

        void updateUI(MyUser mUser, Message chatMessage) {
            tv_name.setText(mUser.getName());
            Glide.with(itemView.getContext())
                    .load(mUser.getPhoto())
                    .into(ivPic);
            tv_msg.setText(chatMessage.getText());
            tv_time.setText(chatMessage.getTime());
            if(!chatMessage.getPhoto().equals("") && chatMessage.getPhoto()!=null) {
                tv_msg.setVisibility(View.INVISIBLE);
            } else  {
                tv_msg.setVisibility(View.VISIBLE);
            }
            Glide.with(itemView.getContext())
                    .load(chatMessage.getPhoto())
                    .into(ivPhoto);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );



        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadPhoto(imageBitmap);
        }
    }

    private void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final DatabaseReference ref = mRef.child(messagesPath).push();
        UploadTask uploadTask = reference.child(ref.getKey()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(mainActivity, "error", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    ref.setValue(new Message(myUser.getUid(), "", Calendar.getInstance().getTime().toString(), downloadUrl.toString()));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
