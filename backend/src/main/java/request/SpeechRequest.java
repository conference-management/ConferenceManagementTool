package request;

import user.User;

public class SpeechRequest extends Request {

    /**
     * Construct a SpeechRequest with the following Parameter, especially without fixed id (next free Id is used):
     *
     * @param requester
     * @param topic
     * @param timestamp
     */
    public SpeechRequest(User requester, Requestable topic, long timestamp) {
        super(topic, requester, timestamp);
    }

    /**
     * Construct a ChangeRequest Object with the following Parameters, especially with fixed id:
     *
     * @param id
     * @param requester
     * @param topic
     * @param timestamp
     */
    public SpeechRequest(int id, User requester, Requestable topic, long timestamp) {
        super(id, topic, requester, timestamp);
    }

    @Override
    public void reopen() {
        try {
            lock.getWriteAccess();
            this.open = true;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }

    @Override
    public Request shallowClone() {
        SpeechRequest req = new SpeechRequest(ID, getRequester(), new SimpleRequestable(requestable.getRequestableName()),
                getTimeStamp());
        if(!isOpen()) {
            req.close();
        }
        return req;
    }

    /**
     * Close SpeechRequest, if a user spoke to a topic.
     */
    public void close() {
        try {
            lock.getWriteAccess();
            this.open = false;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }
}
