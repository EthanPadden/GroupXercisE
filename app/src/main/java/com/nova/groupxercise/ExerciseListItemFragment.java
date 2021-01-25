package com.nova.groupxercise;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private TextView mSetsText;
    private TextView mRepsText;
    private Spinner mLevelSpinner;
    private String mSelectedLevel;
    private ArrayAdapter<CharSequence> mLevelSpinnerAdapter;
    private DataSnapshot mStrengthStandards;
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
        mSetsText =  view.findViewById( R.id.text_sets );
        mRepsText =  view.findViewById( R.id.text_reps );

        mLevelSpinner = view.findViewById( R.id.spinner_level );
        mLevelSpinner.setAdapter( mLevelSpinnerAdapter );
        mSelectedLevel = getResources().getString(R.string.level_beginner);

        // Apply the adapter to the spinner
        mLevelSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            public void onItemSelected( AdapterView< ? > parent, View view, int pos, long id ) {
                Object selectedOption = parent.getItemAtPosition( pos );
                String selectedOptionStr = selectedOption.toString();
                mSelectedLevel = selectedOptionStr;
                calculateSuggestedWeight();
            }

            public void onNothingSelected( AdapterView< ? > parent ) {
                mSelectedLevel = null;
            }
        } );

        mSetGoalTitleText.setText( R.string.set_goal_title + mExerciseName );
        mSetsText.setText( "3" );
        mRepsText.setText( "10" );
        retrieveStrengthStandards( mExerciseName );
        calculateSuggestedWeight();
    }


    public ArrayAdapter< CharSequence > getmLevelSpinnerAdapter() {
        return mLevelSpinnerAdapter;
    }

    public void setmLevelSpinnerAdapter( ArrayAdapter< CharSequence > mLevelSpinnerAdapter ) {
        this.mLevelSpinnerAdapter = mLevelSpinnerAdapter;
    }

    public void retrieveStrengthStandards( String exerciseName) {
        User user = User.getInstance();
        mSuggestedGoalText.setText( R.string.loading );
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
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                // TODO: ISSUE IS PULLING DATA AGAIN, SHOULD NOT HAVE TO
                mStrengthStandards = dataSnapshot;
                calculateSuggestedWeight();
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {}
        });
    }

    private void calculateSuggestedWeight() {
        if (mStrengthStandards == null) {
            // TODO: Error
        } else {
            Long suggestedWeightLong = (Long) mStrengthStandards.child( mSelectedLevel ).getValue();
            double suggestedWeight = suggestedWeightLong.doubleValue();
            mSuggestedGoalText.setText( Double.toString( suggestedWeight ) );
        }
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
