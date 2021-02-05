package com.nova.groupxercise;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    private String mGroupName;
    private String mGroupCreator;
    private ArrayList<String> mGroupMembers;
    private TextView mGroupNameText;
    private TextView mGroupCreatorText;
    private ListView mGroupMembersList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set content view
        setContentView( R.layout.activity_group );

        // Initialise components
        mGroupNameText = findViewById( R.id.text_group_name );
        mGroupCreatorText = findViewById( R.id.text_creator );
        mGroupMembersList = findViewById( R.id.list_members );
    }
}
