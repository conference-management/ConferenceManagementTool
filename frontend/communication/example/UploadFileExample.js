import CommunicationManager from "../CommunicationManager.js";
import UploadFileRequestPacket from "../packets/admin/UploadFileRequestPacket.js";

$( document ).ready(function() {
    document.getElementById('fileInput').addEventListener('change', function (event) {

        const files = event.target.files;

        // Initialize an instance of the `FileReader`
        const reader = new FileReader();

        // Specify the handler for the `load` event
        reader.onload = function (e) {

            function success(packet) {
                console.log("This method is called if a response from the server is received.");
            }

            function fail() {
                console.log("This method is called if something went wrong during the general communication.");
            }

            const packet = new UploadFileRequestPacket("name", files[0].name , e.target.result);
            console.log(files[0].name)

            // Send the request to the server
            CommunicationManager.send(packet, success, fail);


        }

        // Read the file
        reader.readAsArrayBuffer(files[0]);
    }, false);
});
