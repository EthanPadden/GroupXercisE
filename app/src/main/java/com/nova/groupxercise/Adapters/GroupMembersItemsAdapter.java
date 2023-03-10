package com.nova.groupxercise.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.Member;
import com.nova.groupxercise.R;

public class GroupMembersItemsAdapter extends ArrayAdapter {
    private Group mGroup;
    public GroupMembersItemsAdapter( Activity context, Group mGroup ) {
        super( context, 0, mGroup.getmMembers() );
        this.mGroup = mGroup;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if ( listItemView == null ) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.layout_member_list_item, parent, false );
        }

        Member currentMember = ( Member ) getItem( position );

        TextView memberNameText = listItemView.findViewById( R.id.text_member_name );
        memberNameText.setText( currentMember.getmUsername() );

        if(mGroup.getmCreator().compareTo( currentMember.getmUsername() ) == 0) {
            TextView adminStatusText = listItemView.findViewById( R.id.text_member_admin_status );
            adminStatusText.setVisibility( View.VISIBLE );
        }

        return listItemView;
    }
}
