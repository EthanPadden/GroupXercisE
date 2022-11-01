package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nova.groupxercise.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalkingPlanListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkingPlanListItemFragment extends Fragment {

    // Parameters
    private static final String WALKING_PLAN_NAME = "Walking Plan Name";
    private String mWalkingPlanName;

    private TextView mSetGoalTitleText;

    public WalkingPlanListItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mWalkingPlanName Parameter 1.
     * @return A new instance of fragment WalkingPlanListItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalkingPlanListItemFragment newInstance( String mWalkingPlanName ) {
        WalkingPlanListItemFragment fragment = new WalkingPlanListItemFragment();
        Bundle args = new Bundle();
        args.putString( WALKING_PLAN_NAME, mWalkingPlanName );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            mWalkingPlanName = getArguments().getString( WALKING_PLAN_NAME );
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_walking_plan_list_item, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        mSetGoalTitleText = view.findViewById( R.id.text_set_goal_title );

        // Set the fragment title
        mSetGoalTitleText.setText( getResources().getString( R.string.set_goal_title ) + " " + mWalkingPlanName );

    }
}