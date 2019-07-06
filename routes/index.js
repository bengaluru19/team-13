var express = require('express');
var router = express.Router();
var admin = require("firebase-admin");

var serviceAccount = require("../codeforgood-team13.json");
require('dotenv').config({path: __dirname + '/.env'});

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: process.env.DB_URL
});

var db = admin.database();
var ref = db.ref("code_for_good/events_schema");
ref.once("value", function(snapshot) {
    console.log(snapshot.val());
});
var eventsRef = ref.child("events");

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/createEvent', function(req, res, next){
  var date = req.body.date;
  var name_of_event = req.body.name_of_event;
  var address = req.body.address;
  var volunteer_hours = req.body.volunteer_hours;
  var skill_set = req.body.skill_set;
  var no_of_volunteers_needed = req.body.no_of_volunteers_needed;
  eventsRef.push({

  });

});
module.exports = router;
