import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetVotingsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_VOTINGS_REQUEST");
    }
}
