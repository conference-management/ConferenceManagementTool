import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class DownloadQRRequestPacket extends AuthenticatedRequestPacket {

    constructor(userID) {
        super("DOWNLOAD_QR_REQUEST");
        this.id = userID;
    }
}
