package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyGroupsFragment extends Fragment {
    private Button mCreateGroupBtn;
    private ArrayList<Group> mGroups;
    private ArrayAdapter< String > mItemsAdapter;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private TextView mLoadingText;
    private ListView mListView;


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_my_groups, container, false );


    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        mListView = view.findViewById( R.id.groups_list );
        mCreateGroupBtn = view.findViewById( R.id.btn_create_group );
        mLoadingText = view.findViewById( R.id.info_groups );

        mCreateGroupBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent( getActivity(), CreateGroupActivity.class );
                startActivity( intent );
            }
        } );

        retrieveGroupIds();
    }

    private void retrieveGroupIds() {
        final ArrayList<String> groupIds = new ArrayList<>(  );

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String usersGroupPath = "user_groups/" + currentUserId;

        DatabaseReference childRef = mRootRef.child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    groupIds.add(  usersGroupsDataSnapshot.getKey());
                }

                retrieveGroupNames( groupIds );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    private void retrieveGroupNames(ArrayList<String> groupIds) {
// Create an empty list for the group names
        mGroups = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new GroupItemsAdapter( getActivity(),  mGroups );

        final int expectedSize = groupIds.size();
        for( final String groupId : groupIds) {
            String groupPath = "groups/" + groupId;
            DatabaseReference groupRef = mRootRef.child( groupPath );

            groupRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    String groupName = dataSnapshot.child( "name" ).getValue().toString();
                    mGroups.add( new Group( groupName, groupId ) );
                    if(mGroups.size() == expectedSize) {
                        setupGroupsList();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
        if(mGroups.size() == 0) {
            mLoadingText.setText( "You have no groups" );
        }

    }

    private void setupGroupsList() {
        mLoadingText.setVisibility( View.GONE );
        mListView.setAdapter( mItemsAdapter );

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                Group selectedGroup = (Group) mListView.getItemAtPosition( i );

                HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
                homeScreenActivity.getSupportActionBar().setTitle( selectedGroup.getmGroupName() );

                FragmentTransaction ft = homeScreenActivity.getSupportFragmentManager().beginTransaction();
                GroupFragment groupFragment = new GroupFragment(selectedGroup.getmGroupId());
                ft.replace( R.id.frame_home_screen_fragment_placeholder, groupFragment );
                ft.commit();
            }
        } );
    }

}
