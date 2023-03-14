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

        mCreateGroupBtn = findViewById( R.id.btn_create_group );
        mGroupNameEt = findViewById( R.id.et_group_name );

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

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent( CreateGroupActivity.this, HomeScreenActivity.class );
                intent.putExtra( "FRAGMENT_ID", R.id.navigation_groups );
                startActivity( intent );
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    /**
     * Creates a group in the DB:
     *      Creates child in the groups subtree
     *      Creates child in the user_groups subtree
     * Navigates to the home screen
     * @param groupName the name of the group to be created
     */
    private void createGroup( String groupName ) {
        // Path to the groups child
        String groupsPath = "groups/";
        final DatabaseReference groupsChildRef = mRootRef.child( groupsPath );

        // Generate a reference to a new location and add some data
        User currentUser = User.getInstance();
        GroupDBObject groupDBObject = new GroupDBObject( groupName, currentUser.getUsername() );
        DatabaseReference groupRef = groupsChildRef.push();
        groupRef.setValue( groupDBObject );
          groupRef.child( "members" ).child( currentUser.getUsername() ).setValue( true );

        // Updating users_groups child
        String groupId = groupRef.getKey();

        // Path to the user_groups child
        String userGroupsPath = "user_groups/";
        final DatabaseReference usersGroupsChildRef = mRootRef.child( userGroupsPath );
        String currentUserId = mAuth.getCurrentUser().getUid();
        usersGroupsChildRef.child( currentUserId ).child( groupId ).setValue( true );

        Toast.makeText( CreateGroupActivity.this, "Group " + groupName + " created", Toast.LENGTH_SHORT ).show();

        Intent intent = new Intent( CreateGroupActivity.this, HomeScreenActivity.class );
        intent.putExtra( "FRAGMENT_ID", R.id.navigation_groups );
        startActivity( intent );
        // TODO: fix top bar text behaviour
    }
}
