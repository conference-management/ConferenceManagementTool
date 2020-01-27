import Packet from "./Packet.js";
import Cookies from "../utils/Cookies.js";

export default class AuthenticatedRequestPacket extends Packet{

    constructor(packetType) {
        super(packetType);
        this.token = Cookies.getCookie("token");
    }


}