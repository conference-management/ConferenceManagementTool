import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GenerateNewAttendeePasswordRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("GENERATE_NEW_ATTENDEE_PASSWORD");
        this.id = userID;
    }
}
