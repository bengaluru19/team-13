package vola.systers.com.android.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vola.systers.com.android.R;
import vola.systers.com.android.model.Distance;
import vola.systers.com.android.model.Event;
import vola.systers.com.android.utils.NetworkUtil;
import vola.systers.com.android.utils.RetroClient;

import static android.content.ContentValues.TAG;

public class EventDetailViewActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private TextView eventName,eventDescription,locationName,locationCity,locationCountry,eventTime,eventDate;
    private Button register,enter_event,end_event;
    public static String userToken="",eventId="";
    private CoordinatorLayout coordinatorLayout;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public ArrayList starredEvents = new ArrayList();
    private FloatingActionButton fab;

    private Retrofit.Builder builder;
    private Retrofit retrofit;

    private Location presentLocation,event_location;

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

        //location things
        //location things
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,this);

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

        event_location = new Location(LocationManager.GPS_PROVIDER);
        event_location.setLatitude(Double.parseDouble(event.getLongitude()));
        event_location.setLongitude(Double.parseDouble(event.getLatitude()));
        event_location.setAltitude(790.0000);

        eventName = (TextView) findViewById(R.id.event_name);
        eventId= event.getId();
        eventDescription = (TextView)findViewById(R.id.event_description);
        locationName = (TextView) findViewById(R.id.event_location_name);
        locationCity = (TextView)findViewById(R.id.event_location_city);
        locationCountry = (TextView) findViewById(R.id.event_location_state);
        eventTime=(TextView)findViewById(R.id.event_time);
        eventDate=(TextView)findViewById(R.id.event_date);
        register=(Button)findViewById(R.id.btn_register);
        enter_event = (Button)findViewById(R.id.btn_enter_event);
        enter_event.setOnClickListener(this);
        register.setOnClickListener(this);
        end_event = findViewById(R.id.btn_end_event);
        end_event.setOnClickListener(this);
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
        }else{
            enter_event.setAlpha(.5f);
            enter_event.setClickable(false);
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
            case R.id.btn_enter_event:
                enterEvent();
                break;
            case R.id.btn_end_event:
                end_event();
                break;
        }
    }

    private void end_event() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("active_events").child(userToken)
                .child("start_time");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date startTime = dataSnapshot.getValue(Date.class);

                long diff = Calendar.getInstance().getTime().getTime() - startTime.getTime();

                //minute calculation
                diff /= (60000);
                final long finalDiff = diff;

                dataSnapshot.getRef().getParent().child("end_time").setValue(Calendar.getInstance().getTime()).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(userToken).child("total_hours").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                long val =  dataSnapshot.getValue(Long.class);
                                                val+= finalDiff;
                                                dataSnapshot.getRef().setValue(val).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );
                            }
                        }
                );



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void enterEvent() {

        //Average altitude
        Log.i("location",presentLocation.getLatitude()+" "+presentLocation.getLongitude()+" "+presentLocation.getAltitude());
        Log.i("location",event_location.getLatitude()+" "+event_location.getLongitude()+" "+event_location.getAltitude());
        float distance = presentLocation.distanceTo(event_location);
        if(distance<5){
            Toast.makeText(this, "Safe to enter event!!"+distance, Toast.LENGTH_SHORT).show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("active_events").child(userToken)
                    .child("start_time");

            databaseReference.setValue(Calendar.getInstance().getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Toast.makeText(EventDetailViewActivity.this, "Time stamp sent!!", Toast.LENGTH_SHORT).show();
                    enter_event.setVisibility(View.GONE);
                    end_event.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EventDetailViewActivity.this, "Some error occured!!  ", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "You are not at specified location!!", Toast.LENGTH_SHORT).show();
        }
        Log.i("distance between cord",distance+" ");
    }

    @Override
    public void onLocationChanged(Location location) {
        presentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
