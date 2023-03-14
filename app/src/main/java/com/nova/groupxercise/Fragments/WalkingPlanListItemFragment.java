package com.nova.groupxercise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nova.groupxercise.Objects.DBListener;
import com.nova.groupxercise.Objects.WalkingPlan;
import com.nova.groupxercise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    ImageView mImage;
    private Button mSetWalkingPlanBtn;
    private ArrayList< DBListener > mDBListeners;

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

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                DiscoveriesFragment discoveriesFragment = new DiscoveriesFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, discoveriesFragment );
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
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
        mDBListeners = new ArrayList<>();

        mSetGoalTitleText = view.findViewById( R.id.text_set_goal_title );
        mSetWalkingPlanBtn = view.findViewById( R.id.btn_set_walking_plan );

        // Set the fragment title
        mSetGoalTitleText.setText( getResources().getString( R.string.set_goal_title ) + " " + mWalkingPlanName );

        // Set the walking plan image
        // getting ImageView by its id
        mImage = view.findViewById(R.id.img_walking_plan);

        // we will get the default FirebaseDatabase instance
        FirebaseDatabase firebaseDatabase
                = FirebaseDatabase.getInstance();

        // we will get a DatabaseReference for the database
        // root node
        DatabaseReference databaseReference
                = firebaseDatabase.getReference();

        // Here "image" is the child node value we are
        // getting child node data in the getImage variable
        String path = "exercise_list/Cardio/Walking/" + mWalkingPlanName + "/image";
        DatabaseReference getImage
                = databaseReference.child(path);

        // Adding listener for a single change
        // in the data at this location.
        // this listener will triggered once
        // with the value of the data at the location
        getImage.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot dataSnapshot)
                    {
                        // getting a DataSnapshot for the
                        // location at the specified relative
                        // path and getting in the link variable
                        String link = dataSnapshot.getValue(
                                String.class);

                        // loading that data into rImage
                        // variable which is ImageView
                        Picasso.get().load(link).into( mImage );
                    }

                    // this will called when any problem
                    // occurs in getting data
                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError databaseError)
                    {
                        // we are showing that error message in
                        // toast
                        Toast
                                .makeText(getActivity(),
                                        "Error Loading Image",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        mSetWalkingPlanBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                WalkingPlan walkingPlan = new WalkingPlan( mWalkingPlanName );

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
