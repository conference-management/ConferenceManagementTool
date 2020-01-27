package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import voting.AnonymousVotingOption;
import voting.NamedVotingOption;
import voting.Voting;
import voting.VotingOption;
import voting.VotingStatus;

import java.util.LinkedList;
import java.util.List;

/**
 * This packet can be used by an admin to edit an existing voting. This packet is refused if the voting already started or ended.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class EditVotingRequestPacket extends AuthenticatedRequestPacket {

    private int id;
    private String question;
    private List<String> options;
    private int duration;

    /**
     * @param question the new question
     * @param options  the new options
     * @param duration the new duration in seconds
     */
    public EditVotingRequestPacket(String question, List<String> options, int duration) {
        super(PacketType.EDIT_VOTING_REQUEST);
        this.question = question;
        this.options = options;
        this.duration = duration;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Voting voting = conference.getVoting(id);
            if(voting.getStatus() != VotingStatus.Created) {
                throw new IllegalArgumentException();
            }
            if(question == null) {
                question = voting.getQuestion();
            }
            if(options == null) {
                options = new LinkedList<>();
                voting.getOptions().forEach(o -> {
                    options.add(o.getName());
                });
            }

            boolean namedVote = voting.isNamedVote();
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

            boolean result = voting.updateVoteArguments(optionsObjectList, question, namedVote, duration);
            if(result) {
                new ValidResponsePacket().send(connection);
            } else {
                new FailureResponsePacket().send(connection);
            }


        }
    }
}
