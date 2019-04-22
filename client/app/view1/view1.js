'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  
}])

.controller('View1Ctrl', ['$scope', '$http', function($scope, $http) {
  $scope.testScope = "abc";

  $scope.getGreeting = function(){
    var url = "http://localhost:8080/greeting";
    $http.get(url).then(function(res){
      console.log(res);
      var res = res.data;
      $scope.greetingRes = res;
    });
  };
  
  $scope.runTest = function(){
    var url = "http://localhost:8080/runTest";
    $scope.runTestInProgress = true;

    $http.get(url).then(function(res){
      console.log(res);
      var res = res.data;
      $scope.runTestRes = res;
      $scope.runTestInProgress = false;
    });
  };
}]);
