package vola.systers.com.android.model;

public class DateTime {
    private Integer year;
    private Integer month;
    private Integer date;
    private Integer hour;
    private Integer minute;
    private Integer second;

    public DateTime(String[] eventDate,String[] eventTime) {
        this.year=Integer.parseInt(eventDate[0]);
        this.month=Integer.parseInt(eventDate[1]);
        this.date=Integer.parseInt(eventDate[2]);
        this.hour=Integer.parseInt(eventTime[0]);
        this.minute=Integer.parseInt(eventTime[1]);
        this.second=Integer.parseInt(eventTime[2]);
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDate() {
        return date;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getSecond() {
        return second;
    }
}