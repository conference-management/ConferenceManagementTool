package voting;

public interface VotingObservable {

    void register(VotingObserver o);

    void unregister(VotingObserver o);

    void notifyObservers();
}
