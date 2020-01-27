package user;

import com.google.gson.annotations.Expose;

public class Attendee extends User {

    private int numberOfDevices;
    @Expose
    private boolean present;

    /**
     * Construct an Attendee object with the following attributes, especially without explicit ID:
     * Use the next free, unique ID in the Userdatabase to construct the Attendee
     *
     * @param name      unique pre and lastname of the Attendee
     * @param email     unique email of the Attendee
     * @param userName  unique userName of the Attendee (needed to login)
     * @param group     of the Attendee
     * @param residence unique residence of the Attendee (String with plz city, street for example)
     * @param function  of the Attendee in the Conference
     */
    public Attendee(String name, String email, String userName, String group, String residence, String function) {
        this(name, email, userName, group, residence, function, nextFreeId());
    }

    /**
     * Construct an Attendee object with the following attributes, especially with explicit ID:
     *
     * @param name      unique pre and lastname of the Attendee
     * @param email     unique email of the Attendee
     * @param userName  unique userName of the Attendee (needed to login)
     * @param group     of the Attendee
     * @param residence unique residence of the Attendee (String with plz city, street for example)
     * @param function  of the Attendee in the Conference
     * @param ID        unique and not already used ID for the Attendee
     */
    public Attendee(String name, String email, String userName, String group, String residence, String function, int ID) {
        super(name, email, userName, group, function, residence, ID);
        this.numberOfDevices = 0;
        this.present = false;
    }

    /**
     * increase the numberOfDevices for the Attendee object.
     * needs to be done for may have.
     */
    public void additionalDevice() {
        try {
            lock.getWriteAccess();
            numberOfDevices++;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Change that user is present in the Conderence.
     */
    public void attendedConference() {
        try {
            lock.getWriteAccess();
            this.present = true;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Logout user - user isn't present in the conference.
     */
    public void logout() {
        this.present = false;
    }

    /**
     * Check if user is currently present in the conference.
     *
     * @return true iff user is present else false
     */
    public boolean isPresent() {
        try {
            lock.getReadAccess();
            return present;
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the attendence of a user.
     *
     * @param present true iff a user is present
     */
    public void setPresent(boolean present) {
        try {
            lock.getWriteAccess();
            this.present = present;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the current Number of Devices the user use.
     *
     * @return Number of Devices
     */
    public int getNumberOfDevices() {
        try {
            lock.getReadAccess();
            return numberOfDevices;
        } catch (InterruptedException e) {
            return -1;
        } finally {
            lock.finishRead();
        }
    }
}
