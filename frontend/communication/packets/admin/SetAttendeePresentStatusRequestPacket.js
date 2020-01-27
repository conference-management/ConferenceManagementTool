import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class SetAttendeePresentStatusRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID, present) {
        super("SET_ATTENDEE_PRESENT_STATUS_REQUEST");
        this.id = userID;
	this.present = present;
    }
}
