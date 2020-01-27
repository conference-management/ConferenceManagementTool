package main;

import agenda.Agenda;
import agenda.AgendaManagement;
import agenda.AgendaObserver;
import com.google.gson.annotations.Expose;
import database.DB_AgendaManager;
import database.DB_DocumentManager;
import database.DB_RequestManager;
import database.DB_UserManager;
import database.DB_VotingManager;
import document.DB_DocumentManagement;
import document.Document;
import document.DocumentManagement;
import io.nayuki.qrcodegen.QrCode;
import request.DB_RequestManagement;
import request.Request;
import request.RequestManagement;
import user.*;
import utils.Generator;
import utils.Generator_Imp;
import utils.Pair;
import voting.Voting;
import voting.VotingManagement;
import voting.VotingObserver;
import voting.VotingStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Conference implements UserManagement, VotingManagement, RequestManagement, DocumentManagement, AgendaManagement, VotingObserver {

    //Conference Data
    @Expose
    private String name;
    @Expose
    private String organizer;
    @Expose
    private long startsAt;
    @Expose
    private long endsAt;

    private boolean debugingInstance;

    private Generator gen = new Generator_Imp();

    private String documentsPath;
    private String databasePath;
    private String url;

    private Agenda agenda;
    private HashMap<Integer, Voting> votings;
    private HashMap<String, Document> documents;
    private HashMap<Integer, Admin> admins;
    private HashMap<Integer, Request> requests;
    private Voting activeVoting;
    private HashMap<String, Boolean> adminTokens; // a map backed Set
    private HashMap<String, Boolean> volatileUserNames = new HashMap<>(); // user names that are reserved even though the corresponding user is not added (jet)

    //Database System
    private DB_DocumentManagement db_documentManagement;
    private DB_UserManagement db_userManagement;
    private DB_RequestManagement db_requestManagement;
    private DB_VotingManager db_votingManagement;

    //Locks - always take in this order !!!
    private Lock adminLock = new ReentrantLock();
    private Lock attendeeLock = new ReentrantLock();
    private Lock votingLock = new ReentrantLock();
    private Lock requestLock = new ReentrantLock();
    private Lock documentsLock = new ReentrantLock();


    private File tmpDir;


    //Creates a clean conference (for debugging)
    public Conference(boolean cleanStart) {
        this("Test",
                "Team 23",
                System.currentTimeMillis(),
                System.currentTimeMillis() + 1000 * 60 * 60,
                new HashMap<Integer, Admin>(),
                new HashMap<Integer, Voting>(),
                new HashMap<String, Document>(),
                "./docs",
                new HashMap<Integer, Request>(),
                null,
                "./testdb/testdb.db",
                "https://example.eu/conference",
                true,
                cleanStart
        );


    }

    /**
     * Construct a new or persistent Conference with all the Data below and prepare all DataManagement.
     *
     * @param name              the name of the conference
     * @param organizer         the organizer of the conference
     * @param startsAt          the unix epoch of the time the conference starts at
     * @param endsAt            the unix epoch of the time the conference ends at
     * @param admins            the admins of the conference
     * @param votings           the votings of the conference
     * @param documents         the documents of the conference
     * @param documentsPath     the storage path for documents
     * @param requests          the requests of the conference
     * @param activeVoting      the activeVoting ot the conference or null if non-existent
     * @param databasePath      the path to the sqlite database
     * @param deguggingInstance if this is a debugging instance
     * @param cleanStart        if existing data on the conference should be erased
     */
    public Conference(String name, String organizer, long startsAt, long endsAt, HashMap<Integer,
            Admin> admins, HashMap<Integer, Voting> votings, HashMap<String, Document> documents, String documentsPath,
                      HashMap<Integer, Request> requests, Voting activeVoting,
                      String databasePath, String url,
                      boolean deguggingInstance, boolean cleanStart) {
        this.name = name;
        this.organizer = organizer;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.votings = votings;
        this.documents = documents;
        this.requests = requests;
        this.activeVoting = activeVoting;
        this.documentsPath = documentsPath;
        this.databasePath = databasePath;

        this.debugingInstance = deguggingInstance;
        this.admins = admins;
        this.url = url;

        this.adminTokens = new HashMap<>();

        File database = new File(databasePath);

        if(database.getAbsolutePath().startsWith(new File(documentsPath).getAbsolutePath())) {
            System.err.println("Please do not store the database inside the documents folder");
            System.exit(1);
        }


        if(database.exists() && cleanStart) {
            database.delete();
        }

        initUsers();
        initAgenda();
        initDocuments();
        initRequests();
        initVotes();


        if(database.exists() && cleanStart) {
            File[] directoryListing = new File(documentsPath).listFiles();
            for(int i = 0; directoryListing != null && i < directoryListing.length; i++) {
                Document d = db_documentManagement.getDocument(directoryListing[i].getName());
                if(d == null) {
                    directoryListing[i].delete();
                } else {
                    documents.put(d.getName(), d);
                }
            }
        }


        tmpDir = new File(System.getProperty("user.dir") + "/tmp/conference");
        if(!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        long conferenceduration = endsAt - startsAt;
        Timer ActiveTimer = new Timer();
        ActiveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("CONFERENCE IS FINISHED!");
                System.out.println("ALL NONADMINS ARE LOGGED OUT");
                endConference();
            }


        }, conferenceduration);

    }

    /**
     * Initialize Users for Conference
     */
    private void initUsers() {
        db_userManagement = new DB_UserManager(databasePath);
        db_userManagement.getAllAdmins().forEach(a -> admins.put(a.getID(), a));
        db_userManagement.getAllAttendees();
    }

    /**
     * Initialize Agenda for Conference
     */
    private void initAgenda() {
        DB_AgendaManager db_agendaManagement = new DB_AgendaManager(databasePath);
        agenda = db_agendaManagement.getAgenda();
        agenda.register(db_agendaManagement);
    }

    /**
     * Initialize Documents for Conference
     */
    private void initDocuments() {
        db_documentManagement = new DB_DocumentManager(databasePath);
        File documentsFolder = new File(documentsPath);

        if(!documentsFolder.exists() && !documentsFolder.mkdir()) {
            throw new IllegalArgumentException("Could not create directory " + documentsPath);
        }
        if(documentsFolder.exists() && !documentsFolder.isDirectory()) {
            throw new IllegalArgumentException("Could not create directory " + documentsPath + " , because a file with that name already exists");
        }
        if(documentsFolder.exists() && documentsFolder.isDirectory()) {
            File[] directoryListing = documentsFolder.listFiles();
            for(int i = 0; i < directoryListing.length; i++) {
                Document d = db_documentManagement.getDocument(directoryListing[i].getName());
                if(d == null) {
                    directoryListing[i].delete();
                } else {
                    documents.put(d.getName(), d);
                }
            }
        }
    }

    /**
     * Initialize Requests for Conference
     */
    private void initRequests() {
        db_requestManagement = new DB_RequestManager(databasePath);
        db_requestManagement.getAllRequests().forEach(r -> requests.put(r.ID, r));
    }

    /**
     * Initialize Votes for Conference
     */
    private void initVotes() {
        db_votingManagement = new DB_VotingManager(databasePath);
        db_votingManagement.getVotings().forEach(v -> votings.put(v.getID(), v));
    }

    public void endConference() {
        this.logoutNonAdmins(false);
    }

    /**
     * Logout All Attendees which are not Admins from Conference. Invalidate all Token and Password of these.
     *
     * @return true iff logout was successful
     */
    public boolean logoutNonAdmins(Boolean newpw) {
        try {
            attendeeLock.lock();
            boolean success = true;
            for(Attendee a : db_userManagement.getAllAttendees()) {
                if(isAdmin(a.getID())) {
                    continue;
                }
                a.logout();
                if(newpw) {
                    success = db_userManagement.logoutUser(a.getID(), gen.generatePassword(), generateToken()) && success;
                } else {
                    success = db_userManagement.logoutUser(a.getID(), null, null) && success;
                }
            }
            return success;
        } finally {
            attendeeLock.unlock();
        }
    }


    /****************** The Request Management Interface *********/

    /****************** The User Management Interface *********/

    private String generateToken() {
        String token = gen.generateToken();
        while(db_userManagement.checkToken(token) != TokenResponse.TokenDoesNotExist) {
            token = gen.generateToken();
        }
        return token;
    }

    public String getName() {
        return name;
    }

    /**
     * Add Request to Request Database
     */
    @Override
    public void addRequest(Request request) {

        try {
            requestLock.lock();
            if(requests.containsKey(request.ID)) {
                throw new IllegalArgumentException();
            }
            if(!db_requestManagement.addRequest(request)) {
                throw new IllegalArgumentException();
            }
            requests.put(request.ID, request);
        } finally {
            requestLock.unlock();
        }

    }

    /**
     * Get specific Request from the Database
     *
     * @param ID of the Request
     *
     * @return Request
     */
    @Override
    public Request getRequest(int ID) {
        try {
            requestLock.lock();
            return requests.get(ID);
        } finally {
            requestLock.unlock();
        }
    }

    /**
     * Read all Requests from Database and return them.
     *
     * @return List of Requests
     */
    @Override
    public List<Request> getAllRequests() {
        try {
            requestLock.lock();
            return new ArrayList<>(requests.values());
        } finally {
            requestLock.unlock();
        }
    }

    /**
     * Add an Admin with new generated Password and Token to the Database
     *
     * @param a Admin Data
     */
    @Override
    public void addAdmin(Admin a) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            volatileUserNames.remove(a.getUserName());
            if(!db_userManagement.addAdmin(a, gen.generatePassword(), generateToken())) {
                throw new IllegalArgumentException("Database addition failed");
            }
            admins.put(a.getID(), a);
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Read all Admins from Database and return them.
     *
     * @return List of Admins
     */
    @Override
    public List<Admin> getAllAdmins() {
        try {
            adminLock.lock();
            return new ArrayList<>(admins.values());
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Read all personal Data from Admin with AdminId ID and return an Admin Object containing the Data.
     *
     * @param ID AdminId
     *
     * @return Admin
     */
    @Override
    public Admin getAdminPersonalData(int ID) {
        try {
            adminLock.lock();
            return admins.get(ID);
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Remove Admin with AdminId ID from Database, so he can´t login anymore and delete all Data from the Admin.
     *
     * @param ID AdminId
     */
    @Override
    public void removeAdmin(int ID) {
        try {
            adminLock.lock();
            if(admins.get(ID) == null) {
                throw new IllegalArgumentException("Admin not found");
            }
            if(!db_userManagement.removeUser(ID)) {
                throw new IllegalArgumentException("Admin can not be removed for unknown reasons");
            }
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Remove Admin with AdminId ID from Database, so he can´t login anymore.
     *
     * @param ID AdminId
     */
    @Override
    public void logoutAdmin(int ID) {
        try {
            adminLock.lock();
            if(admins.get(ID) == null) {
                throw new IllegalArgumentException("Admin not found");
            } else {
                admins.get(ID).logout();
            }
            if(!(db_userManagement.logoutUser(ID, gen.generatePassword(), generateToken()))) {
                throw new IllegalArgumentException("Admin can not be logged out for unknown reasons");
            }
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Edit Admin with AdminId ID in Database.
     *
     * @param ID AdminId
     * @param a  New Admin Data
     */
    @Override
    public void editAdmin(int ID, Admin a) {
        try {
            adminLock.lock();
            if(!admins.containsKey(ID)) {
                throw new IllegalArgumentException("Admin not found");
            }
            if(!db_userManagement.editAdmin(a)) {
                throw new IllegalArgumentException("Admin can not be edited for unknown reasons");
            }
            admins.replace(ID, a);
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Delete all Admins from the Database.
     */
    @Override
    public void deleteAllAdmins() {
        try {
            adminLock.lock();
            admins = new HashMap<Integer, Admin>();
            if(!db_userManagement.removeAllAdmins()) {
                throw new IllegalArgumentException("Can´t delete all Admins in the Database");
            }
        } finally {
            adminLock.unlock();
        }
    }

    //for debugging
    public void addAdmin(Admin a, String pwd) {
        assert (debugingInstance); // close the server since this operation is illegal
        try {
            adminLock.lock();
            AtomicBoolean alreadyExists = new AtomicBoolean(false);
            db_userManagement.getAllAttendees().forEach(ad -> {
                if(ad.getID() == a.getID()) {
                    alreadyExists.set(true);
                }
            });
            if(!alreadyExists.get() && !db_userManagement.addAdmin(a, pwd, generateToken())) {
                throw new IllegalArgumentException("Database addition failed");
            }
            admins.put(a.getID(), a);
        } finally {
            adminLock.unlock();
        }
    }

    //for debugging
    public void addAttendee(Attendee a, String pwd) {
        assert (debugingInstance); // close the server since this operation is illegal
        try {
            adminLock.lock();
            AtomicBoolean alreadyExists = new AtomicBoolean(false);
            db_userManagement.getAllAttendees().forEach(ad -> {
                if(ad.getID() == a.getID()) {
                    alreadyExists.set(true);
                }
            });
            if(!alreadyExists.get() && !db_userManagement.addAttendee(a, pwd, generateToken())) {
                throw new IllegalArgumentException("Database addition failed");
            }
        } finally {
            adminLock.unlock();
        }
    }

    /**
     * Add new Attendee to the Database.
     *
     * @param a Attendee Data
     */
    @Override
    public void addAttendee(Attendee a) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            volatileUserNames.remove(a.getUserName());
            if(!db_userManagement.addAttendee(a, gen.generatePassword(), generateToken())) {
                throw new IllegalArgumentException("Attendee can not be edited for unknown reasons");
            }

        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Read all Attendees from Database and return them in a List
     *
     * @return List of Attendees
     */
    @Override
    public List<Attendee> getAllAttendees() {
        try {
            attendeeLock.lock();
            return db_userManagement.getAllAttendees();
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Read specific Attendee Data from Attendee with AttendeeId unserID and return them.
     *
     * @param userID AttendeeId
     *
     * @return Attendee
     */
    @Override
    public Attendee getAttendeeData(int userID) {
        try {
            attendeeLock.lock();
            return db_userManagement.getAttendeeData(userID);
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Remove Attendee with AttendeeId userId from Database, so the Attendee cant login anymore and the Attendee Data are deleted.
     * Furthermore all requests with the AttendeeID will be removed.
     *
     * @param userID AttendeeId
     */
    @Override
    public void removeAttendee(int userID) {
        try {
            attendeeLock.lock();
            requestLock.lock();
            if(!db_userManagement.removeUser(userID)) {
                throw new IllegalArgumentException("Attendee can not be removed for unknown reasons");
            }
            if(!db_requestManagement.removeRequest(userID)) {
                throw new IllegalArgumentException("Attendees requests can not be removed for unknown reasons");
            }
            List<Integer> toRemove = new ArrayList<>();
            requests.forEach((i, r) -> {
                if(r.getRequester().getID() == userID) {
                    toRemove.add(i);
                }
            });
            for(Integer i : toRemove) {
                requests.remove(i);
            }
        } finally {
            requestLock.unlock();
            attendeeLock.unlock();
        }
    }

    /**
     * Remove User with UserId userId from Databse, so the User cant login anymore.
     *
     * @param userID UserId
     */
    @Override
    public void logoutUser(int userID) {
        try {
            attendeeLock.lock();
            if(!db_userManagement.logoutUser(userID, gen.generatePassword(), generateToken())) {
                throw new IllegalArgumentException("Attendee can not be logged out for unknown reasons");
            }
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Edit existing Attendee in the Databse.
     *
     * @param attendee Attendee
     */
    @Override
    public void editAttendee(Attendee attendee) {
        try {
            attendeeLock.lock();
            if(!db_userManagement.editAttendee(attendee)) {
                throw new IllegalArgumentException("Attendee could not be edited for unknown reasons");
            }
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Generate a new Password for an User with UserId userId and store it in Database.
     *
     * @param userID UserId
     */
    @Override
    public void generateNewUserPassword(int userID) {
        try {
            attendeeLock.lock();
            String password = gen.generatePassword();
            System.out.println("Generated password:" + password + "for user " + userID);
            if(!db_userManagement.storeNewPassword(userID, password)) {
                throw new IllegalArgumentException();
            }
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Generate a new Token for an User with UserId userId and store it in Database.
     *
     * @param userID UserId
     */
    @Override
    public void generateNewUserToken(int userID) {
        try {
            attendeeLock.lock();
            if(!db_userManagement.storeNewToken(userID, generateToken())) {
                throw new IllegalArgumentException();
            }
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Generates for All User new Passwords and store them in Database.
     */
    @Override
    public void generateAllMissingUserPasswords() {
        try {
            attendeeLock.lock();
            boolean success = true;
            for(Pair<User, String> p : db_userManagement.getAllPasswords()) {
                if(p.second() == null) {
                    success = db_userManagement.storeNewPassword(p.first().getID(), gen.generatePassword()) && success;
                }
            }

            if(!success) {
                throw new IllegalArgumentException();
            }
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Read Password from User with UserId userId and return it.
     *
     * @param userID UserId
     *
     * @return Pair with User and Password
     */
    @Override
    public Pair<User, String> getUserPassword(int userID) {
        try {
            attendeeLock.lock();
            for(Pair<User, String> p : db_userManagement.getAllPasswords()) {
                if(p.first().getID() == userID) {
                    return p;
                }
            }
            throw new IllegalArgumentException();
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Read Password from  all User and return them.
     *
     * @return List of Pair with User and Password
     */
    @Override
    public List<Pair<User, String>> getAllUsersPasswords() {
        try {
            attendeeLock.lock();
            return db_userManagement.getAllPasswords();
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * Logout All Attendees from Conference. Invalidate all Token and Password in Database.
     *
     * @return true iff logout was successful
     */
    @Override
    public boolean logoutAllUsers() {
        try {
            attendeeLock.lock();
            boolean success = true;
            for(Attendee a : db_userManagement.getAllAttendees()) {
                a.logout();
                success =  db_userManagement.logoutUser(a.getID(), gen.generatePassword(), generateToken()) && success;
            }
            for(Attendee a : db_userManagement.getAllAdmins()) {
                a.logout();
                success =  db_userManagement.logoutUser(a.getID(), gen.generatePassword(), generateToken()) && success;
            }
            return success;
        } finally {
            attendeeLock.unlock();
        }
    }

    /**
     * A function that the communication system can use to check if a login is valid
     *
     * @param userName - the username provided by the request
     * @param password - the password provided by the request
     *
     * @return - A pair consisting of a {@link LoginResponse}, a token, a token, and the number of seconds until the token
     * should expire.
     * If the {@link LoginResponse} is not Valid then the second argument will be null
     */
    @Override
    public Pair<LoginResponse, Pair<String, Long>> login(String userName, String password) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            Pair<LoginResponse, String> response = db_userManagement.checkLogin(userName, password);
            if(response.first() != LoginResponse.Valid) {
                return new Pair<>(response.first(), null);
            } else {
                return new Pair<>(response.first(), new Pair<>(response.second(), endsAt));
            }
        } finally {
            adminLock.unlock();
            attendeeLock.unlock();
        }
    }

    /**
     * Read the specific UserID from a User with Token token.
     *
     * @param token Token
     *
     * @return UserId
     */
    @Override
    public int tokenToID(String token) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            return db_userManagement.tokenToID(token);
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Check if Admin with AdminId id is an Admin.
     *
     * @param id AdminId
     *
     * @return true iff User is an Admin
     */
    @Override
    public boolean isAdmin(int id) {
        return db_userManagement.getAdminData(id) != null;
    }

    /**
     * Checks the Status of the Token token.
     *
     * @param token Token
     *
     * @return TokenResponse
     */
    @Override
    public TokenResponse checkToken(String token) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            if(adminTokens.containsKey(token)) {
                return TokenResponse.ValidAdmin;
            } else {
                TokenResponse res = db_userManagement.checkToken(token);
                if(res == TokenResponse.ValidAdmin) {
                    adminTokens.put(token, true);
                }
                return res;
            }
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Create an unique UserName from Name name, that isn´t stored in the Database.
     *
     * @param name Name
     *
     * @return Username
     */
    @Override
    public String getFreeUserName(String name) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            name = name.replaceAll("[^A-Za-z0-9]", ".");
            String nameAux = name;
            int i = 1;
            while(volatileUserNames.containsKey(nameAux) || db_userManagement.userNameAlreadyUsed(nameAux)) {
                nameAux = name + i;
                i++;
            }
            volatileUserNames.put(nameAux, true);
            return nameAux;
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Get a list of unique groups existing i.e. there is
     * an attendee with the corresponding group in the database
     *
     * @return groups
     */
    public List<String> getExistingGroups() {
        try {
            adminLock.lock();
            attendeeLock.lock();
            List<String> groups = db_userManagement.getAllGroupsFromUser();
            return groups;
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /**
     * Edit present value of a user.
     *
     * @param username username of the user
     * @param present  new present value of the user
     *
     * @return
     */
    public Boolean setPresentValue(String username, Boolean present) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            return db_userManagement.setPresentValueofUser(username, present);
        } finally {
            attendeeLock.unlock();
            adminLock.unlock();
        }
    }

    /****************** The Voting Management Interface *********/

    /**
     * called if a vote start
     */
    public Boolean startVoting(Voting vote) {
        try {
            votingLock.lock();
            if(this.getActiveVoting() != null) {
                return false;
            }
            vote.startVote();
            update(vote);
            int duration = vote.getDuration();

            Timer ActiveTimer = new Timer();
            ActiveTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    vote.endVote();
                    //db_votingManagement.addVoting(vote);
                    update(vote);
                }


            }, 1000 * duration);


            return true;
        } finally {
            votingLock.unlock();
        }
    }


    /**
     * Get the Actual Active Voting
     *
     * @return Voting
     */
    @Override
    public Voting getActiveVoting() {
        try {
            votingLock.lock();
            return activeVoting;
        } finally {
            votingLock.unlock();
        }
    }

    /**
     * Get created Voting with VotingId ID.
     *
     * @param ID VotingId
     *
     * @return Voting
     */
    @Override
    public Voting getVoting(int ID) {
        try {
            votingLock.lock();
            return votings.get(ID);
        } finally {
            votingLock.unlock();
        }
    }

    /**
     * Get all created Votings.
     *
     * @return List of Voting
     */
    @Override
    public List<Voting> getVotings() {
        try {
            votingLock.lock();
            return new ArrayList<>(votings.values());
        } finally {
            votingLock.unlock();
        }
    }

    /**
     * Add created Voting
     *
     * @param voting Voting
     */
    @Override
    public void addVoting(Voting voting) {

        try {
            votingLock.lock();
            votings.put(voting.getID(), voting);
            voting.register(db_votingManagement);
        } finally {
            votingLock.unlock();
        }
    }

    /**
     * Delete Voting from created Votings
     *
     * @param voting Voting
     */
    @Override
    public void removeVoting(Voting voting) {
        try {
            votingLock.lock();
            votings.remove(voting.getID(), voting);

        } finally {
            votingLock.unlock();
        }
    }

    /**
     * Updated a specific Voting
     *
     * @param v The updates {@link Voting}.
     *
     * @return if the voting was updated successfully
     */
    @Override
    public boolean update(Voting v) {
        try {
            votingLock.lock();
            if(v.getStatus() == VotingStatus.Closed) {
                activeVoting = null;
            }
            if(v.getStatus() == VotingStatus.Running) {
                if(activeVoting != null) {
                    return false;
                } else {
                    activeVoting = v;
                }
            }
            return true;
        } finally {
            votingLock.unlock();
        }
    }

    /****************** The Agenda Management Interface *********/

    /**
     * Get the current Agenda.
     *
     * @return Agenda
     */
    @Override
    public Agenda getAgenda() {
        return agenda;
    }

    @Override
    public void updateAgenda(Agenda newAgenda) {
        ConcurrentHashMap<AgendaObserver, Boolean> observers = this.agenda.getObservers();
        for(Map.Entry<AgendaObserver, Boolean> o : observers.entrySet()) {
            newAgenda.register(o.getKey());
        } //Two loops to avoid ConcurrentModification
        for(Map.Entry<AgendaObserver, Boolean> o : observers.entrySet()) {
            this.agenda.unregister(o.getKey());
        }
        this.agenda = newAgenda;

        this.agenda.notifyObservers();
    }


    /****************** The Document Management Interface *********/

    /**
     * Update an existing Document and store the updated Document in the Database.
     *
     * @param name       the name of the document to update
     * @param fileType   the fileType of the document
     * @param file       the file of the document
     * @param isCreation if this is the creation of a document or a new version of an old one
     */
    @Override
    public void updateDocument(String name, String fileType, File file, boolean isCreation) {
        try {
            documentsLock.lock();
            if(file.length() > 1024 * 1024 * 500) {
                throw new IllegalArgumentException("The file is to large");
            }
            if(!name.endsWith(fileType)) {
                throw new IllegalArgumentException("File extensions differ");
            }
            String fullName = name;
            File f;
            if(!documents.containsKey(fullName)) {
                f = new File(documentsPath + "/" + fullName);
            } else {
                f = documents.get(fullName).getFile();
            }
            if(f.exists() && isCreation) {
                throw new IllegalArgumentException("File already exists");
            }
            if(!f.exists() && !isCreation) {
                throw new IllegalArgumentException("File does not exist");
            }
            try {
                if(f.exists() || f.createNewFile()) {
                    f.delete();
                    Files.move(file.toPath(), f.toPath());
                }

            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            if(isCreation) {
                Document doc = new Document(f.getPath(), fullName);
                if(db_documentManagement.addDocument(doc)) {
                    documents.put(fullName, doc);
                }
            } else {
                documents.get(fullName).incrementRevision();
            }

        } finally {
            documentsLock.unlock();
        }
    }

    /**
     * Delete an existing Document with DocumentName name in the Database and in the folder.
     *
     * @param name DocumentName
     */
    @Override
    public void deleteDocument(String name) {
        try {
            documentsLock.lock();
            if(!documents.containsKey(name)) {
                throw new IllegalArgumentException("Document does not exist");
            }
            File f = documents.get(name).getFile();
            if(!f.delete()) {
                throw new IllegalArgumentException("Could not remove document from server");
            }
            if(!db_documentManagement.deleteDocument(name)) {
                throw new IllegalArgumentException("Could not remove the document from the database");
            }
            documents.remove(name);

        } finally {
            documentsLock.unlock();
        }
    }

    /**
     * Read Content from Document with DocumentName name
     *
     * @param name DocumentName
     *
     * @return Byte List
     */
    @Override
    public byte[] getDocumentContent(String name) {
        try {
            documentsLock.lock();
            if(!documents.containsKey(name)) {
                throw new IllegalArgumentException("file does not exist");
            }

            File f = documents.get(name).getFile();

            byte[] fileBytes = new byte[(int) f.length()];
            try {
                FileInputStream fis = new FileInputStream(f);
                fis.read(fileBytes);
                fis.close();
                return fileBytes;

            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read file");
            }
        } finally {
            documentsLock.unlock();
        }
    }

    /**
     * Read Document Data with DocumentName name and return it.
     *
     * @param name DocumentName
     *
     * @return Document
     */
    @Override
    public Document getDocument(String name) {
        try {
            documentsLock.lock();
            if(!documents.containsKey(name)) {
                throw new IllegalArgumentException("Document does not exist");
            }
            return documents.get(name);
        } finally {
            documentsLock.unlock();
        }
    }

    /**
     * Read all Documents and return them.
     *
     * @return List of Documents
     */
    @Override
    public List<Document> getAllDocuments() {
        try {
            documentsLock.lock();
            return new ArrayList<>(documents.values());
        } finally {
            documentsLock.unlock();
        }
    }

    /**
     * Read Content from Document with DocumentName name
     *
     * @param name DocumentName
     *
     * @return File
     */
    public File getDocumentContentAsFile(String name) {
        try {
            documentsLock.lock();
            if(!documents.containsKey(name)) {
                throw new IllegalArgumentException("file does not exist");
            }

            File f = documents.get(name).getFile();

            return f;
        } finally {
            documentsLock.unlock();
        }
    }

    /****************** The Functionality related to QR code generation *********/

    public byte[] getQrCode(int attendeeId) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            generateQRCode(attendeeId);
            Attendee a = getAttendeeData(attendeeId);
            File f = new File(tmpDir.getAbsolutePath() + "/qr/" + a.getUserName() + "/qr-code.png");
            try {
                byte[] fileBytes = new byte[(int) f.length()];
                FileInputStream fis = new FileInputStream(f);
                fis.read(fileBytes);
                fis.close();
                return fileBytes;

            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read file");
            }

        } finally {
            adminLock.unlock();
            attendeeLock.unlock();
        }
    }

    public byte[] getAllQrCodes() {
        try {
            adminLock.lock();
            attendeeLock.lock();
            generateAllQRCodes();
            String sourceFile = tmpDir.getAbsolutePath() + "/qr/";

            File f = new File(tmpDir.getAbsolutePath() + "/qrs.zip");
            if(f.exists()) {
                f.delete();
            }

            FileOutputStream fos = new FileOutputStream(f);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);

            zipFile(fileToZip, fileToZip.getName(), zipOut, true);
            zipOut.close();

            byte[] fileBytes = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);
            fis.read(fileBytes);
            fis.close();
            return fileBytes;


        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        } finally {
            adminLock.unlock();
            attendeeLock.unlock();
        }

    }

    public void generateQRCode(int attendeeId) {
        try {
            adminLock.lock();
            attendeeLock.lock();
            Attendee a = db_userManagement.getAttendeeData(attendeeId);

            File qrDir = new File(tmpDir.getAbsolutePath() + "/qr/");
            if(!qrDir.exists() || !qrDir.isDirectory()) {
                generateCleanDirectory(qrDir);
            }

            File userDir = new File(tmpDir.getAbsolutePath() + "/qr/" + a.getUserName());
            generateCleanDirectory(userDir);

            try {


                File aux = new File(userDir.getAbsolutePath() + "/data.txt");
                aux.createNewFile();
                FileWriter fw = new FileWriter(aux);
                fw.write(a.toString());
                fw.append("\npassword:" + getUserPassword(a.getID()).second());
                fw.close();

                QrCode qr = QrCode.encodeText(url + "?name=" + a.getUserName() + "&pwd=" + getUserPassword(attendeeId).second(), QrCode.Ecc.MEDIUM);
                BufferedImage img = qr.toImage(4, 10);
                ImageIO.write(img, "png", new File(userDir.getAbsolutePath() + "/qr-code.png"));
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        } finally {
            adminLock.unlock();
            attendeeLock.unlock();
        }


    }

    public void generateAllQRCodes() {
        try {
            adminLock.lock();
            attendeeLock.lock();
            purgeDirectory(new File(tmpDir.getAbsolutePath() + "/qr/"));
            for(Attendee a : getAllAttendees()) {
                generateQRCode(a.getID());
            }
        } finally {
            adminLock.unlock();
            attendeeLock.unlock();
        }

    }

    void generateCleanDirectory(File userDir) {
        if(userDir.exists() && !userDir.isDirectory()) {
            userDir.delete();
        }
        if(!userDir.exists()) {
            userDir.mkdir();
        } else {
            if(userDir.isDirectory()) {
                purgeDirectory(userDir);
            }
        }
    }

    void purgeDirectory(File dir) {
        if(!dir.exists()) return;
        for(File file : dir.listFiles()) {
            if(file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, boolean root) throws IOException {
        if(fileToZip.isHidden()) {
            return;
        }
        if(root) {
            File[] children = fileToZip.listFiles();
            for(File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut, false);
            }
            return;
        }
        if(fileToZip.isDirectory()) {
            if(fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for(File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut, false);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();

    }
}


