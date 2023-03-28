package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupMembersFragment extends Fragment {
    private Group mGroup;
    private ListView mGroupMembersListView;
    private TextView mGroupMembersLoadingText;
    private ArrayAdapter mGroupMembersAdapter;
    private boolean isAdmin;

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

        isAdmin = false;
        User currentUser = User.getInstance();
        if(mGroup.getmCreator().compareTo( currentUser.getUsername() ) == 0) {
            isAdmin = true;
        }

        mGroupMembersListView = view.findViewById( R.id.list_group_members );
        mGroupMembersLoadingText = view.findViewById( R.id.text_group_members_loading );

        ArrayList< User > members = mGroup.getmMembers();
        ArrayList< String > memberNames = new ArrayList<>();

        for (User member : members) {
            memberNames.add( member.getUsername() );
        }

        // Set up group members list
        mGroupMembersAdapter = new ArrayAdapter( getActivity(),android.R.layout.simple_list_item_1,  memberNames );
        if(mGroup.getmMembers().size() > 0) {
            mGroupMembersLoadingText.setVisibility( View.GONE );
        }
        mGroupMembersListView.setAdapter( mGroupMembersAdapter );

        // Set onclick listeners for group members list
        mGroupMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMemberName = ( String ) mGroupMembersListView.getItemAtPosition( position );

                HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
                homeScreenActivity.getSupportActionBar().setTitle( "Group member" );
                FragmentTransaction ft = homeScreenActivity.getSupportFragmentManager().beginTransaction();
                GroupMemberDetailsFragment groupMemberDetailsFragment = new GroupMemberDetailsFragment( selectedMemberName, mGroup );
                ft.replace( R.id.frame_home_screen_fragment_placeholder, groupMemberDetailsFragment );
                ft.commit();
            }
        });
    }
}
