package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to delete a file (document) which was priory uploaded. Responds with a {@link communication.packets.BasePacket}.
 */
public class DeleteFileRequestPacket extends AuthenticatedRequestPacket {

    private String name;

    /**
     * @param name the name of the file (document) to be deleted
     */
    public DeleteFileRequestPacket(String name) {
        super(PacketType.DELETE_FILE_REQUEST);
        this.name = name;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.deleteDocument(name);
            new ValidResponsePacket().send(connection);
        }
    }
}
