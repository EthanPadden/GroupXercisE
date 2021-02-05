package com.nova.groupxercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupFragment extends Fragment {
    private Group mGroup;
    private String mGroupId;
    private TextView mGroupNameText;
    private TextView mGroupCreatorText;
    private ListView mGroupMembersList;
    private ArrayAdapter< String > mItemsAdapter;
    private Button mAddMemberBtn;
    private Button mDeleteGroupBtn;
    private Button mRemoveMemberBtn;
    private EditText mMemberNameEt;


    public GroupFragment( String mGroupId ) {
        this.mGroupId = mGroupId;
    }

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
        mAddMemberBtn = view.findViewById( R.id.btn_add_member );
        mMemberNameEt = view.findViewById( R.id.et_member_name );
        mDeleteGroupBtn = view.findViewById( R.id.btn_delete_group );
        mRemoveMemberBtn = view.findViewById( R.id.btn_remove_member );

        // Set event listeners
        mAddMemberBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String username = mMemberNameEt.getText().toString();
                if ( checkIfUsernameIsValid( username ) ) {
                    checkIfUserExists( username );
                } else {
                    Toast.makeText( getActivity(), "Invalid username", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        mRemoveMemberBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String username = mMemberNameEt.getText().toString();
                if ( checkIfUsernameIsValid( username ) ) {
                    // You cannot remove the group creator

                    if ( mGroup.getmGroupCreator().compareTo( username ) == 0 ) {
                        Toast.makeText( getActivity(), "You cannot remove the creator", Toast.LENGTH_SHORT ).show();
                    } else {
                        removeMember( username );
                    }
                } else {
                    Toast.makeText( getActivity(), "Invalid username", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        mDeleteGroupBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                deleteGroup();
            }
        } );

        retrieveGroupInfo();
    }

    private void deleteGroup() {
        // Remove all members
        for ( String memberUsername : mGroup.getMembers() ) {
            removeMember( memberUsername );
        }

        // Delete group subtree
        String groupPath = "groups/" + mGroupId;
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference groupRef = homeScreenActivity.getmRootRef().child( groupPath );
        groupRef.removeValue();

        // Set the fragment to be the my groups fragment
        FragmentTransaction ft = homeScreenActivity.getSupportFragmentManager().beginTransaction();
        MyGroupsFragment myGroupsFragment = new MyGroupsFragment();
        ft.replace( R.id.frame_home_screen_fragment_placeholder, myGroupsFragment );
        ft.commit();
    }

    private boolean checkIfUsernameIsValid( String username ) {
        return username != null && username.compareTo( "" ) != 0;
    }

    private void checkIfUserExists( final String username ) {
        Toast.makeText( getActivity(), "Searching for user: " + username, Toast.LENGTH_SHORT ).show();

        // Path to the username child
        String path = "usernames/" + username;

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    Toast.makeText( getActivity(), "User found: " + username, Toast.LENGTH_SHORT ).show();
                    String userId = dataSnapshot.getValue().toString();
                    addUserToGroup( username, userId );
                } else {
                    Toast.makeText( getActivity(), "Username not found", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    private void addUserToGroup( String username, String userId ) {
        /** Updating groups subtree */
        // Path to this groups members child
        String thisGroupMembersPath = "groups/" + mGroupId + "/members";

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference groupsChildRef = homeScreenActivity.getmRootRef().child( thisGroupMembersPath );

        // TODO: check if the user is already a member - error?

        groupsChildRef.child( username ).setValue( false );

        /** Updating user_groups subtree */
        String userGroupsPath = "user_groups/" + userId;
        DatabaseReference userGroupsChildRef = homeScreenActivity.getmRootRef().child( userGroupsPath );
        userGroupsChildRef.child( mGroupId ).setValue( false );

        /** Updating group in memory and UI */
        mGroup.getMembers().add( username );
    }

    private void removeMember( final String username ) {

        /** Updating groups subtree */
        // Path to this groups members child
        String thisGroupMembersPath = "groups/" + mGroupId + "/members";

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference groupsChildRef = homeScreenActivity.getmRootRef().child( thisGroupMembersPath );

        // TODO: check if the user is already a member - error?
        // TODO: what if that user is not a member? = error?
        groupsChildRef.child( username ).removeValue();


        /** Updating user_groups subtree */
        String usernamePath = "usernames/" + username;
        final DatabaseReference rootRef = homeScreenActivity.getmRootRef();
        DatabaseReference usernameChildRef = rootRef.child( usernamePath );
        usernameChildRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                String userId = dataSnapshot.getValue().toString();

                String userGroupsPath = "user_groups/" + userId;
                DatabaseReference userGroupsChildRef = rootRef.child( userGroupsPath );
                userGroupsChildRef.child( mGroupId ).removeValue();

                /** Updating group in memory and UI */
                mGroup.getMembers().remove( username );
            }


            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Gets the group information from the DB using the group ID
     */
    private void retrieveGroupInfo() {
        // Path to the group
        String path = "groups/" + mGroupId;

        // Get the DB reference
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Get values
                String dbGroupName = dataSnapshot.child( "name" ).getValue().toString();
                String dbGroupCreator = dataSnapshot.child( "creator" ).getValue().toString();
                DataSnapshot membersDataSnapshot = dataSnapshot.child( "members" );
                ArrayList< String > dbMembers = new ArrayList<>();
                for ( DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren() ) {
                    dbMembers.add( memberDataSnapshot.getKey() );
                }

                // Create group object
                mGroup = new Group( dbGroupName, mGroupId );
                mGroup.setmGroupCreator( dbGroupCreator );
                mGroup.setMembers( dbMembers );

                // Update UI
                mGroupNameText.setText( mGroup.getmGroupName() );
                mGroupCreatorText.setText( mGroup.getmGroupCreator() );
                mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mGroup.getMembers() );
                mGroupMembersList.setAdapter( mItemsAdapter );

                User currentUser = User.getInstance();
                String currentUsername = currentUser.getUsername();
                if ( mGroup.getmGroupCreator().compareTo( currentUsername ) == 0 ) {
                    mAddMemberBtn.setVisibility( View.VISIBLE );
                    mMemberNameEt.setVisibility( View.VISIBLE );
                    mDeleteGroupBtn.setVisibility( View.VISIBLE );
                    mRemoveMemberBtn.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
