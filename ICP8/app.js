'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [])
    .controller('View1Ctrl', function ($scope, $http) {
        $scope.venueList = new Array();
        $scope.mostRecentReview;
        $scope.getVenues = function () {
            var placeEntered = document.getElementById("txt_placeName").value;
            if (placeEntered != null && placeEntered != "") {
                document.getElementById('div_ReviewList').style.display = 'none';
                //This is the API that gives the list of venues based on the place and search query.
                var handler = $http.get("http://api.walmartlabs.com/v1/search?apiKey=vwtzj6yrpv53yrp62squshbm&ls" +
                    "&query=" + placeEntered);
                handler.success(function (data) {                    
                    if (data != null && data.items != null) {
                        for (var i = 0; i < data.items.length; i++) {
                            console.log(data.items[i]);
                            $scope.venueList[i] = {
                                "name": data.items[i].name,
                                "salesPrice": data.items[i].salePrice,
                                "description": data.items[i].longDescription,
                                "mediumImage": data.items[i].mediumImage
                            };
                        }
                    }

                })
                handler.error(function (data) {
                    alert("There was some error processing your request. Please try after some time.");
                });
            }
        }

        $scope.getReviews = function (venue) {
            $scope.showt = true;
            $scope.shows = false;
            console.log(venue);
            $scope.ReviewWithSentiment = {
                
                "Name": venue.name,
                "SalesPrice": venue.salesPrice,
                "mediumImage": venue.mediumImage,
                "description": venue.description
            };
            document.getElementById('div_ReviewList').style.display = 'block';
        };

    });
