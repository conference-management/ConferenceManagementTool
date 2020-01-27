package communication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import communication.enums.PacketType;
import communication.packets.BasePacket;
import communication.packets.RequestPacket;
import communication.packets.request.*;
import communication.packets.request.admin.*;
import communication.packets.response.FailureResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.TokenResponse;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A handler called from a {@link CommunicationManager} to handle incoming messages.
 */
public class CommunicationHandler {

    private Gson gson = new Gson();
    private Conference conference;
    private int timeoutAfter;
    private boolean debugging;
    private int maxUserConnections;
    /**
     * A service which disconnects connections which exceeded the timeout.
     */
    private ScheduledExecutorService connectionLimitationService;
    private ReentrantLock tokenConnectionMappingLock = new ReentrantLock();
    private HashMap<String, List<Connection>> tokenConnectionsMap = new HashMap<>();
    private HashMap<Connection, String> connectionTokenMap = new HashMap<>();
    private ReentrantLock timeoutLock = new ReentrantLock();
    private HashMap<Connection, Long> timeout = new HashMap<>();

    /**
     * @param conference         the conference the handler operates on
     * @param timeoutAfter       the timout until unauthorized connections should be closed
     * @param maxUserConnections the maximum amount of connections per user
     * @param debugging          if this is a debugging instance
     */
    public CommunicationHandler(Conference conference, int timeoutAfter, int maxUserConnections, boolean debugging) {
        this.conference = conference;
        this.timeoutAfter = timeoutAfter;
        this.debugging = debugging;
        this.maxUserConnections = maxUserConnections;

        connectionLimitationService = Executors.newSingleThreadScheduledExecutor();
        connectionLimitationService.scheduleAtFixedRate(() -> {
            try {
                timeoutLock.lock();
                new HashSet<>(timeout.keySet()).forEach((key) -> {
                    if(timeout.get(key) < System.currentTimeMillis()) {
                        timeout.remove(key);
                        key.close();
                    }
                });
            } finally {
                timeoutLock.unlock();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Handles incoming messages of the type (JSON-)String
     *
     * @param conn    the connections from which the message was received
     * @param message the (JSON-)String
     */
    public void onMessage(Connection conn, String message) {
        try {

            RequestPacket pack;
            PacketType packetType = gson.fromJson(message, BasePacket.class).getPacketType();

            if(debugging && packetType != PacketType.GET_ACTIVE_VOTING_REQUEST && packetType != PacketType.IS_ADMIN_REQUEST) {
                System.out.println(message);
            }

            String token = gson.fromJson(message, SimpleAuthenticatedRequestPacket.class).getToken();

            if(token != null) {
                TokenResponse tokenResponse = conference.checkToken(token);
                if(tokenResponse == TokenResponse.ValidAttendee || tokenResponse == TokenResponse.ValidAdmin) {
                    //removes the timeout since the connection is now authenticated
                    try {
                        tokenConnectionMappingLock.lock();
                        if(!tokenConnectionsMap.containsKey(token)) {
                            tokenConnectionsMap.put(token, new LinkedList<>());
                        }
                        tokenConnectionsMap.get(token).add(conn);
                        connectionTokenMap.put(conn, token);
                        if(tokenConnectionsMap.get(token).size() > maxUserConnections) {
                            conn.close();
                            return;
                        }
                    } finally {
                        tokenConnectionMappingLock.unlock();
                    }
                    try {
                        timeoutLock.lock();
                        timeout.remove(conn);
                    } finally {
                        timeoutLock.unlock();
                    }
                }
            }

            switch(packetType) {
                /* ADMIN PACKETS */
                case ADD_ATTENDEE_REQUEST:
                    pack = gson.fromJson(message, AddAttendeeRequestPacket.class);
                    break;
                case ADD_FULL_AGENDA_REQUEST:
                    pack = gson.fromJson(message, AddFullAgendaRequestPacket.class);
                    break;
                case ADD_MULTIPLE_ATTENDEES_REQUEST:
                    pack = gson.fromJson(message, AddMultipleAttendeesRequestPacket.class);
                    break;
                case ADD_TOPIC_REQUEST:
                    pack = gson.fromJson(message, AddTopicRequestPacket.class);
                    break;
                case ADD_VOTING_REQUEST_PACKET:
                    pack = gson.fromJson(message, AddVotingRequestPacket.class);
                    break;
                case DELETE_AGENDA_REQUEST:
                    pack = gson.fromJson(message, DeleteAgendaRequestPacket.class);
                    break;
                case DELETE_FILE_REQUEST:
                    pack = gson.fromJson(message, DeleteFileRequestPacket.class);
                    break;
                case DOWNLOAD_ALL_QR_REQUEST:
                    pack = gson.fromJson(message, DownloadAllQRRequestPacket.class);
                    break;
                case DOWNLOAD_QR_REQUEST:
                    pack = gson.fromJson(message, DownloadQRRequestPacket.class);
                    break;
                case EDIT_USER_REQUEST:
                    pack = gson.fromJson(message, EditUserRequestPacket.class);
                    break;
                case EDIT_VOTING_REQUEST:
                    pack = gson.fromJson(message, EditVotingRequestPacket.class);
                    break;
                case GENERATE_MISSING_ATTENDEE_PASSWORDS:
                    pack = gson.fromJson(message, GenerateMissingAttendeePasswordsRequestPacket.class);
                    break;
                case GENERATE_NEW_ATTENDEE_TOKEN:
                    pack = gson.fromJson(message, GenerateNewAttendeeTokenRequestPacket.class);
                    break;
                case GENERATE_NEW_ATTENDEE_PASSWORD:
                    pack = gson.fromJson(message, GenerateNewAttendeePasswordRequestPacket.class);
                    break;
                case GET_ALL_ATTENDEE_PASSWORDS:
                    pack = gson.fromJson(message, GetAllAttendeePasswordsRequestPacket.class);
                    break;
                case GET_ALL_ATTENDEES_REQUEST:
                    pack = gson.fromJson(message, GetAllAttendeesRequestPacket.class);
                    break;
                case GET_ALL_REQUESTS_REQUEST:
                    pack = gson.fromJson(message, GetAllRequestsRequestPacket.class);
                    break;
                case GET_ATTENDEE_DATA_REQUEST:
                    pack = gson.fromJson(message, GetAttendeeDataRequestPacket.class);
                    break;
                case GET_ATTENDEE_PASSWORD_REQUEST:
                    pack = gson.fromJson(message, GetAttendeePasswordRequestPacket.class);
                    break;
                case GET_EXISTING_GROUPS_REQUEST:
                    pack = gson.fromJson(message, GetExistingGroupsRequestPacket.class);
                    break;
                case GET_VOTINGS_REQUEST:
                    pack = gson.fromJson(message, GetVotingsRequestPacket.class);
                    break;
                case LOGOUT_ALL_ATTENDEES:
                    pack = gson.fromJson(message, LogoutAllAttendeesRequestPacket.class);
                    break;
                case LOGOUT_ATTENDEE_REQUEST:
                    pack = gson.fromJson(message, LogoutAttendeeRequestPacket.class);
                    break;
                case REMOVE_ATTENDEE_REQUEST:
                    pack = gson.fromJson(message, RemoveAttendeeRequestPacket.class);
                    break;
                case REMOVE_TOPIC_REQUEST:
                    pack = gson.fromJson(message, RemoveTopicRequestPacket.class);
                    break;
                case REMOVE_VOTING_REQUEST:
                    pack = gson.fromJson(message, RemoveVotingRequestPacket.class);
                    break;
                case RENAME_TOPIC_REQUEST:
                    pack = gson.fromJson(message, RenameTopicRequestPacket.class);
                    break;
                case SET_ATTENDEE_PRESENT_STATUS_REQUEST:
                    pack = gson.fromJson(message, SetAttendeePresentStatusRequestPacket.class);
                    break;
                case SET_REQUEST_STATUS_REQUEST:
                    pack = gson.fromJson(message, SetRequestStatusRequestPacket.class);
                    break;
                case START_VOTING_REQUEST:
                    pack = gson.fromJson(message, StartVotingRequestPacket.class);
                    break;
                case UPDATE_FILE_REQUEST:
                    pack = gson.fromJson(message, UpdateFileRequestPacket.class);
                    break;
                /* USER PACKETS */
                case ADD_VOTE_REQUEST:
                    pack = gson.fromJson(message, AddVoteRequestPacket.class);
                    break;
                case DOWNLOAD_FILE_REQUEST:
                    pack = gson.fromJson(message, DownloadFileRequestPacket.class);
                    break;
                case GET_ACTIVE_VOTING_REQUEST:
                    pack = gson.fromJson(message, GetActiveVotingRequestPacket.class);
                    break;
                case GET_AGENDA_REQUEST:
                    pack = gson.fromJson(message, GetAgendaRequestPacket.class);
                    break;
                case GET_DOCUMENT_LIST_REQUEST:
                    pack = gson.fromJson(message, GetDocumentListRequestPacket.class);
                    break;
                case GET_PREVIOUS_VOTINGS_REQUEST:
                    pack = gson.fromJson(message, GetPreviousVotingsRequestPacket.class);
                    break;
                case CONFERENCE_DATA_REQUEST:
                    pack = gson.fromJson(message, GetConferenceDataRequestPacket.class);
                    break;
                case IS_ADMIN_REQUEST:
                    pack = gson.fromJson(message, IsAdminRequestPacket.class);
                    break;
                case LOGIN_REQUEST:
                    pack = gson.fromJson(message, LoginRequestPacket.class);
                    break;
                case PERSONAL_DATA_REQUEST:
                    pack = gson.fromJson(message, PersonalDataRequestPacket.class);
                    break;
                case REQUEST_OF_CHANGE_REQUEST:
                    pack = gson.fromJson(message, RequestOfChangeRequestPacket.class);
                    break;
                case REQUEST_OF_SPEECH_REQUEST:
                    pack = gson.fromJson(message, RequestOfSpeechRequestPacket.class);
                    break;
                default:
                    throw new IllegalArgumentException("Packet type " + packetType + " does not exist.");
            }
            pack.handle(conference, conn);
        } catch (Exception e) {
            if(e instanceof IllegalArgumentException) {
                new FailureResponsePacket(e.getMessage()).send(conn);
            } else if(e instanceof JsonSyntaxException) {
                if(debugging) {
                    e.printStackTrace();
                } else {
                    //If this is no debugging instance, this is most likely a malicious request, therefore close the connection
                    conn.close();
                }
            } else {
                new FailureResponsePacket().send(conn);
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles incoming messages of binary type
     *
     * @param conn the connections from which the message was received
     * @param file containing the binary data
     */
    public void onMessage(Connection conn, File file) {
        UpdateFileRequestPacket packet = UpdateFileRequestPacket.getRequestFromConnectionIfExists(conn);
        if(packet != null) {
            packet.handleFileTransfer(conference, conn, file);
        } else {
            //this is most likely a malicious request, therefore close the connection
            conn.close();
        }
    }

    /**
     * A method indicating weather a connection may transfer binary data. This can be used to interrupt a connection
     * before binary data is even received and therefore avoid traffic.
     *
     * @param conn the connection from which the binary data is received
     *
     * @return if the connection is allowed to send binary data
     */
    public boolean mayTransferBinary(Connection conn) {
        return UpdateFileRequestPacket.existingRequest(conn);
    }

    /**
     * A method registering a connection being established causing the side effect of adding a timeout timestamp.
     *
     * @param conn the connection
     */
    public void onRegistered(Connection conn) {
        try {
            timeoutLock.lock();
            timeout.put(conn, System.currentTimeMillis() + timeoutAfter * 1000);
        } finally {
            timeoutLock.unlock();
        }
    }

    /**
     * A method unregistering a connection being disconnected causing the timeout timestamp to be removed.
     *
     * @param conn the connection
     */
    public void onUnregistered(Connection conn) {
        try {
            timeoutLock.lock();
            timeout.remove(conn);
        } finally {
            timeoutLock.unlock();
        }
        try {
            tokenConnectionMappingLock.lock();
            if(connectionTokenMap.containsKey(conn)) {
                String token = connectionTokenMap.get(conn);
                connectionTokenMap.remove(conn);
                tokenConnectionsMap.get(token).remove(conn);
                if(tokenConnectionsMap.get(token).isEmpty()) {
                    tokenConnectionsMap.remove(token);
                }
            }
        } finally {
            tokenConnectionMappingLock.unlock();
        }
    }

    /**
     * A method called to handle a proper shutdown of communication handling.
     */
    public void stop() {
        connectionLimitationService.shutdown();
    }
}
