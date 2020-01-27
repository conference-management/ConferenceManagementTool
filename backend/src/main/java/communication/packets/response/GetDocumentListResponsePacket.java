package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import document.Document;

import java.util.List;

public class GetDocumentListResponsePacket extends ResponsePacket {

    @Expose
    private List<Document> documents;

    /**
     * @param documents a list of documents object containing following exposed attributes
     *                  name: String - the name of the document
     *                  revisionNumber: int - the revision number of the document
     */
    public GetDocumentListResponsePacket(List<Document> documents) {
        super(PacketType.GET_DOCUMENT_LIST_RESPONSE, RequestResult.Valid);
        this.documents = documents;
    }
}
