package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import voting.AnonymousVotingOption;
import voting.NamedVotingOption;
import voting.Voting;
import voting.VotingOption;

import java.util.LinkedList;
import java.util.List;

/**
 * This packet can be used by an admin to add a voting to the conference. This voting is not started until
 * a {@link StartVotingRequestPacket} is received. Responds with a {@link communication.packets.BasePacket}.
 */
public class AddVotingRequestPacket extends AuthenticatedRequestPacket {

    private String question;
    private List<String> options;
    private boolean namedVote;
    private int duration;

    /**
     * @param question  the question which should be decided by the voting
     * @param options   the possible voting options as a list of strings
     * @param namedVote if the vote is named i.e. if it is public who voted for which option
     * @param duration  the duration of the voting after it started in seconds
     */
    public AddVotingRequestPacket(String question, List<String> options, boolean namedVote, int duration) {
        super(PacketType.ADD_VOTING_REQUEST_PACKET);
        this.question = question;
        this.options = options;
        this.namedVote = namedVote;
        this.duration = duration;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            VotingOption votingOptionObject;
            List<VotingOption> optionsObjectList = new LinkedList<>();
            int id = 0;
            for(String option : options) {
                if(namedVote) {
                    votingOptionObject = new NamedVotingOption(id, option);
                } else {
                    votingOptionObject = new AnonymousVotingOption(id, option);
                }
                optionsObjectList.add(votingOptionObject);
                id++;
            }
            Voting voting = new Voting(optionsObjectList, question, namedVote, duration);
            conference.addVoting(voting);
            new ValidResponsePacket().send(connection);
        }
    }
}
