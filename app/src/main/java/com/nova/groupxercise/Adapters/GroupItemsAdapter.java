package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.R;

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
        groupNameText.setText( currentGroup.getmName() );

        TextView memberCountText = listItemView.findViewById( R.id.text_group_members_count );
        int numMembers = currentGroup.getmMembers().size();

        String countUnit = numMembers == 1? "member":"members";
        memberCountText.setText( numMembers + " " + countUnit );

        return listItemView;
    }
}
