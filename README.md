# team-13 (Web Backend)

<h2>Create Event</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
      "description" : "This is a value added evernt...",
      "enddate" : "13-10-2019",
      "endtime" : "1:00",
      "location" : {
        "city" : "Blr",
        "country" : "India",
        "latitude" : 12.934135,
        "longitude" : 77.699627,
        "name" : "blr"
      },
      "name" : "Karthik",
      "needs_volunteers" : true,
      "startdate" : "12-10-2019",
      "starttime" : "12:200"
}
</pre>
<p>Possible returns from the endpoint</p>
<pre>
{
   code:0,
   message:"Event has been successfully created!"
}
</pre>

<pre>
{
    code:1,
    message:"Error while creating event, please try again",
    error:errorObject.code
}
</pre>

<h2>Delete Event</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
	"eventID":"-Lj6TBDF2ZNph3APbGgh"
}
</pre>

<p>Possible returns from the endpoint</p>
<pre>
{
  code: 0,
  message: "Event details have been successfully fetched",
  data: snapshot.val()
}
</pre>

<pre>
{
  code : 1,
  message : "Error fetching event details, please check event ID"
}
</pre>

<pre>
{
  code : 1,
  message : "Fatal error fetching event details",
  error: errorObject.code
}
</pre>
