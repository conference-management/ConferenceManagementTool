import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class LogoutAttendeeRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("LOGOUT_ATTENDEE_REQUEST");
        this.id = userID;
    }
}
