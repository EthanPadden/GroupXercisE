package com.nova.groupxercise;

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

        retrieveGroupInfo();

    }

    private void retrieveGroupInfo() {
        // Path to the group
        String path = "groups/" + mGroupId;

        // Get the DB reference
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );



        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                String dbGroupName = dataSnapshot.child( "name" ).getValue().toString();
                String dbGroupCreator = dataSnapshot.child( "creator" ).getValue().toString();
                DataSnapshot membersDataSnapshot = dataSnapshot.child( "members" );
                ArrayList<String> dbMembers = new ArrayList<>(  );
                for(DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren()) {
                    dbMembers.add( memberDataSnapshot.getKey() );
                }

                mGroup = new Group( dbGroupName, mGroupId );
                mGroup.setmGroupCreator( dbGroupCreator );
                mGroup.setMembers( dbMembers );

                mGroupNameText.setText( mGroup.getmGroupName() );
                mGroupCreatorText.setText( mGroup.getmGroupCreator() );

                mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mGroup.getMembers() );
                mGroupMembersList.setAdapter( mItemsAdapter );

            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
