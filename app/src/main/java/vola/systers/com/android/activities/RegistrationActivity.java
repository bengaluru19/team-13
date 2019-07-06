package vola.systers.com.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;
import vola.systers.com.android.utils.NetworkUtil;


public class RegistrationActivity extends AppCompatActivity {

    private EditText fname, lname, email, affiliations;
    public static String userToken="",emailId="",first_name="",last_name="";
    private TextView title;
    private Button btnRegister;
    private RadioGroup attendeeTypeGroup;
    private RadioButton attendeeTypeButton;
    private CoordinatorLayout coordinatorLayout;
    private RadioButton volunteer;
    public static String eventId,eventName;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        toolbar.setTitle("Event Registration");
        setSupportActionBar(toolbar);

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

        fname = (EditText) findViewById(R.id.input_fname);
        lname = (EditText) findViewById(R.id.input_lname);
        email = (EditText) findViewById(R.id.input_email);
        affiliations = (EditText)findViewById(R.id.input_affiliations);
        title=(TextView) findViewById(R.id.title);
        btnRegister = (Button) findViewById(R.id.btn_register);
        attendeeTypeGroup= (RadioGroup)findViewById(R.id.radioAttendee);
        volunteer = (RadioButton)findViewById(R.id.radioVolunteer);

        Event event = (Event) getIntent().getSerializableExtra("event");
        eventId = event.getId();
        eventName = event.getName();
        String status=event.getStatus();
        if(!status.equals("Require Volunteers"))
        {
            volunteer.setEnabled(false);
        }
        email.setEnabled(false);
        title.setText("Register to "+ eventName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailId = user.getEmail();
            userToken = user.getUid();
            FirebaseDatabase eventsDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference eventsRef = eventsDatabase.getReference("users");
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        lname.setText(dataSnapshot.child(userToken).child("last_name").getValue().toString());
                        fname.setText(dataSnapshot.child(userToken).child("first_name").getValue().toString());
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
                eventsRef.addValueEventListener(valueEventListener);
            }
        email.setText(emailId);
    }

    public void registerToEvent(View view) {
        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {
            if (emailId != "") {
                int selectedId = attendeeTypeGroup.getCheckedRadioButtonId();
                attendeeTypeButton = (RadioButton) findViewById(selectedId);
                String selectedAttendeeType = attendeeTypeButton.getText().toString();

                DatabaseReference eventsRef = database.getReference("event_registrations");
                eventsRef.child(userToken).child(eventId).child("attendee_type").setValue(selectedAttendeeType);
                eventsRef.child(userToken).child(eventId).child("first_name").setValue(fname.getText().toString());
                eventsRef.child(userToken).child(eventId).child("last_name").setValue(lname.getText().toString());
                eventsRef.child(userToken).child(eventId).child("email").setValue(emailId);
                eventsRef.child(userToken).child(eventId).child("affiliation").setValue(affiliations.getText().toString());

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Registration Successfull!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                snackbar.show();

                Intent i = new Intent(RegistrationActivity.this,MenuActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

