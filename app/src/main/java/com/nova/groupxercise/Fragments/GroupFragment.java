package com.nova.groupxercise.Fragments;

import android.content.Context;
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
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupFragment extends Fragment {
    private Group mGroup;
    private String mGroupId;
    private Button mAddMemberBtn;
    private Button mDeleteGroupBtn;
    private Button mRemoveMemberBtn;
    private EditText mMemberNameEt;
    private TextView mGroupGoalsLoadingText;
    private LinearLayout mGroupMembersLayout;
    private LinearLayout mGroupGoalsLayout;
    private ArrayList< DBListener > mDBListeners;
    private boolean adminGroup;


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

        adminGroup = false;
        // Initialise components
        mAddMemberBtn = view.findViewById( R.id.btn_add_member );
        mMemberNameEt = view.findViewById( R.id.et_member_name );
        mDeleteGroupBtn = view.findViewById( R.id.btn_delete_group );
        mRemoveMemberBtn = view.findViewById( R.id.btn_remove_member );
        mGroupGoalsLoadingText = view.findViewById( R.id.text_group_goals_loading );
        mGroupMembersLayout = view.findViewById( R.id.layout_members );
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );

        mDBListeners = new ArrayList<>();

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

        mGroup = new Group( mGroupId );
//        retrieveGroupInfo();

        DBListener groupInfoListener = new DBListener() {
            public void onRetrievalFinished() {
                User currentUser = User.getInstance();
                String currentUsername = currentUser.getUsername();
                if ( mGroup.getmGroupCreator().compareTo( currentUsername ) == 0 ) {
                    mAddMemberBtn.setVisibility( View.VISIBLE );
                    mMemberNameEt.setVisibility( View.VISIBLE );
                    mDeleteGroupBtn.setVisibility( View.VISIBLE );
                    mRemoveMemberBtn.setVisibility( View.VISIBLE );
                    adminGroup = true;
                }
                DBListener groupProgressListener = new DBListener() {
                    public void onRetrievalFinished( Object retrievedData ) {
                        DataSnapshot membersDataSnapshot = ( DataSnapshot ) retrievedData;
                        ArrayList< String > dbMembers = new ArrayList<>();
                        for ( DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren() ) {
                            String username = memberDataSnapshot.getKey();
                            dbMembers.add( username );

                            // For each member
                            View memberCard = getLayoutInflater().inflate( R.layout.layout_group_member_card, null );
                            LinearLayout memberCardRootLayout = memberCard.findViewById( R.id.layout_card_root );


                            View memberNameView = getLayoutInflater().inflate( R.layout.layout_group_member_name, memberCardRootLayout );
                            TextView usernameTextView = memberNameView.findViewById( R.id.text_member_name );
                            TextView userStatusTextView = memberNameView.findViewById( R.id.text_member_status );
                            usernameTextView.setText( username );
                            if ( username.compareTo( mGroup.getmGroupCreator() ) == 0 ) {
                                userStatusTextView.setText( "Admin" );
                                userStatusTextView.setVisibility( View.VISIBLE );
                            }

                            else if(adminGroup) {
                                Button removeMemberBtn =  memberCard.findViewById( R.id.btn_remove_member );
                                removeMemberBtn.setVisibility(View.VISIBLE );
                            }


                            for ( DataSnapshot progressDataSnapshot : memberDataSnapshot.child( "progress" ).getChildren() ) {
                                String exerciseName = progressDataSnapshot.getKey();
                                Object progressObj = progressDataSnapshot.getValue();
                                float progress;
                                if ( progressObj instanceof Long ) {
                                    progress = ( ( Long ) progressObj ).floatValue();
                                } else {
                                    progress = ( ( Float ) progressObj ).floatValue();
                                }

                                View progressView = getLayoutInflater().inflate( R.layout.layout_group_member_progress, memberCardRootLayout );
                                TextView exerciseNameText = progressView.findViewById( R.id.text_progress_exercise_name );
                                TextView progressText = progressView.findViewById( R.id.text_progress );
                                exerciseNameText.setText( exerciseName );
                                progressText.setText( Float.toString( progress ) );

                            }

                            mGroupMembersLayout.addView( memberCard );
                        }

                        // Create group object
                        mGroup = new Group( mGroup.getmGroupName(), mGroupId );
                        mGroup.setmGroupCreator( mGroup.getmGroupCreator() );
                        mGroup.setMembers( dbMembers );
                        mDBListeners.remove( this );

                    }
                };
                mDBListeners.add( groupProgressListener );
                mGroup.retrieveGroupProgress( groupProgressListener );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( groupInfoListener );
        mGroup.retrieveGroupInfo( groupInfoListener );


        DBListener groupGoalListener = new DBListener() {
            public void onRetrievalFinished() {
                if ( mGroup.getGoals().size() == 0 ) {
                    mGroupGoalsLoadingText.setText( "No goals" );
                } else {
                    mGroupGoalsLoadingText.setVisibility( View.GONE );

                    for ( Goal goal : mGroup.getGoals() ) {
                        TextView textView = new TextView( getActivity() );
                        textView.setText( goal.getmExerciseName() + ": " + goal.getmTarget() );

                        mGroupGoalsLayout.addView( textView );
                    }
                }
                mDBListeners.remove( this );

            }
        };
        mDBListeners.add( groupGoalListener );
        mGroup.retrieveGroupGoals( groupGoalListener );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            // Deactivate any active listeners
            dbListener.setActive( false );
        }
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>();
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
     *
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
     *
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
     *
     * @param username the username of the user to add
     * @param userId   the ID of the user to add
     */
    private void addUserToGroup( String username, String userId ) {
        /** Updating groups subtree */
        // Path to this groups members child
        String thisGroupMembersPath = "groups/" + mGroupId + "/members";

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference groupsChildRef = homeScreenActivity.getmRootRef().child( thisGroupMembersPath );

        groupsChildRef.child( username ).setValue( false );

        // TODO: check if the user is already a member - error?

        /** Updating user_groups subtree */
        String userGroupsPath = "user_groups/" + userId;
        DatabaseReference userGroupsChildRef = homeScreenActivity.getmRootRef().child( userGroupsPath );
        userGroupsChildRef.child( mGroupId ).setValue( false );

        /** Updating group in memory and UI */
        mGroup.getMembers().add( username );

        /** Create subtree for the progress of that user towards the goals */
        for ( Goal goal : mGroup.getGoals() ) {
            User user = new User();
            user.setUsername( username );
            goal.matchUserProgressToGroup( userId, user, mGroup );
        }

    }

    /**
     * Removes a user from the group, updating both the groups and user_groups subtrees
     *
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

}
