package com.nova.groupxercise.Objects;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class Group {
    // Reflecting DB
    private String mName;
    private String mGroupId;
    private String mCreator;
    private ArrayList< Goal > mGoals;
    private ArrayList< Member > mMembers;

    public Group( String mName, String mGroupId ) {
        this( mGroupId );
        this.mName = mName;
    }

    public Group( String mGroupId ) {
        this.mGroupId = mGroupId;
        mMembers = new ArrayList<>();
        mGoals = new ArrayList<>();
    }

    public ArrayList< Member > getmMembers() {
        return mMembers;
    }

    public void setmMembers( ArrayList< Member > mMembers ) {
        this.mMembers = mMembers;
    }

    /**
     * Navigates to the user_groups subtree and retrieves all the group IDs of groups that the current user is a member of
     * @param listener called when the retrieval is finished
     */
    public static void retrieveGroupIds( final DBListener listener ) {

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        String usersGroupPath = "user_groups/" + currentUserId;


        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                ArrayList<String> groupIds = new ArrayList<>(  );
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    groupIds.add( usersGroupsDataSnapshot.getKey() );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished(groupIds);
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public static void retrieveGroupIds( @NotNull final ArrayList< String > adminGroupIds, @NotNull final ArrayList< String > groupIds, final DBListener listener ) {

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        String usersGroupPath = "user_groups/" + currentUserId;


        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    Boolean isAdmin = ( Boolean ) usersGroupsDataSnapshot.getValue();
                    if ( isAdmin.booleanValue() == true ) {
                        // If the current user is an admin of the group
                        adminGroupIds.add( usersGroupsDataSnapshot.getKey() );
                    }
                    groupIds.add( usersGroupsDataSnapshot.getKey() );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Retrieves the following information about the group from the DB and attaches to the object:
     * Group name
     * Group creator
     *
     * @param listener the listener that is called when the retrieval is finished
     */
    public void retrieveGroupInfo( final DBListener listener ) {
        // Path to the group
        final String path = "groups/" + mGroupId;

        // Get the DB reference
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                // Get group name
                String dbGroupName = dataSnapshot.child( "name" ).getValue().toString();
                setmName( dbGroupName );

                String dbGroupCreator = dataSnapshot.child( "creator" ).getValue().toString();
                setmCreator( dbGroupCreator );

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );

    }

    public void retrieveGroupProgress( final DBListener listener ) {
        // Path to the group
        final String path = "groups/" + mGroupId + "/members";

        // Get the DB reference
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                if ( listener != null && listener.isActive() )
                    listener.onRetrievalFinished( dataSnapshot );
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );

    }
//
//    public void addMember( final String username, final String userId, final DBListener listener ) {
//        /** Updating groups subtree */
//        // Path to this groups members child
//        String thisGroupMembersPath = "groups/" + mGroupId + "/members";
//
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference groupsChildRef = rootRef.child( thisGroupMembersPath );
//
//        groupsChildRef.child( username ).setValue( false );
//
//        // TODO: check if the user is already a member - error?
//
//        /** Updating user_groups subtree */
//        String userGroupsPath = "user_groups/" + userId;
//        DatabaseReference userGroupsChildRef = rootRef.child( userGroupsPath );
//        userGroupsChildRef.child( mGroupId ).setValue( false );
//
//        /** Updating group in memory and UI */
//        members.add( username );
//
////        /MATCHING
//        for ( Goal goal : mGoals ) {
//            User user = new User();
//            user.setUsername( username );
//            goal.matchUserProgressToGroup( userId, user, this );
//            goal.matchGroupProgressToUser( userId, user, this );
//        }
//
//
//        if ( listener != null && listener.isActive() ) listener.onRetrievalFinished( mGoals );
//    }

//    public void deleteGroup() {
//        for ( String member : members ) {
//            removeMember( member, null );
//        }
//        // Delete group subtree
//        String groupPath = "groups/" + mGroupId;
//
//        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child( groupPath );
//        groupRef.removeValue();
//    }

    public void saveGoal( final Goal goal, final DBListener listener ) {
        // Path to the group goal
        String path = "groups/" + mGroupId + "/mGoals/" + goal.getmExerciseName();

        // Get the DB reference
        final DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        // Check if we have a set of mGoals for that particular user
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( listener != null && listener.isActive() ) {
                    if ( dataSnapshot.exists() ) {
                        listener.onRetrievalFinished( true );
                    } else {
                        listener.onRetrievalFinished( false );
                    }
                }

                childRef.setValue( goal.getmTarget() );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

//    public void removeMember( final String username, final DBListener listener ) {
//        /** Updating groups subtree */
//        // Path to this groups members child
//        String thisGroupMembersPath = "groups/" + mGroupId + "/members";
//
//        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference groupsChildRef = rootRef.child( thisGroupMembersPath );
//
//        // TODO: check if the user is already a member - error?
//        // TODO: what if that user is not a member? = error?
//        groupsChildRef.child( username ).removeValue();
//
//
//        /** Updating user_groups subtree */
//        String usernamePath = "usernames/" + username;
//        DatabaseReference usernameChildRef = rootRef.child( usernamePath );
//        usernameChildRef.addListenerForSingleValueEvent( new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot dataSnapshot ) {
//                String userId = dataSnapshot.getValue().toString();
//
//                String userGroupsPath = "user_groups/" + userId;
//                DatabaseReference userGroupsChildRef = rootRef.child( userGroupsPath );
//                userGroupsChildRef.child( mGroupId ).removeValue();
//
//                /** Updating group in memory and UI */
//                members.remove( username );
//
//                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
//            }
//
//
//            @Override
//            public void onCancelled( DatabaseError databaseError ) {
//            }
//        } );
//    }

    /**
     * Retrieves the group goals from the DB and attaches to the object
     *
     * @param listener
     */
    public void retrieveGroupGoals( final DBListener listener ) {

        // Path to group mGoals
        String path = "groups/" + mGroupId + "/mGoals";

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    for ( DataSnapshot goalDataSnapshot : dataSnapshot.getChildren() ) {
                        String exerciseName = goalDataSnapshot.getKey();
                        // We have a goal for this exercise
                        Object targetObj = goalDataSnapshot.getValue();
                        float target;
                        if ( targetObj instanceof Long ) {
                            target = ( ( Long ) targetObj ).floatValue();
                        } else {
                            target = ( ( Float ) targetObj ).floatValue();
                        }
                        mGoals.add( new Goal( exerciseName, 0, target ) );
                    }
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

//    public void retrieveGroupMembers( final DBListener listener ) {
//        String path = "groups/" + mGroupId + "/members";
//        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
//        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot dataSnapshot ) {
//                if ( dataSnapshot.exists() ) {
//                    for ( DataSnapshot memberDataSnapshot : dataSnapshot.getChildren() ) {
//                        String memberName = memberDataSnapshot.getKey();
//
//                        members.add( memberName );
//                    }
//                }
//
//                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
//            }
//
//            @Override
//            public void onCancelled( DatabaseError databaseError ) {
//            }
//        } );
//
//    }

    public static void retrieveGroupNames( @NotNull final ArrayList< String > groupIds,
                                           @NotNull final ArrayList< Group > groups,
                                           final DBListener listener ) {


        // The UI is updated when all of the group names have been added
        // Necessary because of the async call within the for loop
        final int expectedSize = groupIds.size();

        if ( expectedSize == 0 ) {
            if ( listener != null && listener.isActive() )
                listener.onRetrievalFinished();
        } else {
            for ( final String groupId : groupIds ) {
                String groupPath = "groups/" + groupId;
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child( groupPath );

                groupRef.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        String groupName = dataSnapshot.child( "name" ).getValue().toString();
                        groups.add( new Group( groupName, groupId ) );
                        if ( groups.size() == expectedSize ) {
                            if ( listener != null && listener.isActive() )
                                listener.onRetrievalFinished();
                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {
                    }
                } );
            }
        }
    }

    public void retrieveThisMembersProgress( final DBListener listener, final Goal goal ) {
        String username = User.getInstance().getUsername();
        String path = "groups/" + mGroupId + "/members/" + username + "/progress/" + goal.getmExerciseName();
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    Object progressObj = dataSnapshot.getValue();
                    float progress;
                    if ( progressObj instanceof Long ) {
                        progress = ( ( Long ) progressObj ).floatValue();
                    } else {
                        progress = ( ( Float ) progressObj ).floatValue();
                    }
                    goal.setmCurrentStatus( progress );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }


    public void updateMyStatusFromPersonalGoals( final Goal goal, final DBListener listener ) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String path = "user_goals/" + userId + "/" + goal.getmExerciseName() + "/current_status";

        final DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    Object currentStatusObj = dataSnapshot.getValue();
                    float currentStatus;
                    if ( currentStatusObj instanceof Long ) {
                        currentStatus = ( ( Long ) currentStatusObj ).floatValue();
                    } else {
                        currentStatus = ( ( Float ) currentStatusObj ).floatValue();
                    }
                    goal.setmCurrentStatus( currentStatus );

                    if ( listener != null && listener.isActive() )
                        listener.onRetrievalFinished( goal );
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public String getmName() {
        return mName;
    }

    public void setmName( String mName ) {
        this.mName = mName;
    }

    public String getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId( String mGroupId ) {
        this.mGroupId = mGroupId;
    }


    public String getmCreator() {
        return mCreator;
    }

    public void setmCreator( String mCreator ) {
        this.mCreator = mCreator;
    }

    public ArrayList< Goal > getmGoals() {
        return mGoals;
    }

    public void setmGoals( ArrayList< Goal > mGoals ) {
        this.mGoals = mGoals;
    }
}
