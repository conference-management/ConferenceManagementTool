import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class DownloadAllQRRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("DOWNLOAD_ALL_QR_REQUEST");
    }
}
