package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    public GroupMemberDetailsFragment( String memberName, Group group) {
        this.mMemberName = memberName;
        this.mGroup = group;

        mMember = new User(memberName);

        // Create arraylist for DB single-value events
        mDBListeners = new ArrayList<>();
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

        memberDetailsTitleText = view.findViewById( R.id.text_member_details_title );
        //TODO: loading text
        memberDetailsTitleText.setText( mMemberName );
        mMemberProgressesListView = view.findViewById( R.id.list_member_progress );
        loadingText = view.findViewById( R.id.text_member_progress_loading );

        DBListener userIDListener = new DBListener() {
            public void onRetrievalFinished(Object retrievedData) {
                if(retrievedData != null) {
                    String userID = (String) retrievedData;
                    // TODO: get member details method
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
    }

    private void displayMemberProgress( ArrayList<Goal> memberProgresses) {
        mItemsAdapter = new GoalItemsAdapter( getActivity(), memberProgresses );
        mMemberProgressesListView.setAdapter( mItemsAdapter );
        loadingText.setVisibility( View.GONE );
        // TODO: test out properly and display other parts of goal eg target
    }


    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for ( DBListener dbListener : mDBListeners ) {
            // Deactivate any active listeners
            dbListener.setActive( false );
        }
    }
}
