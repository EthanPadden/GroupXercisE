package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.Member;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupMembersFragment extends Fragment {
    private Group mGroup;
    private ListView mGroupMembersListView;
    private TextView mGroupMembersLoadingText;
    private ArrayAdapter mGroupMembersAdapter;

    public GroupMembersFragment( Group mGroup ) {
        this.mGroup = mGroup;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_group_members, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        mGroupMembersListView = view.findViewById( R.id.list_group_members );
        mGroupMembersLoadingText = view.findViewById( R.id.text_group_members_loading );

        ArrayList< Member > members = mGroup.getmMembers();
        ArrayList< String> memberNames = new ArrayList<>();

        for (Member member : members) {
            memberNames.add( member.getmUsername() );
        }

        mGroupMembersAdapter = new ArrayAdapter( getActivity(),android.R.layout.simple_list_item_1,  memberNames );

        if(mGroup.getmMembers().size() > 0) {
            mGroupMembersLoadingText.setVisibility( View.GONE );
        }

        mGroupMembersListView.setAdapter( mGroupMembersAdapter );
    }
}
