package vola.systers.com.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;
import vola.systers.com.android.utils.NetworkUtil;

import static android.content.ContentValues.TAG;

public class EventDetailViewActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,eventTime,eventDate;
    private Button register;
    public static String userToken="",eventId="";
    private CoordinatorLayout coordinatorLayout;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public ArrayList starredEvents = new ArrayList();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userToken = user.getUid();
        }
        DatabaseReference starredEventsRef = database.getReference("starred_events").child(userToken);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    starredEvents.add(ds.getKey().toString());
                }
                if(starredEvents.contains(eventId)){
                    Log.i("EVENT IDS", eventId);
                    fab.setAlpha(.5f);
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.emerald)));
                    fab.setClickable(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }

        };
        starredEventsRef.addValueEventListener(valueEventListener);

        Event event = (Event) getIntent().getSerializableExtra("selectedEvent");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(event.getName());
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String name = event.getName();
        String description = event.getDescription();
        String city = "CITY : " +event.getCity();
        String location = "LOCATION : " +event.getLocationName();
        String country = "COUNTRY : " +event.getCountry();
        String date = "DATE : " + event.getStartDate()+" to "+event.getEndDate();
        String time = "TIME : " + event.getStartTime()+" to "+event.getEndTime();

        eventName = (TextView) findViewById(R.id.event_name);
        eventId= event.getId();
        eventDescription = (TextView)findViewById(R.id.event_description);
        locationName = (TextView) findViewById(R.id.event_location_name);
        locationCity = (TextView)findViewById(R.id.event_location_city);
        locationCountry = (TextView) findViewById(R.id.event_location_state);
        eventTime=(TextView)findViewById(R.id.event_time);
        eventDate=(TextView)findViewById(R.id.event_date);
        register=(Button)findViewById(R.id.btn_register);
        register.setOnClickListener(this);
        fab.setOnClickListener(this);

        eventName.setText(name);
        eventDescription.setText(description);
        locationName.setText(location);
        locationCity.setText(city);
        locationCountry.setText(country);
        eventDate.setText(date);
        eventTime.setText(time);

        if(!event.getStatus().equals("Require Volunteers") && !event.getStatus().equals(""))
        {
            register.setAlpha(.5f);
            register.setClickable(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerEvent(){

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Event event = (Event) getIntent().getSerializableExtra("selectedEvent");
                Intent i=new Intent(EventDetailViewActivity.this,RegistrationActivity.class);
                i.putExtra("event",event);
                startActivity(i);
            }
            else
            {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("You Need to Login to register for an Event!")
                        .setMessage("Do You want to Login?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i=new Intent(EventDetailViewActivity.this,SignInActivity.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        }
    }

    private void bookMarkEvent()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {
            if (user!=null) {

                userToken = user.getUid();
                DatabaseReference eventsRef = database.getReference("starred_events");
                eventsRef.child(userToken).child(eventId).child("bookmarked").setValue("true");

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "BookMarked Successfull!", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                snackbar.show();
            }
          }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_register:
                registerEvent();
                break;
            case R.id.fab:
                bookMarkEvent();
                break;
        }
    }
}
