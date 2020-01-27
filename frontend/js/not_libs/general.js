import CommunicationManager from "../../communication/CommunicationManager.js";
import IsAdminRequestPacket from "../../communication/packets/IsAdminRequestPacket.js";
import GetActiveVotingRequestPacket from "../../communication/packets/GetActiveVotingRequestPacket.js";
import Cookies from "../../communication/utils/Cookies.js";


$( document ).ready(function() {

	const packet = new IsAdminRequestPacket();

	CommunicationManager.send(packet, success, fail);

	function success(packet) {
		console.log(packet);
		if(packet.result === "Valid") {

			if(packet.admin){
				$(".adminField").each(function(i, field){
					$(field).removeClass("adminField");
				})
			}
			window.isAdmin = packet.admin;

		}
		else if(packet.result =="InvalidToken"){
			window.location = "./index.html"
		}
	}

	function fail() {
		console.log("This method is called if something went wrong during the general communication.");
	}

	// setInterval function will check in every second that new vote has started or not.
	stopInterval = setInterval(checkVoteExistance, 1000, true);


});

var stopInterval

// checkVoteExistance function will first check that vote exits or not if it exists then
// it will redirect only once if vote starts.

function checkVoteExistance() {
	function success(packet){
		if(packet.result === "Valid"){
			if(packet.exists){
				var voteExpiryDate = new Date(packet.voting.openUntil);
Cookies.setCookieWithDate("voteID", packet.voting.ID, voteExpiryDate);
if(Cookies.getCookie("voteID") != null){
	var redirectToVote = sessionStorage.getItem("redirect");

	if (redirectToVote === null){
		clearInterval(stopInterval);
		window.location.href = './vote.html';
	}
}
} else {
	sessionStorage.removeItem("redirect");
}
}
}

function fail() {
	console.log("Something went wrong during, get active vote question & options.");
}

const getActiveVote = new GetActiveVotingRequestPacket();

CommunicationManager.send(getActiveVote, success, fail);


}