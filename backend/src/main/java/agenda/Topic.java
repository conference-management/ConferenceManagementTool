package agenda;

import com.google.gson.annotations.Expose;
import request.Requestable;
import utils.WriterBiasedRWLock;

import java.util.List;


public class Topic implements Requestable {

    @Expose
    protected Agenda subTopics;
    @Expose
    private String name;
    private Agenda parent;
    private WriterBiasedRWLock lock; //used for the agenda

    /**
     * Create a new Topic Object with the Name and the Parent of the Topic.
     *
     * @param name   the name of the topic
     * @param parent the parent agenda of the topic
     */
    public Topic(String name, Agenda parent) {
        this.parent = parent;
        this.name = name;
        this.lock = parent.lock;

        this.subTopics = new Agenda(parent, lock);
    }

    /**
     * Remove Topic from Parent List.
     *
     * @return true iff removing was successful
     */
    public boolean remove() {
        try {
            lock.getWriteAccess();
            return this.parent.removeTopic(this);
        } catch (InterruptedException e) {
            return false;
            //do nothing
        } finally {
            parent.notifyObservers();
            lock.finishWrite();
        }
    }

    /**
     * Rename Topic.
     *
     * @param name new Name
     */
    public void rename(String name) {
        try {
            lock.getWriteAccess();
            this.name = name;
            parent.notifyObservers();
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get the actual Topic Name.
     *
     * @return Topic Name
     */
    public String getName() {
        try {
            lock.getReadAccess();
            return this.name;
        } catch (InterruptedException e) {
            //do nothing
            return "";
        } finally {
            lock.finishRead();
        }
    }

    public String getRequestableName() {
        try {
            lock.getReadAccess();
            String res = (parent.getPreorder().isEmpty() ? "" : (parent.getPreorder() + ".")) + (parent.topics.indexOf(this) + 1) + " " + this.name;
            System.out.println(res);
            return res;
        } catch (InterruptedException e) {
            //do nothing
            return "";
        } finally {
            lock.finishRead();
        }
    }


    /**
     * Get the Topic at the position preorder.
     *
     * @param preorder Position in the  subTopics
     *
     * @return the topic matching the preorder string
     */
    protected Topic getTopicFromPreorderList(List<Integer> preorder) {
        if(preorder.isEmpty()) {
            return this;
        } else if(subTopics != null) {
            return subTopics.getTopicFromPreorderList(preorder);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Moves the topic to the given agenda, deleting it from the old parent agenda
     *
     * @param agenda the agenda to move
     * @param pos    the position to move the agenda to
     *
     * @return /
     */
    @Deprecated
    public boolean moveToNewAgenda(Agenda agenda, int pos) {
        try {
            lock.getWriteAccess();
            this.parent = agenda;
            return agenda.addTopic(this, pos);
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Reorder the Topic inside the Agenda
     *
     * @param pos the new position of the topic
     *
     * @return true iff it was successful
     */
    @Deprecated
    public boolean reorder(int pos) {
        try {
            lock.getWriteAccess();
            return this.parent.reOrderTopic(this, pos);
        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get all SubTopics
     *
     * @return Agenda with all SubTopics
     */
    public Agenda getSubTopics() {
        try {
            lock.getReadAccess();
            return this.subTopics;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * ToString Method to compare.
     *
     * @return name + subTopics
     */
    public String toString() {
        try {
            lock.getReadAccess();
            return name + " " + subTopics.toString();
        } catch (InterruptedException e) {
            return "";
        } finally {
            lock.finishRead();
        }
    }


}
