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

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/createEvent', function(req, res, next){
    let ref = db.ref("events");

    ref.once("value", function(snapshot) {
      console.log(snapshot.val());
      res.send({
         code:0,
         message:"Event has been successfully created!"
      });
    }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
        res.send({
            code:1,
            message:"Error while creating event, please try again",
            error:errorObject.code
        });
    });

    let enddate = req.body.enddate;
    let endtime = req.body.endtime;
    let description = req.body.description;
    let location = {
        city : req.body.location.city,
        country : req.body.location.country,
        latitude : req.body.location.latitude,
        longitude : req.body.location.longitude,
        name : req.body.location.name
    };
    let name = req.body.name;
    let needs_volunteers = true;
    let startdate = req.body.startdate;
    let starttime = req.body.starttime;

    ref.push({
        enddate : enddate,
        endtime : endtime,
        description : description,
        location : location,
        name : name,
        needs_volunteers : needs_volunteers,
        startdate : startdate,
        starttime : starttime,
  });
});

router.post('/allEvents', function(req, res, next){
    let events = db.ref("events");

    events.on("value", function(snapshot) {
        res.send({
            code : 0,
            message : "Event details have been successfully fetched",
            data : snapshot.val()
        });
    }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
        res.send({
           code : 1,
           message : "Error fetching Event details"
        });
    });
});

router.post('/deleteEvent', function(req, res, next){
    let eventID = req.body.eventID;
    let del_ref = db.ref("events/" + eventID);
    del_ref.remove(function(error) {
        if(error){
            res.send({
                code:1,
                message:"Error while deleting event, please check event ID",
                error:error
            });
        }
        else{
            res.send({
                code:0,
                message:"Successfully deleted event"
            })
        }
    });
});

module.exports = router;
