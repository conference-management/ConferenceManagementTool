import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class StartVotingRequestPacket extends AuthenticatedRequestPacket {

    constructor(id) {
        super("START_VOTING_REQUEST");
        this.id = id;
    }
}
