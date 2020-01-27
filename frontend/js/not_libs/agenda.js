import CommunicationManager from "../../communication/CommunicationManager.js";
import GetAgendaRequestPacket from "../../communication/packets/GetAgendaRequestPacket.js";
import AddTopicRequestPacket from "../../communication/packets/admin/AddTopicRequestPacket.js";
import RemoveTopicRequestPacket from "../../communication/packets/admin/RemoveTopicRequestPacket.js";
import RenameTopicRequestPacket from "../../communication/packets/admin/RenameTopicRequestPacket.js";
import AddFullAgendaRequestPacket from "../../communication/packets/admin/AddFullAgendaRequestPacket.js";
import DeleteAgendaRequestPacket from "../../communication/packets/admin/DeleteAgendaRequestPacket.js";
import IsAdminRequestPacket from "../../communication/packets/IsAdminRequestPacket.js";

var agendaContainer = $('#agendaContainer');

/**
    Performs the initial data fetch and some initialization
*/
$( document ).ready(function() {

    window.appendToAgenda = newTopic// export the function to the global scope
    window.subtopicToAgenda = newSubtopic// export the function to the global scope
    window.removeFromAgenda = remove// export the function to the global scope
    window.editAgenda = edit// export the function to the global scope
    window.uploadAgenda = uploadAgenda;
    window.deleteAgenda = deleteAgenda;

    checkAdminStatus();

    document.getElementById('upAgenda').addEventListener('change', handleAgendaUpload, false);


});

function successAgendaReq(packet) {
    if(packet.result === "Valid") {
    //    checkAdminStatus();
        renderAgenda(packet.agenda, agendaContainer);
    }
}

function failAgendaReq() {
    console.log("This method is called if something went wrong during the general communication.");
}




/**
    This function displays an agenda
    @param data - the agenda object (the parsed json string)
    @param 
*/
function renderAgenda(data, parent) {
    // create an inner item
    agendaContainer.html("");
    if(data.topics.length === 0){
        createDefault(parent);
        return;
    }

    var fontSize = 32;
    var fontSizeDifference = 2;
    var fontSizeMin = 26;

    for (var i = 0; i < data.topics.length; i++) {
        createInner(data.topics[i], parent, i+1);
    }
    
    /**
     * This function calls itself recursively in order to render nested topics
     *
     * @param agenda - the (sub)agenda object
     * @param parent - the DOM object to which this agenda should be appended
     * @param preOrder - the pre-order string of the agenda in the larger context
     */
    function createInner(agenda, parent, preOrder) {
        var li = generateAgendaRow(agenda.name, preOrder, fontSize).appendTo(parent);

        var decreased;

        if(fontSize >= fontSizeMin){
            fontSize -= fontSizeDifference;
            decreased = true;
        } else{
            decreased = false;
        }

        if (agenda.subTopics.topics !== undefined && agenda.subTopics.topics.length > 0) {
            var innerList = $('<ul class="list agendaList"></ul>').appendTo(li);
            for (var i = 0; i < agenda.subTopics.topics.length; i++) {
                var child = agenda.subTopics.topics[i];
                createInner(child, innerList,  preOrder+"."+(i+1));
            }
        }

        if(decreased){
            fontSize += fontSizeDifference;
        }

        /**
         * This function renders an individual agenda row. Note that this method also specifies the event handlers
         * @param name - the name of the TOP
         * @param preorder - the id of the TOP
         * @param fontSize - the fontsize of the TOP. Allows nicer UIs
         */
        function generateAgendaRow(name, preorder, fontSize){

            var nameSpan = $('<span>')
            nameSpan.text(name)


            return $(
                '<li class = "agendaRow" style="font-size: '+fontSize+'px;">'+(nameSpan.html())+(window.isAdmin?
                '<span style="display:inline-block; width: 60px;">'+
                '</span><span style="font-size:32px;" class="glyphicon glyphicon-plus" onclick = "appendToAgenda(\''+preorder+'\')"></span>'+
                '<span style="display:inline-block; width: 30px;">'+
                '</span><span style="font-size:32px" class="glyphicon glyphicon-chevron-down" onclick = "subtopicToAgenda(\''+preorder+'\')"></span>'+
                '<span style="display:inline-block; width: 30px;">'+
                '</span><span style="font-size:32px" class="glyphicon glyphicon-pencil" onclick = "editAgenda(\''+preorder+'\')"></span>'+
                 '<span style="display:inline-block; width: 30px;">'+
                '</span><span style="font-size:32px" class="glyphicon glyphicon-trash" onclick = "removeFromAgenda(\''+preorder+'\')"></span>':"")+
                '</li>'
            );
        }

    }

    /**
     * This function is used in the case the agenda is empty.
     * It displays a nice message for attendees and provides a button which allows admins to upload a new agenda
     */
    function createDefault(target) {
        if (window.isAdmin) {
            $("<div class=\"row \">" +

                "<div class=\"form-group mt-3 col-lg-12\" style=\"float: left; margin-left: 10pt;\">" +
                "    <button class=\"button button-contactForm btn-class-box \" onclick=\"appendToAgenda(\'0\')\">Add Topic</button>" +
                "</div>" +
                "</div>").appendTo(target);
        } else {
            $("<div class=\"row \">" +

                "<div class=\"form-group mt-3 col-lg-12\" style=\"float: left;\">" +
                "    Currently the agenda is empty" +
                "</div>" +
                "</div>").appendTo(target);
        }


    }

    
}

