import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class RequestOfChangeRequestPacket extends AuthenticatedRequestPacket {

    constructor(refersToTopic, reference, request) {
        super("REQUEST_OF_CHANGE_REQUEST");
        this.refersToTopic = refersToTopic;
        this.reference = reference;
        this.request = request;
    }
}
