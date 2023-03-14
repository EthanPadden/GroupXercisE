package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.nova.groupxercise.R;

public class TutorialActivity extends AppCompatActivity {
    private TextView mTutorialText;
    private TextView mTutorialTitleText;
    private ImageView mTutorialImage;
    private Button mNextButton;
    private int mCurrentStep = 0;
    String[] mTutorialStrings;
    String[] mTutorialTitleStrings;
    private boolean backButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mTutorialText = findViewById(R.id.text_tutorial );
        mTutorialTitleText = findViewById(R.id.text_tutorial_title );
        mTutorialImage = findViewById(R.id.img_tutorial );
        mNextButton = findViewById(R.id.next_button);
        mTutorialStrings = getResources().getStringArray(R.array.tutorial );
        mTutorialTitleStrings = getResources().getStringArray(R.array.tutorial_titles );

        int[] imageIds = {
                R.drawable.tutorial_step_0,
                R.drawable.tutorial_step_1,
                R.drawable.tutorial_step_1,
                R.drawable.tutorial_step_3,
                R.drawable.tutorial_step_4,
                R.drawable.tutorial_step_5,
                R.drawable.tutorial_step_5,
                R.drawable.tutorial_step_7,
                R.drawable.tutorial_step_8,
                R.drawable.tutorial_step_9,
        };

        mNextButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentStep++;
                if ( mCurrentStep < mTutorialStrings.length) {
                    mTutorialText.setText( mTutorialStrings[mCurrentStep]);
                    mTutorialTitleText.setText( mTutorialTitleStrings[mCurrentStep] );

                    mTutorialImage.setImageResource( imageIds[mCurrentStep] );

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

        backButtonPressed = false;

        Toast backBtnToast = Toast.makeText( this, "Press back button again to skip tutorial", Toast.LENGTH_SHORT );
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(!backButtonPressed) {
                    backBtnToast.show();
                    backButtonPressed = true;
                } else {
                    // Back button pressed twice - exit appp
                    Intent intent = new Intent(TutorialActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    backButtonPressed = false;
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }
}
