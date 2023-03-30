package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.Objects.WalkingPlan;
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class DiscoveriesFragment extends Fragment {
    private ArrayList< String > mWalkingPlanNameList;
    private ArrayList< String > mExerciseNameList;
    private ListView mWalkingPlanListView;
    private ListView mExerciseListView;
    private TextView mWalkingPlanLoadingText;
    private TextView mExerciseLoadingText;
    private ArrayAdapter< String > mWalkingPlansItemsAdapter;
    private ArrayAdapter< String > mStrengthExercisesItemsAdapter;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;
    protected ArrayList< DBListener > mDBListeners;
    private Button mCustomWalkingPlanBtn;
    private boolean backButtonPressed;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set back button behaviour
        backButtonPressed = false;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!backButtonPressed) {
                    Toast.makeText( getActivity(), "Press back button again to exit", Toast.LENGTH_SHORT ).show();
                    backButtonPressed = true;
                } else {
                    // Back button pressed twice - exit appp
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    backButtonPressed = false;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>(  );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_discoveries, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mExerciseListView = view.findViewById( R.id.exercise_list );
        mWalkingPlanListView = view.findViewById( R.id.walking_plan_list );
        mExerciseLoadingText = view.findViewById( R.id.text_loading_exercise_list );
        mWalkingPlanLoadingText = view.findViewById( R.id.text_loading_walking_plan_list );
        mLevelSpinnerAdapter = ArrayAdapter.createFromResource( getActivity(),
                R.array.level_array, android.R.layout.simple_spinner_item );
        mCustomWalkingPlanBtn = view.findViewById( R.id.btn_custom_walking_plan );

        // Specify the layout to use when the list of choices appears
        mLevelSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        // Create an empty list for the walking plans and the strength exercise names
        mWalkingPlanNameList = new ArrayList<>();
        mExerciseNameList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mWalkingPlansItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mWalkingPlanNameList );
        mStrengthExercisesItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mExerciseNameList );

        // Get the list of walking plans from the database
        DBListener walkingPlanListener = new DBListener() {
            public void onRetrievalFinished() {
                // Display exercise list on UI
                mWalkingPlanLoadingText.setVisibility( View.GONE );
                mWalkingPlanListView.setAdapter( mWalkingPlansItemsAdapter );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( walkingPlanListener );
        WalkingPlan.retrieveWalkingPlanList( mWalkingPlanNameList,  walkingPlanListener);

        // Get the list of strength exercises from the database
        DBListener exerciseListener = new DBListener() {
            public void onRetrievalFinished() {
                // Display exercise list on UI
                mExerciseLoadingText.setVisibility( View.GONE );
                mExerciseListView.setAdapter( mStrengthExercisesItemsAdapter );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( exerciseListener );
        ExerciseActivity.retrieveExerciseList( mExerciseNameList,  exerciseListener);

        // Set event listeners for walking plans
        mWalkingPlanListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                // Get the walking plan name
                String walkingPlanName = mWalkingPlanListView.getItemAtPosition( i ).toString();

                // Create a fragment and set the spinner adapter on the fragment
                WalkingPlanListItemFragment walkingPlanListItemFragment = WalkingPlanListItemFragment.newInstance( walkingPlanName );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, walkingPlanListItemFragment );
                ft.commit();
            }
        } );

        // Set event listeners for strength exercises
        mExerciseListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                // Get the exercise name
                String exerciseName = mExerciseListView.getItemAtPosition( i ).toString();

                // Create a fragment and set the spinner adapter on the fragment
                ExerciseListItemFragment exerciseListItemFragment = ExerciseListItemFragment.newInstance( exerciseName );
                exerciseListItemFragment.setmLevelSpinnerAdapter( mLevelSpinnerAdapter );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, exerciseListItemFragment );
                ft.commit();
            }
        } );

        // Set on click listeners
        mCustomWalkingPlanBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                CustomWalkingPlanListItemFragment customWalkingPlanListItemFragment = CustomWalkingPlanListItemFragment.newInstance( "Custom" );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, customWalkingPlanListItemFragment );
                ft.commit();
            }
        } );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate any active listeners
        for(DBListener dbListener : mDBListeners) {
            dbListener.setActive( false );
        }
    }
}
