package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nova.groupxercise.Adapters.GoalItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupMemberDetailsFragment extends Fragment {
    private User mMember;
    private String mMemberName;
    private TextView memberDetailsTitleText;
    private ArrayList< DBListener > mDBListeners;
    private Group mGroup;
    private ListView mMemberProgressesListView;
    private GoalItemsAdapter mItemsAdapter;
    private TextView loadingText;
    private TextView adminStatusText;
    private Button removeMemberBtn;

    public GroupMemberDetailsFragment( String memberName, Group group) {
        this.mMemberName = memberName;
        this.mGroup = group;
        mMember = new User(memberName);
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                GroupFragment groupFragment = new GroupFragment( mGroup.getmGroupId() );
                ft.replace( R.id.frame_home_screen_fragment_placeholder, groupFragment );
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_group_member_details, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        memberDetailsTitleText = view.findViewById( R.id.text_member_details_title );
        memberDetailsTitleText.setText( mMemberName );
        mMemberProgressesListView = view.findViewById( R.id.list_member_progress );
        loadingText = view.findViewById( R.id.text_member_progress_loading );
        adminStatusText = view.findViewById( R.id.text_member_admin_status );
        removeMemberBtn = view.findViewById( R.id.btn_remove_member );

        DBListener userIDListener = new DBListener() {
            public void onRetrievalFinished(Object retrievedData) {
                if(retrievedData != null) {
                    String userID = (String) retrievedData;
                    DBListener progressListener = new DBListener() {
                        @Override
                        public void onRetrievalFinished( Object retrievedData ) {
                            if ( retrievedData != null ) {
                                ArrayList< Goal > userProgresses = ( ArrayList< Goal > ) retrievedData;

                                // Remove any goal from the userProgresses that is not in the group goals
                                for(Goal userProgress : userProgresses) {
                                    boolean isInGroupGoals = false;
                                    for(Goal groupGoal : mGroup.getmGoals()) {
                                        if(userProgress.getmExerciseName().compareTo( groupGoal.getmExerciseName() ) == 0) {
                                            isInGroupGoals = true;
                                            // Update the member progress with the target from the group goals
                                            userProgress.setmTarget( groupGoal.getmTarget() );
                                            break;
                                        }
                                    }

                                    if(!isInGroupGoals) {
                                        userProgresses.remove( userProgress );
                                    }
                                }

                                displayMemberProgress( userProgresses );
                            } else {
                                loadingText.setText( "No progress has been made by this member" );
                            }

                            mDBListeners.remove( this );
                        }
                    };
                    mDBListeners.add(progressListener);
                    Goal.retrieveUserProgress( progressListener, userID );
                } else {
                    loadingText.setText( "Failed to get member progress" );
                }

                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( userIDListener );
        mMember.retrieveUserID(userIDListener);

        // Show admin status if the member is an admin
        if(mMemberName.compareTo( mGroup.getmCreator() ) == 0) {
            adminStatusText.setVisibility( View.VISIBLE );
        }

        // If the logged in user is the admin of the group, make the remove button visible
        User loggedInUser = User.getInstance();
        if(loggedInUser.getUsername().compareTo( mGroup.getmCreator() ) == 0) {
            removeMemberBtn.setVisibility( View.VISIBLE );
        }
    }

    private void displayMemberProgress( ArrayList<Goal> memberProgresses) {
        // Use the group object to display the goals first
        mItemsAdapter = new GoalItemsAdapter( getActivity(), memberProgresses );
        mMemberProgressesListView.setAdapter( mItemsAdapter );
        loadingText.setVisibility( View.GONE );
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate any active listeners
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }
}
