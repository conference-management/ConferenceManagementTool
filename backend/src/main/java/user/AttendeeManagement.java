package user;

import utils.Pair;

import java.util.List;

public interface AttendeeManagement {

    void addAttendee(Attendee a);

    List<Attendee> getAllAttendees();

    Attendee getAttendeeData(int userID);

    void removeAttendee(int userID);

    void logoutUser(int userID);

    void editAttendee(Attendee attendee);

    void generateNewUserPassword(int userID);

    void generateNewUserToken(int userID);

    void generateAllMissingUserPasswords();

    Pair<User, String> getUserPassword(int userID);

    List<Pair<User, String>> getAllUsersPasswords();

    boolean logoutAllUsers();

}
