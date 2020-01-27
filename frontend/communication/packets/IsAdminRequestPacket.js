import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class IsAdminRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("IS_ADMIN_REQUEST");
    }
}
