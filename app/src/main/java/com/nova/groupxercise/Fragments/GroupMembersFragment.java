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

import com.nova.groupxercise.Adapters.GroupMembersItemsAdapter;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.R;

public class GroupMembersFragment extends Fragment {
    private Group mGroup;
    private ListView mGroupMembersListView;
    private boolean mAdminGroup;
    private TextView mGroupMembersLoadingText;
    private ArrayAdapter mGroupMembersAdapter;

    public GroupMembersFragment( Group mGroup, boolean mAdminGroup ) {
        this.mGroup = mGroup;
        this.mAdminGroup = mAdminGroup;
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

        mGroupMembersAdapter = new GroupMembersItemsAdapter( getActivity(), mGroup );


        // Initially assume the user is not an admin, and add the delete buttons as necessary

//        // Add group members to UI
//        for( Member member : mGroup.getmMembers() ) {
//            View memberLayout = LayoutInflater.from( getContext() ).inflate(
//                    R.layout.layout_group_member, null );
//
//            TextView memberNameText = memberLayout.findViewById( R.id.text_member_name );
//            memberNameText.setText( member.getmUsername() );
//
//            if(mGroup.getmCreator().compareTo( member.getmUsername() ) == 0) {
//                TextView adminStatusText = memberLayout.findViewById( R.id.text_member_admin_status );
//                adminStatusText.setVisibility( View.VISIBLE );
//            }
//
//            // Set on click method to open member details fragment
//            memberLayout.setOnClickListener( new View.OnClickListener() {
//                public void onClick( View v ) {
//                    // TODO: show member progress
//                    Toast.makeText( getActivity(), member.getmUsername(), Toast.LENGTH_SHORT );
//                }
//            } );
//        }

        if(mGroup.getmMembers().size() > 0) {
            mGroupMembersLoadingText.setVisibility( View.GONE );
        }

        mGroupMembersListView.setAdapter( mGroupMembersAdapter );
    }
}
