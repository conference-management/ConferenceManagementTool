package voting;

public interface VotingObserver {

    /**
     * Updates the {@link Voting} after the {@link VotingObservable} was changed.
     *
     * @param v The updates {@link Voting}.
     *
     * @return True, iff the updates was successful.
     */
    boolean update(Voting v);
}
