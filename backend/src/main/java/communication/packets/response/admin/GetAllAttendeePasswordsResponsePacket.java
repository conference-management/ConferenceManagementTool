package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import user.User;
import utils.Pair;

import java.util.List;

public class GetAllAttendeePasswordsResponsePacket extends ResponsePacket {

    @Expose
    private List<Pair<User, String>> passwords;

    /**
     * @param passwords a pair of all users and their passwords
     *                  The exposed attributes of users are the same as of attendees in {@link communication.packets.response.PersonalDataResponsePacket}.
     */
    public GetAllAttendeePasswordsResponsePacket(List<Pair<User, String>> passwords) {
        super(PacketType.GET_ALL_ATTENDEE_PASSWORDS_RESPONSE, RequestResult.Valid);
        this.passwords = passwords;
    }
}
