package communication.packets.request;

import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.Packet;
import communication.packets.RequestPacket;
import communication.packets.ResponsePacket;
import communication.packets.response.LoginResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.LoginResponse;
import utils.Pair;

/**
 * This packet handles an login request from either an attendee or an admin and responds with an {@link LoginResponsePacket}.
 */
public class LoginRequestPacket extends RequestPacket {


    private String username;
    private String password;

    /**
     * @param username the username to login with
     * @param password the password to use for login
     */
    public LoginRequestPacket(String username, String password) {
        super(PacketType.LOGIN_REQUEST);
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(password == null) {
            password = "";
        }
        Pair<LoginResponse, Pair<String, Long>> result;
        result = conference.login(username, password);

        Packet response;
        if(result.first() == LoginResponse.Valid) {
            response = new LoginResponsePacket(result.second().first(), result.second().second());
        } else {
            response = new ResponsePacket(PacketType.LOGIN_RESPONSE, RequestResult.Failure);
        }
        response.send(connection);
    }
}
