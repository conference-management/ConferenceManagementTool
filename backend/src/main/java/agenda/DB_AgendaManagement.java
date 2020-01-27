package agenda;

@SuppressWarnings("checkstyle:typename")
public interface DB_AgendaManagement extends AgendaObserver {

    /**
     * Observer for the Agenda. Updates the Agenda when the {@link AgendaObservable} changes.
     *
     * @param a The new {@link Agenda}.
     *
     * @return True, iff the agenda was updates properly.
     */
    public boolean update(Agenda a);


    /**
     * @return the {@link Agenda} object reconstructed from the database.
     */
    Agenda getAgenda();
}
