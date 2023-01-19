package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.chatapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    DatabaseReference databaseReferenceSender, databaseReferenceReciver;
    String reciverId;

    String senderRoom, recieverRoom;

    //mesg ka adpater
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reciverId = getIntent().getStringExtra("id");

        //sender room
        senderRoom = FirebaseAuth.getInstance().getUid() + reciverId;
        recieverRoom = reciverId + FirebaseAuth.getInstance().getUid();

        messageAdapter = new MessageAdapter(this);
        binding.rec2.setAdapter(messageAdapter);
        binding.rec2.setLayoutManager(new LinearLayoutManager(this));

        databaseReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceReciver = FirebaseDatabase.getInstance().getReference("chats").child(recieverRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for (DataSnapshot dbshop : snapshot.getChildren()) {
                    MessageModel messageModel = dbshop.getValue(MessageModel.class);

                    //addapter
                    messageAdapter.add(messageModel);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = binding.msgEd.getText().toString();

                if (message.trim().length() > 0)
                    sendmessage(message);
            }


        });

    }

    private void sendmessage(String message) {
        String msgId = UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel(msgId, FirebaseAuth.getInstance().getUid(), message);

        //add ker dein mesg model jesy he user send keryga bina network load k

        messageAdapter.add(messageModel);


        databaseReferenceSender.child(msgId).setValue(messageModel);
        databaseReferenceReciver.child(msgId).setValue(messageModel);

    }
}