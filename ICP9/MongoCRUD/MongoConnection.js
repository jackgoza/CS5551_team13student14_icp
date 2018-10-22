var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var url = 'mongodb://jack:jack1goza@ds027748.mlab.com:27748/asefall2018';
MongoClient.connect(url, function(err, db) {
  assert.equal(null, err);
  console.log("Connected correctly to server.");
  db.close();
});

/*
url :   mongodb://jack:jack1goza@ds027748.mlab.com:27748/asefall2018
 */