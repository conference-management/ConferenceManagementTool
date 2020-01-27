import CommunicationManager from "../../communication/CommunicationManager.js";
import GetDocumentListRequestPacket from "../../communication/packets/GetDocumentListRequestPacket.js";
import UploadFileRequestPacket from "../../communication/packets/admin/UploadFileRequestPacket.js";
import GetFileRequestPacket from "../../communication/packets/DownloadFileRequestPacket.js";
import DeleteFileRequestPacket from "../../communication/packets/admin/DeleteFileRequestPacket.js";
import IsAdminRequestPacket from "../../communication/packets/IsAdminRequestPacket.js";



var documentContainer = $('#documentsContainer');
var editFileInput = $("#editFile")

var documents

$( document ).ready(function() {

    window.downloadDocument = download// export the function to the global scope
    window.editDocument = edit// export the function to the global scope
    window.removeDocument = remove// export the function to the global scope


 documents = []
    checkAdminStatus();

    document.getElementById('editFile').addEventListener('change', function (event) {
        uploadFile(event.target.files, editFileInput.attr("data-name"));
    }, false);

	let fileToUpload = null;
	document.getElementById('uploadFile').addEventListener('change', function (event) {
	    fileToUpload = event.target.files;
	    console.log(files);
    }, false);

    document.getElementById('submitUpload').onclick = () => {
        uploadFile(fileToUpload);
    }

});

function uploadFile(files, originalName = "") {
    var isCreation = false;
    if(originalName === "") isCreation = true; 
    if(files == null) {
            console.log("implement feedback");
            //TODO feedback
            return;
        }
        console.log(files);

        // Initialize an instance of the `FileReader`
        const reader = new FileReader();

        // Specify the handler for the `load` event
        reader.onload = function (e) {

            function success(packet) {
                location.reload();
                console.log("This method is called if a response from the server is received.");
                //TODO implement feedback
            }

            function fail() {
                console.log("This method is called if something went wrong during the general communication.");
            }
            console.log(e.target)
            const packet = new UploadFileRequestPacket(files[0].name, originalName , e.target.result, isCreation);
            console.log(files[0].name)

            // Send the request to the server
            CommunicationManager.send(packet, success, fail);


        }

        // Read the file
        reader.readAsArrayBuffer(files[0]);
}

function checkAdminStatus(){
    const packet = new IsAdminRequestPacket();

    CommunicationManager.send(packet, success, fail);

    function success(packet) {
        if(packet.result === "Valid") {

        
            window.isAdmin = packet.admin;     
            const packet2 = new GetDocumentListRequestPacket();

            CommunicationManager.send(packet2, success2, fail2);

            function success2(packet) {
                if(packet.result === "Valid") {
                    
                    if(packet.documents.length === 0){
                        $("<div class=\"col-lg-9\">"+"Currently no document is available"+"</div>").appendTo(documentContainer);
                        return;
                    }          
                    for(var doc of packet.documents){
                        generateDocument(doc).appendTo(documentContainer);
                    }
                }
    }

    function fail2() {
        console.log("This method is called if something went wrong during the general communication.");
    }     
            
        }
        else if(packet.result =="InvalidToken"){
             window.location = "./index.html"
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }
}

function edit(id){
    var name = documents[id]
    editFileInput.attr("data-name", name);
    editFileInput.click()

}

function remove(id){
      var name = documents[id]
    const packet = new DeleteFileRequestPacket(name);
    CommunicationManager.send(packet, success, fail);
    

    function success(packet) {
        console.log(packet);
        if(packet.result === "Valid") {
                location.reload();
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }
}


function download(id){
    var name = documents[id]
    function success(packet) {
        if(packet.result === "Valid") {
            var bytes = new Uint8Array(packet.fileBytes);

            var blob=new Blob([bytes]);

            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download=packet.fileName;
            link.click();
        }
    }

    function fail() {
        //TODO handle
    }

    const packet = new GetFileRequestPacket(name);

    // Send the request to the server
    CommunicationManager.send(packet, success, fail);
}

function escapeFilename(name){
    return name.replace("'", "\\'")
}



function generateDocument(document){



    var nameSpan = $("<span>")
    nameSpan.text(document.name)
    console.log(documents)
	var res =  $("<div class=\"row\">"+
                                            "<div class=\"col-sm-8 col-lg-8\" "+(!window.isAdmin? "onclick = \"downloadDocument(\'"+documents.length+"\')\"":"")+">"+
                                                "<li >"+(!window.isAdmin?"<a href ='#'>":"")+(nameSpan.html() + "<span style='font-size:50%'> &emsp; &emsp; version:"+document.revisionNumber+"</span>  ")+(!window.isAdmin?"</a>":"")+"</li>"+
                                            "</div>"+

                                            (window.isAdmin?"<div class=\"col-lg\">"+
                                                       "<a href=\"#\" style=\"font-size: 25px; margin-right: 42px; padding-left: 24px;\">"+
                                                      "<span onclick = \"downloadDocument(\'"+documents.length+"\')\" class=\"glyphicon glyphicon-download-alt \"></span>"+
                                                    "</a>"+
                                            //former color: #00D363;
                                            // "<div class=\"col-lg-auto\">"+
                                                       "<a href=\"#\" style=\"font-size: 25px; margin-right: 42px;\">"+
                                                      "<span onclick = \"editDocument(\'"+documents.length+"\')\" class=\"glyphicon glyphicon-edit\"></span>"+
                                                    "</a>"+
                                            // "</div>"+
                                            // "<div class=\"col-lg-auto\">"+
											
                                                       "<a href=\"#\" style=\"font-size: 25px; margin-right: 20px;\">"+
                                                      "<span onclick = \"removeDocument(\'"+documents.length+"\')\" class=\"glyphicon glyphicon-trash \"></span>"+
                                                    "</a>":"")+
                                            "</div>"+
                                        "</div>");
        documents.push(document.name)
            console.log(documents)
        return res;
}
