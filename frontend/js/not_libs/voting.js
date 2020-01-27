import CommunicationManager from "../../communication/CommunicationManager.js";
// import GetActiveVotingRequestPacket from "../../communication/packets/GetActiveVotingRequestPacket.js";
import AddVoteRequestPacket from "../../communication/packets/AddVoteRequestPacket.js";
import GetVotingsRequestPacket from "../../communication/packets/admin/GetVotingsRequestPacket.js";
import AddVotingRequestPacket from "../../communication/packets/admin/AddVotingRequestPacket.js";
import EditVotingRequestPacket from "../../communication/packets/admin/EditVotingRequestPacket.js";
import RemoveVotingRequestPacket from "../../communication/packets/admin/RemoveVotingRequestPacket.js";
import StartVotingRequestPacket from "../../communication/packets/admin/StartVotingRequestPacket.js";
import Packet from "../../communication/packets/Packet.js";



var createdVotesContainer = $("#createdVotesList");

var votings;

var booleanCheck = false;


$(document).ready( function() {

	// as the page ready RenderVotings will be called.
	renderVotings();

	// These functions are exposed to global scope by using window object.
    window.createNewVoting = create;
    window.addVotingOption = addOption;
    window.saveVoting = save;
    window.deleteVote = remove;
    window.startVote = start;
    window.deleteVotingOption = deleteOption;
	
	

});

/**
 * save function will be called when the admin click on save changes button. Basically, Purpose of this funtion is to save changes that has been made in options of the vote question.
 * The following paramters should be included in the request.
 * @param ID of the vote,
 * @param Vote question,
 * @param Available Options for the question, 
 * @param Need to specify vote type i.e Named vote or Anonymous vote. As this parameter only accepts boolean value so true for name vote & false for Anonymous,
 * @param Need to specify duration of the vote,
 */

function save(voteId, flag = false){
	var vote;

	for(var _vote of votings){
		if(_vote.ID == voteId){
			vote = _vote;
			break;
		}
	}

	var options =[]
	var checkEmptyOption = false;

	for(var elem of $("#votingOptions"+voteId).children()){
		console.log($($(elem).children()[0]).val());
		// var option = $($($(elem).children()[0]).children()[0]).val();
		var option = $($(elem).children()[0]).val();
		// console.log(option);
		options.push(option)
	}
	// console.log(options);

	for (var i in options){
		if(options[i] == ''){
			checkEmptyOption = true;
			break;
		}
	}

	if(checkEmptyOption){ 
		$('#failureMessage').html("Option fields should not be empty");
		$("#dialogMessageForOption").dialog("open");
		booleanCheck = true;
		return false;
	}
	else {
		booleanCheck = false;
	}


	const packet = new EditVotingRequestPacket(voteId, vote.question, options ,vote.named, vote.duration);

	function success(packet){

		
		if(packet.result === "Valid"){

			if(flag){
				return;
			}
			
			$("#dialogMessage").dialog("open");
			return false;

		}
	}

	 function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    }

    CommunicationManager.send(packet, success, fail);
}



$("#dialogMessage").dialog({
	autoOpen: false,
	resizable: false,
	// autoOpen:false,
	height: 210,
	width: 376,
	modal: true,
	title: "Success",
// 		buttons: {
// 		"Do Something": function() {
// 		},
// 	Cancel: function() {

// 	$(this).dialog("close");
// 	}
// }
});

/**
 * remove function will be called when the admin click on delete button. This will delete a vote question.
 * It will send a request to server to delate this vote from database by specifying vote ID. 
 * @param ID of the vote,
 * if everything is done successfully renderVotings function will be called in order to get all votes question again that are currently available in the database. 
 */

function remove(voteId){
    const packet = new RemoveVotingRequestPacket(voteId);

    function success(packet){
    	if(packet.result === "Valid"){
			// console.log(packet);
            renderVotings();

        }
    }

     function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    } 

    CommunicationManager.send(packet, success, fail);
}


/**
 * send a request to the server to get all available votes question in Database.
 * After for each vote question renderCreatedVote function will be called in order to display the vote quesiton. 
 * @param vote object parameter will be passed to renderCreatedVote function, 
 */

