import CommunicationManager from "../../communication/CommunicationManager.js";
import PersonalDataRequestPacket from "../../communication/packets/PersonalDataRequestPacket.js";

/**
 * The furpose of the ready function is to assign id's to variables and to get packet from the server.
 * Then printPersonalData function will be called to pass packet data.
 * @param packet( with attendee details) 
 * @param profileName
 * @param profileEmail
 * @param profileGroup
 * @param profileFunction
 * @param profileResidence
 */



$(document).ready( function(){
	
	// All input values are assigned to variables.

    var profileName = $('#personalDataID');
    var profileUsername = $('#profileUsername');
    var profileEmail = $('#profileEmail');
    var profileGroup = $('#profileGroup');
    var profileResidence = $('#profileResidence');
    var profileFunction = $('#profileFunction');

    function success(packet){
        if(packet.result === "Valid"){
            printPersonalData(packet.attendee, profileName, profileUsername, profileEmail, profileGroup, profileResidence, profileFunction);
        }
    }

    function fail(){
        console.log("Something went wrong during Get Personal Data Request");
    }

    const requestPacket = new PersonalDataRequestPacket();

    CommunicationManager.send(requestPacket, success, fail)
});

/**
 * printPersonalData will print/display packet data.
 * It requires the following arguments
 * @param attendee (with attendee details)
 * @param $name - ID which the attendee name shall be pasted into
 * @param $username - ID which the attendee username shall be pasted into
 * @param $email - ID which the attendee email shall be pasted into
 * @param $group - ID which the attendee group shall be pasted into
 * @param $residence - ID which the attendee residence shall be pasted into
 * @param $function - ID which the attendee function shall be pasted into
 */


function printPersonalData(attendee, $name, $username, $email, $group, $residence, $function){
    //append all data to respective place
    var insertName = $('<h2 class="contact-title">').appendTo($name);
    insertName.text(attendee.name);
    $('</h2>').appendTo(insertName);

    var insertUsername = $('<div style="font-size: large">').appendTo($username);
    insertUsername.text(attendee.userName);

    var insertEmail = $('<div style="font-size: large">').appendTo($email);
    insertEmail.text(attendee.email);

    var insertGroup = $('<div style="font-size: large">').appendTo($group);
    insertGroup.text(attendee.group);

    var insertResidence = $('<div style="font-size: large">').appendTo($residence);
    insertResidence.text(attendee.residence);

    var insertFunction = $('<div style="font-size: large">').appendTo($function);
    insertFunction.text(attendee.function);

}