package com.nova.groupxercise.Objects;

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
    private ArrayList<String> members;
    private ArrayList<Goal> goals;

    public Group( String mGroupName, String mGroupId ) {
        this(mGroupId);
        this.mGroupName = mGroupName;
    }

    public Group( String mGroupId ) {
        this.mGroupId = mGroupId;
    }

    public static void retrieveGroupIds( @NotNull final ArrayList<String> groupIds, final DBListener listener) {

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        String usersGroupPath = "user_groups/" + currentUserId;


        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    groupIds.add(  usersGroupsDataSnapshot.getKey());
                }

                listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }


    public static void retrieveGroupIds( @NotNull final ArrayList<String> adminGroupIds, @NotNull final ArrayList<String> groupIds, final DBListener listener) {

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
                    groupIds.add(  usersGroupsDataSnapshot.getKey());
                }

                listener.onRetrievalFinished();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }


    public static void retrieveGroupNames( @NotNull final ArrayList<String> groupIds,
                                           @NotNull final ArrayList<Group> groups,
                                           final DBListener listener) {



        // The UI is updated when all of the group names have been added
        // Necessary because of the async call within the for loop
        final int expectedSize = groupIds.size();

        for( final String groupId : groupIds) {
            String groupPath = "groups/" + groupId;
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child( groupPath );

            groupRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    String groupName = dataSnapshot.child( "name" ).getValue().toString();
                    groups.add( new Group( groupName, groupId ) );
                    if(groups.size() == expectedSize) {
                       listener.onRetrievalFinished();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
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
