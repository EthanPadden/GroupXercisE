package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Activities.HomeScreenActivity;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class DiscoveriesFragment extends Fragment {
    private ArrayList< String > mExerciseNameList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;
    protected ArrayList< DBListener > mDBListeners;
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
        mListView = view.findViewById( R.id.exercise_list );
        mLoadingText = view.findViewById( R.id.text_loading_exercise_list );
        mLevelSpinnerAdapter = ArrayAdapter.createFromResource( getActivity(),
                R.array.level_array, android.R.layout.simple_spinner_item );

        // Specify the layout to use when the list of choices appears
        mLevelSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        // Get the list of exercises from the database
//        retrieveExerciseList();
        // Create an empty list for the exercise names
        mExerciseNameList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mExerciseNameList );

        DBListener exerciseListener = new DBListener() {

            public void onRetrievalFinished() {
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
                mDBListeners.remove( this );

            }


        };
        mDBListeners.add( exerciseListener );
        ExerciseActivity.retrieveExerciseList( mExerciseNameList,  exerciseListener);

        // Set event listeners
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int i, long l ) {
                // Get the exercise name
                String exerciseName = mListView.getItemAtPosition( i ).toString();

                // Create a fragment and set the spinner adapter on the fragment
                ExerciseListItemFragment exerciseListItemFragment = ExerciseListItemFragment.newInstance( exerciseName );
                exerciseListItemFragment.setmLevelSpinnerAdapter( mLevelSpinnerAdapter );

                // Set the fragment to be displayed in the frame view
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, exerciseListItemFragment );
                ft.commit();
            }
        } );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        for(DBListener dbListener : mDBListeners) {
            dbListener.setActive( false );
        }
    }

    /**
     * Gets the list of exercises from the DB and makes the UI list visible when retrieved
     */
    private void retrieveExerciseList() {
        // TODO: change to differentiate between strength and cardio
        String category = "strength";
        String path = "exercise_list/" + category;
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        // Create an empty list for the exercise names
        mExerciseNameList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mExerciseNameList );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Add the exercise names to the list
                    String exerciseName = exerciseDataSnapshot.getKey();
                    mExerciseNameList.add( exerciseName );
                }
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
