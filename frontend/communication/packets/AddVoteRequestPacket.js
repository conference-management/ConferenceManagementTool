import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class AddVoteRequestPacket extends AuthenticatedRequestPacket {

    constructor(voteID, optionID) {
        super("ADD_VOTE_REQUEST");
        this.voteID = voteID;
        this.optionID = optionID;
    }
}
