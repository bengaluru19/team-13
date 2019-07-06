package vola.systers.com.android.model;

import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String locationName;
    private String description;
    private String city;
    private String latitude;
    private String longitude;
    private String status;
    private String country;

    public Event(String id, String name, String startDate, String endDate, String startTime,String endTime,String locationName, String description,String city,String country,String longitude,String latitude,String status) {
        this.id=id;
        this.name=name;
        this.startDate=startDate;
        this.endDate=endDate;
        this.startTime=startTime;
        this.endTime=endTime;
        this.locationName=locationName;
        this.description=description;
        this.city=city;
        this.country=country;
        this.status=status;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocationName() { return locationName; }

    public String getDescription() { return description; }

    public String getCity() { return city; }

    public String getCountry() { return country; }

    public String getStatus() { return status; }

    public String getLatitude() { return latitude; }

    public String getLongitude() { return longitude; }

}