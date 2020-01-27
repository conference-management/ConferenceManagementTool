import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class PersonalDataRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("PERSONAL_DATA_REQUEST");
    }
}
