package agenda;

public interface AgendaObservable {

    void register(AgendaObserver o);

    void unregister(AgendaObserver o);

    void notifyObservers();
}
