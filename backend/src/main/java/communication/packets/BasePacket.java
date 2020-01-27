package communication.packets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.wrapper.Connection;

/**
 * An implementation of {@link Packet} which introduces an constructor, forcing subclasses to specify their {@link PacketType}.
 */
public class BasePacket implements Packet {

    @Expose
    private PacketType packetType;

    /**
     * Initializes the packet by setting it's {@link PacketType}.
     *
     * @param packetType the {@link PacketType} of this packet
     */
    public BasePacket(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void send(Connection socket) {
        socket.send(toJson());
    }

    /**
     * Converts this packet to a JSON equivalent.
     *
     * @return JSON String of this packet
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

}
