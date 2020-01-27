package voting;

import com.google.gson.annotations.Expose;
import utils.WriterBiasedRWLock;

import java.util.List;

public abstract class VotingOption {

    protected WriterBiasedRWLock lock;
    @Expose
    protected String name;
    @Expose
    protected int optionID;
    @Expose
    protected int publicVotes;
    private Voting voting;

    /**
     * Set voting for which the voting option is valid
     *
     * @param v voting
     */
    public void setParent(Voting v) {
        voting = v;
        lock = v.lock;
    }

    /**
     * Get VoteOptionID from VotingOption.
     *
     * @return VoteOptionID
     */
    public int getOptionID() {
        return this.optionID;
        /*try {
            lock.getReadAccess();
            System.out.println(this.optionID);
            return this.optionID;
        }
        catch (InterruptedException e){
            return -1;
        }
        finally {
            lock.finishRead();
        }*/
    }

    /*try {
        lock.getReadAccess();
        return this.name;
    } catch (InterruptedException e) {
        return null;
    } finally {
        lock.finishRead();
    }
}

/**
 * Set the VoteOptionID to newID.
 * @param newID
 */
    public void setOptionID(int newID) {
        try {
            lock.getWriteAccess();
            this.optionID = newID;
        } catch (InterruptedException e) {

        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Change Name of the VoteOption
     *
     * @param newName for the VoteOption
     */
    public void changeName(String newName) {
        try {
            lock.getWriteAccess();
            this.name = newName;
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            lock.finishWrite();
        }
    }

    /**
     * Get Name of the VoteOption
     *
     * @return VoteOptionName
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the number of users that vote for this vote to votes.
     *
     * @param votes
     */
    protected void setPublicVotes(int votes) {
        this.publicVotes = votes;
    }

    abstract protected void addVote(int userID, String name);

    abstract public int getCurrentResult();

    abstract public List<Integer> getVoters();

    protected void notifyObservers() {
        voting.notifyObservers();
    }

    protected abstract void publishVotes();
}
