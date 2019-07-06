package vola.systers.com.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vola.systers.com.android.R;
import vola.systers.com.android.manager.PrefManager;
import vola.systers.com.android.utils.NetworkUtil;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private String emailId, userToken,displayName;
    private EditText fname,lname,email,affiliations,role;
    private CoordinatorLayout coordinatorLayout;
    private Button logout,saveProfile;
    private PrefManager prefManager;
    FirebaseDatabase usersDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference usersRef = usersDatabase.getReference("users");
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        prefManager = new PrefManager(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailId = user.getEmail();
            userToken = user.getUid();
            displayName = user.getDisplayName();

            fname = (EditText) findViewById(R.id.input_fname);
            lname = (EditText) findViewById(R.id.input_lname);
            email = (EditText) findViewById(R.id.input_email);
            affiliations=(EditText)findViewById(R.id.input_affiliations);
            role=(EditText)findViewById(R.id.role);

            logout = (Button) findViewById(R.id.btn_logout);
            saveProfile=(Button)findViewById(R.id.saveProfile);

            FetchUserData();

            saveProfile.setOnClickListener(this);
            logout.setOnClickListener(this);
            saveProfile.setVisibility(View.GONE);
            fname.addTextChangedListener(tw);
            lname.addTextChangedListener(tw);
            affiliations.addTextChangedListener(tw);
            role.addTextChangedListener(tw);
            email.setText(emailId);
            email.setEnabled(false);
        }
        else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("You Need to Login to view your profile!")
                    .setMessage("Do You want to Login?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(ProfileActivity.this,SignInActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(ProfileActivity.this,MenuActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .show();
        }
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            saveProfile.setVisibility(View.GONE);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(fname.getText().toString().equals(dataSnapshot.child(userToken).child("first_name").getValue().toString())
                        && lname.getText().toString().equals(dataSnapshot.child(userToken).child("last_name").getValue().toString())
                        && (dataSnapshot.child(userToken).hasChild("title") ? role.getText().toString().equals(dataSnapshot.child(userToken).child("title").getValue().toString()) :role.getText().toString().equals(""))
                        && (dataSnapshot.child(userToken).hasChild("affiliations") ? affiliations.getText().toString().equals(dataSnapshot.child(userToken).child("affiliations").getValue().toString()) : affiliations.getText().toString().equals("")))
                    {
                        saveProfile.setVisibility(View.GONE);
                    }
                    else {
                        saveProfile.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Failed to read value.", databaseError.toException());
                }
            };
            usersRef.addValueEventListener(valueEventListener);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser()
    {
        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            fAuth.signOut();
            prefManager.setFirstTimeLaunch(true);
            Intent i=new Intent(ProfileActivity.this,SignInActivity.class);
            startActivity(i);
        }
    }

    public void saveProfileButtonClick()
    {
        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to update?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveProfile.setVisibility(View.GONE);
                            saveProfileDetails();

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FetchUserData();
                            saveProfile.setVisibility(View.GONE);
                        }
                    })
                    .show();
        }
    }

    public void saveProfileDetails()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(userToken).child("affiliations").setValue(affiliations.getText().toString());
        usersRef.child(userToken).child("first_name").setValue(fname.getText().toString());
        usersRef.child(userToken).child("last_name").setValue(lname.getText().toString());
        usersRef.child(userToken).child("title").setValue(role.getText().toString());
    }


    public void FetchUserData()
    {
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lname.setText(dataSnapshot.child(userToken).child("last_name").getValue().toString());
                fname.setText(dataSnapshot.child(userToken).child("first_name").getValue().toString());
                if(dataSnapshot.child(userToken).hasChild("title"))
                {
                    role.setText(dataSnapshot.child(userToken).child("title").getValue().toString());
                }
                else {
                    role.setText("");
                }
                if(dataSnapshot.child(userToken).hasChild("affiliations"))
                {
                    affiliations.setText(dataSnapshot.child(userToken).child("affiliations").getValue().toString());
                }
                else {
                    affiliations.setText("");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        };
        usersRef.addValueEventListener(valueEventListener);

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_logout:
                logoutUser();
                break;
            case R.id.saveProfile:
                saveProfileButtonClick();
        }

    }
}
