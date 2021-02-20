package com.nova.groupxercise.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nova.groupxercise.Objects.Goal;
import com.nova.groupxercise.Objects.MemberProgress;
import com.nova.groupxercise.R;

import java.util.ArrayList;

public class GroupMemberRecyclerAdapter extends RecyclerView.Adapter<GroupMemberRecyclerAdapter.ViewHolder> {
    private ArrayList<MemberProgress> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    //https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    public GroupMemberRecyclerAdapter( Context context, ArrayList< MemberProgress > data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = mInflater.inflate( R.layout.layout_group_member_card, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberProgress memberProgress = mData.get(position);
        holder.memberNameText.setText(memberProgress.getUsername());
        if(memberProgress.getUsername().compareTo( "vicky" ) == 0) {
            holder.memberNameText.setText(memberProgress.getUsername());
        }
        holder.displayProgresses( memberProgress.getMemberProgresses() );
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView memberNameText;
        LinearLayout progressLayout;

        ViewHolder(View itemView) {
            super(itemView);
            memberNameText = itemView.findViewById(R.id.text_member_name);
            progressLayout = itemView.findViewById( R.id.layout_progresses );
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
                currentStatusText.setText( Float.toString( progress.getmCurrentStatus()) );
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
    MemberProgress getItem(int id) {
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
