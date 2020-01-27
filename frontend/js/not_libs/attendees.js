import GetAllAttendeesRequestPacket from "../../communication/packets/admin/GetAllAttendeesRequestPacket.js";
import CommunicationManager from "../../communication/CommunicationManager.js";
import RemoveAttendeeRequestPacket from "../../communication/packets/admin/RemoveAttendeeRequestPacket.js";
import EditUserRequestPacket from "../../communication/packets/admin/EditUserRequestPacket.js";
import AddAttendeeRequestPacket from "../../communication/packets/admin/AddAttendeeRequestPacket.js";
import AddMultipleAttendeesRequestPacket from "../../communication/packets/admin/AddMultipleAttendeesRequestPacket.js";
import LogoutAttendeeRequestPacket from "../../communication/packets/admin/LogoutAttendeeRequestPacket.js";
import GenerateNewAttendeePasswordRequestPacket
    from "../../communication/packets/admin/GenerateNewAttendeePasswordRequestPacket.js";
import { getSortedList } from "./attendeeSorting.js";
import GenerateNewAttendeeTokenRequestPacket
    from "../../communication/packets/admin/GenerateNewAttendeeTokenRequestPacket.js";
import GetAttendeePasswordRequestPacket from "../../communication/packets/admin/GetAttendeePasswordRequestPacket.js";
import DownloadQRRequestPacket from "../../communication/packets/admin/DownloadQRRequestPacket.js";
import DownloadAllQRRequestPacket from "../../communication/packets/admin/DownloadAllQRRequestPacket.js";
import GetExistingGroupsRequestPacket from "../../communication/packets/admin/GetExistingGroupsRequestPacket.js";
import SetAttendeePresentStatusRequestPacket
    from "../../communication/packets/admin/SetAttendeePresentStatusRequestPacket.js";

//Importing JQuery and JQuery UI
//import $ from "../../node_modules/jquery";
//import "../../node_modules/jquery-ui-bundle";



/**
 * Initializing
 */
$(function(){


    //Initializing dialog options
    createDialog = $('#creationDialog').dialog({
        title: "Create Attendee",
        autoOpen: false,
        modal: true,
        draggable: false,
        containment: window,
        close: function () {
            createForm[ 0 ].reset();
            createFields.removeClass("ui-state-error");
        },
        open: function () {
            this.scrollTop("0");
        }
    });

    createForm = createDialog.find("form").on("submit", function(event){
        event.preventDefault();
        clickCreateAttendee();
    });

    $('#create-attendee').on("click", function (e) {
        e.preventDefault();
        getExistingGroups(false);
        createDialog.dialog("open");
    });


    fileDialog = $('#fileErrorDialog').dialog({
        title: "Error in Attendee File",
        autoOpen: false,
        modal: true,
        draggable: false
    });

    //Editing dialog mostly similar to creation dialog
    editDialog = $('#editDialog').dialog({
        title: "Edit Attendee",
        autoOpen: false,
        modal: true,
        draggable: false,
        containment: window,
        close: function () {
            editForm[ 0 ].reset();
            editFields.removeClass("ui-state-error");
        },
        open: function () {
            this.scrollTop("0");
        }
    });


    editForm = editDialog.find("form").on("submit", function(event){
        event.preventDefault();
        clickEditAttendee();
    });
});

$(document).ready( function() {
    //Move functions to global scope so onclick parameters can call them
    window.deleteAttendeeGlobal = deleteAttendee;
    window.editAttendeeGlobal = editAttendee;
    window.getNewAttendeePasswordGlobal = getNewAttendeePassword;
    window.logoutAttendeeGlobal = logoutAttendee;
    window.uploadUserList = uploadUserList;
    window.downloadAllQrCodes = downloadAllQrCodes;
    window.downloadQR = downloadQR;

    window.clickCreateAttendee = clickCreateAttendee;
    window.clickEditAttendee = clickEditAttendee;
    window.closeCreateAttendee = closeCreateAttendee;
    window.closeEditAttendee = closeEditAttendee;
    window.closeErrorDialog = closeErrorDialog;

    //Add listeners to buttons/dropdown menus that don't need to be generated dynamically
    document.getElementById("sortingOptions").addEventListener("change", changeSort, false);
    document.getElementById("upUserList").addEventListener("change", handleListUpload, false);


    /* //Global functions for click events
    window.clickCreateAttendeeGlobal = clickCreateAttendee;
    window.clickEditAttendeeGlobal = clickEditAttendee; */


    //Load the page
    refresh();
});

