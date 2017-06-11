package com.d4vinci.chatapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mRef;

    EditText etMsg;
    EditText etName;
    Button btSend;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mRef = FirebaseDatabase.getInstance().getReference();

        etMsg = (EditText) v.findViewById(R.id.et_message);
        btSend = (Button) v.findViewById(R.id.bt_send);
        etName = (EditText) v.findViewById(R.id.et_name);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                if(!etMsg.getText().toString().equals("")) {
                    mRef.child("messages").push().setValue(new Chat(etName.getText().toString(), etMsg.getText().toString(), String.valueOf(now.getTime())));
                }
                etMsg.setText("");
                autoScroll();
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_messages);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);


        adapter =  new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, R.layout.viewholder, ChatHolder.class, mRef.child("messages")) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.setName(chatMessage.getName());
                chatMessageViewHolder.setText(chatMessage.getText());
                chatMessageViewHolder.setTime(chatMessage.getTime());
                chatMessageViewHolder.cvMsg.setRadius(32);
                if(chatMessage.getName().equals(etName.getText().toString())) {
                    chatMessageViewHolder.alignRight();
                } else {
                    chatMessageViewHolder.alignLeft();
                }
            }

            @Override
            protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
                super.onChildChanged(type, index, oldIndex);
                autoScroll();
            }
        };
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void autoScroll() {
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }


    public static class ChatHolder extends RecyclerView.ViewHolder {

        TextView tv_msg;
        TextView tv_name;
        TextView tv_time;
        CardView cvMsg;

        public ChatHolder(View itemView) {
            super(itemView);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_message);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            cvMsg = (CardView)itemView.findViewById(R.id.cv_msg);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            cvMsg.setRadius(8.0f);
        }

        public void setName(String name) {
            tv_name.setText(name);
        }

        public void setText(String text) {
            tv_msg.setText(text);
        }

        public void setTime(String time) {
            tv_time.setText(time);
        }

        public void alignRight() {
            cvMsg.setCardBackgroundColor(Color.YELLOW);
            tv_msg.setTextColor(Color.BLACK);
            tv_name.setTextColor(Color.BLACK);
            tv_time.setTextColor(Color.BLACK);
        }

        public void alignLeft() {
            cvMsg.setCardBackgroundColor(Color.BLUE);
            tv_msg.setTextColor(Color.WHITE);
            tv_name.setTextColor(Color.WHITE);
            tv_time.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
