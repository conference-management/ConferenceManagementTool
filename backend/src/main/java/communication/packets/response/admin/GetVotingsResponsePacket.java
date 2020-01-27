package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import voting.Voting;

import java.util.List;

public class GetVotingsResponsePacket extends ResponsePacket {

    @Expose
    private List<Voting> votings;

    /**
     * @param votings a list of all votings (see {@link communication.packets.response.GetActiveVotingResponsePacket}) for exposed properties of the {@link Voting} objects
     */
    public GetVotingsResponsePacket(List<Voting> votings) {
        super(PacketType.GET_VOTINGS_RESPONSE, RequestResult.Valid);
        this.votings = votings;
    }
}
