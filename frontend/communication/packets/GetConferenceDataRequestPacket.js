import Packet from "./Packet.js";

export default class GetConferenceDataRequestPacket extends Packet {

    constructor() {
        super("CONFERENCE_DATA_REQUEST");
    }
}
