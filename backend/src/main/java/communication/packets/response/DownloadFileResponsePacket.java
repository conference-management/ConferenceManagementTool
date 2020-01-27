package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

public class DownloadFileResponsePacket extends ResponsePacket {

    @Expose
    private byte[] fileBytes;
    @Expose
    private String fileName;

    /**
     * @param fileBytes the file bytes of the file (document)
     * @param fileName  the name of the file (document)
     */
    public DownloadFileResponsePacket(byte[] fileBytes, String fileName) {
        super(PacketType.DOWNLOAD_FILE_RESPONSE, RequestResult.Valid);
        this.fileBytes = fileBytes;
        this.fileName = fileName;
    }
}
