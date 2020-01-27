import CommunicationManager from "../CommunicationManager.js";
import GetFileRequestPacket from "../packets/DownloadFileRequestPacket.js";

// An example on how to download a file received as result of a DownloadFileRequest.

function success(packet) {
    console.log("This method is called if a response from the server is received.");
    if(packet.result === "Valid") {
        var bytes = new Uint8Array(packet.fileBytes);

        //var blob=new Blob([bytes], {type: "application/pdf"});
        var blob=new Blob([bytes]);

        var link=document.createElement('a');
        link.href=window.URL.createObjectURL(blob);
        link.download=packet.fileName;
        link.click();
    }
}

function fail() {
    console.log("This method is called if something went wrong during the general communication.");
}

const packet = new GetFileRequestPacket();

// Send the request to the server
CommunicationManager.send(packet, success, fail);