// By default, always sort by attendee Name
var sortingRelation = 'attendeeName';

// The current state of the attendee list locally, used for edit commands
var localAttendeeList;

// ID the attendeeList will be pasted into
const attendeeContainer = $('#attendeeList');


//----------------------------------- SENDING REQUEST AND SORTING METHODS ----------------------------------------------


/**
 * Sends a request to the server to get the current information about each and every attendee in the database.
 *
 * @return a list of attendee objects
 */
function updateAttendeeList(){
    function success(packet){
        if(packet.result === "Valid"){
            sortAttendeeList(packet.attendees);
        }
        else{
            alert(packet.details);
        }
    }

    function fail() {
        alert("Something went wrong while trying to access the server.");
    }

    const requestPacket = new GetAllAttendeesRequestPacket();

    CommunicationManager.send(requestPacket, success, fail);
}

/**
 * Gets called by updateAttendeeList to sort the entries before printing them. Calls {@link getSortedList} from attendeeSorting,
 * uses the current {@link sortingRelation}. Calls {@link generateAttendeeList} after sorting.
 *
 * @param attendeeList that needs to be sorted.
 */
function sortAttendeeList(attendeeList){

    //Calls getSortedList from attendeeSorting.js
    const sortedList = getSortedList(attendeeList, sortingRelation);

    //Update local list right before rendering
    localAttendeeList = sortedList;
    generateAttendeeList(sortedList);
}


/**
 * Gets called from changeSort hook when dropdown option in user_management.html gets changed (Listener for that dropdown
 * menu got added in ready method of the document)
 *
 * @param relation to be sorted by.
 */
function sort(relation){
    //Sets general sorting relation to the new one
    sortingRelation = relation;

    //Refreshes page so list gets rendered with new sorting relation
    refresh();
}


//---------------------------------- RENDERING ATTENDEE LIST METHODS ---------------------------------------------------

/**
 * Gets called whenever the attendee list needs to reload.
 */
function refresh(){
    updateAttendeeList();
}

/**
 * Inserts the list of attendees into the ID given by the global variable {@link attendeeContainer }, in a tabular format.
 * Calls {@link generateAttendee } for each attendee inside the given list to get the HTML code for the table entry of that
 * particular attendee.
 *
 * @param attendeeList - Current list of attendees that shall be pasted inside the table
 */
function generateAttendeeList(attendeeList){

    //Replaces old list content with empty HTML
    $('#attendeeList').empty();

    //Generates new list content
    for (var currIndex = 0; currIndex < attendeeList.length; currIndex++){
        generateAttendee(currIndex, attendeeList[currIndex]).appendTo(attendeeContainer);

        //giving the edit button of the just created attendee functionality
        $('#editAttendee' + currIndex).on("click", function () {
            editedAttendeeIndex = this.getAttribute("data-attendee-id");
            const currAttendee = attendeeList[editedAttendeeIndex];

            editNameID.val(currAttendee.name);
            editMailID.val(currAttendee.email);

            editGroupID.val(currAttendee.group);
            //Pastes existing groups into data list of group input field
            getExistingGroups(true);

            editResidenceID.val(currAttendee.residence);
            editFunctionID.val(currAttendee.function);

            editDialog.dialog("open");
        });
    }

}

/**
 * Gets called by {@link generateAttendeeList } to deliver the data of the given attendee in a certain HTML format.
 * Each call creates one table entry.
 *
 * @param listIndex tells the index of the attendee inside the list (which is important to get their data for editing later on)
 * @param attendee that shall be printed
 * @return {jQuery|HTMLElement} - HTML code for the table entry of that attendee
 */
