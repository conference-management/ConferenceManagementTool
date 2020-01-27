package database;

import request.ChangeRequest;
import request.DB_RequestManagement;
import request.Request;
import request.RequestObservable;
import request.Requestable;
import request.SpeechRequest;
import user.Attendee;
import user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("checkstyle:typename")
public class DB_RequestManager extends DB_Controller implements DB_RequestManagement {

    private static String table = "requests";

    public DB_RequestManager(String url) {
        super(url);
    }


    /**
     * Initializes the request tables for the database
     */
    @Override
    protected void init() {
        String userTable = "CREATE TABLE IF NOT EXISTS users (\n"
                + "     userID INTEGER PRIMARY KEY,\n"
                + "     fullname TEXT NOT NULL,\n"
                + "     username TEXT NOT NULL UNIQUE,\n"
                + "     password TEXT UNIQUE,\n"
                + "     token TEXT UNIQUE,\n"
                + "     email TEXT NOT NULL UNIQUE,\n"
                + "     groups TEXT NOT NULL,\n"
                + "     function TEXT NOT NULL,\n"
                + "     residence TEXT NOT NULL UNIQUE,\n"
                + "     isAdmin BOOL NOT NULL,\n"
                + "     present BOOL NOT NULL\n"
                + ") WITHOUT ROWID;";
        String requestTable = "CREATE TABLE IF NOT EXISTS requests (\n"
                + "     requestID INTEGER PRIMARY KEY,\n"
                + "     userID INTEGER NOT NULL,\n"
                + "     requestType INTEGER NOT NULL,\n"//0 for Change, 1 for Speech
                + "     requestableName TEXT NOT NULL,\n"
                + "     timestamps BIGINT NOT NULL,\n"
                + "     content TEXT,\n"
                + "     approved BOOL\n"
                + ") WITHOUT ROWID;";
        Connection connection = openConnection();
        try {
            connection.createStatement().execute(userTable);
            connection.createStatement().execute(requestTable);
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            System.err.println(e.getMessage());
        }
        closeConnection(connection);
    }

    /**
     * Adds a new {@link Request} to the database.
     *
     * @param req The {@link Request} to be added.
     *
     * @return True, iff the {@link Request} was successfully added.
     */
    @Override
    public boolean addRequest(Request req) {
        Connection connection = this.openConnection();
        String sqlstatement = "INSERT INTO requests(requestID, userID, requestType, requestableName, timestamps," +
                "content, approved) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setInt(1, req.ID);
            stmt.setInt(2, req.getRequester().getID());
            if(req instanceof ChangeRequest) {
                stmt.setInt(3, 0);
                stmt.setString(6, ((ChangeRequest) req).getMessage());
                stmt.setBoolean(7, ((ChangeRequest) req).isApproved());
            } else if(req instanceof SpeechRequest) {
                stmt.setInt(3, 1);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.BOOLEAN);
            } else {
                System.err.println("Requestable Type not supported by database implementation.");
                return false;
            }
            stmt.setString(4, req.getRequestable().getRequestableName());
            stmt.setLong(5, req.getTimeStamp());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("An error occurred while adding a new request to the database.");
            System.err.println(ex.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }

