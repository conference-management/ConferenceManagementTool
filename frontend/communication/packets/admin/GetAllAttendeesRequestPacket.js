import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetAllAttendeesRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_ALL_ATTENDEES_REQUEST");
    }
}
