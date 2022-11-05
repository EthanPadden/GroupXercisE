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

        ExerciseActivity currentActivity = ( ExerciseActivity ) getItem( position );

        TextView exerciseNameText = listItemView.findViewById( R.id.activity_exercise_name );
        exerciseNameText.setText( currentActivity.getmExerciseName() );
        TextView levelText = listItemView.findViewById( R.id.activity_level );
        levelText.setText( Float.toString( currentActivity.getmLevel() ));
        TextView unitText = listItemView.findViewById( R.id.activity_unit );
        if (currentActivity.getmExerciseName().compareTo( "Walking" ) == 0) {
            unitText.setText( "steps" );
        }
        TextView timeText = listItemView.findViewById( R.id.activity_time );
        // Format for output
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
        // Printing the date
        String timeStr = dtfOut.print(currentActivity.getmTime());
        timeText.setText( timeStr);

        return listItemView;
    }
}
