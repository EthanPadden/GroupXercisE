package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

public class LogWalkFragment extends Fragment {
    private Button mLogWalkBtn;
    private EditText mStepsEt;

    public LogWalkFragment() {
        // Required empty public constructor
    }

    public static LogWalkFragment newInstance() {
        LogWalkFragment fragment = new LogWalkFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to Goals fragment
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                GoalsFragment goalsFragment = new GoalsFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, goalsFragment );
                ft.commit();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_log_walk, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mLogWalkBtn = getActivity().findViewById( R.id.btn_log_walk );
        mStepsEt = getActivity().findViewById( R.id.et_steps );

        // Set on click listeners
        mLogWalkBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String stepsStr = mStepsEt.getText().toString();
                if ( stepsStr != null && stepsStr.compareTo( "" ) != 0 ) {
                    try {
                        int steps = Integer.parseInt( stepsStr );
                        ExerciseActivity exerciseActivity = new ExerciseActivity( "Walking", DateTime.now(), steps );
                        String msg = "Logging " + steps + " steps";
                        Toast.makeText( getActivity(), msg, Toast.LENGTH_SHORT ).show();
                        logWalk( exerciseActivity );
                    } catch ( NumberFormatException e ) {
                        Toast.makeText( getActivity(), "Steps must be a number", Toast.LENGTH_SHORT ).show();
                    }
                }  else {
                    Toast.makeText( getActivity(), "Enter steps", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    private void logWalk( ExerciseActivity exerciseActivity ) {
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

        // Update progress towards daily goal in goals subtree
        String goalsPath = "personal_goals/" + currentUserId + "/Walking";
        DatabaseReference goalsRef = homeScreenActivity.getmRootRef().child( goalsPath );

        goalsRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                // LocalDate represents a date without time
                long lastWalkTimeTS = (Long) dataSnapshot.child( "last_walk_time" ).getValue();
                DateTime lastWalkTimeDT = new DateTime( lastWalkTimeTS );
                LocalDate lastWalkDate = lastWalkTimeDT.toLocalDate();

                DateTime todayDT = DateTime.now();
                LocalDate todayDate = todayDT.toLocalDate();

                // Get steps as an int rather than a float
                int steps = Math.round( exerciseActivity.getmLevel() );

                if(!lastWalkDate.equals( todayDate )) {
                    // It is a new day, so set todays progress as the steps for this walk
                    goalsRef.child( "progress" ).setValue( exerciseActivity.getmLevel() );
                } else {
                    // It is the same day, so append the steps
                    Object currentStepsObj = dataSnapshot.child( "progress" ).getValue();
                    int currentSteps, newSteps;
                    if (dataSnapshot.exists()) {
                        if (currentStepsObj instanceof Long ){
                            currentSteps = ( ( Long ) currentStepsObj ).intValue();
                        } else {
                            currentSteps = ( ( Integer ) currentStepsObj ).intValue();
                        }
                        newSteps = currentSteps + steps;
                    } else {
                        newSteps = steps;
                    }

                    goalsRef.child( "progress" ).setValue( newSteps );
                }

                // Set the new last walk time TS as now TS
                long nowTS = DateTime.now().getMillis();
                goalsRef.child( "last_walk_time" ).setValue( nowTS );
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {

            }
        } );
    }
}
