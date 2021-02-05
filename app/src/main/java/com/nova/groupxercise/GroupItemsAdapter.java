package com.nova.groupxercise;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupItemsAdapter extends ArrayAdapter {
    private static final String LOG_TAG = GroupItemsAdapter.class.getSimpleName();

    public GroupItemsAdapter( Activity context, ArrayList< Group > groups ) {
        super( context, 0, groups );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_group_list_item, parent, false );
        }

        Group currentGroup = ( Group ) getItem( position );

        TextView groupNameText = listItemView.findViewById( R.id.text_group_name );
        groupNameText.setText( currentGroup.getmGroupName() );
        TextView groupIdText = listItemView.findViewById( R.id.text_group_id );
        groupIdText.setText( currentGroup.getmGroupId() );

        return listItemView;
    }
}