function renderVotings(){
	 function success(packet){
        if(packet.result === "Valid"){
        	createdVotesContainer.html("");
        	votings = packet.votings;

            for(var voting of packet.votings){
            	if(voting.status === "Created"){

					renderCreatedVote(voting)
            	}
            }

        }
    }

    function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    }

    const packet = new GetVotingsRequestPacket();

    CommunicationManager.send(packet, success, fail);
}

/**
 * To add a new option to the vote question addOption function will be called after clicking Add option button.
 * New option will append to that particular vote question.
 */

function addOption(voteId){
	const optionsField = $("#votingOptions"+voteId);
	
	
	// $("<div class = \"col-md-4\"><div><input style=\"font-size:20px; margin-top: 5px;\" class=\"form-control\" "+
		// "type=\"text\" value=\"\" placeholder = \"Please provide the voting option\"></div></div>").appendTo(optionsField);
		// var numberofButtons = $("#votingOptions"+voteId+" button");
		// for(var i=0; i<=numberofButtons.length; i++) {
			
			// console.log(i);
			
					// }
			// console.log(voteId);		
			$('<div class="input-group mb-3">'+
			'<input type="text" class="form-control" placeholder="Please provide the voting option" aria-label="Please provide the voting option" aria-describedby="button-addon2">'+
			'<div class="input-group-append">'+
			// '<button class="btn btn-outline-secondary" type="button" id="button-addon2">Button</button>'+
			'<button type="button" class="btn btn-outline-info" onclick="deleteVotingOption(this);">Remove</button>'+
			'</div>'+
			'</div>').appendTo(optionsField);
			


		

		// console.log(numberofButtons.length)
		// console.log(numberofButtons.length)
		
}



// deleteOption function will be called when you want to delete an option per Vote question.

/**
 * To delete an option from the vote question deleteOption function will be called after clicking delete option button.
 */

function deleteOption(btn) {
	
	((btn.parentNode.parentNode).parentNode).removeChild(btn.parentNode.parentNode);
	
	// ((btn.parentNode).parentNode).removeChild(btn.parentNode.previousElementSibling);
	// ((btn.parentNode).parentNode).removeChild(btn.parentNode);
	
	// var div = document.getElementById('votingOptions'+voteId);
	
}


/**
 * send a request to the server to create a new vote question.
 * The following paramters will be send along.
 * @param Vote question 
 * @param options parameter for vote question will be an empty array at first place.
 * @param Need to specify vote type i.e Named vote or Anonymous vote. As this parameter only accepts boolean value so true for name vote & false for Anonymous,
 * @param Duration of vote,
 * if everything is done successfully renderVotings function will be called in order to get all votes question that are currently available in the database. 
 */

$("#dialog").dialog({
	autoOpen: false,
	resizable: false,
	// autoOpen:false,
	height: 380,
	width: 500,
	modal: true,
	title: "Creat Vote Panel"
});


$('#createVote').on("click", function () {
	
	$("#dialog").dialog("open");
	return false;
});


function create(){

	// e.preventDefault();
	// createDialog.dialog("open");
	

	var voteQuestion = $('#VoteText').val();
	var TimeDuration = $('#duration').val();
	var voteType = $('input[name="yes-no"]:checked').val();

	if( (voteQuestion == '' && TimeDuration == '') || voteQuestion == '' || TimeDuration == ''){
		return;}
	
	if (voteType === '1'){ 
		var voteTypeBoolean = true;
	}	else {
		var voteTypeBoolean = false;
	}

	const packet = new AddVotingRequestPacket(voteQuestion, [], voteTypeBoolean, TimeDuration * 60);
	CommunicationManager.send(packet, success, fail);

	 function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    }

	function success(packet){
 		if(packet.result === "Valid"){
            renderVotings();

        }
	}
}

$("#dialogMessageForOption").dialog({
	autoOpen: false,
	resizable: false,
	// autoOpen:false,
	height: 226,
	width: 418,
	modal: true,
	title: "Message",

});

// var startVoteResult;

/**
 * start function will be called if admin wants to start a vote.
 * The following paramter will be send along to specify which vote will start.
 * @param Vote ID
 */

