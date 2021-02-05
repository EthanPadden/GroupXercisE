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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


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
    private ArrayAdapter mItemsAdapter;
    private ArrayList mAdminGroupNames;
    private ListView mListView;
    private TextView mLoadingText;

    // For storing retrieved strength standards based on user details
    private DataSnapshot mStrengthStandards;

    // DB root reference
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    // Goal option selection
    public enum GoalOption {
        AUTOMATIC, MANUAL
    }

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
        mListView = view.findViewById( R.id.goal_groups_list );
        mLoadingText = view.findViewById( R.id.text_loading_group_list );


        // Set spinner adapter
        mLevelSpinner.setAdapter( mLevelSpinnerAdapter );

        // Set default options
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
                if ( mSelectedGoalOption == GoalOption.AUTOMATIC ) {
                    // Automatic goal calculation option: use suggested goal
                    float target = Float.parseFloat( mSuggestedGoalText.getText().toString() );
                    saveGoal( new Goal( mExerciseName, 0, target ) );
                } else if ( mSelectedGoalOption == GoalOption.MANUAL ) {
                    // Manual goal calculation option: use user-set goal
                    String targetStr = ( mManualGoalET.getText().toString() );

                    if ( targetStr != null && targetStr.compareTo( "" ) != 0 ) {
                        float target = Float.parseFloat( targetStr );
                        saveGoal( new Goal( mExerciseName, 0, target ) );
                    } else {
                        Toast.makeText( getActivity(), R.string.error_no_target_entered, Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( getActivity(), R.string.error_generic, Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        mGoalOptionsAutomaticRatioBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Select automatic option
                mSelectedGoalOption = GoalOption.AUTOMATIC;

                // Update UI
                mGoalOptionsAutomaticRatioBtn.setChecked( true );
                mGoalOptionsManualRatioBtn.setChecked( false );
            }
        } );
        mGoalOptionsManualRatioBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Select manual option
                mSelectedGoalOption = GoalOption.MANUAL;

                // Update UI
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
        retrieveGroupIds();
    }

    private void retrieveGroupIds() {
        // Create empty list for the group IDs that the user is an admin of
        final ArrayList<String> groupIds = new ArrayList<>(  );

        // Get the current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Path to the reference
        final String usersGroupPath = "user_groups/" + currentUserId;
        DatabaseReference childRef = mRootRef.child( usersGroupPath );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot usersGroupsDataSnapshot : dataSnapshot.getChildren() ) {
                    Boolean isAdmin = (Boolean) usersGroupsDataSnapshot.getValue();
                    if(isAdmin.booleanValue() == true) {
                        // If the current user is an admin of the group
                        groupIds.add(  usersGroupsDataSnapshot.getKey());
                    }
                }

                retrieveGroupNames( groupIds );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }

    private void retrieveGroupNames(ArrayList<String> groupIds) {
        // Create an empty list for the group names
        mAdminGroupNames = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mAdminGroupNames );

        // The UI is updated when all of the group names have been added
        // Necessary because of the async call within the for loop
        final int expectedSize = groupIds.size();

        for( final String groupId : groupIds) {
            String groupPath = "groups/" + groupId;
            DatabaseReference groupRef = mRootRef.child( groupPath );

            groupRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    String groupName = dataSnapshot.child( "name" ).getValue().toString();
                    mAdminGroupNames.add( groupName );
                    if(mAdminGroupNames.size() == expectedSize) {
                        // When we have all the group names retrieved
                        setupGroupsList();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
        if(mAdminGroupNames.size() == 0) {
            mLoadingText.setText( "You are the admin of no groups" );
        }
    }
    private void setupGroupsList() {
        mLoadingText.setVisibility( View.GONE );
        mListView.setAdapter( mItemsAdapter );

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                String selectedGroupName = mListView.getItemAtPosition( i ).toString();
                Toast.makeText( getActivity(), selectedGroupName,Toast.LENGTH_SHORT ).show();
            }
        } );

        mListView.setVisibility( View.VISIBLE );
    }

    /**
     * Checks if there is a JSON subtree for the user's goals in the DB
     * If so:
     * If a goal for that exercise already exists, update it
     * If not, create one for that exercise
     *
     * @param goal the goal object to save to the DB
     */
    private void saveGoal( final Goal goal ) {
        if ( goal == null ) {
            Toast.makeText( getActivity(), R.string.error_goal_setting, Toast.LENGTH_SHORT ).show();
        } else {
            // Path to the users goals
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String path = "user_goals/" + userId;

            // Get the DB reference
            HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
            final DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

            // Check if we have a set of goals for that particular user
            childRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                    if ( dataSnapshot.exists() ) {
                        // This means there is a set of goals associated with the user
                        // (this could be an empty list)

                        // Get the DB object for the goal
                        GoalDBObject goalDBObject = goal.getmGoalDBObject();

                        // Check if a goal already exists for the exercise
                        DataSnapshot exerciseDataSnapshot = dataSnapshot.child( goal.getmExerciseName() );
                        if ( exerciseDataSnapshot.exists() ) {
                            // If so, the operation is an update
                            Toast.makeText( getActivity(), R.string.info_updating_goal, Toast.LENGTH_SHORT ).show();
                        } else {
                            // If not, the operation is a create
                            Toast.makeText( getActivity(), R.string.info_creating_goal, Toast.LENGTH_SHORT ).show();
                        }

                        // If no child exists, this will create a new one
                        // If one does, this will update it
                        childRef.child( goal.getmExerciseName() ).setValue( goalDBObject );
                    } else {
                        // This is an error
                        Toast.makeText( getActivity(), R.string.error_no_goalset_found, Toast.LENGTH_SHORT ).show();
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                }
            } );
        }
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
        User currentUser = User.getInstance();

        // Set the loading message
        mSuggestedGoalText.setText( R.string.loading );

        // Check if all user details are set correctly
        if ( currentUser.isUserDetailsAreSet() && currentUser.detailsAreValid() ) {
            // Build the path and retrieve the strength standards
            int weightClass = getWeightClass( currentUser.getWeight() );
            String path = "strength_standards/" + exerciseName + "/" + currentUser.getSex().toString() + "/" + weightClass;

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
        } else {
            Toast.makeText( getActivity(), R.string.error_invalid_user_details, Toast.LENGTH_SHORT ).show();
            mSuggestedGoalText.setText( R.string.error_invalid_user_details );
        }
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
