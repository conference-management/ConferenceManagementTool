package communication.packets;

import communication.enums.PacketType;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.InvalidTokenResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.TokenResponse;

/**
 * A superclass for packets which require the client to be an authenticated user.
 */
public abstract class AuthenticatedRequestPacket extends RequestPacket {

    private String token;

    public AuthenticatedRequestPacket(PacketType packetType) {
        super(packetType);
    }

    /**
     * This method has the side effects, that it sends a {@link FailureResponsePacket} or {@link FailureResponsePacket}
     * if the request was not successful.
     *
     * @param conference     the conference to check permissions for
     * @param socket         the socket to cause side effects on
     * @param adminOperation weather to check if the token provides {@link TokenResponse#ValidAdmin}
     *
     * @return true iff the requesting client has the necessary permissions for the desired request
     */
    public boolean isPermitted(Conference conference, Connection socket, boolean adminOperation) {
        TokenResponse value = conference.checkToken(getToken());
        if(value == TokenResponse.ValidAdmin || (value == TokenResponse.ValidAttendee && !adminOperation)) {
            return true;
        } else {
            new InvalidTokenResponsePacket().send(socket);
            return false;
        }
    }

    /**
     * @return An authentication token provided by the sender.
     */
    public String getToken() {
        return token;
    }

    public AuthenticatedRequestPacket setToken(String token) {
        this.token = token;
        return this;
    }
}
