package com.nova.groupxercise.Fragments;

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

import com.nova.groupxercise.Objects.ExerciseActivity;
import com.nova.groupxercise.R;

import org.joda.time.DateTime;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogWalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogWalkFragment extends Fragment {
    private Button mLogWalkBtn;
    private EditText mStepsEt;

    public LogWalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogWalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogWalkFragment newInstance() {
        LogWalkFragment fragment = new LogWalkFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                GoalsFragment goalsFragment = new GoalsFragment();
                ft.replace( R.id.frame_home_screen_fragment_placeholder, goalsFragment );
                ft.commit();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_log_walk, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        mLogWalkBtn = getActivity().findViewById( R.id.btn_log_walk );
        mStepsEt = getActivity().findViewById( R.id.et_steps );

        mLogWalkBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String stepsStr = mStepsEt.getText().toString();
                if ( stepsStr != null && stepsStr.compareTo( "" ) != 0 ) {
                    try {
                        int steps = Integer.parseInt( stepsStr );
                        ExerciseActivity exerciseActivity = new ExerciseActivity( "Walking", DateTime.now(), steps );
                        String msg = "Logging " + steps + " steps";
                        Toast.makeText( getActivity(), msg, Toast.LENGTH_SHORT ).show();
                    } catch ( NumberFormatException e ) {
                        Toast.makeText( getActivity(), "Steps must be a number", Toast.LENGTH_SHORT ).show();
                    }
                }  else {
                    Toast.makeText( getActivity(), "Enter steps", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }
}
