
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var url = 'mongodb://jack:jack1goza@ds027748.mlab.com:27748/asefall2018';
var insertDocument = function(db, callback) {
    console.log(db);
    db.db("asefall2018").collection('demoase').insertOne( {
        "fname" : "Sidrah",
        "lname" : "Junaid",
        "address":{
            "city":"Kansas City",
            "state":"MO"
        },
        "education" : {
            "university":"UMKC",
            "degree":"Master of Science",
            "major":"Computer Science"
        },
        "mail":"sjhv6@mail.umkc.edu"
    }, function(err, result) {
        assert.equal(err, null);
        console.log("Inserted a document into the asedemo collection.");
        callback();
    });
};
MongoClient.connect(url, (err, db) => {
    console.log(db);
    assert.equal(null, err);
    insertDocument(db, function() {
        db.close();
    });
});