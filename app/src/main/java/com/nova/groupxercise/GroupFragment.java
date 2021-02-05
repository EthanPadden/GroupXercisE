package com.nova.groupxercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class GroupFragment extends Fragment {
    private String mGroupName;
    private String mGroupCreator;
    private ArrayList<String> mGroupMembers;
    private TextView mGroupNameText;
    private TextView mGroupCreatorText;
    private ListView mGroupMembersList;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_group, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mGroupNameText = view.findViewById( R.id.text_group_name );
        mGroupCreatorText = view.findViewById( R.id.text_creator );
        mGroupMembersList = view.findViewById( R.id.list_members );
    }
}
