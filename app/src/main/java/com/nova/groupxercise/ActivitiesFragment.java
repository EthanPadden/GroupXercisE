package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ActivitiesFragment extends Fragment {
    private ArrayList< ExerciseActivity > mActivitesList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    private Button mAddActivityBtn;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_activities, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mListView = view.findViewById( R.id.activities_list );
        mLoadingText = view.findViewById( R.id.text_loading_activities );
        mAddActivityBtn = view.findViewById( R.id.btn_add_activity );

        // Set event listeners
        mAddActivityBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                // Go to log activity screen
                Intent intent = new Intent( getActivity(), LogActivityActivity.class );
                startActivity( intent );
            }
        } );

        retrieveActivities();
    }

    private void retrieveActivities() {
        // Path to the users goals
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "activities/" + userId;

        // Get the DB reference
        HomeScreenActivity homeScreenActivity = ( HomeScreenActivity ) getActivity();
        DatabaseReference childRef = homeScreenActivity.getmRootRef().child( path );

        // Create an empty list for the goals
        mActivitesList = new ArrayList<>();

        // Set the list as the list for the items adapter
        mItemsAdapter = new ActivityItemsAdapter( getActivity(), mActivitesList );

        childRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                for ( DataSnapshot exerciseDataSnapshot : dataSnapshot.getChildren() ) {
                    // Get the exercise name
                    String exerciseName = exerciseDataSnapshot.getKey();

                    for ( DataSnapshot activityDataSnapshot : exerciseDataSnapshot.getChildren() ) {
                        String timestampStr = activityDataSnapshot.getKey();
                        long timestamp = Long.parseLong( timestampStr );
                        DateTime time = new DateTime( timestamp );

                        Object levelObj = activityDataSnapshot.getValue();
                        float level;
                        if ( levelObj instanceof Long ) {
                            level = ( ( Long ) levelObj ).floatValue();
                        } else {
                            level = ( ( Float ) levelObj ).floatValue();
                        }


                        ExerciseActivity activity = new ExerciseActivity( exerciseName, time, level );
                        mActivitesList.add( activity );
                    }
                }

                if ( mActivitesList.isEmpty() ) {
                    mLoadingText.setText( "No activities" );
                } else {
                    // Update UI
                    mLoadingText.setVisibility( View.GONE );
                    mListView.setAdapter( mItemsAdapter );
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
            }
        } );
    }
}
