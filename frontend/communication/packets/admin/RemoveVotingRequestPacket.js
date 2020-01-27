import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class RemoveVotingRequestPacket extends AuthenticatedRequestPacket {

    constructor(id) {
        super("REMOVE_VOTING_REQUEST");
        this.id = id;
    }
}
