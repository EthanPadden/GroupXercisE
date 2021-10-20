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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Adapters.GroupMemberRecyclerAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.Member;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupFragment extends Fragment {
    private Group mGroup;
    private String mGroupId;
    private Button mAddMemberBtn;
    private Button mDeleteGroupBtn;
    private EditText mMemberNameEt;
    private TextView mGroupGoalsLoadingText;
    //    private LinearLayout mGroupMembersLayout;
    private LinearLayout mGroupGoalsLayout;
    private ArrayList< DBListener > mDBListeners;
    private boolean adminGroup;
    private RecyclerView mGroupMembersRecycler;
    private GroupMemberRecyclerAdapter mGroupMemberRecyclerAdapter;
    private DatabaseReference mGroupMembersRef;
    private ValueEventListener mGroupMembersListener;


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

        // Initially assume that the current user is not a group admin
        // This will be updated if it is found that they are
        adminGroup = false;

        // Initialise components
        mAddMemberBtn = view.findViewById( R.id.btn_add_member );
        mMemberNameEt = view.findViewById( R.id.et_member_name );
        mDeleteGroupBtn = view.findViewById( R.id.btn_delete_group );
        mGroupGoalsLoadingText = view.findViewById( R.id.text_group_goals_loading );
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );
        mGroupMembersRecycler = view.findViewById( R.id.recycler_members );

        // Create arraylist for DB single-value events
        mDBListeners = new ArrayList<>();

        // Set event listeners
        mAddMemberBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                // Get the username of the user to be added
                final String username = mMemberNameEt.getText().toString();

                if ( User.checkIfUsernameIsValid( username ) ) {
                    // If the username is valid, check does the user exist
                    DBListener userCheckListener = new DBListener() {
                        public void onRetrievalFinished( Object retrievedData ) {
                            if ( retrievedData == null ) {
                                // The user does not exist
                                Toast.makeText( getActivity(), "User not found: " + username, Toast.LENGTH_SHORT ).show();
                            } else {
                                // The user exists - get the user ID and add the user to the group
                                String userId = ( String ) retrievedData;
                                DBListener additionListener = new DBListener() {
                                    public void onRetrievalFinished( Object retrievedData ) {
                                        Toast.makeText( getActivity(), "Member added: " + username, Toast.LENGTH_SHORT ).show();
                                        mDBListeners.remove( this );
                                    }
                                };
                                mDBListeners.add( additionListener );
                                mGroup.addMember( username, userId, additionListener );
                            }

                            mDBListeners.remove( this );
                        }
                    };
                    mDBListeners.add( userCheckListener );
                    User.checkIfUserExists( username, userCheckListener );
                } else {
                    Toast.makeText( getActivity(), "Invalid username", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
//        mDeleteGroupBtn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick( View view ) {
//                mGroup.deleteGroup();
//                // Return to my groups fragment
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                MyGroupsFragment myGroupsFragment = new MyGroupsFragment();
//                ft.replace( R.id.frame_home_screen_fragment_placeholder, myGroupsFragment );
//                ft.commit();
//            }
//        } );

        // Retrieve the group information
        DBListener groupInfoListener = new DBListener() {
            public void onRetrievalFinished() {
                // If the current user is the admin, show the controls
                String currentUsername = User.getInstance().getUsername();
                if ( mGroup.getmCreator().compareTo( currentUsername ) == 0 ) {
                    mAddMemberBtn.setVisibility( View.VISIBLE );
                    mMemberNameEt.setVisibility( View.VISIBLE );
                    mDeleteGroupBtn.setVisibility( View.VISIBLE );
                    adminGroup = true;
                }

                // Set up listener for changes to the members subtree
                setupGroupMemberListeners();

                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( groupInfoListener );
        mGroup.retrieveGroupInfo( groupInfoListener );

        DBListener groupGoalListener = new DBListener() {
            public void onRetrievalFinished() {
                if ( mGroup.getmGoals().size() == 0 ) {
                    mGroupGoalsLoadingText.setText( "No goals" );
                } else {
                    mGroupGoalsLoadingText.setVisibility( View.GONE );

                    for ( Goal goal : mGroup.getmGoals() ) {
                        View goalView = createGroupGoalUIComponent( goal );
                        mGroupGoalsLayout.addView( goalView );
                    }
                }
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( groupGoalListener );
        mGroup.retrieveGroupGoals( groupGoalListener );
    }

    /**
     * Creates a listener for the members subtree of the group in the DB
     * When it chagnes (including the initial call), it updates the UI
     */
    private void setupGroupMemberListeners() {
        final String path = "groups/" + mGroupId + "/members";
        mGroupMembersRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );
        mGroupMemberRecyclerAdapter = new GroupMemberRecyclerAdapter( getContext(), mGroup, adminGroup);
        mGroupMembersRecycler.setAdapter( mGroupMemberRecyclerAdapter );

        // Get the DB reference
        mGroupMembersRef = FirebaseDatabase.getInstance().getReference().child( path );

        mGroupMembersListener = new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot membersDataSnapshot ) {
//                 Parse the snapshot, updating the group in memory
                for ( DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren() ) {
                    // Retrieve member username and create member object
                    final String username = memberDataSnapshot.getKey();
                    Member member = new Member( username );

                    // For every progress in the progress subtree, create a goal object
                    // and add to the member object
                    for ( DataSnapshot progressDataSnapshot : memberDataSnapshot.child( "progress" ).getChildren() ) {
                        // Get the exercise name
                        String exerciseName = progressDataSnapshot.getKey();

                        // Get the current status (member progress towards that goal
                        Object currentStatusObj = progressDataSnapshot.getValue();
                        float currentStatus;
                        if ( currentStatusObj instanceof Long ) {
                            currentStatus = ( ( Long ) currentStatusObj ).floatValue();
                        } else {
                            currentStatus = ( ( Float ) currentStatusObj ).floatValue();
                        }

                        // We are not concerned with the target, it is stored in the group goal
                        float target = 0;

                        Goal goal = new Goal( exerciseName, currentStatus, target );
                        member.getmProgress().add( goal );
                    }

                    mGroup.getmMembers().add( member );
                }

                mGroupMemberRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        };

        mGroupMembersRef.addValueEventListener( mGroupMembersListener );
    }

    private View createGroupGoalUIComponent( Goal goal ) {
        View goalView = getLayoutInflater().inflate( R.layout.layout_goal_list_item, null );
        TextView exerciseNameText = goalView.findViewById( R.id.goal_exercise_name );
        TextView currentStatusText = goalView.findViewById( R.id.goal_current_status );
        TextView dividerText = goalView.findViewById( R.id.goal_divider );
        TextView targetText = goalView.findViewById( R.id.goal_target );
        exerciseNameText.setText( goal.getmExerciseName() );
        currentStatusText.setVisibility( View.GONE );
        dividerText.setVisibility( View.GONE );
        targetText.setText( Float.toString( goal.getmTarget() ) );
        return goalView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            // Deactivate any active listeners
            dbListener.setActive( false );
        }

        mGroupMembersRef.removeEventListener( mGroupMembersListener );
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>();
        mGroup = new Group( mGroupId );
    }
}
