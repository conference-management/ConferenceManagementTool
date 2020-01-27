import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class UpdateFileRequestPacket extends AuthenticatedRequestPacket {

    /**
     *
     * @param name the display name of the uploaded file
     * @param originalName the original name of the uploaded file
     * @param fileBytes an ArrayBuffer of the file's content
     */
    constructor(name, originalName, fileBytes, creation) {
        super("UPDATE_FILE_REQUEST");
        this.name = name;
        this.originalName = originalName;
        this.fileBytes = fileBytes;
        this.creation = creation;
    }
}
