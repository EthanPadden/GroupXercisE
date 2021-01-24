package com.nova.groupxercise;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseListItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseListItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXERCISE_NAME = "Exercise Name";

    // TODO: Rename and change types of parameters
    private String mExerciseName;

    private OnFragmentInteractionListener mListener;

    private TextView mSetGoalTitleText;
    private TextView mSuggestedGoalText;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


    public ExerciseListItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mExerciseName Parameter 1.
     * @return A new instance of fragment ExerciseListItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseListItemFragment newInstance( String mExerciseName) {
        ExerciseListItemFragment fragment = new ExerciseListItemFragment();
        Bundle args = new Bundle();
        args.putString( EXERCISE_NAME, mExerciseName );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            mExerciseName = getArguments().getString( EXERCISE_NAME );
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSetGoalTitleText =  view.findViewById( R.id.text_set_goal_title );
        mSuggestedGoalText =  view.findViewById( R.id.text_suggested_goal );
        mSetGoalTitleText.setText( "Set Goal: " + mExerciseName );
    }

    public void calculateStrengthGoal( String exerciseName) {
        User user = User.getInstance();
        mSuggestedGoalText.setText( "Loading..." );
        // Check if all user details are set correctly
        User testUser = new User();

        if ( !user.detailsAreValid() ) {
            // TODO: replace with returning null with toast message
            testUser.setName( "John Doe" );
            testUser.setSex( User.Sex.MALE );
            testUser.setWeight( 68 );
            testUser.setDob( new DateTime( 1990, 9,1,0,0 ) );
        }

        int weightClass = getWeightClass( testUser.getWeight() );
        String path = "strength_standards/" + exerciseName + "/" + testUser.getSex().toString() + "/" + weightClass;
        DatabaseReference childRef = mRootRef.child( path );
        final Goal goal = new Goal( exerciseName );
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                int[] standards = new int[5];
                Long[] standardLongValues = new Long[5];
                standardLongValues[0] = (Long) dataSnapshot.child( "Beginner" ).getValue();
                standardLongValues[1] = (Long) dataSnapshot.child( "Novice" ).getValue();
                standardLongValues[2] = (Long) dataSnapshot.child( "Intermediate" ).getValue();
                standardLongValues[3] = (Long) dataSnapshot.child( "Advanced" ).getValue();
                standardLongValues[4] = (Long) dataSnapshot.child( "Elite" ).getValue();
                for( int i = 0; i < standardLongValues.length; i++ ) {
                    standards[i] = standardLongValues[i] == null ? null : Math.toIntExact(standardLongValues[i]);
                }

                goal.setmStandards( standards );
                mSuggestedGoalText.setText( goal.toString() );
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {}
        });
    }
    private int getWeightClass(float weight) {
        return (int)(Math.floor( weight/5 )*5);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_exercise_list_item, container, false );
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed( Uri uri ) {
        if ( mListener != null ) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnFragmentInteractionListener ) {
            mListener = ( OnFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction( Uri uri );
    }

}
