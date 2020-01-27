import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GenerateMissingAttendeePasswordsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GENERATE_MISSING_ATTENDEE_PASSWORDS");
    }
}
