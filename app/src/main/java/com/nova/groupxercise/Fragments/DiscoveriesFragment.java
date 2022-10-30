package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.nova.groupxercise.R;

import java.util.ArrayList;


public class DiscoveriesFragment extends Fragment {
    private ArrayList< String > mExerciseNameList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    private ArrayAdapter< CharSequence > mLevelSpinnerAdapter;
    protected ArrayList< DBListener > mDBListeners;

    private boolean backButtonPressed;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backButtonPressed = false;

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
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

        // The callback can be enabled or disabled here or in handleOnBackPressed()
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
        mListView = view.findViewById( R.id.exercise_list );
        mLoadingText = view.findViewById( R.id.text_loading_exercise_list );
        mLevelSpinnerAdapter = ArrayAdapter.createFromResource( getActivity(),
                R.array.level_array, android.R.layout.simple_spinner_item );

        // Specify the layout to use when the list of choices appears
        mLevelSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        // Create an empty list for the exercise names
        mExerciseNameList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ArrayAdapter< String >( getActivity(), android.R.layout.simple_list_item_1, mExerciseNameList );

        DBListener exerciseListener = new DBListener() {
            public void onRetrievalFinished() {
                // Display exercise list on UI
                mLoadingText.setVisibility( View.INVISIBLE );
                mListView.setAdapter( mItemsAdapter );
                mDBListeners.remove( this );
            }
        };
        mDBListeners.add( exerciseListener );

        // Get the list of exercises from the database
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
}
