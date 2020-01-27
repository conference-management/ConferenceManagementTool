import CommunicationManager from "../../communication/CommunicationManager.js";

import RequestOfChangeRequestPacket from "../../communication/packets/RequestOfChangeRequestPacket.js";

var referenceToTopic = false;

$("#request").on("click", function () {
	
	var topicDocument = $('#topic-document option:selected').text();
	var requestMessage = $('textarea#message').val();
	
	console.log(topicDocument);
	console.log(requestMessage);
	
	if(topicDocument == "Topic 1"){
		referenceToTopic = true;
		
	}

    function success(packet){
        console.log(packet)
        if(packet.result === "Valid"){
            alert('your request is accepted');
        }
    }

    function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    }

	console.log(referenceToTopic);
    const requestOfChange = new RequestOfChangeRequestPacket(referenceToTopic, topicDocument, requestMessage);

    CommunicationManager.send(requestOfChange, success, fail);

});