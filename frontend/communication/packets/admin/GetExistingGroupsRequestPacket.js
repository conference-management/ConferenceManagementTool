import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class GetExistingGroupsRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_EXISTING_GROUPS_REQUEST");
    }
}
