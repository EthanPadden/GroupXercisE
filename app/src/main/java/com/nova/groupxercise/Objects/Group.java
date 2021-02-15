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
    private String mGroupName;
    private String mGroupId;
    private String mGroupCreator;
    private ArrayList< String > members;
    private ArrayList< Goal > goals;

    public Group( String mGroupName, String mGroupId ) {
        this( mGroupId );
        this.mGroupName = mGroupName;
    }

    public Group( String mGroupId ) {
        this.mGroupId = mGroupId;
        members = new ArrayList<>();
        goals = new ArrayList<>();
    }

    public static void retrieveGroupIds( @NotNull final ArrayList< String > groupIds, final DBListener listener ) {

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        String usersGroupPath = "user_groups/" + currentUserId;


        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    groupIds.add( usersGroupsDataSnapshot.getKey() );
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
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
                setmGroupName( dbGroupName );

                String dbGroupCreator = dataSnapshot.child( "creator" ).getValue().toString();
                setmGroupCreator( dbGroupCreator );

                DataSnapshot membersDataSnapshot = dataSnapshot.child( "members" );
                for ( DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren() ) {
                    String username = memberDataSnapshot.getKey();
                    members.add( username );
                }

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

    public void addMember( final String username, final String userId, final DBListener listener ) {
        /** Updating groups subtree */
        // Path to this groups members child
        String thisGroupMembersPath = "groups/" + mGroupId + "/members";

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupsChildRef = rootRef.child( thisGroupMembersPath );

        groupsChildRef.child( username ).setValue( false );

        // TODO: check if the user is already a member - error?

        /** Updating user_groups subtree */
        String userGroupsPath = "user_groups/" + userId;
        DatabaseReference userGroupsChildRef = rootRef.child( userGroupsPath );
        userGroupsChildRef.child( mGroupId ).setValue( false );

        /** Updating group in memory and UI */
        members.add( username );

        /** Create subtree for the progress of that user towards the goals */
        for ( Goal goal : goals ) {
            User user = new User();
            user.setUsername( username );
            goal.matchUserProgressToGroup( userId, user, this );
        }

        if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
    }

    public void deleteGroup() {
        for ( String member : members ) {
            removeMember( member, null );
        }
        // Delete group subtree
        String groupPath = "groups/" + mGroupId;

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child( groupPath );
        groupRef.removeValue();
    }

    public void saveGoal( final Goal goal, final DBListener listener ) {
        // Path to the group goal
        String path = "groups/" + mGroupId + "/goals/" + goal.getmExerciseName();

        // Get the DB reference
        final DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        // Check if we have a set of goals for that particular user
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

    public void removeMember( final String username, final DBListener listener ) {
        /** Updating groups subtree */
        // Path to this groups members child
        String thisGroupMembersPath = "groups/" + mGroupId + "/members";

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupsChildRef = rootRef.child( thisGroupMembersPath );

        // TODO: check if the user is already a member - error?
        // TODO: what if that user is not a member? = error?
        groupsChildRef.child( username ).removeValue();


        /** Updating user_groups subtree */
        String usernamePath = "usernames/" + username;
        DatabaseReference usernameChildRef = rootRef.child( usernamePath );
        usernameChildRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                String userId = dataSnapshot.getValue().toString();

                String userGroupsPath = "user_groups/" + userId;
                DatabaseReference userGroupsChildRef = rootRef.child( userGroupsPath );
                userGroupsChildRef.child( mGroupId ).removeValue();

                /** Updating group in memory and UI */
                members.remove( username );

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }


            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public void retrieveGroupGoals( final DBListener listener ) {

        // Path to group goals
        String path = "groups/" + mGroupId + "/goals";

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
                        goals.add( new Goal( exerciseName, 0, target ) );
                    }
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    public void retrieveGroupMembers( final DBListener listener ) {
        String path = "groups/" + mGroupId + "/members";
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( path );
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    for ( DataSnapshot memberDataSnapshot : dataSnapshot.getChildren() ) {
                        String memberName = memberDataSnapshot.getKey();

                        members.add( memberName );
                    }
                }

                if ( listener != null && listener.isActive() ) listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );

    }

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

    public String getmGroupName() {
        return mGroupName;
    }

    public void setmGroupName( String mGroupName ) {
        this.mGroupName = mGroupName;
    }

    public String getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId( String mGroupId ) {
        this.mGroupId = mGroupId;
    }

    public ArrayList< String > getMembers() {
        return members;
    }

    public void setMembers( ArrayList< String > members ) {
        this.members = members;
    }

    public String getmGroupCreator() {
        return mGroupCreator;
    }

    public void setmGroupCreator( String mGroupCreator ) {
        this.mGroupCreator = mGroupCreator;
    }

    public ArrayList< Goal > getGoals() {
        return goals;
    }

    public void setGoals( ArrayList< Goal > goals ) {
        this.goals = goals;
    }
}
