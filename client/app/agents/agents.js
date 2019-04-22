'use strict';

angular.module('myApp.agents', ['ngRoute'])
.controller('AgentsCtrl', ['$scope', '$http', function($scope, $http) {

    /* Websockets */

    var ws;
    $scope.wsStateMapping = ['CONNECTING', 'OPEN', 'CLOSING', 'CLOSED'];

    $scope.setupWebsocket = function(){
        var socketUrl = 'ws://localhost:8080/sockets';
        ws = new WebSocket(socketUrl);

        ws.onmessage = function(evt){
            var msg = JSON.parse(evt.data);
            if(msg.key == "existing-agent"){
                var deleteIdx;
                var updatedAgents = JSON.parse(msg.msg);
                for(var i=0; i<updatedAgents.length;i++){
                    var updatedAgent = updatedAgents[i];
                    for(var j=0; j<$scope.agents.length; j++){
                        if($scope.agents[j].idx == updatedAgent.idx){
                            $scope.agents[j] = updatedAgent;
                            if(updatedAgent.state == "TURNED_OFF"){
                                deleteIdx = j;
                            }
                            break;
                        }
                    }
                    if(j == $scope.agents.length){
                        if(updatedAgent.state != "TURNED_OFF"){
                            $scope.agents.push(updatedAgent);
                        }
                    }
                }

                if(deleteIdx != null){
                    $scope.agents.splice(deleteIdx);
                }
            } else if(msg.key == "update-all-agents"){
                var allAgents = JSON.parse(msg.msg);
                $scope.agents = allAgents;
            }
        };

        ws.onclose = function(){
            var reconnectIntervalMs = 5000;
            setTimeout(function(){
                console.error("Websocket closing. Reconnect in " + reconnectIntervalMs + " ms.");
                $scope.initWebsocket();
            }, reconnectIntervalMs);
        }
    }

    $scope.updateWebocketStatus = function(){
        $scope.websocketStatus = $scope.wsStateMapping[ws.readyState];
        $scope.$apply();
    }

    $scope.initWebsocket = function(){
        $scope.setupWebsocket();
        $scope.updateWebocketStatus;

        // Check Websocket status every half second.
        setInterval(function(){
            $scope.updateWebocketStatus();
            if($scope.webSocketStatus == 3){        // CLOSED
                $scope.reconnectWs();
            }
        }, 500);
    }

    /* Agent Actions */
    $scope.startAgent = function(idx){
        let url = "http://localhost:8080/agent/start";
        let data = {
            idx: idx
        };
        $http({
            method: "POST",
            url: url,
            params: data
        }).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )
    }

    $scope.stopAgent = function(idx){
        console.log("idx: " + idx);
        let url = "http://localhost:8080/agent/stop";
        let data = {
            idx: idx
        };
        $http({
            method: "POST",
            url: url,
            params: data
        }).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )    
    }

    $scope.shutdown = function(){
        let url = "http://localhost:8080/shutdown"
        $http({
            method: "POST",
            url: url
        }).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )
    }

    /* Selecting Agents */
    $scope.openAgentList = function(){
        $("#myModal").modal("show");                                        // TODO: show processing bar.
    }

    $scope.toggleSelect = function(){
        $scope.agentsSelected = ($scope.agentsSelected==true)? false:true;
		angular.forEach($scope.agents, function(item){
            item.selected = $scope.agentsSelected;
		});
    }
    
    $scope.selectRangeAgents = function(){
        $scope.agentsSelected = true;
        $scope.toggleSelect();
        for(var x=$scope.range.start; x<=$scope.range.end;x++){
            $scope.savedAgents[x].selected = true;
        }
    }

    $scope.getMultipleAgentState = function(){
        let url = "http://localhost:8080/agents/info";
        let data = {
            idx: idx
        };
        $http({
            method: "GET",
            url: url,
            params: data
        }).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )
    }

    
    $scope.getState = function(idx){
        let url = "http://localhost:8080/agent/"+idx+"/info";
        let data = {
            idx: idx
        };
        $http({
            method: "GET",
            url: url,
            params: data
        }).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )
    }
    

    $scope.stopMultipleAgent = function(idxs){
        var agentIdxs = JSON.stringify(idxs.split(","));

        if(agentIdxs.length>0){
            let url = "http://localhost:8080/agents/stop";
            let data = {
                idxs: agentIdxs
            };
            $http({
                method: "POST",
                url: url,
                params: data
            }).then(
                function(result){
                    var result = result.data;
                    console.log(result);
                },
                function(err){
                    console.error(err);
                }
            )
        }
        else{
            console.error("No Agent Selected.");
        }
    }

    $scope.startMultipleAgent = function(idxs){
        var agentIdxs = JSON.stringify(idxs.split(","));

        if(agentIdxs.length>0){
            let url = "http://localhost:8080/agents/start";
            let data = {
                idxs: agentIdxs
            };
            $http({
                method: "POST",
                url: url,
                params: data
            }).then(
                function(result){
                    var result = result.data;
                    console.log(result);
                },
                function(err){
                    console.error(err);
                }
            )
        }
        else{
            console.error("No Agent Selected.");
        }

    }

    $scope.selectAgentsModal = function(){
        var checkedAgents = [];

        angular.forEach($scope.savedAgents, function(item){
            if(item.selected==true){
                checkedAgents.push(item.idx);
            }
        });

        $scope.startAgentIndexs = checkedAgents.join();
        console.log($scope.startAgentIndexs);
    }

    /* Init */

    $scope.initAgents = function(){
        $http.get("http://localhost:8080/agents/active/info").then(        // TODO: set localhost:8080 as BASE_URL
            function(result){
                var result = result.data.agents;
                console.log(result)
                $scope.statesCounter.TOTAL = result.length;
                //count the states
                for(let x=0; x<result.length;x++)
                {
                    var item = result[x];
                    switch (item.state) {
                    case 'READY':
                    $scope.statesCounter.READY = $scope.statesCounter.READY+1;
                    break;
                    case 'NOT_READY':
                    $scope.statesCounter.NOT_READY = $scope.statesCounter.NOT_READY+1;
                    break;
                    case 'LOGOUT':
                    $scope.statesCounter.LOGOUT  = $scope.statesCounter.LOGOUT+1;
                    break;
                    case 'TURNED_OFF':
                    $scope.statesCounter.TURNED_OFF = $scope.statesCounter.STARTED+1;
                    default:
                    }
                }
                $scope.agents = result;
                $scope.agentsSelected = false;
            },
            function(error){
                console.error("ERROR | " + error);
            }
        );
    }

    $scope.init = function(){
        console.log("init");

        $scope.agentIndexs = "";
        $scope.statesCounter = {
            "TURNED_ON": 0,
            "STARTED" : 0,
            "CONNECTED" : 0,
            "NOT_READY" : 0, 
            "READY" : 0,
            "LOGOUT" : 0,
            "TURNED_OFF" : 0,
            "TOTAL" : 0
        }

        $http.get("http://localhost:8080/agents/info").then(        // TODO: set localhost:8080 as BASE_URL
            function(result){
                var result = result.data.agents;
                $scope.savedAgents = result;
            },
            function(error){
                console.error("ERROR | " + error);
            }
        );


        $scope.initWebsocket();
        $scope.initAgents();
    }
    $scope.init();

    /* Testing */
    
    $scope.socketSampleStopAgent = function(idx){
        let url = "http://localhost:8080/sockets/sample-socket-stop-agent?idx=" + idx;
        $http.get(url).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )    
    }

    
    
    $scope.socketSampleStartAgent = function(idx){
        let url = "http://localhost:8080/sockets/sample-socket-start-agent?idx=" + idx;
        $http.get(url).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )    
    }

    $scope.socketSampleAllAgents = function(){
        let url = "http://localhost:8080/sockets/sample-socket-update-all-agents";
        $http.get(url).then(
            function(result){
                var result = result.data;
                console.log(result);
            },
            function(err){
                console.error(err);
            }
        )    
    }
}])
