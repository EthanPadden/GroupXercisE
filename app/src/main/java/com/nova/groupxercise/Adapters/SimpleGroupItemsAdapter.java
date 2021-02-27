package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.Group;

import java.util.ArrayList;

public class SimpleGroupItemsAdapter extends ArrayAdapter {
    private static final String LOG_TAG = SimpleGroupItemsAdapter.class.getSimpleName();

    public SimpleGroupItemsAdapter( Activity context, ArrayList< Group > groups ) {
        super( context, 0, groups );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    android.R.layout.simple_list_item_1, parent, false );
        }

        Group currentGroup = ( Group ) getItem( position );

        TextView groupNameText = ( TextView)listItemView.findViewById( android.R.id.text1);
        groupNameText.setText( currentGroup.getmName() );


        return listItemView;
    }
}