function generateAttendee(listIndex, attendee){
    return  $('<tr data-toggle="collapse" data-target="#user_accordion'+ listIndex +'" class="clickable">'+
        '<td>'+attendee.name+'</td>'+
        '<td>'+attendee.group+'</td>'+
        '<td>'+attendee.function+'</td>'+
        '<td>'+attendee.present+'</td>'+
        '</tr>'+
        '<tr>'+
        '<td colspan="4">'+
        '<div id="user_accordion'+ listIndex +'"  class="collapse">'+
        '<h4 style="color:grey;">Username: '+attendee.userName +'</h4>'+
        '<h4 style="color:grey;">Email: '+attendee.email+'</h4>'+
        '<h4 style="color:grey;">Residence: '+attendee.residence+'</h4>'+
        '<span style="display:inline-block; width: 30px;">' +
        '</span><span class="glyphicon glyphicon-pencil" id="editAttendee' + listIndex + '" data-attendee-id="' + listIndex + '"></span>'+
        '<span style="display:inline-block; width: 30px;">'+
        '</span><span class="glyphicon glyphicon-lock" onclick="getNewAttendeePasswordGlobal(' + listIndex +')"></span>'+
        '<span style="display:inline-block; width: 30px;">' +
        '</span><span class="glyphicon glyphicon-log-out" onclick="logoutAttendeeGlobal(' + listIndex +')"></span>'+
        '<span style="display:inline-block; width: 30px;">'+
        '</span><span class="glyphicon glyphicon-trash" onclick="deleteAttendeeGlobal(' + listIndex +')"></span>' +
         '<span style="display:inline-block; width: 30px;">'+
        '</span><span class="glyphicon glyphicon-qrcode" onclick="downloadQR(' + listIndex +')"></span>' +
        '</div>'+
        '</td>'+
        '</tr>');
}

/*
function addIconListeners(attendee) {
    document.getElementById("newPassword" + attendee.ID).addEventListener("click", clickNewPassword, false);
    document.getElementById("logout" + attendee.ID).addEventListener("click", clickLogout, false);
    document.getElementById("delete" + attendee.ID).addEventListener("click", clickDelete, false);
} */



//-------------------------------- FUNCTIONS TO BE CALLED BY GLYPHICONS ------------------------------------------------

/**
*
*/
function downloadQR(attendeeIndex){
    const attendeeID = localAttendeeList[attendeeIndex].ID;
    const packet = new DownloadQRRequestPacket(attendeeID);
    requestAndHandleFileDownload(packet);
}

function downloadAllQrCodes(){
    const packet = new DownloadAllQRRequestPacket();
    requestAndHandleFileDownload(packet);
}

/**
 * //TODO move to communication lib
 * This method sends a packet capable of requesting a file download and downloads the file received on success.
 * @param packet the packet to be sent
 * @param _success success hook which should be executed after the file download completed
 * @param _fail failure hook which should be executed if the file download failed
 */
function requestAndHandleFileDownload(packet, _success = (packet) => {}, _fail = () => {}){
    function success(packet) {
        if(packet.result === "Valid") {
            var bytes = new Uint8Array(packet.fileBytes);

            var blob=new Blob([bytes]);

            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download=packet.fileName;
            link.click();
            _success(packet);
        } else {
            _fail();
        }
    }

    function fail() {
        _fail();
    }

    // Send the request to the server
    CommunicationManager.send(packet, success, fail);
}

/**
 *
 */
function uploadUserList() {
    document.getElementById('upUserList').click();
}
/*
 *
 */
function handleListUpload(event) {
    let files = event.target.files;
    if (files[0].name.split(".").pop().localeCompare("csv") === 0) {
        var file = files[0];
        if (file) {
            var reader = new FileReader();
            var text = "";
            reader.onload = function (evt) {
                text = evt.target.result;
                parseUsers(text);
            };
            reader.onerror = function(event) {
                console.error("File could not be read");
            };
            reader.readAsText(file, "UTF-8");
        } else {
            console.error("Something went terribly wrong.");
        }
    } else {
        alert("Wrong File Extension. Only .csv files allowed.")
    }


    function parseUsers(text) {
        let packet = new AddMultipleAttendeesRequestPacket();
        const attendeeField = text.split(/\r?\n/);


        //Empty error handling dialog
        $('#fileErrorDialogContent').empty();

        var correctData = true;

        for(var i = 0; i < attendeeField.length; i++){
            //skip empty lines!
            if(attendeeField[i] === ""){
                continue;
            }
            var currAttendeeData = attendeeField[i].split(":");

            //adding line count to the data
            currAttendeeData.push(i);
            correctData = correctData && checkValidFileData.apply(this, currAttendeeData);

            //line count will get cut off after to send the request without
            currAttendeeData.pop();
            packet.addAttendee.apply(packet, currAttendeeData);
        }

        //attendeeField.forEach(entry => packet.addAttendee.apply(packet, entry.split(":")));

        if(correctData){
            CommunicationManager.send(packet, success, fail);
        }


        function success(packet){
            if(packet.result === "Valid"){
                refresh();
            }
        }

        function fail(){
            alert("Something went wrong while trying to access the server.");
        }
    }

}

