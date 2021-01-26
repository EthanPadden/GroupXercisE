package com.nova.groupxercise;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GoalItemsAdapter extends ArrayAdapter {
    private static final String LOG_TAG = GoalItemsAdapter.class.getSimpleName();

    public GoalItemsAdapter( Activity context, ArrayList< Goal > goals ) {
        super( context, 0, goals );
    }


    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_goal_list_item, parent, false );
        }

        Goal currentGoal = ( Goal ) getItem( position );

        TextView exerciseNameText = listItemView.findViewById( R.id.goal_exercise_name );
        exerciseNameText.setText( currentGoal.getmExerciseName() );
        TextView currentStatusText = listItemView.findViewById( R.id.goal_current_status );
        currentStatusText.setText( Float.toString( currentGoal.getmCurrentStatus() ) );
        TextView targetText = listItemView.findViewById( R.id.goal_target );
        targetText.setText( Float.toString( currentGoal.getmTarget() ) );

        return listItemView;
    }
}
