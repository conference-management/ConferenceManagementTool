package request;

public interface RequestObserver {

    /**
     * Updates the {@link Request} after the {@link RequestObservable} was changed.
     *
     * @param r The updates {@link Request}.
     *
     * @return True, iff the updates was successful.
     */
    boolean update(Request r);
}