/**
 * Can be called by "onclick" param of glyphicon for each attendee, sends request to the server to delete the attendee
 * with the index corresponding to the given listIndex. In case the deleting went on successfully, the attendee list will
 * reload.
 *
 * @param attendeeIndex: list index of the attendee that is to be deleted.
 */
function deleteAttendee(attendeeIndex){
    const attendeeID = localAttendeeList[attendeeIndex].ID;

    const requestPacket = new RemoveAttendeeRequestPacket(attendeeID);

    function successDeleteAttendee(packet){
        if(packet.result === "Valid"){
            refresh();
        }
        else{
            //Print alert if deletion is not successful
            alert(packet.details);
        }
    }

    function failDeleteAttendee(){
        alert("Something went wrong while trying to access the server.");
    }

    CommunicationManager.send(requestPacket, successDeleteAttendee, failDeleteAttendee);

}


/**
 * Sends an EditAttendeeRequest to the server. If the operation was successful, the attendee list will reload.
 *
 * @param attendeeIndex represents the list index of the attendee that is to edit
 * @param name represents the (new) name of the attendee
 * @param email represents the (new) email of the attendee
 * @param group represents the (new) group of the attendee
 * @param residence represents the (new) residence of the attendee
 * @param fnctn represents the (new) function of the attendee
 * @param present represents the current present status of the attendee
 */

function editAttendee(attendeeIndex, name, email, group, residence, fnctn, present){
    const attendeeID = localAttendeeList[attendeeIndex].ID;

    const editRequestPacket = new EditUserRequestPacket(attendeeID, name, email, group, residence, fnctn);
    const editPresenceRequestPacket = new SetAttendeePresentStatusRequestPacket(attendeeID, present);

    function successEditAttendee(packet){
        if(packet.result === "Valid"){
            CommunicationManager.send(editPresenceRequestPacket, successEditPresence, failEditAttendee);
        }
        else{
            alert(packet.details);
        }
    }

    function successEditPresence(packet){
        if(packet.result === "Valid"){
            refresh();
        }
        else{
            alert(packet.details);
        }
    }

    function failEditAttendee(){
        alert("Something went wrong while trying to access the server.")
    }

    CommunicationManager.send(editRequestPacket, successEditAttendee, failEditAttendee);
}


/**
 * Sends a request to create a new attendee using the data given below. When the operation was successful, the attendee list will
 * reload.
 *
 * @param name of the attendee
 * @param email of the attendee
 * @param group of the attendee
 * @param residence of the attendee
 * @param fnctn of the attendee
 */
function createAttendee(name, email, group, residence, fnctn){
    const createRequestPacket = new AddAttendeeRequestPacket(name, email, group, residence, fnctn);

    function successCreateAttendee(packet) {
        if (packet.result === "Valid"){
            refresh();
        }
        else{
            alert(packet.details);
        }
    }

    function failCreateAttendee(){
        alert("Something went wrong while trying to access the server.");
    }

    CommunicationManager.send(createRequestPacket, successCreateAttendee, failCreateAttendee);
}

/**
 * Sends a request to logout an attendee. Note that after destroying the old token, a new one has to be generated. To
 * prevent as much concurrency as possible between sent requests, "Successful" message only gets sent after successfully
 * generating a new token.
 *
 * @param attendeeIndex - List index of the attendee that is to be logged out
 */
function logoutAttendee(attendeeIndex){
    const attendeeID = localAttendeeList[attendeeIndex].ID;

    const logoutRequestPacket = new LogoutAttendeeRequestPacket(attendeeID);
    const newTokenRequestPacket = new GenerateNewAttendeeTokenRequestPacket(attendeeID);

    function successNewToken(packet){
        if(packet.result === "Valid"){
            alert("Attendee has successfully been logged out!");
        } else{
            alert(packet.details);
        }
    }

    function successLogoutAttendee(packet){
        if(packet.result === "Valid"){
            CommunicationManager.send(newTokenRequestPacket, successNewToken, failLogoutAttendee);
        }
        else{
            alert(packet.details);
        }
    }

    function failLogoutAttendee(){
        alert("Something went wrong while trying to access the server.");
    }

    CommunicationManager.send(logoutRequestPacket, successLogoutAttendee, failLogoutAttendee);
}


/**
 * Gets called when a new password shall be generated for a certain attendee. Note that after generating a new password,
 * a separate request to get the password has to be sent.
 *
 * @param attendeeIndex - List index of the attendee for which a new password shall be generated
 */
