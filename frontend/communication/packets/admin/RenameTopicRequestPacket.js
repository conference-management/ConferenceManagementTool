import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class RenameTopicRequestPacket extends AuthenticatedRequestPacket {

    /**
     *
     * @param position the position of the topic as string (e.g. "1.4.3")
     * @param name the new name for the topic
     */
    constructor(position, name) {
        super("RENAME_TOPIC_REQUEST");
        this.position = position;
        this.name = name;
    }
}
