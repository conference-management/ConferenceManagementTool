package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.GetActiveVotingResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import voting.Voting;

/**
 * This packet can be used by an attendee to retrieve the currently active voting.
 * Responds with a {@link GetActiveVotingResponsePacket}.
 */
public class GetActiveVotingRequestPacket extends AuthenticatedRequestPacket {

    public GetActiveVotingRequestPacket() {
        super(PacketType.GET_ACTIVE_VOTING_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            Voting voting = conference.getActiveVoting();
            Packet response;
            if(voting != null) {
                response = new GetActiveVotingResponsePacket(voting);
            } else {
                response = new GetActiveVotingResponsePacket();
            }
            response.send(connection);
        }
    }
}
