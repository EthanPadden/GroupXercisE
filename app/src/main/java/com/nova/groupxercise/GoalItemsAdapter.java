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

    public GoalItemsAdapter( Activity context, ArrayList<Goal> goals) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, goals);
    }


    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_goal_list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Goal currentGoal = (Goal) getItem(position);

        TextView exerciseNameText = listItemView.findViewById( R.id.goal_exercise_name );
        exerciseNameText.setText( currentGoal.getmExerciseName() );
        TextView currentStatusText = listItemView.findViewById( R.id.goal_current_status );
        currentStatusText.setText( Float.toString( currentGoal.getmCurrentStatus() ) );
        TextView targetText = listItemView.findViewById( R.id.goal_target );
        targetText.setText( Float.toString( currentGoal.getmTarget()) );

        return listItemView;
    }
}
