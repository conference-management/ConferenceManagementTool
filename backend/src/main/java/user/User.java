package user;

import com.google.gson.annotations.Expose;
import utils.WriterBiasedRWLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class User {

    private static int idAt = 0;
    private static Lock idsLock = new ReentrantLock();
    @Expose
    protected final String userName;
    @Expose
    protected final int ID;
    @Expose
    protected String name;
    @Expose
    protected String group;
    @Expose
    protected String function;
    @Expose
    protected String residence;
    @Expose
    protected String email;
    protected WriterBiasedRWLock lock = new WriterBiasedRWLock();

    /**
     * Use to create Attendee or Admin object with the following attributes:
     *
     * @param name      unique pre and lastname of the User
     * @param email     unique email of the User
     * @param userName  unique userName of the User (needed to login)
     * @param group     of the User
     * @param residence unique residence of the User (String with plz city, street for example)
     * @param function  of the User in the Conference
     * @param ID        unique and not already used ID for the User
     */
    public User(String name, String email, String userName, String group, String function, String residence, int ID) {
        this.name = name;
        this.email = email;
        this.group = group;
        this.residence = residence;
        this.userName = userName;
        this.function = function;
        try {
            idsLock.lock();
            this.ID = ID;
            if(ID > idAt) {
                idAt = ID;
            }
        } finally {
            idsLock.unlock();
        }
    }

    /**
     * Calculate next free ID, if the userIDs was created only with this function.
     *
     * @return nextFreeId
     */
    protected static int nextFreeId() {
        try {
            idsLock.lock();
            idAt++;
            return idAt;
        } finally {
            idsLock.unlock();
        }
    }

    /**
     * Get the Name of the user
     *
     * @return Name
     */
    public String getName() {
        try {
            lock.getReadAccess();
            return name;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the Name of the user
     *
     * @param name Name of the user
     */
    public void setName(String name) {
        try {
            lock.getWriteAccess();
            this.name = name;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the Username of the user
     *
     * @return Username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get the Group of the user
     *
     * @return Group
     */
    public String getGroup() {
        try {
            lock.getReadAccess();
            return group;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the Group of the user
     *
     * @param group Group of the user
     */
    public void setGroup(String group) {
        try {
            lock.getWriteAccess();
            this.group = group;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the Function of the user
     *
     * @return Function
     */
    public String getFunction() {
        try {
            lock.getReadAccess();
            return function;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the Function of the user
     *
     * @param function Function of the user
     */
    public void setFunction(String function) {
        try {
            lock.getWriteAccess();
            this.function = function;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the Residence of the user
     *
     * @return Residence
     */
    public String getResidence() {
        try {
            lock.getReadAccess();
            return residence;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the Residence of the user
     *
     * @param residence Residence of the user
     */
    public void setResidence(String residence) {
        try {
            lock.getWriteAccess();
            this.residence = residence;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the ID of the user
     *
     * @return ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Get the Email of the user
     *
     * @return Email
     */
    public String getEmail() {
        try {
            lock.getReadAccess();
            return email;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Set the Email of the user
     *
     * @param email Email of the user
     */
    public void setEmail(String email) {
        try {
            lock.getWriteAccess();
            this.email = email;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    @Override
    public String toString() {
        try {
            lock.getReadAccess();
            StringBuilder sb = new StringBuilder();
            sb.append("Name : ").append(name).append("\n").
                    append("Email : ").append(email).append("\n").
                    append("Residence : ").append(residence).append("\n").
                    append("Group : ").append(group).append("\n").
                    append("Function : ").append(function).append("\n").
                    append("Username : ").append(userName);
            return sb.toString();
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishRead();
        }
        return null;
    }
}
