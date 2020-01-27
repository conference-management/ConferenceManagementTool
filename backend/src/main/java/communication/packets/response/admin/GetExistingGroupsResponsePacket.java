package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

import java.util.List;

public class GetExistingGroupsResponsePacket extends ResponsePacket {

    @Expose
    private List<String> groups;

    /**
     * @param groups the list of unique groups i.e. groups that are assigned to some attendee
     */
    public GetExistingGroupsResponsePacket(List<String> groups) {
        super(PacketType.GET_EXISTING_GROUPS_RESPONSE, RequestResult.Valid);
        this.groups = groups;
    }
}
