package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import voting.Voting;
import voting.VotingOption;
import voting.VotingStatus;

/**
 * This packet can be used by an admin to start a voting which not already started or ended.
 * Respond with a {@link communication.packets.BasePacket}.
 */
public class StartVotingRequestPacket extends AuthenticatedRequestPacket {

    private int id;

    /**
     * @param id the id of the voting to be started
     */
    public StartVotingRequestPacket(int id) {
        super(PacketType.START_VOTING_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Packet response;
            Voting votingToStart = conference.getVoting(id);
            if(votingToStart == null) {
                response = new FailureResponsePacket("The voting with the id " + id + " does not exist.");
            } else if(votingToStart.getStatus() != VotingStatus.Created) {
                response = new FailureResponsePacket("Voting could not be started since it's status is " + votingToStart.getStatus());
            } else {
                if(votingToStart.getOptions().size() >= 2) {
                    if(conference.getActiveVoting() != null) {
                        response = new FailureResponsePacket("Can´t start a voting because a vote is already running");
                    } else {
                        Boolean acept = true;
                        for(VotingOption vo : votingToStart.getOptions()) {
                            if(vo.getName() == "") {
                                acept = false;
                            }
                        }
                        if(!acept) {
                            response = new FailureResponsePacket("Can´t start a voting because a vote option is empty");
                        } else {
                            if(conference.startVoting(votingToStart)) {
                                response = new ValidResponsePacket();
                            } else {
                                response = new FailureResponsePacket("Can´t start a voting because some problem occure in Backend");
                            }
                        }

                    }
                } else {
                    response = new FailureResponsePacket("Can´t start a voting with less than 2 options");
                }
            }
            response.send(connection);
        }
    }
}
