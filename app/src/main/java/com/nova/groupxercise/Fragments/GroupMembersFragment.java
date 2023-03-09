package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.Member;
import com.nova.groupxercise.R;

public class GroupMembersFragment extends Fragment {
    private Group mGroup;
    private LinearLayout mGroupMembersLayout;
    private boolean mAdminGroup;
    private TextView mGroupMembersLoadingText;

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

        mGroupMembersLayout = view.findViewById( R.id.layout_group_members );
        mGroupMembersLoadingText = view.findViewById( R.id.text_group_members_loading );

        // Initially assume the user is not an admin, and add the delete buttons as necessary

        // Add group members to UI
        for( Member member : mGroup.getmMembers() ) {
            View memberLayout = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_group_member, null );

            TextView memberNameText = memberLayout.findViewById( R.id.text_member_name );
            memberNameText.setText( member.getmUsername() );

            if(mGroup.getmCreator().compareTo( member.getmUsername() ) == 0) {
                TextView adminStatusText = memberLayout.findViewById( R.id.text_member_admin_status );
                adminStatusText.setVisibility( View.VISIBLE );
            }

            mGroupMembersLayout.addView( memberLayout );
        }

        if(mGroup.getmMembers().size() > 0) {
            mGroupMembersLoadingText.setVisibility( View.GONE );
        }

        // TODO: show member progress
    }
}
