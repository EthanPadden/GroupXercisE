package com.nova.groupxercise.Fragments;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Adapters.SimpleGroupItemsAdapter;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.User;
import com.nova.groupxercise.R;

import java.util.ArrayList;

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
    private TextView mNoDetailsSetErrorText;
    private Spinner mLevelSpinner;
    private String mSelectedLevel;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;
    private Button mSetGoalBtn;
    private RadioButton mGoalOptionsAutomaticRatioBtn;
    private RadioButton mGoalOptionsManualRatioBtn;
    private EditText mManualGoalET;
    private ArrayAdapter mItemsAdapter;
    private ArrayList <Group>mAdminGroups;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayList< String > mGroupIds;
    private ArrayList< String > mAdminGroupIds;
    private LinearLayout mUserDetailsOptionsLayout;

    // For storing retrieved strength standards based on user details
    private DataSnapshot mStrengthStandards;
    private ArrayList< DBListener > mDBListeners;

    // Goal option selection
    public enum GoalOption {
        AUTOMATIC, MANUAL
    }

    private GoalOption mSelectedGoalOption;

    public ExerciseListItemFragment() {
        // Required empty public constructor
    }

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

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to Discoveries fragment
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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
        mNoDetailsSetErrorText = view.findViewById( R.id.text_no_details_set_error );
        mUserDetailsOptionsLayout = view.findViewById( R.id.layout_user_details_options );

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
                    User currentUser = User.getInstance();
                    if ( currentUser.isUserDetailsAreSet() ) {
                        // Automatic goal calculation option: use suggested goal
                        float target = Float.parseFloat( mSuggestedGoalText.getText().toString() );
                        savePersonalGoal( new Goal( mExerciseName, target ) );
                    } else {
                        Toast.makeText( getActivity(),
                                "Setting a goal based on your details is unavailable. Please set these details in the Profile section.",
                                Toast.LENGTH_SHORT ).show();
                    }
                } else if ( mSelectedGoalOption == GoalOption.MANUAL ) {
                    // Manual goal calculation option: use user-set goal
                    String targetStr = ( mManualGoalET.getText().toString() );

                    if ( targetStr != null && targetStr.compareTo( "" ) != 0 ) {
                        float target = Float.parseFloat( targetStr );
                        savePersonalGoal( new Goal( mExerciseName, target ) );
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
        mSetGoalTitleText.setText( getResources().getString( R.string.set_goal_title ) + " " + mExerciseName );

        // Get the current user
        User currentUser = User.getInstance();

        // Set the loading message
        mSuggestedGoalText.setText( R.string.loading );

        // Check if all user details are set correctly
        // If so, get the strength standards from the DB based on the user details
        DBListener strengthStandardsListener = new DBListener() {
            public void onRetrievalFinished( Object retrievedData ) {
                mStrengthStandards = ( DataSnapshot ) retrievedData;
                calculateSuggestedWeight();
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( strengthStandardsListener );
        if ( currentUser.isUserDetailsAreSet() && currentUser.detailsAreValid() ) {
            Goal.retrieveStrengthStandards( mExerciseName, strengthStandardsListener );
        } else {
            mUserDetailsOptionsLayout.setVisibility( View.GONE );
            mNoDetailsSetErrorText.setVisibility( View.VISIBLE );
        }

        mGroupIds = new ArrayList<>();
        mAdminGroupIds = new ArrayList<>();
        DBListener groupIdsListener = new DBListener() {

            public void onRetrievalFinished() {
                mAdminGroups = new ArrayList<>();
                // Set the list as the list for the items adapter
                mItemsAdapter = new SimpleGroupItemsAdapter( getActivity(), mAdminGroups );

                DBListener groupNamesListener = new DBListener() {

                    public void onRetrievalFinished() {
                        setupGroupsList();
                        mDBListeners.remove( this );
                    }

                };
                mDBListeners.add( groupNamesListener );
                Group.retrieveGroupNames( mAdminGroupIds, mAdminGroups, groupNamesListener );
                mDBListeners.remove( this );
            }


        };
        mDBListeners.add( groupIdsListener );
        Group.retrieveGroupIds( mAdminGroupIds, mGroupIds, groupIdsListener );
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate any active listeners
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }

    /**
     * Builds the listview to display group names
     */
    private void setupGroupsList() {
        mLoadingText.setVisibility( View.GONE );
        mListView.setAdapter( mItemsAdapter );

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                Group selectedGroup = ( Group ) mListView.getItemAtPosition( i );
                final String groupId = selectedGroup.getmGroupId();

                if ( mSelectedGoalOption == GoalOption.AUTOMATIC ) {
                    Toast.makeText( getActivity(), "Cannot group goal based on personal details", Toast.LENGTH_SHORT ).show();
                } else if ( mSelectedGoalOption == GoalOption.MANUAL ) {
                    // Manual goal calculation option: use user-set goal
                    String targetStr = ( mManualGoalET.getText().toString() );

                    if ( targetStr != null && targetStr.compareTo( "" ) != 0 ) {
                        float target = Float.parseFloat( targetStr );
                        final Goal goal = new Goal( mExerciseName, target );
                        DBListener goalSaveListener = new DBListener() {
                            public void onRetrievalFinished() {
                                Toast.makeText( getActivity(), "Group goal set", Toast.LENGTH_SHORT ).show();
                                mDBListeners.remove( this );
                            }
                        };
                        mDBListeners.add( goalSaveListener );
                        Group group = new Group( groupId );
                        group.saveGoal( goal, goalSaveListener );
                    } else {
                        Toast.makeText( getActivity(), R.string.error_no_target_entered, Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( getActivity(), R.string.error_generic, Toast.LENGTH_SHORT ).show();
                }
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
    private void savePersonalGoal( final Goal goal ) {
        if ( goal == null ) {
            Toast.makeText( getActivity(), R.string.error_goal_setting, Toast.LENGTH_SHORT ).show();
        } else {
            // Path to the users goals
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String path = "personal_goals/" + userId + "/Strength/" + goal.getmExerciseName();

            // Get the DB reference
            HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
            final DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

            // Check if we have a set of goals for that particular user
            childRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot ) {
                  if ( getActivity() != null ) {
                        // If we have not moved into another fragment
                        if ( dataSnapshot.exists() ) {
                            // If so, the operation is an update
                            Toast.makeText( getActivity(), R.string.info_updating_goal, Toast.LENGTH_SHORT ).show();
                        } else {
                            // If not, the operation is a create
                            Toast.makeText( getActivity(), R.string.info_creating_goal, Toast.LENGTH_SHORT ).show();
                        }
                    }

                    // If no child exists, this will create a new one
                    // If one does, this will update it
                    childRef.setValue( goal.getmTarget() );
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
     * Display the current suggested weight based on the user details and selected level
     */
    private void calculateSuggestedWeight() {
        if ( mStrengthStandards == null ) {
            Toast.makeText( getActivity(), "Error in retrieving strength standards for calculating suggested intensity", Toast.LENGTH_SHORT);
        } else {
            Long suggestedWeightLong = ( Long ) mStrengthStandards.child( mSelectedLevel ).getValue();
            double suggestedWeight = suggestedWeightLong.doubleValue();
            mSuggestedGoalText.setText( Double.toString( suggestedWeight ) );
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_exercise_list_item, container, false );
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>();
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
        void onFragmentInteraction( Uri uri );
    }
}
