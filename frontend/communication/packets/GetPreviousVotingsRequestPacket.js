import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class GetPreviousVotingsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_PREVIOUS_VOTINGS_REQUEST");
    }
}
