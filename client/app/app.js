'use strict';

// Declare app level module which depends on views, and core components
angular.module('myApp', [
  'ngRoute',
  'myApp.view1',
  'myApp.agents',
  'myApp.version'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  $locationProvider.hashPrefix('!');

  $routeProvider.otherwise({redirectTo: '/agents'});

  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html',
    controller: 'View1Ctrl'
  });


  $routeProvider.when('/agents', {
    templateUrl: 'agents/agents.html',
    controller: 'AgentsCtrl'
  });

  $routeProvider.when('/wslogs', {
    templateUrl: 'wslogs/wslogs.html',
    controller: 'WslogsCtrl'
  });

  $routeProvider.when('/setting', {
    templateUrl: 'setting/setting.html',
    controller: 'SettingCtrl'
  });


}]);