function getNewAttendeePassword(attendeeIndex){
    const attendeeID = localAttendeeList[attendeeIndex].ID;

    const newPasswordRequestPacket = new GenerateNewAttendeePasswordRequestPacket(attendeeID);
    const getPasswordRequestPacket = new GetAttendeePasswordRequestPacket(attendeeID);


    function successGetPassword(packet){
        if(packet.result === "Valid"){
            alert("New Attendee Password: " + packet.password);
        } else{
            alert(packet.details);
        }
    }

    function successNewPassword(packet){
        if(packet.result === "Valid"){
            CommunicationManager.send(getPasswordRequestPacket, successGetPassword, failNewPassword);
        }
        else{
            alert(packet.details);
        }
    }

    function failNewPassword(){
        alert("Something went wrong while trying to access the server.");
    }

    CommunicationManager.send(newPasswordRequestPacket, successNewPassword, failNewPassword);
}


/**
 * Gets called whenever the list of existing groups needs to be pasted into the datalist of a Creating/Editing dialog.
 *
 * @param editing - Should the list be pasted into the editing window? (false defaults to the creation window)
 */
function getExistingGroups(editing){
    const requestPacket = new GetExistingGroupsRequestPacket();

    function successGetGroups(packet){

        if(packet.result === "Valid"){
            if(editing){
                updateEditGroupList(packet.groups);
            } else{
                updateCreateGroupList(packet.groups);
            }
        } else{
            alert(packet.details);
        }
    }

    function failGetGroups(){
        alert("Something went wrong while trying to access the server.");
    }

    CommunicationManager.send(requestPacket, successGetGroups, failGetGroups);
}



//----------------------------------------- HOOKS FOR ONCLICK EVENTS ---------------------------------------------------

/**
 * Hook that gets called by EventListener on Dropdown menu
 */
function changeSort(){
    const selectedOption = this.options[this.selectedIndex].value;

    sort(selectedOption);
}


//------------------------------------ CREATE DIALOG WINDOW FUNCTIONALITY ----------------------------------------------

// Variable specifying the dialog window
var createDialog;

// Variable specifying the submit form
var createForm;

//Variable specifying the uploading dialog
var fileDialog;


// Variables needed for the creation of a new attendee
var newNameID = $('#createName'),
    newMailID = $('#createEmail'),
    newGroupID = $('#createGroup'),
    newResidenceID = $('#createResidence'),
    newFunctionID = $('#createFnctn'),
    createFields = $( [] ).add(newNameID).add(newMailID).add(newGroupID).add(newResidenceID).add(newFunctionID),
    createTipField = $('#creationTips');

//Mail regex from https://www.w3resource.com/javascript/form/email-validation.php
const mailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

//Name Regex just excluding letters that are not allowed for usernames
const nameRegex = /[^\$%\^\*£=~@_]/;

function updateTips(tipID, newText){
    tipID.empty();
    tipID.text(newText);
}

function alertTips(line, newText){
    //Line index in file beginning from 1!
    line++;

    $('#fileErrorDialogContent').append("Error in line " + line + ": " + newText).append("<br>");
}

function checkLength(checkedObject, fieldName, min, max, tipID){
    if(checkedObject.val().length > max || checkedObject.val().length < min){
        updateTips(tipID, "Length of " + fieldName + " must be between " + min + " and " + max + " characters.");
        return false;
    }
    return true;
}


function checkLengthString(checkedString, fieldName, min, max, line){
    if(checkedString.length > max || checkedString.length < min){
        alertTips(line, "Length of " + fieldName + " must be between " + min + " and " + max + " characters.");
        return false;
    }
    return true;
}

function checkRegex(checkedObject, regex, message, tipID){
    if( !( regex.test(checkedObject.val()) ) ){
        updateTips(tipID, message);
        return false;
    }
    return true;
}

function checkRegexString(checkedString, regex, message, line){
    if( !( regex.test(checkedString) ) ){
        alertTips(line, message);
        return false;
    }
    return true;
}

