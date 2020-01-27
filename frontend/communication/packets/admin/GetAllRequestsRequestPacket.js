import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetAllRequestsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_ALL_REQUESTS_REQUEST");
    }
}
