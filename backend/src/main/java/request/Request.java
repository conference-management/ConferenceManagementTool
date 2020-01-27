package request;

import com.google.gson.annotations.Expose;
import user.User;
import utils.WriterBiasedRWLock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Request implements RequestObservable {

    protected static int lastUsedID = 0;
    //Locking static variable lastUsedID for multiple admins
    private static Lock idLock = new ReentrantLock();
    @Expose
    public final int ID;
    @Expose
    protected final long timeStamp;
    protected WriterBiasedRWLock lock = new WriterBiasedRWLock();
    @Expose
    protected Requestable requestable;
    @Expose
    protected User requester;
    @Expose
    protected boolean open;
    private ConcurrentHashMap<RequestObserver, Boolean> observers = new ConcurrentHashMap<>(); // a map backed hashset

    /**
     * Construct a Request Object with the following Parameters, especially without Id (next free Id is used):
     */
    protected Request(Requestable topic, User requester, long timestamp) {
        this(getNextID(), topic, requester, timestamp);
    }

    /**
     * Construct a Request Object with the following Parameters, especially with fixed id:
     *
     * @param id        the id of the request
     * @param topic     the topic or document the request refers to
     * @param requester the requester
     * @param timestamp the unix epoch of the time the request was submitted
     */
    protected Request(int id, Requestable topic, User requester, long timestamp) {
        try {
            idLock.lock();

            if(id > lastUsedID) {
                lastUsedID = id;
            }

            this.ID = id;
            this.requestable = topic;
            this.requester = requester;
            this.timeStamp = timestamp;

            this.open = true;

        } finally {
            idLock.unlock();
        }
    }

    /**
     * Get next free ID to create Request.
     *
     * @return free ID
     */
    protected static int getNextID() {
        try {
            idLock.lock();
            lastUsedID++;
            return lastUsedID;
        } finally {
            idLock.unlock();
        }
    }

    public abstract void reopen();

    /**
     * Get the TimeStamp of the Request
     *
     * @return Timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Get the Requestable of the Request, for example Dokumentname or Agenda topic.
     *
     * @return Requestable
     */
    public Requestable getRequestable() {
        try {
            lock.getReadAccess();
            return requestable;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Get the user who submit the Requets.
     *
     * @return User
     */
    public User getRequester() {
        try {
            lock.getReadAccess();
            return this.requester;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Checks if the Request is still open or an Admin has closed the Request.
     *
     * @return true iff the Request is still open
     */
    public boolean isOpen() {
        try {
            lock.getReadAccess();
            return this.open;
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.finishRead();
        }
    }

    public int getID() {
        return ID;
    }

    public abstract Request shallowClone();

    @Override
    public void register(RequestObserver o) {
        observers.put(o, true);
    }

    @Override
    public void unregister(RequestObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        observers.forEachKey(2, o -> o.update(this));
    }
}
