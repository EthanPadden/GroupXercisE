package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nova.groupxercise.Activities.CreateGroupActivity;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Adapters.GroupItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class MyGroupsFragment extends Fragment {
    private FloatingActionButton mCreateGroupBtn;
    private ArrayList< Group > mGroups;
    private GroupItemsAdapter mItemsAdapter;
    private TextView mLoadingText;
    private ListView mListView;
    private ArrayList< DBListener > mDBListeners;

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>();
    }

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

        // Initialise components
        mListView = view.findViewById( R.id.groups_list );
        mCreateGroupBtn = view.findViewById( R.id.btn_create_group );
        mLoadingText = view.findViewById( R.id.info_groups );

        // Set event listeners
        mCreateGroupBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent( getActivity(), CreateGroupActivity.class );
                startActivity( intent );
            }
        } );

        DBListener groupIdListener = new DBListener() {
            public void onRetrievalFinished( Object retrievedData ) {
                ArrayList<String> retrievedGroupIds = (ArrayList< String>) retrievedData;
                // Create an empty list for the group names
                mGroups = new ArrayList<>();

                // Set the list as the list for the items adapter
                mItemsAdapter = new GroupItemsAdapter( getActivity(), mGroups );

                DBListener groupNameListener = new DBListener() {
                    public void onRetrievalFinished() {
                        setupGroupsList();
                        for ( Group group : mGroups ) {
                            DBListener groupMembersListener = new DBListener() {
                                public void onRetrievalFinished() {
                                    mDBListeners.remove( this );
                                    mItemsAdapter.notifyDataSetChanged();
                                }
                            };
                            mDBListeners.add( groupMembersListener );
                            group.retrieveGroupMembers( groupMembersListener );
                        }
                        mItemsAdapter.notifyDataSetChanged();
                        mDBListeners.remove( this );
                    }
                };
                mDBListeners.add( groupNameListener );
                Group.retrieveGroupNames( retrievedGroupIds, mGroups, groupNameListener );



                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( groupIdListener );
        Group.retrieveGroupIds( groupIdListener );
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }

    /**
     * Updates the UI with the group names and sets event listeners for the list items
     */
    private void setupGroupsList() {
        if(mGroups.size() == 0) {
            mLoadingText.setText( "You have no groups" );
        }else {
            mLoadingText.setVisibility( View.GONE );
            mListView.setAdapter( mItemsAdapter );

            // Set event listeners
            mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                    Group selectedGroup = ( Group ) mListView.getItemAtPosition( i );

                    HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
                    homeScreenActivity.getSupportActionBar().setTitle( selectedGroup.getmName() );

                    FragmentTransaction ft = homeScreenActivity.getSupportFragmentManager().beginTransaction();
                    GroupFragment groupFragment = new GroupFragment( selectedGroup.getmGroupId() );
                    ft.replace( R.id.frame_home_screen_fragment_placeholder, groupFragment );
                    ft.commit();
                }
            } );
        }
    }

}
