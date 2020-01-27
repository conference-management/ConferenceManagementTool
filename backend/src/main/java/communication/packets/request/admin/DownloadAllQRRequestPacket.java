package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.DownloadFileResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;

/**
 * This packet can be used by an admin to request to download all QR-Code files available.
 * Responds with a {@link DownloadFileResponsePacket}.
 */
public class DownloadAllQRRequestPacket extends AuthenticatedRequestPacket {

    private int id;

    /**
     * @param id the id of the attendee to download a qr file for
     */
    public DownloadAllQRRequestPacket(int id) {
        super(PacketType.DOWNLOAD_ALL_QR_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Attendee attendee = conference.getAttendeeData(id);
            byte[] qrFile = conference.getAllQrCodes();
            Packet response = new DownloadFileResponsePacket(qrFile, "QR_Codes.zip");
            response.send(connection);
        }
    }
}
