import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class GetDocumentListRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_DOCUMENT_LIST_REQUEST");
    }
}