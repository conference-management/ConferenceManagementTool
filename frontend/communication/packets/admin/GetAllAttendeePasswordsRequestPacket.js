import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetAllAttendeePasswordsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_ALL_ATTENDEE_PASSWORDS");
    }
}
