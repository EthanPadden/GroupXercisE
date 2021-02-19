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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.nova.groupxercise.Adapters.GroupMemberRecyclerAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.MemberProgress;
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
    private ArrayList< MemberProgress > mMemberProgresses;
    private GroupMemberRecyclerAdapter mGroupMemberRecyclerAdapter;


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
        mGroupGoalsLoadingText = view.findViewById( R.id.text_group_goals_loading );
//        mGroupMembersLayout = view.findViewById( R.id.layout_members );
        mGroupGoalsLayout = view.findViewById( R.id.layout_group_goals );
        mGroupMembersRecycler = view.findViewById( R.id.recycler_members );

        mDBListeners = new ArrayList<>();

        // Set event listeners
        mAddMemberBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                final String username = mMemberNameEt.getText().toString();
                if ( User.checkIfUsernameIsValid( username ) ) {
                    DBListener userCheckListener = new DBListener() {
                        public void onRetrievalFinished(Object retrievedData) {
                            if(retrievedData == null) {
                                Toast.makeText( getActivity(), "User not found: " + username, Toast.LENGTH_SHORT ).show();
                            } else {
                                String userId = (String) retrievedData;
                                DBListener additionListener = new DBListener() {
                                    public void onRetrievalFinished() {
                                        mDBListeners.remove( this );
                                    }
                                };
                                mDBListeners.add( additionListener );
                                mGroup.addMember( username, userId, additionListener );
                                Toast.makeText( getActivity(), "Member added: " + username, Toast.LENGTH_SHORT ).show();
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

        mDeleteGroupBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                mGroup.deleteGroup();
                // Set the fragment to be the my groups fragment
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                MyGroupsFragment myGroupsFragment = new MyGroupsFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, myGroupsFragment );
                ft.commit();
            }
        } );

        mGroup = new Group( mGroupId );
        mMemberProgresses = new ArrayList<>(  );
//        retrieveGroupInfo();

        DBListener groupInfoListener = new DBListener() {
            public void onRetrievalFinished() {
                // If the current user is the admin, show the controls
                User currentUser = User.getInstance();
                String currentUsername = currentUser.getUsername();
                if ( mGroup.getmGroupCreator().compareTo( currentUsername ) == 0 ) {
                    mAddMemberBtn.setVisibility( View.VISIBLE );
                    mMemberNameEt.setVisibility( View.VISIBLE );
                    mDeleteGroupBtn.setVisibility( View.VISIBLE );
                    adminGroup = true;
                }

                DBListener groupProgressListener = new DBListener() {
                    public void onRetrievalFinished( Object retrievedData ) {
                        DataSnapshot membersDataSnapshot = ( DataSnapshot ) retrievedData;
                        ArrayList< String > dbMembers = new ArrayList<>();
                        for ( DataSnapshot memberDataSnapshot : membersDataSnapshot.getChildren() ) {
                            final String username = memberDataSnapshot.getKey();
                            dbMembers.add( username );
                            MemberProgress memberProgress = new MemberProgress( username );
                            for(DataSnapshot goalDataSnaphot: memberDataSnapshot.child( "progress" ).getChildren()){
                                String exerciseName = goalDataSnaphot.getKey();
                                Object personalProgressObj = goalDataSnaphot.getValue();

                                float personalProgress;
                                if ( personalProgressObj instanceof Long ) {
                                    personalProgress = ( ( Long ) personalProgressObj ).floatValue();
                                } else {
                                    personalProgress = ( ( Float ) personalProgressObj ).floatValue();
                                }

                                Goal goal = new Goal( exerciseName, personalProgress, 0.0f );
                                memberProgress.getMemberProgresses().add( goal );
                            }

                            mMemberProgresses.add( memberProgress );

//                            View memberCard = createMemberUIComponent( memberDataSnapshot, adminGroup );
//                            mGroupMembersLayout.addView( memberCard );
                        }

                        mGroupMembersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        mGroupMemberRecyclerAdapter = new GroupMemberRecyclerAdapter(getContext(), mMemberProgresses);
                        mGroupMembersRecycler.setAdapter(mGroupMemberRecyclerAdapter);

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

    private View createGroupGoalUIComponent(Goal goal){
        View goalView = getLayoutInflater().inflate( R.layout.layout_goal_list_item, null );
        TextView exerciseNameText = goalView.findViewById( R.id.goal_exercise_name );
        TextView currentStatusText = goalView.findViewById( R.id.goal_current_status );
        TextView dividerText = goalView.findViewById( R.id.goal_divider );
        TextView targetText = goalView.findViewById( R.id.goal_target );
        exerciseNameText.setText( goal.getmExerciseName() );
        currentStatusText.setVisibility( View.GONE );
        dividerText.setVisibility( View.GONE );
        targetText.setText( Float.toString(  goal.getmTarget()) );
        return goalView;
    }

//    private View createMemberUIComponent(DataSnapshot memberDataSnapshot, boolean adminGroup) {
//        final String username = memberDataSnapshot.getKey();
//        View memberCard = getLayoutInflater().inflate( R.layout.layout_group_member_card, null );
//        LinearLayout memberCardRootLayout = memberCard.findViewById( R.id.layout_card_root );
//
//
//        View memberNameView = getLayoutInflater().inflate( R.layout.layout_group_member_name, memberCardRootLayout );
//        TextView usernameTextView = memberNameView.findViewById( R.id.text_member_name );
//        TextView userStatusTextView = memberNameView.findViewById( R.id.text_member_status );
//        usernameTextView.setText( username );
//        if ( username.compareTo( mGroup.getmGroupCreator() ) == 0 ) {
//            userStatusTextView.setText( "Admin" );
//            userStatusTextView.setVisibility( View.VISIBLE );
//        }
//
//        else if(adminGroup) {
//            final Button removeMemberBtn =  memberCard.findViewById( R.id.btn_remove_member );
//            removeMemberBtn.setVisibility(View.VISIBLE );
//            removeMemberBtn.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick( View view ) {
//                    DBListener removalListener = new DBListener() {
//                        public void onRetrievalFinished() {
//                            mDBListeners.remove( this );
//                        }
//                    };
//                    mDBListeners.add( removalListener );
//                    mGroup.removeMember( username, removalListener );
//                }
//            } );
//        }
//
//        for ( DataSnapshot progressDataSnapshot : memberDataSnapshot.child( "progress" ).getChildren() ) {
//            String exerciseName = progressDataSnapshot.getKey();
//            Object progressObj = progressDataSnapshot.getValue();
//            float progress;
//            if ( progressObj instanceof Long ) {
//                progress = ( ( Long ) progressObj ).floatValue();
//            } else {
//                progress = ( ( Float ) progressObj ).floatValue();
//            }
//
//            View progressView = getLayoutInflater().inflate( R.layout.layout_group_member_progress, null );
//            TextView exerciseNameText = progressView.findViewById( R.id.text_progress_exercise_name );
//            TextView progressText = progressView.findViewById( R.id.text_progress );
//            exerciseNameText.setText( exerciseName );
//            progressText.setText( Float.toString( progress ) );
//            memberCardRootLayout.addView( progressView );
//        }
//
//        return memberCard;
//    }

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



}
