import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class RemoveAttendeeRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("REMOVE_ATTENDEE_REQUEST");
        this.id = userID;
    }
}
