import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class PersonalDataRequestPacket extends AuthenticatedRequestPacket {

    constructor(name) {
        super("DOWNLOAD_FILE_REQUEST");
        this.name = name;
    }
}
