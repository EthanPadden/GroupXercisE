package com.nova.groupxercise.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.Group;
import com.nova.groupxercise.Objects.Member;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupMemberRecyclerAdapter extends RecyclerView.Adapter<GroupMemberRecyclerAdapter.ViewHolder> {
    private ArrayList< Member > mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Group mGroup;
    private boolean adminGroup;
    //https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    public GroupMemberRecyclerAdapter( Context context, Group group, boolean adminGroup ) {
        this.mInflater = LayoutInflater.from(context);
        this.mGroup = group;
        this.mData = group.getmMembers();
        this.adminGroup = adminGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = mInflater.inflate( R.layout.layout_group_member_card, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Member member = mData.get(position);
        holder.memberNameText.setText(member.getmUsername());
        if(member.getmUsername().compareTo( mGroup.getmCreator() ) == 0) {
            holder.memberStatusText.setVisibility( View.VISIBLE );
        }
        if(adminGroup && member.getmUsername().compareTo( mGroup.getmCreator() ) != 0) {
            holder.removeMemberBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    mGroup.removeMember( member );
                    mData.remove( member );
                }
            } );
            holder.removeMemberBtn.setVisibility( View.VISIBLE );
        }
        holder.displayProgresses( member.getmProgress() );
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView memberNameText;
        TextView memberStatusText;
        LinearLayout progressLayout;
        Button removeMemberBtn;

        ViewHolder(View itemView) {
            super(itemView);
            memberNameText = itemView.findViewById(R.id.text_member_name);
            progressLayout = itemView.findViewById( R.id.layout_progresses );
            memberStatusText = itemView.findViewById(R.id.text_member_status);
            removeMemberBtn = itemView.findViewById(R.id.btn_remove_member);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void displayProgresses( ArrayList< Goal > progresses ) {
            for(Goal progress : progresses) {
                View row = mInflater.inflate(R.layout.layout_goal_list_item , null);
                TextView exerciseNameText = row.findViewById( R.id.goal_exercise_name );
                TextView currentStatusText = row.findViewById( R.id.goal_current_status );
                TextView dividerText = row.findViewById( R.id.goal_divider );
                TextView targetText = row.findViewById( R.id.goal_target );
                TextView unitText = row.findViewById( R.id.goal_unit );

                exerciseNameText.setText( progress.getmExerciseName() );
//                currentStatusText.setText( Float.toString( progress.getmCurrentStatus()) );
                dividerText.setVisibility( View.GONE );
                targetText.setVisibility( View.GONE );
                unitText.setVisibility( View.GONE );

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)currentStatusText.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                currentStatusText.setLayoutParams(params);

                progressLayout.addView( row );
            }
        }
    }

    // convenience method for getting data at click position
    Member getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
