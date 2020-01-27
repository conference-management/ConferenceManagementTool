import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetAttendeeDataRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("GET_ATTENDEE_DATA_REQUEST");
        this.id = userID;
    }
}
