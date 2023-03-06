package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nova.groupxercise.R;

public class TutorialActivity extends AppCompatActivity {
    private TextView mTutorialText;
    private TextView mTutorialTitleText;
    private Button mNextButton;
    private int mCurrentStep = 0;
    String[] mTutorialStrings;
    String[] mTutorialTitleStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mTutorialText = findViewById(R.id.text_tutorial );
        mTutorialTitleText = findViewById(R.id.text_tutorial_title );
        mNextButton = findViewById(R.id.next_button);
        mTutorialStrings = getResources().getStringArray(R.array.tutorial );
        mTutorialTitleStrings = getResources().getStringArray(R.array.tutorial_titles );

        mNextButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentStep++;
                if ( mCurrentStep < mTutorialStrings.length) {
                    mTutorialText.setText( mTutorialStrings[mCurrentStep]);
                    mTutorialTitleText.setText( mTutorialTitleStrings[mCurrentStep] );
                    // Need to program in images too
                    if ( mCurrentStep == mTutorialStrings.length - 1) {
                        mNextButton.setText("Finish");
                    }
                } else {
                    Intent intent = new Intent( TutorialActivity.this, MainActivity.class );
                    startActivity( intent );
                }
            }
        });
    }
}
