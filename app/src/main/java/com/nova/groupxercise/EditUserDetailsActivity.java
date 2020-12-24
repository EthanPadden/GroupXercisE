package com.nova.groupxercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class EditUserDetailsActivity extends AppCompatActivity {
    private EditText mNameEt;
    private TextView mDobText;
    private EditText mWeightEt;
    private Spinner mSexSpinner;
    private Button mUpdateBtn;
    private User.Sex mSelectedSex;
    private Calendar mSelectedDob;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Set content view
        setContentView( R.layout.activity_edit_user_details );

        // Initialise components
        mNameEt = findViewById( R.id.et_name );
        mDobText = findViewById( R.id.text_dob );
        mWeightEt = findViewById( R.id.et_weight );
        mSexSpinner = findViewById( R.id.spinner_sex );
        mUpdateBtn = findViewById( R.id.btn_update );


        // Set default selected values
        mSelectedSex = User.Sex.MALE;
        mSelectedDob = Calendar.getInstance();

        // Set adapters
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter< CharSequence > adapter = ArrayAdapter.createFromResource( this,
                R.array.sex_array, android.R.layout.simple_spinner_item );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        // Apply the adapter to the spinner
        mSexSpinner.setAdapter( adapter );

        // Set event listeners
        mUpdateBtn.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // TODO: Set user details
                // Temporarily:
                String msg = "DETAILS SET:"
                        + "\nDob: " + mSelectedDob.getTime().toString()
                        + "\nSex: " + mSelectedSex.toString();
                Toast.makeText( EditUserDetailsActivity.this, msg,
                        Toast.LENGTH_SHORT ).show();
            }
        } );
        mSexSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            public void onItemSelected( AdapterView< ? > parent, View view, int pos, long id ) {
                Object selectedOption = parent.getItemAtPosition( pos );
                updateSelectedSex( selectedOption );
            }

            public void onNothingSelected( AdapterView< ? > parent ) {
                updateSelectedSex( null );
            }
        } );
        mDobText.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // Create new dialog fragment, passing the date and textview objects to update them
                DialogFragment newFragment = new DatePickerFragment( mSelectedDob, mDobText );
                newFragment.show( getSupportFragmentManager(), "datePicker" );
            }
        } );
    }

    private void updateSelectedSex( Object selectedOption ) {
        String selectedOptionStr = selectedOption.toString();

        if ( selectedOptionStr.compareTo( "Male" ) == 0 ) {
            mSelectedSex = User.Sex.MALE;
        } else if ( selectedOptionStr.compareTo( "Female" ) == 0 ) {
            mSelectedSex = User.Sex.FEMALE;
        } else {
            Toast.makeText( EditUserDetailsActivity.this, "There was an error setting your details",
                    Toast.LENGTH_SHORT ).show();
            mSelectedSex = null;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Calendar mSelectedDate;
        private TextView mDobText;

        public DatePickerFragment( Calendar mSelectedDate, TextView mDobText ) {
            this.mSelectedDate = mSelectedDate;
            this.mDobText = mDobText;
        }

        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get( Calendar.YEAR );
            int month = c.get( Calendar.MONTH );
            int day = c.get( Calendar.DAY_OF_MONTH );

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog( getActivity(), this, year, month, day );
        }

        public void onDateSet( DatePicker view, int year, int month, int day ) {
            // Set the selected date value
            mSelectedDate.set( year, month, day );

            // Update UI element
            mDobText.setText( String.format( "%d/%d/%d", day, month, year ) );
        }
    }
}
