package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupFragment extends Fragment {
    private Group mGroup;
    private String mGroupId;
    private TextView mGroupNameText;
    private TextView mGroupCreatorText;
    private Button mAddMemberBtn;
    private Button mDeleteGroupBtn;
    private Button mRemoveMemberBtn;
    private EditText mMemberNameEt;
    private TextView mGroupGoalsLoadingText;
    private ArrayList mGroupGoalNames;
    private LinearLayout mGroupMembersLayout;
    private LinearLayout mGroupGoalsLayout;


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
        mAddMemberBtn = view.findViewById( R.id.btn_add_member );
        mMemberNameEt = view.findViewById( R.id.et_member_name );
        mDeleteGroupBtn = view.findViewById( R.id.btn_delete_group );
        mRemoveMemberBtn = view.findViewById( R.id.btn_remove_member );
        mGroupGoalsLoadingText = view.findViewById( R.id.text_group_goals_loading );
        mGroupMembersLayout = view.findViewById( R.id.layout_members );
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );

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
        retrieveGroupGoals();
    }

    /**
     * Retrieves the group goals from the DB
     */
    private void retrieveGroupGoals() {
        // Path to group goals
        String path = "groups/" + mGroupId + "/goals";

        // Get the DB reference
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
               if(dataSnapshot.exists()) {
                   mGroupGoalNames = new ArrayList(  );
                    for(DataSnapshot goalDataSnapshot : dataSnapshot.getChildren()) {
                        String exerciseName = goalDataSnapshot.getKey();
                        String target = goalDataSnapshot.getValue().toString();
                        mGroupGoalNames.add( exerciseName );
                        mGroupGoalsLoadingText.setVisibility( View.GONE );

                        TextView textView = new TextView( getActivity() );
                        textView.setText( exerciseName );

                        mGroupGoalsLayout.addView( textView );

                    }
               } else {
                   mGroupGoalsLoadingText.setText( "No goals" );
               }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Removes all members from the group (including the creator)
     * Deletes the group
     * Sets the fragment to be the my groups fragment
     */
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

    /**
     * Checks the argument string is a valid username
     * @param username the username to check
     * @return true if the username is valid
     */
    private boolean checkIfUsernameIsValid( String username ) {
        return username != null && username.compareTo( "" ) != 0;
    }

    /**
     * Checks if there is a user with the argument username
     * If not, show error message
     * If so, find the user ID and call addUserToGroup
     * @param username
     */
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

    /**
     * Adds a user to the group, updating both the groups and user_groups subtrees
     * @param username the username of the user to add
     * @param userId the ID of the user to add
     */
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

        /** Create subtree for the progress of that user towards the goals */
        createProgressSubtree(username);
    }

    private void createProgressSubtree(String username) {
        String progressPath = "groups/" + mGroupId + "/members/" + username + "/progress";
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( progressPath );

        for( Object goalObj : mGroupGoalNames ) {
            String goalName = (String) goalObj;
            childRef.child( goalName ).setValue( 0 );
        }
    }

    /**
     * Removes a user from the group, updating both the groups and user_groups subtrees
     * @param username the username of the user to remove
     */
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
                for(String username : mGroup.getMembers()) {
                    TextView textView = new TextView( getActivity() );
                    textView.setText( username );
                    mGroupMembersLayout.addView( textView );
                }

                // If the user is the creator, show the components that allows the user admin controls
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
