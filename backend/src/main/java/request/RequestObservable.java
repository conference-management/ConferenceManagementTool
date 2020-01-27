package request;

public interface RequestObservable {

    void register(RequestObserver o);

    void unregister(RequestObserver o);

    void notifyObservers();
}