    /**
     * Reconstructs a given {@link Request} from the database.
     *
     * @param ID The ID of the {@link Request}.
     *
     * @return the reconstructed {@link Request}.
     */
    @Override
    public Request getRequest(int ID) {
        Connection connection = this.openConnection();
        Request request = null;
        String sqlstatement = "SELECT * FROM requests WHERE requestID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setInt(1, ID);
            ResultSet table = stmt.executeQuery();
            int requestID = table.getInt("requestID");
            int userID = table.getInt("userID");
            int requestType = table.getInt("requestType");
            long timestamp = table.getInt("timestamps");
            String text = table.getString("content");
            boolean approved = table.getBoolean("approved");

            String name = table.getString("requestableName");
            Requestable requestable = new Requestable() {
                @Override
                public String getRequestableName() {
                    return name;
                }
            };
            User attendee = null;
            String userstmt = "SELECT * FROM users WHERE userID = ?";
            try (PreparedStatement user = connection.prepareStatement(userstmt)) {
                user.setInt(1, userID);
                ResultSet att = user.executeQuery();
                attendee = new Attendee(att.getString("fullname"),
                        att.getString("email"),
                        att.getString("username"),
                        att.getString("groups"),
                        att.getString("residence"),
                        att.getString("function"),
                        att.getInt("userID"));
            }
            switch(requestType) {
                case 0: //Is ChangeRequest
                    ChangeRequest req = new ChangeRequest(requestID, attendee, requestable, timestamp, text);
                    if(approved) {
                        req.approve();
                    }
                    request = req;
                    break;
                case 1: //Is SpeechRequest
                    request = new SpeechRequest(requestID, attendee, requestable, timestamp);
                    break;
                default:
                    System.err.println("RequestType " + requestType + "found in database, which is not a" +
                            "valid request in this implementation");
                    return null;
            }
        } catch (SQLException ex) {
            System.err.println("An error occurred while trying to reconstruct a request.");
            System.err.println(ex.getMessage());
            return null;
        } finally {
            this.closeConnection(connection);
        }
        return request;
    }


    /**
     * @return a list of all reconstructed {@link Request}s from the database.
     */
    @Override
    public List<Request> getAllRequests() {
        Connection connection = this.openConnection();
        List<Request> requests = new LinkedList<>();
        String sqlstatement = "SELECT * FROM requests";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement);
             ResultSet table = stmt.executeQuery()) {
            while(table.next()) {
                int requestID = table.getInt("requestID");
                int userID = table.getInt("userID");
                int requestType = table.getInt("requestType");
                long timestamp = table.getInt("timestamps");
                String text = table.getString("content");
                boolean approved = table.getBoolean("approved");
                String name = table.getString("requestableName");
                Requestable requestable = new Requestable() {
                    @Override
                    public String getRequestableName() {
                        return name;
                    }
                };
                String userstmt = "SELECT * FROM users WHERE userID = ?";
                User attendee = null;
                try (PreparedStatement user = connection.prepareStatement(userstmt)) {
                    user.setInt(1, userID);
                    ResultSet att = user.executeQuery();
                    attendee = new Attendee(att.getString("fullname"),
                            att.getString("email"),
                            att.getString("username"),
                            att.getString("groups"),
                            att.getString("residence"),
                            att.getString("function"),
                            att.getInt("userID"));
                }
                switch(requestType) {
                    case 0: //Is ChangeRequest
                        ChangeRequest req = new ChangeRequest(requestID, attendee, requestable, timestamp, text);
                        if(approved) {
                            req.approve();
                        }
                        requests.add(req);
                        break;
                    case 1: //Is SpeechRequest
                        requests.add(new SpeechRequest(requestID, attendee, requestable, timestamp));
                        break;
                    default:
                        System.err.println("RequestType " + requestType + "found in database, which is not a" +
                                "valid request in this implementation");
                        return null;
                }
            }
        } catch (SQLException ex) {
            System.err.println("An error occured while reconstructing all requests.");
            System.err.println(ex.getMessage());
            return null;
        } finally {
            this.closeConnection(connection);
        }
        return requests;
    }

    /**
     * Updates the {@link Request} after the {@link RequestObservable} was changed.
     *
     * @param r The updates {@link Request}.
     *
     * @return True, iff the updates was successful.
     */
    @Override
    public boolean update(Request r) {
        Connection connection = this.openConnection();
        String sqlstatement = "UPDATE requests SET approved = ?, requestableName = ?, timestamps = ?, userID = ?, content = ?"
                + " WHERE requestID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            if(r instanceof ChangeRequest) {
                stmt.setBoolean(1, ((ChangeRequest) r).isApproved());
                stmt.setString(5, ((ChangeRequest) r).getMessage());
            } else if(r instanceof SpeechRequest) {
                stmt.setNull(1, Types.BOOLEAN);
                stmt.setNull(5, Types.VARCHAR);
            } else {
                System.err.println("Requestable Type not supported by Database implementation.");
                return false;
            }
            stmt.setString(2, r.getRequestable().getRequestableName());
            stmt.setLong(3, r.getTimeStamp());
            stmt.setInt(4, r.getRequester().getID());
            stmt.setInt(6, r.ID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("An error occurred while updating a request.");
            System.err.println(e.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }


    /**
     * This methods deletes all request entry from a specific user in the database.
     *
     * @param userID The ID of the user.
     *
     * @return True, iff the requests was successfully removed.
     */
    @Override
    public boolean removeRequest(int userID) {
        Connection connection = this.openConnection();
        String sqlstatement = "DELETE FROM requests WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("An exception occurred while removing all requests from user from the database.");
            System.err.println(e.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }
}
