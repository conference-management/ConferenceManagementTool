import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class EditVotingRequestPacket extends AuthenticatedRequestPacket {

    constructor(id, question = null, options = null, namedVote = null, duration = 0) {
        super("EDIT_VOTING_REQUEST");
	this.id = id;
        this.question = question;
        this.options = options;
	this.duration = duration;
    }
}
