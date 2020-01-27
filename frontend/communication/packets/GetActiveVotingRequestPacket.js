import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class GetActiveVotingRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_ACTIVE_VOTING_REQUEST");
    }
}
