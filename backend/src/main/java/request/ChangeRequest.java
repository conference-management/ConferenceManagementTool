package request;

import com.google.gson.annotations.Expose;
import user.User;

public class ChangeRequest extends Request {

    @Expose
    private boolean approved;
    @Expose
    private String message;

    /**
     * Construct a ChangeRequest Object with the following Parameters, especially without fixed id (next free Id is used):
     *
     * @param requester
     * @param topic
     * @param timestamp
     * @param message
     */
    public ChangeRequest(User requester, Requestable topic, long timestamp, String message) {
        super(topic, requester, timestamp);
        this.message = message;

        this.approved = false;
    }

    /**
     * Construct a ChangeRequest Object with the following Parameters, especially with fixed id:
     *
     * @param id
     * @param requester
     * @param topic
     * @param timestamp
     * @param message
     */
    public ChangeRequest(int id, User requester, Requestable topic, long timestamp, String message) {
        super(id, topic, requester, timestamp);
        this.message = message;
        this.approved = false;
    }

    /**
     * Revert a aprroved ChangeRequest
     */
    @Override
    public void reopen() {
        try {
            lock.getWriteAccess();
            this.open = true;
            this.approved = false;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }

    @Override
    public Request shallowClone() {
        ChangeRequest res = new ChangeRequest(ID, getRequester(), new SimpleRequestable(requestable.getRequestableName()),
                getTimeStamp(),
                getMessage());
        if(!isOpen()) {
            if(isApproved()) {
                res.approve();
            } else {
                res.disapprove();
            }
        }
        return res;
    }

    /**
     * Get the Reuqest Message
     *
     * @return Message
     */
    public String getMessage() {

        try {
            lock.getReadAccess();
            return this.message;
        } catch (InterruptedException e) {
            return "";
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Checks if a ChangeRequest is approved.
     *
     * @return true iff CR is approved
     */
    public boolean isApproved() {
        try {
            lock.getReadAccess();
            return this.approved;
        } catch (InterruptedException e) {
            return this.approved;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Approve a ChangeRequest
     */
    public void approve() {
        try {
            lock.getWriteAccess();
            this.approved = true;
            this.open = false;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }

    /**
     * Disapprove a ChangeRequest
     */
    public void disapprove() {
        try {
            lock.getWriteAccess();
            this.approved = false;
            this.open = false;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }


}
