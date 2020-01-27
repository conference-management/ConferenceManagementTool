package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetVotingsResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import voting.VotingStatus;

import java.util.stream.Collectors;

/**
 * This packet can be used by a user to retrieve all previous votings.
 * Responds with a {@link GetVotingsResponsePacket}.
 */
public class GetPreviousVotingsRequestPacket extends AuthenticatedRequestPacket {

    public GetPreviousVotingsRequestPacket() {
        super(PacketType.GET_PREVIOUS_VOTINGS_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection webSocket) {
        if(isPermitted(conference, webSocket, false)) {
            Packet response = new GetVotingsResponsePacket(conference.getVotings().stream().filter((voting) -> {
                return voting.getStatus() == VotingStatus.Closed;
            }).collect(Collectors.toList()));
            response.send(webSocket);
        }
    }
}
