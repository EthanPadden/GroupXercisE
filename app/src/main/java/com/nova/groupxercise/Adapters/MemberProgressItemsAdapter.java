package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.MemberProgress;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class MemberProgressItemsAdapter extends ArrayAdapter {
    private static final String LOG_TAG = GroupItemsAdapter.class.getSimpleName();

    public MemberProgressItemsAdapter( Activity context, ArrayList< MemberProgress > memberProgresses ) {
        super( context, 0, memberProgresses );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_goal_list_item, parent, false );
        }

        MemberProgress currentMemberProgress = ( MemberProgress ) getItem( position );

        TextView goalNameText = listItemView.findViewById( R.id.goal_exercise_name );
        goalNameText.setText( currentMemberProgress.getUsername() );
        TextView currentStatusText = listItemView.findViewById( R.id.goal_current_status );
        currentStatusText.setText( Float.toString( currentMemberProgress.getCurrentStatus() ) );

        return listItemView;
    }
}