function checkValidData(nameID, mailID, groupID, residenceID, functionID, tipID){
    var validUser = true;

    validUser = validUser && checkLength(nameID, "name", 1, 64, tipID);
    validUser = validUser && checkLength(mailID, "email", 5, 64, tipID);
    validUser = validUser && checkLength(groupID, "group", 1, 64, tipID);
    validUser = validUser && checkLength(residenceID, "residence", 1, 256, tipID);
    validUser = validUser && checkLength(functionID, "function", 1, 64, tipID);

    validUser = validUser && checkRegex(nameID, nameRegex, "Name mustn't contain $%^*£=~@_", tipID);
    validUser = validUser && checkRegex(mailID, mailRegex, "Invalid mail. Example for a valid mail: user@domain.com", tipID);
    validUser = validUser && checkRegex(groupID, nameRegex, "Group mustn't contain $%^*£=~@_", tipID);
    validUser = validUser && checkRegex(residenceID, nameRegex, "Residence mustn't contain $%^*£=~@_", tipID);
    validUser = validUser && checkRegex(functionID, nameRegex, "Function mustn't contain $%^*£=~@_", tipID);

    return validUser;
}

//TODO
function checkValidFileData(name, mail, group, residence, fun, line){
    var validUser = true;

    validUser = validUser && checkLengthString(name, "name", 1, 64, line);
    validUser = validUser && checkLengthString(mail, "email", 5, 64, line);
    validUser = validUser && checkLengthString(group, "group", 1, 64, line);
    validUser = validUser && checkLengthString(residence, "residence", 1, 256, line);
    validUser = validUser && checkLengthString(fun, "function", 1, 64, line);

    validUser = validUser && checkRegexString(name, nameRegex, "Name mustn't contain $%^*£=~@_", line);
    validUser = validUser && checkRegexString(mail, mailRegex, "Invalid mail. Example for a valid mail: user@domain.com", line);
    validUser = validUser && checkRegexString(group, nameRegex, "Group mustn't contain $%^*£=~@_", line);
    validUser = validUser && checkRegexString(residence, nameRegex, "Residence mustn't contain $%^*£=~@_", line);
    validUser = validUser && checkRegexString(fun, nameRegex, "Function mustn't contain $%^*£=~@_", line);

    if(!validUser){
        fileDialog.open();
    }

    return validUser;
}

/**
 * Gets called when clicking the confirm button of the creation dialog
 */
function clickCreateAttendee(){
    if(checkValidData(newNameID, newMailID, newGroupID, newResidenceID, newFunctionID, createTipField)){
        createDialog.dialog("close");

        createAttendee(newNameID.val(),
            newMailID.val(),
            newGroupID.val(),
            newResidenceID.val(),
            newFunctionID.val());
    }
}


//------------------------------------- EDIT DIALOG WINDOW FUNCTIONALITY -----------------------------------------------

var editDialog, editForm;
var editedAttendeeIndex;

var editNameID = $('#editName'),
    editMailID = $('#editEmail'),
    editGroupID = $('#editGroup'),
    editResidenceID = $('#editResidence'),
    editFunctionID = $('#editFnctn'),
    editTipField = $('#editingTips'),
    editFields = $( [] ).add(editNameID).add(editMailID).add(editGroupID).add(editResidenceID).add(editFunctionID);

/**
 *
 */
function clickEditAttendee(){
    if(checkValidData(editNameID, editMailID, editGroupID, editResidenceID, editFunctionID, editTipField)){
        editDialog.dialog("close");

        const editPresentID = $('input[name="edit-yes-no"]:checked');

        // Has to be compared like this!! === always results in false
        const present = editPresentID.val() == 1;

        editAttendee(editedAttendeeIndex,
            editNameID.val(),
            editMailID.val(),
            editGroupID.val(),
            editResidenceID.val(),
            editFunctionID.val(),
            present);
    }
}

function closeCreateAttendee(){
    createDialog.dialog("close");
}

function closeEditAttendee(){
    editDialog.dialog("close");
}

function closeErrorDialog(){
    fileDialog.dialog("close");
}


//------------------------------ EXISTING GROUP LIST FUNCTIONALITY -----------------------------------------------------


function updateCreateGroupList(groups){
    const groupListID = $('#existingCreateGroups');
    printGroupList(groupListID, groups);
}


function updateEditGroupList(groups){
    const groupListID = $('#existingEditGroups');
    printGroupList(groupListID, groups);
}

/**
 * Prints a given group list into a datalist with given ID using HTML code; Deletes old HTML code inside the datalist
 *
 * @param listID - ID of the datalist
 * @param groups - List of the groups currently existing
 */
function printGroupList(listID, groups){
    listID.empty();

    for(var currGroup of groups){
        listID.append('<option value="' + currGroup + '">');
    }
}