package communication.packets.request;

import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.ResponsePacket;
import communication.packets.response.FailureResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;
import voting.Voting;
import voting.VotingStatus;

/**
 * This packet can be used by an attendee to submit their vote for an ongoing voting.
 * Responds with a {@link communication.packets.ResponsePacket}.
 */
public class AddVoteRequestPacket extends AuthenticatedRequestPacket {

    private int voteID;
    private int optionID;

    /**
     * @param voteID   the ID of the voting the attendee wants to submit a vote for
     * @param optionID the ID of the choice the attendee wants to vote for
     */
    public AddVoteRequestPacket(int voteID, int optionID) {
        super(PacketType.ADD_VOTE_REQUEST);
        this.voteID = voteID;
        this.optionID = optionID;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            Voting voting = conference.getVoting(voteID);
            int userID = conference.tokenToID(getToken());
            Attendee attendee = conference.getAttendeeData(userID);
            if(!attendee.isPresent()) {
                new FailureResponsePacket("You can only vote if you are present.").send(connection);
                return;
            }
            String name = attendee.getName();
            Packet response;
            if(voting.addVote(optionID, userID, name)) {
                response = new ResponsePacket(PacketType.ADD_VOTE_RESPONSE, RequestResult.Valid);
            } else {
                if(voting.getStatus() == VotingStatus.Closed) {
                    response = new FailureResponsePacket("The time for voting is up");
                } else {
                    response = new FailureResponsePacket("Your vote could not be registered. Most likely you have already submitted a vote");
                }
            }
            response.send(connection);
        }
    }
}
