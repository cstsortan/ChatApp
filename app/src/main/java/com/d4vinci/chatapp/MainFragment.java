package com.d4vinci.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DatabaseReference mRef;

    private EditText etMsg;
    private EditText etName;
    private Button btSend;

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
                if(!etMsg.getText().toString().equals("")) {
                    mRef.child("messages").push().setValue(new Chat(etName.getText().toString(), etMsg.getText().toString()));
                }
                etMsg.setText("");
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_messages);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        adapter =  new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, R.layout.viewholder, ChatHolder.class, mRef.child("messages")) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.setName(chatMessage.getName());
                chatMessageViewHolder.setText(chatMessage.getText());
            }

            @Override
            protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
                super.onChildChanged(type, index, oldIndex);
            }
        };
        recyclerView.setAdapter(adapter);

        return v;
    }



    public static class ChatHolder extends RecyclerView.ViewHolder {

        TextView tv_msg;
        TextView tv_name;

        public ChatHolder(View itemView) {
            super(itemView);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_message);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);

        }

        public void setName(String name) {
            tv_name.setText(name);
        }

        public void setText(String text) {
            tv_msg.setText(text);
        }
    }

}
