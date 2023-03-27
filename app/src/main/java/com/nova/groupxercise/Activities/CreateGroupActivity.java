package com.nova.groupxercise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nova.groupxercise.DBObjects.GroupDBObject;
import com.nova.groupxercise.R;
import com.nova.groupxercise.Objects.User;

public class CreateGroupActivity extends AppCompatActivity {
    private Button mCreateGroupBtn;
    private EditText mGroupNameEt;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_create_group );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize components
        mCreateGroupBtn = findViewById( R.id.btn_create_group );
        mGroupNameEt = findViewById( R.id.et_group_name );

        // Set event listeners
        mCreateGroupBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String groupName = mGroupNameEt.getText().toString();
                String username = User.getInstance().getUsername();

                if ( groupName == null || groupName.compareTo( "" ) == 0 ) {
                    Toast.makeText( CreateGroupActivity.this, "Invalid group name", Toast.LENGTH_SHORT ).show();
                } else if(username == null || username.compareTo( "" ) == 0) {
                    Toast.makeText( CreateGroupActivity.this, "Invalid username", Toast.LENGTH_SHORT ).show();
                }
                else {
                    createGroup( groupName );
                }
            }
        } );

        // Set back button behaviour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to Groups fragment
                Intent intent = new Intent( CreateGroupActivity.this, HomeScreenActivity.class );
                intent.putExtra( "FRAGMENT_ID", R.id.navigation_groups );
                startActivity( intent );
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Creates a group in the DB:
     *      Creates child in the groups subtree
     *      Creates child in the user_groups subtree
     * Navigates to the home screen
     * @param groupName the name of the group to be created
     */
    private void createGroup( String groupName ) {
        // Path to the groups subtree
        String groupsPath = "groups/";
        final DatabaseReference groupsChildRef = mRootRef.child( groupsPath );

        // Create a GroupDBObject to put into the database
        User currentUser = User.getInstance();
        GroupDBObject groupDBObject = new GroupDBObject( groupName, currentUser.getUsername() );
        DatabaseReference groupRef = groupsChildRef.push();
        groupRef.setValue( groupDBObject );

        // Create a child in the members subtree and set the value to true, indicating that this is a group admin
        groupRef.child( "members" ).child( currentUser.getUsername() ).setValue( true );

        // Updating users_groups child
        String groupId = groupRef.getKey();
        String userGroupsPath = "user_groups/";
        final DatabaseReference usersGroupsChildRef = mRootRef.child( userGroupsPath );
        String currentUserId = mAuth.getCurrentUser().getUid();
        usersGroupsChildRef.child( currentUserId ).child( groupId ).setValue( true );

        // Inform the user the group has been created
        Toast.makeText( CreateGroupActivity.this, "Group " + groupName + " created", Toast.LENGTH_SHORT ).show();

        // Navigate to the groups fragment
        Intent intent = new Intent( CreateGroupActivity.this, HomeScreenActivity.class );
        intent.putExtra( "FRAGMENT_ID", R.id.navigation_groups );
        startActivity( intent );
    }
}