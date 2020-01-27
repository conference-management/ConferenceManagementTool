import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GenerateNewAttendeeTokenRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("GENERATE_NEW_ATTENDEE_TOKEN");
        this.id = userID;
    }
}
