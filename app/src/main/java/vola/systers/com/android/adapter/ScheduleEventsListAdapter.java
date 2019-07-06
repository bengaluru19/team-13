package vola.systers.com.android.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.provider.CalendarContract.Events;

import vola.systers.com.android.R;
import vola.systers.com.android.model.DateTime;
import vola.systers.com.android.model.Event;

public class ScheduleEventsListAdapter extends ArrayAdapter<Event> implements View.OnClickListener{

    private ArrayList<Event> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView eventName;
        TextView date;
        TextView time;
        TextView location;
        TextView status;
        ImageView calendar;
        ImageView navigate;
    }

    public ScheduleEventsListAdapter(ArrayList<Event> data, Context context) {
        super(context, R.layout.schedule_list_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Event event = (Event) object;

        switch (v.getId())
        {
            case R.id.calendar:
                // Add event to the calendar
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                String [] start_date = event.getStartDate().split("-");
                String [] start_time = event.getStartTime().split(":");
                DateTime startdateTime = new DateTime(start_date,start_time);
                cal.set(
                        startdateTime.getYear(),
                        startdateTime.getMonth(),
                        startdateTime.getDate(),
                        startdateTime.getHour(),
                        startdateTime.getMinute(),
                        startdateTime.getSecond()
                );
                intent.putExtra("beginTime", cal.getTimeInMillis());

                String [] end_date = event.getEndDate().split("-");
                String [] end_time = event.getEndTime().split(":");
                DateTime enddateTime = new DateTime(end_date,end_time);
                cal.set(
                        enddateTime.getYear(),
                        enddateTime.getMonth(),
                        enddateTime.getDate(),
                        enddateTime.getHour(),
                        enddateTime.getMinute(),
                        enddateTime.getSecond()
                );
                intent.putExtra("endTime", cal.getTimeInMillis());
                intent.putExtra("title", event.getName());
                intent.putExtra("eventLocation",event.getLocationName());
                mContext.startActivity(intent);
                break;
            case R.id.navigate:
                // Navigate to the event
                String latitude = event.getLatitude();
                String longitude = event.getLongitude();
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+longitude+","+latitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.schedule_list_item, parent, false);
            viewHolder.eventName = (TextView) convertView.findViewById(R.id.event_name);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.status = (TextView)convertView.findViewById(R.id.status);

            viewHolder.calendar = (ImageView)convertView.findViewById(R.id.calendar);
            viewHolder.navigate = (ImageView)convertView.findViewById(R.id.navigate);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.eventName.setText(event.getName());
        viewHolder.date.setText(event.getStartDate()+" to "+event.getEndDate());
        viewHolder.time.setText(event.getStartTime()+" to "+event.getEndTime());
        viewHolder.location.setText(event.getLocationName());
        viewHolder.status.setText(event.getStatus());

        viewHolder.calendar.setOnClickListener(this);
        viewHolder.navigate.setOnClickListener(this);

        viewHolder.calendar.setTag(position);
        viewHolder.navigate.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}