function uploadAgenda() {
    if (window.isAdmin) {
        document.getElementById('upAgenda').click();
    }
}

function handleAgendaUpload(event) {

    let files = event.target.files;
    if (files[0].name.split(".").pop().localeCompare("txt") === 0) {
        var file = files[0];
        if (file) {
            var reader = new FileReader();
            var text = "";
            reader.onload = function (evt) {
                text = evt.target.result;
                const packet = new AddFullAgendaRequestPacket(text);
                console.log(packet);
                CommunicationManager.send(packet, success, fail);
            };
            reader.onerror = function(event) {
                console.error("File could not be read");
            };
            reader.readAsText(file, "UTF-8");
        } else {
            console.error("Something went terribly wrong.");
        }
    } else {
        alert("Wrong File Extension. Only .txt files allowed.")
    }

    function success(packet) {
        if (packet.result === "Valid") {
            window.location.reload()
        } else {
            console.log(packet)
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }
}

function deleteAgenda() {
    if (window.isAdmin) {
        const packet = new DeleteAgendaRequestPacket();
        CommunicationManager.send(packet, success, fail)
    }

    function success(packet) {
        if (packet.result === "Valid") {
            window.location.reload()
        } else {
            console.log(packet)
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }
}

/**
 * This function allows admins to add new topics to the agenda
 * @param preorder - the preorder address at which the admin wished to add a agenda point
 * @param isSubtopic - used to differentiate whether or not the admin wished to create a topic by using the
 * 'add topic' or the 'add subtopic' button. While this makes no difference for the request send it does lead to a
 * better UX.
 */
function append(preorder, isSubtopic){
    var split = (""+preorder).split(".");
    var elem = split.pop();
    var newOrder = (parseInt(elem) +1);
    if(split.length !== 0){
        newOrder = split.join(".")+ "." + newOrder
    }

    var res;
    if(isSubtopic){
        res = prompt("Enter name of new subtopic")
    } else{
        res = prompt("Enter name of new topic");
    }

    if(res != null){
        const packet = new AddTopicRequestPacket(newOrder, res);
        CommunicationManager.send(packet, success, fail);
    }

    function success(packet) {
        console.log(packet);
        if(packet.result === "Valid") {
                const packet = new GetAgendaRequestPacket();
                CommunicationManager.send(packet, successAgendaReq, failAgendaReq);
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }


}

/**
 * This function allows admins to remove (sub)topics
 * @param preorder - the id of the topic the admins wish to remove
 */
function remove(preorder){

    const packet = new RemoveTopicRequestPacket(preorder);

    function success(packet) {
        if(packet.result === "Valid") {
                const packet = new GetAgendaRequestPacket();
                CommunicationManager.send(packet, successAgendaReq, failAgendaReq);
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }

    
    CommunicationManager.send(packet, success, fail);
}


/**
@see append
*/
function newSubtopic(preorder){
    append(preorder+".0", true);
}

/**
@see append
*/
function newTopic(preorder){
     append(preorder, false);
}
 
/**
This function allows admins to change the name of (sub)topics
@param preorder : the id of the topic the admins wish to edit
*/
function edit(preorder){
    var res = prompt("Enter new topic name");
    if(res){
        const packet = new RenameTopicRequestPacket(preorder, res);
        CommunicationManager.send(packet, success, fail);
    }

    function success(packet) {
        console.log(packet);
        if(packet.result === "Valid") {
                const packet = new GetAgendaRequestPacket();
                CommunicationManager.send(packet, successAgendaReq, failAgendaReq);
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }


} 


/**
    Tests if the user currently seeing the page is an admin. 
    The result of the test is stored inside the 'isAdmin' field of the top level window object for later use (this minimizes the number of requests needed)
    The function also hiddes admin fields if necessary or redirects the user to the login page is the token is invalid
*/
function checkAdminStatus(){
    const packet = new IsAdminRequestPacket();

    CommunicationManager.send(packet, success, fail);

    function success(packet) {
        console.log(packet);
        if(packet.result === "Valid") {

            window.isAdmin = packet.admin;       
            const packet2 = new GetAgendaRequestPacket();

            CommunicationManager.send(packet2, successAgendaReq, failAgendaReq);   
            
        }
        else if(packet.result =="InvalidToken"){
             window.location = "./index.html"
        }
    }

    function fail() {
        console.log("This method is called if something went wrong during the general communication.");
    }
}

