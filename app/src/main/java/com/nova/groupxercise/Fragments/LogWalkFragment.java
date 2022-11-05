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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogWalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogWalkFragment extends Fragment {
    private Button mLogWalkBtn;
    private EditText mStepsEt;

    public LogWalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogWalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogWalkFragment newInstance() {
        LogWalkFragment fragment = new LogWalkFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
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
        mLogWalkBtn = getActivity().findViewById( R.id.btn_log_walk );
        mStepsEt = getActivity().findViewById( R.id.et_steps );

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
                // Get the value for the last walk time
                // TODO: test!!!
                // https://www.baeldung.com/joda-time
                long lastWalkTimeTS = (Long) dataSnapshot.child( "last_walk_time" ).getValue();
                Instant lastWalkTimeInstant = new Instant(lastWalkTimeTS);
                DateTime lastWalkTimeDateTime = lastWalkTimeInstant.toDateTime();
                int lastWalkDateTimeDay = lastWalkTimeDateTime.getDayOfMonth();
                int today = DateTime.now().getDayOfMonth();

                // Get steps as an int rather than a float
                int steps = Math.round( exerciseActivity.getmLevel() );

                if(lastWalkDateTimeDay != today) {
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
