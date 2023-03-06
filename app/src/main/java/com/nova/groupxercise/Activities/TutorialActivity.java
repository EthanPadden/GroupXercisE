package com.nova.groupxercise.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nova.groupxercise.R;

public class TutorialActivity extends AppCompatActivity {
    private TextView mTutorialText;
    private Button mNextButton;
    private int mCurrentStep = 0;
    String[] mDiscoveriesStepStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mTutorialText = findViewById(R.id.tutorial_text);
        mNextButton = findViewById(R.id.next_button);
        mDiscoveriesStepStrings = getResources().getStringArray(R.array.tutorial );

        mNextButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentStep++;
                if ( mCurrentStep < mDiscoveriesStepStrings.length) {
                    mTutorialText.setText(mDiscoveriesStepStrings[mCurrentStep]);
                    // Need to program in images too
                    if ( mCurrentStep == mDiscoveriesStepStrings.length - 1) {
                        mNextButton.setText("Finish");
                    }
                } else {
                    finish();
                }
            }
        });
    }
}
