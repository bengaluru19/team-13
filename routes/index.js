var express = require('express');
var router = express.Router();
var admin = require("firebase-admin");
var path = require("path");

var serviceAccount = require("../codeforgood-team13.json");
console.log("In Directory: ",__dirname);
require('dotenv').config({path: __dirname + '/../.env'});

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: process.env.DB_URL
});

var db = admin.database();

//To generate random cookie value
function rand_cookieVal(length, current) {
    current = current ? current : '';
    return length ? rand_cookieVal(--length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz".charAt(Math.floor(Math.random() * 60)) + current) : current;
}

var verifyAdmin = (req,res,next)=>{
    var cookie = req.cookies.cookieVal;
    if(cookie === undefined){
        res.redirect("/login");
    }else{
        db.ref('admin').orderByChild("cookieVal")
            .equalTo(cookie)
            .once("value", function(snapshot) {
                if(snapshot.val()) {
                    console.log("Valid Login");
                    next();
                }
                else{
                    res.redirect("/login");
                }
            });
    }
};

/* GET Login page. */
router.get('/login', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

/* GET Login page. */
router.get('/home', verifyAdmin, function(req, res, next) {
    res.render('error', { title: 'Express' });
});

//Admin Signup
router.post('/signup', function(req, res) {

    let newUserName = req.body.user_name;
    let newUserPass = req.body.user_pass;

    let ref = db.ref("admin");
    ref.push({
        name : newUserName,
        password : newUserPass
    });
});


//Perform authentication of admin
router.post('/login', function(req, res){
    console.log(req.body);

    var user_name = req.body.user_name;
    var user_password = req.body.user_pass;

    db.ref('admin').orderByChild("name")
        .equalTo(user_name)
        .once("value", function(snapshot) {
            if(snapshot.val()) {
                snapshot.forEach(function (child) {
                    console.log(child.val());
                    if(child.val().password === user_password) {
                        let cookieVal = rand_cookieVal(10);
                        child.ref.update({ 'cookieVal': cookieVal});
                        res.cookie('cookieVal',cookieVal, { maxAge: 90000, httpOnly: true }).send({
                            code: 0,
                            message: "Valid Login"
                        });
                    }
                    else{
                        res.cookie('cookieVal',null, { maxAge: -1, httpOnly: true }).send({
                            code: 1,
                            message: "Invalid Username/Password"
                        });
                    }
                });
            }
            else{
                res.send({
                    code: 1,
                    message: "Username does not exist in the DB"
                });
            }
        });

});

router.post('/createEvent', verifyAdmin, function(req, res, next){
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

router.post('/allEvents', verifyAdmin, function(req, res, next){
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

router.post('/deleteEvent', verifyAdmin, function(req, res, next){
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

router.post('/fetchEvent', verifyAdmin, function(req, res, next){
    let eventID = req.body.eventID;
    let events = db.ref("events/" + eventID);

    events.on("value", function(snapshot) {
        if(snapshot.val()) {
            res.send({
                code: 0,
                message: "Event details have been successfully fetched",
                data: snapshot.val()
            });
        }
        else{
            res.send({
                code : 1,
                message : "Error fetching event details, please check event ID"
            });
        }
    }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
        res.send({
            code : 1,
            message : "Fatal error fetching event details",
            error: errorObject.code
        });
    });
});

module.exports = router;
