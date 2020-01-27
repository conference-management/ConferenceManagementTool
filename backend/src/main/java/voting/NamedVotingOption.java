package voting;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class NamedVotingOption extends VotingOption {

    @Expose
    public List<Integer> voters = new ArrayList<>();
    @Expose
    public List<String> votersname = new ArrayList<>();

    private List<String> privatevotersname = new ArrayList<>();
    private List<Integer> privateVoters = new ArrayList<>();

    /**
     * Database reconstructor to remove the need to iterate again.
     *
     * @param ID     The ID of this option.
     * @param name   The name of this option.
     * @param voters The userIDs of the users who voted for this option.
     */
    public NamedVotingOption(int ID, String name, List<Integer> voters, List<String> votersname) {
        this(ID, name);
        this.voters = voters;
        this.votersname = votersname;
    }

    /**
     * Standard constructor.
     */
    public NamedVotingOption(int ID, String name) {
        this.optionID = ID;
        this.name = name;
    }

    /**
     * Add user with userID and name to the list of user that vote for the NamedVotingOption.
     *
     * @param userID
     * @param name
     */
    @Override
    protected void addVote(int userID, String name) {
        privateVoters.add(userID);
        privatevotersname.add(name);
    }


    /**
     * Get Number of User that vote for the NamedVotingOption.
     * return Number of User or -1
     */
    @Override
    public int getCurrentResult() {
        return voters.size();
        /*
        try {
            lock.getReadAccess();
            return voters.size();
        }
        catch (InterruptedException e){
            return -1;
        }
        finally {
            lock.finishRead();
        }*/
    }

    /**
     * Get UserIDs of User that vote for the NamedVotingOption.
     *
     * @return a list of the user ids of the voters
     */
    @Override
    public List<Integer> getVoters() {
        return new ArrayList<>(voters);
        /*
        try {
            lock.getReadAccess();
            return new ArrayList<>(voters);
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }*/
    }

    /**
     * Publish the Number of User that vote for the NamedVotingOption.
     */
    @Override
    protected void publishVotes() {
        try {
            lock.getWriteAccess();
            setPublicVotes(privateVoters.size());
            voters.addAll(privateVoters);
            votersname.addAll(privatevotersname);
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            lock.finishWrite();
        }
    }
}
