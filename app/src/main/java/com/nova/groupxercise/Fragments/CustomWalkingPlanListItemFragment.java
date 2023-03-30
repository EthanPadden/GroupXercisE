package com.nova.groupxercise.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.WalkingPlan;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class CustomWalkingPlanListItemFragment extends Fragment {
    private static final String WALKING_PLAN_NAME = "Walking Plan Name";
    private String mWalkingPlanName;
    private Button mSetWalkingPlanBtn;
    private ArrayList< DBListener > mDBListeners;
    private EditText mStepGoalEt;

    public CustomWalkingPlanListItemFragment() {
        // Required empty public constructor
    }

    public static CustomWalkingPlanListItemFragment newInstance( String mWalkingPlanName ) {
        CustomWalkingPlanListItemFragment fragment = new CustomWalkingPlanListItemFragment();
        Bundle args = new Bundle();
        args.putString( WALKING_PLAN_NAME, mWalkingPlanName );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set fragment arguments
        if ( getArguments() != null ) {
            mWalkingPlanName = getArguments().getString( WALKING_PLAN_NAME );
        }

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onAttach( @NonNull Context context ) {
        super.onAttach( context );
        mDBListeners = new ArrayList<>();
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_custom_walking_plan_list_item, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mSetWalkingPlanBtn = view.findViewById( R.id.btn_set_walking_plan );
        mStepGoalEt = view.findViewById( R.id.et_step_goal );

        // Set on click listener for button
        mSetWalkingPlanBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                WalkingPlan walkingPlan = new WalkingPlan( mWalkingPlanName );

                try {
                    int dailyStepGoal = Integer.parseInt( mStepGoalEt.getText().toString() );

                    // Because this is a custom walking plan, the details are not stored in the database
                    // All details of the walking plan needed are local including daily step goal
                    walkingPlan.setmIncrement( 0 );
                    walkingPlan.setmLastWalkTime( 0 );
                    walkingPlan.setmProgress( 0 );
                    walkingPlan.setmLastTimeStepGoalWasReset( DateTime.now().getMillis() );
                    walkingPlan.setmStartTime( DateTime.now().getMillis() );
                    walkingPlan.setmGoal( dailyStepGoal );
                    walkingPlan.setmStartingPoint( dailyStepGoal );
                    walkingPlan.setmTodaysStepGoal( dailyStepGoal );
                    walkingPlan.setmWalkingPlanName( "Custom" );

                    DBListener walkingPlanDetailsListener = new DBListener() {
                        public void onRetrievalFinished() {
                            DBListener walkingPlanSaveListener = new DBListener() {
                                @Override
                                public void onRetrievalFinished() {
                                    String msg = mWalkingPlanName + " walking plan set!";
                                    Toast.makeText( getActivity(), msg, Toast.LENGTH_SHORT ).show();
                                    mDBListeners.remove( this );
                                }
                            };
                            mDBListeners.add( walkingPlanSaveListener );
                            walkingPlan.saveWalkingPlanAsPersonalGoal( walkingPlanSaveListener );
                            mDBListeners.remove( this );
                        }
                    };
                    mDBListeners.add( walkingPlanDetailsListener );
                    walkingPlan.retrieveWalkingPlanDetails( walkingPlanDetailsListener );
                } catch ( NumberFormatException e ) {
                    Toast.makeText( getActivity(), "Steps must be a number", Toast.LENGTH_LONG ).show();
                }
            }
        } );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Deactivate any active listeners
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }
}
