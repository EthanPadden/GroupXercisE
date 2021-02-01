package com.nova.groupxercise;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseListItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseListItemFragment extends Fragment {
    // Parameters
    private static final String EXERCISE_NAME = "Exercise Name";
    private String mExerciseName;

    // Event listeners
    private OnFragmentInteractionListener mListener;

    // Components
    private TextView mSetGoalTitleText;
    private TextView mSuggestedGoalText;
    private TextView mSetsText;
    private TextView mRepsText;
    private Spinner mLevelSpinner;
    private String mSelectedLevel;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;
    private Button mSetGoalBtn;
    private RadioButton mGoalOptionsAutomaticRatioBtn;
    private RadioButton mGoalOptionsManualRatioBtn;
    private EditText mManualGoalET;

    // For storing retrieved strength standards based on user details
    private DataSnapshot mStrengthStandards;

    // DB root reference
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public enum GoalOption {AUTOMATIC, MANUAL}

    private GoalOption mSelectedGoalOption;


    public ExerciseListItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mExerciseName Parameter 1.
     * @return A new instance of fragment ExerciseListItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseListItemFragment newInstance( String mExerciseName ) {
        ExerciseListItemFragment fragment = new ExerciseListItemFragment();
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

    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState ) {
        // Initialise components
        mSetGoalTitleText = view.findViewById( R.id.text_set_goal_title );
        mSuggestedGoalText = view.findViewById( R.id.text_suggested_goal );
        mSetsText = view.findViewById( R.id.text_sets );
        mRepsText = view.findViewById( R.id.text_reps );
        mLevelSpinner = view.findViewById( R.id.spinner_level );
        mSetGoalBtn = view.findViewById( R.id.btn_set_goal );
        mGoalOptionsAutomaticRatioBtn = view.findViewById( R.id.radio_btn_goal_option_automatic );
        mGoalOptionsManualRatioBtn = view.findViewById( R.id.radio_btn_goal_option_manual );
        mManualGoalET = view.findViewById( R.id.et_exercise_weight );

        // Set spinner adapter
        mLevelSpinner.setAdapter( mLevelSpinnerAdapter );

        // Set default level
        mSelectedLevel = getResources().getString( R.string.level_beginner );

        mSelectedGoalOption = GoalOption.AUTOMATIC;
        mGoalOptionsAutomaticRatioBtn.setChecked( true );

        // Set event listeners
        mLevelSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            public void onItemSelected( AdapterView< ? > parent, View view, int pos, long id ) {
                // Set selected level
                Object selectedOption = parent.getItemAtPosition( pos );
                String selectedOptionStr = selectedOption.toString();
                mSelectedLevel = selectedOptionStr;

                // Update to show the new suggested weight
                calculateSuggestedWeight();
            }

            public void onNothingSelected( AdapterView< ? > parent ) {
                mSelectedLevel = null;
            }
        } );
        mSetGoalBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                Goal goal;
                if ( mSelectedGoalOption == GoalOption.AUTOMATIC ) {
                    float target = Float.parseFloat( mSuggestedGoalText.getText().toString() );
                    goal = new Goal( mExerciseName, 0, target );
//                    Toast.makeText( getActivity(), goal.toString(), Toast.LENGTH_SHORT ).show();
                    checkDoesUserHaveAnyGoals( goal );
                } else if ( mSelectedGoalOption == GoalOption.MANUAL ) {
                    String targetStr = ( mManualGoalET.getText().toString() );

                    if ( targetStr != null && targetStr.compareTo( "" ) != 0 ) {
                        float target = Float.parseFloat( targetStr );
                        goal = new Goal( mExerciseName, 0, target );
                        Toast.makeText( getActivity(), goal.toString(), Toast.LENGTH_SHORT ).show();
                    } else {
                        Toast.makeText( getActivity(), "Enter a target", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( getActivity(), "There was an error", Toast.LENGTH_SHORT ).show();
                }
//                int selectedId = mGoalOptionsRadioGroup.getCheckedRadioButtonId();

            }
        } );
        mGoalOptionsAutomaticRatioBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                mSelectedGoalOption = GoalOption.AUTOMATIC;
                mGoalOptionsAutomaticRatioBtn.setChecked( true );
                mGoalOptionsManualRatioBtn.setChecked( false );
            }
        } );
        mGoalOptionsManualRatioBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                mSelectedGoalOption = GoalOption.MANUAL;
                mGoalOptionsAutomaticRatioBtn.setChecked( false );
                mGoalOptionsManualRatioBtn.setChecked( true );
            }
        } );

        // Set the fragment title
        mSetGoalTitleText.setText( R.string.set_goal_title + mExerciseName );

        // Set defauls sets and reps
        mSetsText.setText( "3" );
        mRepsText.setText( "10" );

        // Get the strength standards from the DB based on the user details
        retrieveStrengthStandards( mExerciseName );
    }

    private void checkDoesUserHaveAnyGoals( final Goal goal ) {
        if ( goal == null ) {
            Toast.makeText( getActivity(), "There was an error", Toast.LENGTH_SHORT ).show();
        } else {
            // Write a message to the database
            // TODO: use the current user name
            String tempUserName = "john_doe";
            String path = "user_goals/" + tempUserName;

            HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
            DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

            // Check if we have a set of goals for that particular user
            childRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    if ( dataSnapshot.exists() ) {
                        // This means there is a set of goals associated with the user
                        // (this could be an empty list)
                        Toast.makeText( getActivity(), "Goals found!", Toast.LENGTH_SHORT ).show();
                        saveGoal( goal );
                    } else {
                        // This is an error
                        Toast.makeText( getActivity(), "Error: you have no goals", Toast.LENGTH_SHORT ).show();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
    }

    private void saveGoal( Goal goal ) {
        // Write a message to the database
        // TODO: use the current user name
        String tempUserName = "john_doe";
        String path = "user_goals/" + tempUserName + "/" + "Shoulder Press";

        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        // Check if we have a set of goals for that particular user
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                if ( dataSnapshot.exists() ) {
                    Toast.makeText( getActivity(), "You have a goal for this exercise", Toast.LENGTH_SHORT ).show();

                } else {
                    Toast.makeText( getActivity(), "You have no goals", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Set the array adapter for the spinner component
     *
     * @param mLevelSpinnerAdapter
     */
    public void setmLevelSpinnerAdapter( ArrayAdapter< CharSequence > mLevelSpinnerAdapter ) {
        this.mLevelSpinnerAdapter = mLevelSpinnerAdapter;
    }

    /**
     * Get the strength standards from the DB based on the user details
     *
     * @param exerciseName the name of the exercise to retrieve
     */
    public void retrieveStrengthStandards( String exerciseName ) {
        // Get the current user
        User user = User.getInstance();

        // Set the loading messave
        mSuggestedGoalText.setText( R.string.loading );

        // Check if all user details are set correctly
        User testUser = new User();

        if ( !user.detailsAreValid() ) {
            // TODO: replace with returning null with toast message
            testUser.setName( "John Doe" );
            testUser.setSex( User.Sex.MALE );
            testUser.setWeight( 68 );
            testUser.setDob( new DateTime( 1990, 9, 1, 0, 0 ) );
        }

        // Build the path and retrieve the strength standards
        int weightClass = getWeightClass( testUser.getWeight() );
        String path = "strength_standards/" + exerciseName + "/" + testUser.getSex().toString() + "/" + weightClass;
        DatabaseReference childRef = mRootRef.child( path );
        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Store the standards in memory so that they do not have to be retrieved again
                mStrengthStandards = dataSnapshot;

                // Display the current suggested weight
                calculateSuggestedWeight();
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    /**
     * Display the current suggested weight based on the user details and selected level
     */
    private void calculateSuggestedWeight() {
        if ( mStrengthStandards == null ) {
            // TODO: Error
        } else {
            Long suggestedWeightLong = ( Long ) mStrengthStandards.child( mSelectedLevel ).getValue();
            double suggestedWeight = suggestedWeightLong.doubleValue();
            mSuggestedGoalText.setText( Double.toString( suggestedWeight ) );
        }
    }

    /**
     * Return the weight class based on the user details for retrieving strength standards
     *
     * @param weight the user weight
     * @return the weight class as listed in the DB
     */
    private int getWeightClass( float weight ) {
        return ( int ) ( Math.floor( weight / 5 ) * 5 );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_exercise_list_item, container, false );
    }

    public void onButtonPressed( Uri uri ) {
        if ( mListener != null ) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnFragmentInteractionListener ) {
            mListener = ( OnFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction( Uri uri );
    }

}
