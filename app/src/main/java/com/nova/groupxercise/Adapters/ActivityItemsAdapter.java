package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class ActivityItemsAdapter extends ArrayAdapter {
    private static final String LOG_TAG = GroupItemsAdapter.class.getSimpleName();

    public ActivityItemsAdapter( Activity context, ArrayList< ExerciseActivity > activities ) {
        super( context, 0, activities );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_activity_list_item, parent, false );
        }

        // Get the exerciseActivity object
        ExerciseActivity currentActivity = ( ExerciseActivity ) getItem( position );

        // Set the exercise name text
        TextView exerciseNameText = listItemView.findViewById( R.id.activity_exercise_name );
        exerciseNameText.setText( currentActivity.getmExerciseName() );

        // Set the level of the activity
        TextView levelText = listItemView.findViewById( R.id.activity_level );
        // If the activity is a walking activity, we dont want the decimal point
        if(currentActivity.getmExerciseName().compareTo( "Walking" ) == 0) {
            int steps = Math.round( currentActivity.getmLevel() );
            levelText.setText( Integer.toString( steps ) );
        } else {
            levelText.setText( Float.toString( currentActivity.getmLevel() ));
        }

        // Set the unit
        TextView unitText = listItemView.findViewById( R.id.activity_unit );
        if (currentActivity.getmExerciseName().compareTo( "Walking" ) == 0) {
            unitText.setText( "steps" );
        }

        // Format and display the time of the activity
        TextView timeText = listItemView.findViewById( R.id.activity_time );
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
        String timeStr = dtfOut.print(currentActivity.getmTime());
        timeText.setText( timeStr);

        return listItemView;
    }
}
