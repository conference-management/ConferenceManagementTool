import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class LogoutAllAttendeesRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("LOGOUT_ALL_ATTENDEES");
    }
}
