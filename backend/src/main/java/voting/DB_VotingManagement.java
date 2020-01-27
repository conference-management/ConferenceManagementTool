package voting;

import java.util.List;

@SuppressWarnings("checkstyle:typename")
public interface DB_VotingManagement extends VotingObserver {

    /**
     * Adds a new, already finished, {@link Voting} to the database.
     *
     * @param v The {@link Voting} to be added.
     *
     * @return True, iff the {@link Voting} was successfully added.
     */
    boolean addVoting(Voting v);

    /**
     * Reconstructs a given {@link Voting} from the database.
     *
     * @param ID The ID of the {@link Voting}.
     *
     * @return the reconstructed {@link Voting}.
     */
    Voting getVoting(int ID);

    /**
     * @return a list of all reconstructed {@link Voting}s from the database.
     */
    List<Voting> getVotings();
}
