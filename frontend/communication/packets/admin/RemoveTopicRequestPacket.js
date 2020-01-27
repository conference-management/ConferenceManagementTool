import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class RemoveTopicRequestPacket extends AuthenticatedRequestPacket {

    
    constructor(position) {
        super("REMOVE_TOPIC_REQUEST");
        this.position = position;
    }
}
