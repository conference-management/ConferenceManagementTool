import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class AddTopicRequestPacket extends AuthenticatedRequestPacket {

    constructor(position, name) {
        super("ADD_TOPIC_REQUEST");
        this.position = position;
        this.name = name;
    }
}
