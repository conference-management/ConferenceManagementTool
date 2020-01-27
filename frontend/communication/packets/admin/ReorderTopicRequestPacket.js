import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class ReorderTopicRequestPacket extends AuthenticatedRequestPacket {

    /**
     *
     * @param oldPosition the old position of the topic as string (e.g. "1.4.3")
     * @param newPosition the new position of the topic in the (sub-)agenda as int
     */
    constructor(oldPosition, name) {
        super("REORDER_TOPIC_REQUEST");
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
}
