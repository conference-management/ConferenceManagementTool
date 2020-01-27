import CommunicationManager from "../../communication/CommunicationManager.js";
import GetAgendaRequestPacket from "../../communication/packets/GetAgendaRequestPacket.js";
import RequestOfSpeechRequestPacket from "../../communication/packets/RequestOfSpeechRequestPacket.js";
import RequestOfChangeRequestPacket from "../../communication/packets/RequestOfChangeRequestPacket.js";
import GetDocumentListRequestPacket from "../../communication/packets/GetDocumentListRequestPacket.js";

var changeMessage = $("#requestMessage"); // the DOM object representing the input for requests of change
var requestOptions = $(".requestSelect"); // the dropdown that lets the user choose which type of request they wish to submit

var documents = []

$( document ).ready(function() {
	getAgenda();
    getDocuments();

	window.submitRequest = submit; //export the function to the global scope 
});


/**
Submits the request to the server if the data is valid, or prompt the user to change the request data if it is invalid
@ param isSpeechRequest : determines the request type
*/
function submit(isSpeechRequest){
	var selectedOption = requestOptions.find('option:selected');


    if( !selectedOption.attr("data-isTop")){
        alert("Please select a topic");
        return;
    }

	var refersToTopic = selectedOption.attr("data-isTop");
	var reference = selectedOption.attr("data-id");

    refersToTopic = (""+refersToTopic) === "true"

    if(!refersToTopic){
        reference = documents[reference]
    }

	var packet;
	if(isSpeechRequest){
		packet = new RequestOfSpeechRequestPacket(refersToTopic, reference);
	}
	else{
        if(!changeMessage.val()){
            alert("Please enter a message or submit a speech request");
            return;
        }
		packet = new RequestOfChangeRequestPacket(refersToTopic, reference, changeMessage.val());
	}

    CommunicationManager.send(packet, success, fail);

     function success(packet){
    	if(packet.result === "Valid") {          
        	alert("Your request has been succesfully submited");
            window.location = "./home.html"
    	}
    }


    function fail(){
    	console.log("This method is called if something went wrong during the general communication.");
    }
}

/**
Fetches the agenda from the backend and adds the TOPs to the list of valid requestables.
Note that requestable options need to store their type and an unique id using the data-id and the data-isTop attributes in html
*/
function getAgenda(){
 	const packet = new GetAgendaRequestPacket();

    CommunicationManager.send(packet, success, fail);

    function success(packet){
    	if(packet.result === "Valid") {          
        	var agenda = packet.agenda;
        	for (var i = 0; i < agenda.topics.length; i++) {
        		addTopic(agenda.topics[i], (i+1));
    		}

    	}

    	function addTopic(topic, preorder){
    		requestOptions.each(function(i, option){
                var nameSpan = $("<span>")
                nameSpan.text(topic.name)
	    		$("<option data-id=\""+preorder+"\" data-isTop = true>" +preorder+" "+(nameSpan.html())+"</option>").appendTo(option);
        		for (var i = 0; i < topic.subTopics.topics.length; i++) {
                    var child = topic.subTopics.topics[i];
                    addTopic(child, preorder+"."+(i+1));
                }
    	   });

        }
    }

    function fail(){
    	console.log("This method is called if something went wrong during the general communication.");
    }
}

/**
Fetches the documents from the backend and adds them to the list of valid requestables.
Note that requestable options need to store their type and an unique id using the data-id and the data-isTop attributes in html
*/
function getDocuments(){
	const packet = new GetDocumentListRequestPacket();
    documents = []

    CommunicationManager.send(packet, success, fail);
    function success(packet) {
        if(packet.result === "Valid") {         
            for(var doc of packet.documents){

                var nameSpan = $("<span>")
                nameSpan.text(doc.name)
    

                requestOptions.each(function(i, option){
                    $("<option data-id=\""+(documents.length)+"\" data-isTop = false>" +(nameSpan.html())+"</option>").appendTo(option);
                    documents.push(doc.name)
                })
            }
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }

}
