package user;

import utils.Pair;

import java.util.List;

@SuppressWarnings("checkstyle:typename")
public interface DB_UserManagement {

    /**
     * This method checks whether a pair of a username and a password is a valid combination in the database.
     *
     * @param userName The unique userName of the given User.
     * @param password The password to be checked.
     *
     * @return A {@link LoginResponse} indicating the result and a token, in case the login was valid.
     */
    Pair<LoginResponse, String> checkLogin(String userName, String password);

    /**
     * This method checks whether a given token is a valid token.
     *
     * @param token The token to be checked.
     *
     * @return A {@link TokenResponse} indicating whether the token is valid, was already blocked or whether the token
     * corresponds to an admin or not.
     */
    TokenResponse checkToken(String token);

    /**
     * Edit present value of a user.
     *
     * @param userName userName of the user
     * @param present  new present value
     *
     * @return true, iff the db stored the new present value correctly
     */
    Boolean setPresentValueofUser(String userName, Boolean present);

    /**
     * Converts a token to a user ID.
     *
     * @param token The token of the user.
     *
     * @return the ID of the user with the given token.
     *
     * @throws IllegalArgumentException if the token does not exist.
     */
    int tokenToID(String token);

    /**
     * This methods deletes a user entry in the database.
     *
     * @param userID The ID of the user to be deleted.
     *
     * @return True, iff the user was successfully removed.
     */
    boolean removeUser(int userID);

    /**
     * This method logs out a user in the database. This method must not delete the entry but should indicate that the
     * user cannot log in again.
     *
     * @param userID The ID of the user to be deleted.
     * @param pw     new password for the user.
     * @param token  new token for the user.
     *
     * @return True, iff the operation was successful.
     */
    boolean logoutUser(int userID, String pw, String token);

    /**
     * Returns a list of all passwords to be handed out to the users, combined with their ID.
     *
     * @return A list pairing {@link User}-objects with their passwords.
     */
    List<Pair<User, String>> getAllPasswords();

    /**
     * Overwrites the token of the user, in case there was a problem with the device.
     *
     * @param userID The ID of the user.
     * @param token  The new token.
     *
     * @return True, iff the new token was successfully added to the database.
     */
    boolean storeNewToken(int userID, String token);

    /**
     * Overwrites the password of the user, in case there was a problem with the device.
     *
     * @param userID   The ID of the user.
     * @param password The new password.
     *
     * @return True, iff the new password was successfully added to the database.
     */
    boolean storeNewPassword(int userID, String password);

    /**
     * This methods checks whether a username was already used to enable unique username creation.
     *
     * @param username The username that should be checked.
     *
     * @return True, iff the username was already in the database.
     */
    boolean userNameAlreadyUsed(String username);

    /**
     * This methods checks whether a userid was already used to enable unique ids creation.
     *
     * @param id The username that should be checked.
     *
     * @return True, iff the username was already in the database.
     */
    boolean userIDAlreadyUsed(int id);

    /**
     * @return a list of all IDs.
     */
    List<Integer> getIDs();


    /**
     * Reads all different groups types of all users and return the groups as a list.
     *
     * @return a list of all existing groups.
     */
    List<String> getAllGroupsFromUser();


    /**
     * Adds a new {@link Attendee} to the database that is not an admin.
     *
     * @param a        The new {@link Attendee}.
     * @param password The password of the attendee.
     * @param token    The token of the attendee.
     *
     * @return True, iff the attendee was added correctly.
     */
    boolean addAttendee(Attendee a, String password, String token);

    /**
     * @return a list of all {@link Attendee}s in the database.
     */
    List<Attendee> getAllAttendees();

    /**
     * Returns the {@link Attendee} with the given userID.
     *
     * @param userID The ID of the user.
     *
     * @return the {@link Attendee} object.
     */
    Attendee getAttendeeData(int userID);

    /**
     * Overwrite the {@link Attendee} in the database.
     *
     * @param a The new {@link Attendee} object.
     *
     * @return True, iff the Attendee was overwritten properly
     */
    boolean editAttendee(Attendee a);

    /**
     * Adds a new {@link Admin} to the database.
     *
     * @param a        The new {@link Admin}.
     * @param password The password of the admin.
     * @param token    The token of the admin.
     *
     * @return True, iff the admin was added correctly.
     */
    boolean addAdmin(Admin a, String password, String token);

    /**
     * @return a list of all {@link Admin}s in the database.
     */
    List<Admin> getAllAdmins();

    /**
     * This methods deletes all admins in the database.
     *
     * @return True, iff the user was successfully removed.
     */
    boolean removeAllAdmins();

    /**
     * Returns the {@link Admin} with the given userID.
     *
     * @param userID The ID of the admin.
     *
     * @return the {@link Admin} object.
     */
    Admin getAdminData(int userID);

    /**
     * Overwrite the {@link Admin} in the database.
     *
     * @param a The new {@link Admin} object.
     *
     * @return True, iff the Admin was overwritten properly
     */
    boolean editAdmin(Admin a);
}