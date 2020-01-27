import {ip, port, useSSL} from "../config/config.js"

/**
 * A class used for communication with the backend using the connection defined in config.js.
 */
export default class CommunicationManager {


    /**
     * Opens a connection to the backend defined in config.js.
     * @returns {WebSocket}
     */
    static openConnection() {
        var socket;
        var address;
        if(useSSL) {
            address = "wss://"
        } else {
            address = "ws://"
        }
        const endpoint = "websocket";
        address = address + ip + ":" + port + "/" + endpoint;
        socket = new WebSocket(address);
        return socket;
    }

    /**
     * Sends a packet to the backend and then executes the successHook iff the communication was successful, the errorHook otherwise.
     * @param packet the packet to be sent
     * @param successHook a function which is executed after successful communication with a JSON object as parameter
     * @param errorHook a function which is executed after failed communication
     */
    static send(packet, successHook = (packet) => {}, errorHook = () => {}) {
        var socket = CommunicationManager.openConnection();
        var fileName = null;
        function onmessage(event) {
            if(!(typeof event.data === "string")) {
                if(fileName == null) {
                    return;
                }
                var link=document.createElement('a');
                link.href=window.URL.createObjectURL(event.data);
                link.download=fileName;//packet.fileName;
                link.click();
                return;
            }
            let responsePacket = JSON.parse(event.data);
            if(responsePacket.packetType === "DOWNLOAD_FILE_RESPONSE") {
                if(responsePacket.fileBytes == null) {
                    fileName = responsePacket.fileName;
                    return;
                }
            }
            successHook(responsePacket);
            socket.onclose = function() {};
            socket.close();
        };
        //in case a update file request has been sent, the fileByte upload should start after confirmation from the backend
        if(packet.packetType === "UPDATE_FILE_REQUEST") {
            let fileBytes = packet.fileBytes;
            packet.fileBytes = null;
            socket.onmessage = function (event) {
                var responsePacket = JSON.parse(event.data);
                if(responsePacket.result === "Valid") {
                    socket.onmessage = onmessage;
                    socket.send(fileBytes);
                } else {
                    onmessage(event);
                }
            }
        } else {
            socket.onmessage = onmessage;
        }
        socket.onopen = () => {
            socket.send(JSON.stringify(packet));
        };
        socket.onclose = errorHook;
        socket.onerror = errorHook;
    }
}
