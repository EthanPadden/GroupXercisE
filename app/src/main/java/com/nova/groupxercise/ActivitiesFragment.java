package com.nova.groupxercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ActivitiesFragment extends Fragment {
    private ArrayList< String > mActivitesList;
    private ListView mListView;
    private TextView mLoadingText;
    private ArrayAdapter< String > mItemsAdapter;
    private Button mAddActivityBtn;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_activities, container, false );
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );

        // Initialise components
        mListView = view.findViewById( R.id.activities_list );
        mLoadingText = view.findViewById( R.id.text_loading_activities );
        mAddActivityBtn = view.findViewById( R.id.btn_add_activity );

        // Set event listeners
        mAddActivityBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                // Go to log activity screen
                Intent intent = new Intent( getActivity(), LogActivityActivity.class );
                startActivity( intent );
            }
        } );
    }
}
