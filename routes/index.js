var express = require('express');
var router = express.Router();
var admin = require("firebase-admin");

var serviceAccount = require("../codeforgood-team13.json");
require('dotenv').config({path: __dirname + '/../.env'});

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: process.env.DB_URL
});

var db = admin.database();
var ref = db.ref("code_for_good/events_schema");
var eventsRef = ref.child("events");

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/createEvent', function(req, res, next){
  console.log(req.body);
  console.log("HERE");

  ref.once("value", function(snapshot) {
      console.log(snapshot.val());
  });

  let date = req.body.date;
  let name_of_event = req.body.name_of_event;
  let address = req.body.address;
  let volunteer_hours = req.body.volunteer_hours;
  let skill_set = req.body.skill_set.split(',');
  let no_of_volunteers_needed = req.body.no_of_volunteers_needed;
  let lat = req.body.lat;
  let long = req.body.long;
  let latLong = {
      lat:lat,
      long:long
  };
  eventsRef.push({
      date : date,
      name_of_event : name_of_event,
      address : address,
      volunteer_hours : volunteer_hours,
      skill_set : skill_set,
      no_of_volunteers_needed : no_of_volunteers_needed,
      location : latLong
  });
  res.send("Hello");
});

module.exports = router;
