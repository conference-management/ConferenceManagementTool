import LoginRequestPacket from "../packets/LoginRequestPacket.js";
import Cookies from "../utils/Cookies.js";
import CommunicationManager from "../CommunicationManager.js";

// An example on how to send requests to the server and handle it's responses.

function success(packet) {
    console.log("This method is called if a response from the server is received.");
    // Take a look at the java docs for details on the structure of the packets.
    if(packet.loginResponse === "Valid") {
        Cookies.setCookie("token", packet.token, packet.expiration);
    }
}

function fail() {
    console.log("This method is called if something went wrong during the general communication.");
}

const login = new LoginRequestPacket("user", "password");

// Send the request to the server
CommunicationManager.send(login, success, fail);
