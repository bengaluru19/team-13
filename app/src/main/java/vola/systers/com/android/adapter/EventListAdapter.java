package vola.systers.com.android.adapter;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vola.systers.com.android.R;
import vola.systers.com.android.model.Event;

public class EventListAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView eventName;
        TextView date;
        TextView time;
        TextView location;
        TextView status;
    }

    public EventListAdapter(ArrayList<Event> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext=context;
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
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.eventName = (TextView) convertView.findViewById(R.id.event_name);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.status=(TextView)convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.eventName.setText(event.getName());
        viewHolder.date.setText(event.getStartDate()+" to "+event.getEndDate());
        viewHolder.time.setText(event.getStartTime()+" to "+event.getEndTime());
        viewHolder.location.setText(event.getLocationName());
        viewHolder.status.setText(event.getStatus());
        if(event.getStatus().equals("Require Volunteers"))
        {
            // Events that Require Volunteers
            viewHolder.status.setTextColor(Color.parseColor("#F89728"));
            viewHolder.status.setVisibility(View.VISIBLE);
        }
        else if(event.getStatus()!="")
        {
            // Events that are already registered by the user.
            viewHolder.status.setTextColor(Color.parseColor("#00833E"));
            viewHolder.status.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}