function start(voteId){


	// callForActiveVotePacket();

	// console.log(values);
	// ActiveVotePacketCall();
	var flag = true;
	console.log(booleanCheck);
	save(voteId, flag);

	if(booleanCheck){
		return;
	};

    function success(packet){
		console.log(packet);
		
		

    	if(packet.result === "Valid"){

			// startVoteResult = Packet.result;
			
			// console.log("testing");
            // renderVotings();

        }
		else {
			// alert(packet.details);
			console.log(packet.details)
			// $('#failureMessage').empty();
			$('#failureMessage').html(packet.details);
			
			$("#dialogMessageForOption").dialog("open");
			return false;
			
		}
    }

     function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    } 

	const startVote = new StartVotingRequestPacket(voteId);
    CommunicationManager.send(startVote, success, fail);
}
// export { startVoteResult};
/**
 * The purpose of the function is to display votes questions with the following attributes.
 * Vote ID
 * Vote Question
 * Vote Type i.e. Named or Anonymous
 * Vote Duration
 * Further, it will also create the following buttons and append to the vote question.
 * start
 * Add option 
 * Delete option
 * Save
 * Delete
 */

function renderCreatedVote(vote){
	var durationAux = (vote.duration).toFixed(0);
	var secondsAux = (durationAux % 60).toFixed(0);
	var seconds = (secondsAux < 10 ? "0"+secondsAux:secondsAux);
	var durationMinutes = (Math.floor((durationAux/60)))+":"+seconds;
	var flag = false;

	$( "<tr style ='word-break:break-all;' data-toggle=\"collapse\" data-target=\"#user_accordion"+vote.ID+"\" id='#user_accordion"+vote.ID+"' class=\"clickable\">"+
                                        "<td>"+vote.question+"</td>"+
                                        "<td>"+(vote.namedVote?"Named":"Anonymous")+"</td>"+
                                        "<td>"+durationMinutes+"</td>"+
                                    "</tr>"+
                                    "<tr>"+
                                    "<td colspan=\"3\">"+
                                        "<div id=\"user_accordion"+vote.ID+"\"  class=\"collapse\">"+
                                            "<div style='padding:10px;' class = \"row\" id = \"votingOptions"+vote.ID+"\">"+
                                            "</div>"+
											  '<button type="button" class="btn btn-outline-primary" style="margin-left: 10px; width: 200px; margin-bottom: 10px;" onclick="startVote('+vote.ID+')">Start Vote</button>' +
											// '<div class="btn-group" role="group" aria-label="Basic example">' +
											'<button type="button" class="btn btn-outline-success" style="margin-left: 10px; width: 200px; margin-bottom: 10px;" onclick="addVotingOption('+vote.ID+')">Add</button>' +
											'<button type="button" class="btn btn-outline-success" style="margin-left: 10px; width: 200px; margin-bottom: 10px;" onclick="saveVoting('+vote.ID+','+flag+')">Save Changes</button>' +
											// '</div>' +
											'<button type="button" class="btn btn-outline-danger" style="margin-left: 10px; width: 200px; margin-bottom: 10px;" onclick="deleteVote('+vote.ID+')">Delete</button>' +
                                            "</div>"+
                                        "</div>"+
                                    "</td>"+
                                    "</tr>"

		).appendTo(createdVotesContainer);

	const optionsField = $("#votingOptions"+vote.ID);
    for (var i = 0; i < vote.options.length; i++) {
        var option = vote.options[i];
		// $("<div class = \"row\"><div><input style=\"font-size:20px; margin-top: 5px;\" class=\"form-control\" "+
		// "type=\"text\" value=\""+option.name+"\"></div>").appendTo(optionsField);
		console.log(option.name);
		$('<div class="input-group mb-3">'+
		'<input type="text" class="form-control" placeholder="Please provide the voting option" aria-label="Please provide the voting option" aria-describedby="button-addon2" value="'+option.name+'">'+
		'<div class="input-group-append">'+
		// '<button class="btn btn-outline-secondary" type="button" id="button-addon2">Button</button>'+
		'<button type="button" class="btn btn-outline-info" onclick="deleteVotingOption(this);">Remove</button>'+
		'</div>'+
		'</div>').appendTo(optionsField);	
		
		
	}

	 

}

		
