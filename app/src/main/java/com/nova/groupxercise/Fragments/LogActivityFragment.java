package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogActivityFragment extends Fragment {
    private TextView mTitleText;
    private Button mLogActivityBtn;
    private EditText mLevelEt;
    protected ArrayList< DBListener > mDBListeners;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXERCISE_NAME = "Exercise name";

    // TODO: Rename and change types of parameters
    private String mExerciseName;

    public LogActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        for(DBListener dbListener : mDBListeners) {
            dbListener.setActive( false );
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mExerciseName Parameter 1.
     * @return A new instance of fragment ExerciseListItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogActivityFragment newInstance( String mExerciseName ) {
        LogActivityFragment fragment = new LogActivityFragment();
        Bundle args = new Bundle();
        args.putString( EXERCISE_NAME, mExerciseName );
        fragment.setArguments( args );

        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            mExerciseName = getArguments().getString( EXERCISE_NAME );
        }

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                GoalsFragment goalsFragment = new GoalsFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, goalsFragment );
                ft.commit();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        mTitleText = getActivity().findViewById( R.id.text_title );
        mTitleText.setText( "Log activity for " + mExerciseName );
        mLogActivityBtn = getActivity().findViewById( R.id.btn_log_activity );
        mLevelEt = getActivity().findViewById( R.id.et_level );

        mLogActivityBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String levelStr = mLevelEt.getText().toString();
                if ( levelStr != null && levelStr.compareTo( "" ) != 0 ) {
                    try {
                        float level = Float.parseFloat( levelStr );
                        ExerciseActivity exerciseActivity = new ExerciseActivity( mExerciseName, DateTime.now(), level );
                        logActivity( exerciseActivity );
                    } catch ( NumberFormatException e ) {
                        Toast.makeText( getActivity(), "Level must be a number", Toast.LENGTH_SHORT ).show();
                    }
                }  else {
                    Toast.makeText( getActivity(), "Enter level", Toast.LENGTH_SHORT ).show();
                }
        }
    });}

        private void logActivity( ExerciseActivity exerciseActivity ) {
            // Add activity to activities subtree
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = currentUser.getUid();

            String activitiesPath = "activities/" + currentUserId + "/" + exerciseActivity.getmExerciseName();
            final HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
            DatabaseReference activitiesRef = homeScreenActivity.getmRootRef().child( activitiesPath );

            Instant activityInstant = exerciseActivity.getmTime().toInstant();
            long activityTimeStamp = activityInstant.getMillis();
            String activityTimeStampStr = Long.toString( activityTimeStamp );

            activitiesRef.child( activityTimeStampStr ).setValue( exerciseActivity.getmLevel() );

            // Get the current user progress towards the exercise and see if this beats the record
            Goal goal = new Goal( exerciseActivity.getmExerciseName() );
            DBListener progressListener = new DBListener() {
                public void onRetrievalFinished( Object retrievedData ) {
                    // If retrievedData == null,
                    // there is no progress saved for this exercise yet
                    // so 0 is the default value
                    float progress = 0;

                    if ( retrievedData != null ) {
                        progress = ( ( Float ) retrievedData ).floatValue();
                    }

                    if ( exerciseActivity.getmLevel() > progress) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String userProgressPath = "user_progress/" + userId + "/" + exerciseActivity.getmExerciseName();
                        DatabaseReference userProgressRef = homeScreenActivity.getmRootRef().child( userProgressPath );
                        userProgressRef.setValue( exerciseActivity.getmLevel() );
                    }
                    Toast.makeText( getActivity(), "Activity logged", Toast.LENGTH_SHORT ).show();

                    Intent intent = new Intent( getActivity(), HomeScreenActivity.class );
                    intent.putExtra( "FRAGMENT_ID", R.id.navigation_activities );
                    startActivity( intent );

                    mDBListeners.remove( this );
                }

            };
            mDBListeners.add( progressListener );
            goal.retrieveUserProgress( progressListener );
        }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_log_activity, container, false );
    }
}