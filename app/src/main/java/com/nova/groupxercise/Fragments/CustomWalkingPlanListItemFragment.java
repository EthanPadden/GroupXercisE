package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.WalkingPlan;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalkingPlanListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomWalkingPlanListItemFragment extends Fragment {

    // Parameters
    private static final String WALKING_PLAN_NAME = "Walking Plan Name";
    private String mWalkingPlanName;

    private TextView mSetGoalTitleText;
    ImageView mImage;
    private Button mSetWalkingPlanBtn;
    private ArrayList< DBListener > mDBListeners;
    private EditText mStepGoalEt;

    public CustomWalkingPlanListItemFragment() {
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
        if ( getArguments() != null ) {
            mWalkingPlanName = getArguments().getString( WALKING_PLAN_NAME );
        }
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
        mDBListeners = new ArrayList<>();

        mSetGoalTitleText = view.findViewById( R.id.text_set_goal_title );
        mSetWalkingPlanBtn = view.findViewById( R.id.btn_set_walking_plan );
        mStepGoalEt = view.findViewById( R.id.et_step_goal );

        // we will get the default FirebaseDatabase instance
        FirebaseDatabase firebaseDatabase
                = FirebaseDatabase.getInstance();

        // we will get a DatabaseReference for the database
        // root node
        DatabaseReference databaseReference
                = firebaseDatabase.getReference();

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
        for ( DBListener dbListener : mDBListeners ) {
            dbListener.setActive( false );
        }
    }
}
