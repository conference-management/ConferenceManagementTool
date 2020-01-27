package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import utils.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This packet can be used by an admin to update a file (document) i.e. a creation, replacement of a file.
 * There is no actual file data contained in this packet since on success, this packet initiates a protocol change
 * to communicate using bytes instead of packets. After that, the file bytes may be sent.
 * Responds with a {@link communication.packets.BasePacket} with the {@link PacketType#UPDATE_FILE_RESPONSE} after the protocol change
 * and with a {@link ValidResponsePacket} after the actual file upload completed.
 */
public class UpdateFileRequestPacket extends AuthenticatedRequestPacket {

    private static ReentrantLock lock = new ReentrantLock();
    private static HashMap<Connection, Pair<UpdateFileRequestPacket, Long>> allowedRequests = new HashMap<>();

    private String name;
    private String originalName;
    private boolean creation;

    public UpdateFileRequestPacket(String name, String originalName, boolean creation) {
        super(PacketType.UPDATE_FILE_REQUEST);
        this.name = name;
        this.originalName = originalName;
        this.creation = creation;
    }

    public static UpdateFileRequestPacket getRequestFromConnectionIfExists(Connection connection) {
        try {
            lock.lock();
            removeInvalidRequests();
            if(allowedRequests.containsKey(connection)) {
                UpdateFileRequestPacket packet = allowedRequests.get(connection).first();
                allowedRequests.remove(connection);
                return packet;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private static void removeInvalidRequests() {
        try {
            lock.lock();
            new HashSet<>(allowedRequests.keySet()).forEach(key -> {
                if(allowedRequests.get(key).second() < System.currentTimeMillis()) {
                    allowedRequests.remove(key);
                }
            });
        } finally {
            lock.unlock();
        }
    }

    public static boolean existingRequest(Connection connection) {
        try {
            lock.lock();
            return allowedRequests.containsKey(connection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            try {
                lock.lock();
                allowedRequests.put(connection, new Pair<>(this, System.currentTimeMillis() + 1000 * 60 * 60 * 24));
            } finally {
                lock.unlock();
            }
            new ValidResponsePacket(PacketType.UPDATE_FILE_RESPONSE).send(connection);
        }
    }

    public void handleFileTransfer(Conference conference, Connection connection, File file) {
        try {
            if(isPermitted(conference, connection, true)) {
                String fileType = "";
                String[] split = name.split("\\.");
                if(split.length >= 2) {
                    fileType = "." + split[split.length - 1];
                }
                if(creation) {
                    conference.updateDocument(name, fileType, file, creation);
                } else {
                    conference.updateDocument(originalName, fileType, file, creation);
                }
                new ValidResponsePacket().send(connection);
            }
        } catch (IllegalArgumentException e) {
            new FailureResponsePacket(e.getMessage());
        }
    }
}
