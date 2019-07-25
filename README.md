# team-13 (Web Backend)[API Documentation]

<h2>(POST) Create Event</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
      "description" : "Undertaking the painting of the outer school walls",
      "enddate" : "13-10-2019",
      "endtime" : "1:00",
      "location" : {
        "city" : "Bangalore",
        "country" : "India",
        "latitude" : 12.934135,
        "longitude" : 77.699627,
        "name" : "Mathru Jyothi School"
      },
      "name" : "Painting a School",
      "needs_volunteers" : true,
      "startdate" : "12-10-2019",
      "starttime" : "12:200"
}
</pre>
<p>Possible returns from the endpoint</p>
<ul>
	<li>Event Successfully Created (code = 0)
	<pre>
	{
	   code:0,
	   message:"Event has been successfully created!"
	}
	</pre>
	</li>
	<li>Error while creating event, (code = 1, error = Error code returned by Firebase)
	<pre>
	{
            code:1,
            message:"Error while creating event, please try again",
            error:errorObject.code
        }
	</pre>
	</li>
</ul>

<h2>(POST) Delete Event</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
	"eventID":"-Lj6TBDF2ZNph3APbGgh"
}
</pre>

<p>Possible returns from the endpoint</p>
<ul>
	<li>Event Successfully Deleted (code = 0)
	<pre>
	{
		code:0,
		message:"Successfully deleted event"
	}
	</pre>
	</li>
	<li>Error while creating event, (code = 1, error = Error code returned by Firebase)
	<pre>
	{
            code:1,
            message:"Error while creating event, please try again",
            error:errorObject.code
        }
	</pre>
	</li>
</ul>

<h2>(POST) Fetch Event</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
	"eventID":"-Lj6TBDF2ZNph3APbGgh"
}
</pre>

<p>Possible returns from the endpoint</p>
<ul>
	<li>Event Successfully Fetched (code = 0, data = Fetched Event Data from Firebase)
	<pre>
	{
                code: 0,
                message: "Event details have been successfully fetched",
                data: snapshot.val()
        }
	</pre>
	</li>
	<li>Invalid event ID (code = 1)
	<pre>
	{
                code : 1,
                message : "Error fetching event details, please check event ID"
        }
	</pre>
	</li>
	<li>Error while fetching event, (code = 1, error = Error code returned by Firebase)
	<pre>
	{
            code:1,
            message:"Error while creating event, please try again",
            error:errorObject.code
        }
	</pre>
	</li>
</ul>

<h2>(POST) Fetch All Events</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
Empty Post Request (Only Valid Cookie needs to be added for auth)
</pre>

<p>Possible returns from the endpoint</p>
<ul>
	<li>Event Successfully Fetched (code = 0, data = Fetched Event Data from Firebase)
	<pre>
	{
            code : 0,
            message : "Event details have been successfully fetched",
            data : snapshot.val()
        }
	</pre>
	</li>
	<li>Error while fetching event, (code = 1, error = Error code returned by Firebase)
	<pre>
	{
           code : 1,
           message : "Error fetching Event details", 
           error: errorObject.code
        }
	</pre>
	</li>
</ul>

<h2>(POST) Admin Login Authentication</h2>
<p>The following is a sample call to the endpoint</p>
<pre>
{
	"user_name":"test",
	"user_pass":"password"
}
</pre>

<p>Possible returns from the endpoint</p>
<ul>
	<li>Event Successfully Fetched (code = 0, cookie value set)
	<pre>
	{
	    code: 0,
	    message: "Valid Login"
        }
	A cookie is sent along with this return, with the following parameters:
	Name of Cookie: 'cookieVal'
	Value of Cookie: "dywt8q7w980u"
	maxAge: 90000
	httpOnly: true
	</pre>
	</li>
	<li>Invalid Username/Password, (code = 1, cookie invalidated)
	<pre>
	{
	    code: 1,
	    message: "Invalid Username/Password"
        }
	And Cookie is Invalidated.
	</pre>
	</li>
	<li>Username does not exist in the DB, (code = 1)
	<pre>
	{
	    code: 1,
	    message: "Username does not exist in the DB"
	}
	</pre>
	</li>
</ul>



##### The code ("Code") in this repository was created solely by the student teams during a coding competition hosted by JPMorgan Chase Bank, N.A. ("JPMC").						JPMC did not create or contribute to the development of the Code.  This Code is provided AS IS and JPMC makes no warranty of any kind, express or implied, as to the Code,						including but not limited to, merchantability, satisfactory quality, non-infringement, title or fitness for a particular purpose or use.