import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class RequestOfSpeechRequestPacket extends AuthenticatedRequestPacket {

    constructor(refersToTopic, reference) {
        super("REQUEST_OF_SPEECH_REQUEST");
        this.refersToTopic = refersToTopic;
        this.reference = reference;
    }
}
