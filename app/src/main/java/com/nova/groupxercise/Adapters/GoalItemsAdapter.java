package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.R;

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

        TextView progressText = listItemView.findViewById( R.id.goal_progress );
        progressText.setText( Float.toString( currentGoal.getmProgress() ) );

        TextView dividerText = listItemView.findViewById( R.id.goal_divider );

        TextView targetText = listItemView.findViewById( R.id.goal_target );
        targetText.setText( Float.toString( currentGoal.getmTarget() ) );

        TextView unitText = listItemView.findViewById( R.id.goal_unit );

        if (currentGoal.getmProgress() >= currentGoal.getmTarget()) {
            exerciseNameText.setTextColor( Color.GREEN );
            progressText.setTextColor( Color.GREEN );
            dividerText.setTextColor( Color.GREEN );
            targetText.setTextColor( Color.GREEN );
            unitText.setTextColor( Color.GREEN );
        }

        return listItemView;
    }
}
