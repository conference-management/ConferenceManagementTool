import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetAttendeePasswordRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("GET_ATTENDEE_PASSWORD_REQUEST");
        this.id = userID;
    }
}
