import CommunicationManager from "../../communication/CommunicationManager.js";
import IsAdminRequestPacket from "../../communication/packets/IsAdminRequestPacket.js";
import LoginRequestPacket from "../../communication/packets/LoginRequestPacket.js";
import Cookies from "../../communication/utils/Cookies.js";


/**
 * When the document is ready it will send request to server to check who is logged in i.e Admin or attendee.
 * In response from the server if the packet is valid it will redirect to website home page.
 * For Admin and attendee will work differently. 
 */


$( document ).ready(function() {

    const packet = new IsAdminRequestPacket();

    CommunicationManager.send(packet, success, fail);

    function success(packet) {
    	console.log(packet);
	    if(packet.result === "Valid") {
			
	    	 window.location = "./home.html"
	    }
        else{checkUrlLogin();}

	}

	function fail() {
	    console.log("This method is called if something went wrong during the general communication.");
	}


});


function checkUrlLogin(){
    var url = new URL(window.location);
    var name = url.searchParams.get("name");
    var pwd = url.searchParams.get("pwd");
    if(name && pwd){
        login(name, pwd);
    }
}

/**
 * this Anonymous function will be called when click on submit vote button. Purpose of this function is to send use name and password for authentication.
 * The following paramters should be included in the request.
 * @param name
 * @param password
 * If username and password is correct, a token and it's expiry will be issued and will set as cookie. After, it will redirect to home html page.
 */



            $("#test-form").submit(function (e) {
                //This prevents the default redirection due to form submission
                e.preventDefault();

                var name = document.getElementById("name").value;
                var password = document.getElementById("password").value;
                login(name, password);

                
            });


function login(name, password){
    function success(packet) {
                    console.log("This method is called if a response from the server is received.");
                    // Take a look at the java docs for details on the structure of the packets.
                    if(packet.result === "Valid") {
                        Cookies.setCookie("token", packet.token, packet.expiration);
                        window.location.href = "./home.html";
                    } else {
                        $("#name").focus();
                        $('#message').html('Invalid username or password').css({'color':'red', 'text-align': 'center'});
                        return false;
                    }
                }

                function fail() {
                    console.log("This method is called if something went wrong during the general communication.");
                    $('#message').html('Backend communication failed').css('color', 'red');
                }

                const login = new LoginRequestPacket(name, password);

                // Send the request to the server
                CommunicationManager.send(login, success, fail);
}
