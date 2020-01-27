package agenda;

public interface AgendaObserver {

    /**
     * Observer for the Agenda. Updates the Agenda when the {@link AgendaObservable} changes.
     *
     * @param a The new {@link Agenda}.
     *
     * @return True, iff the agenda was updates properly.
     */
    boolean update(Agenda a);
}
