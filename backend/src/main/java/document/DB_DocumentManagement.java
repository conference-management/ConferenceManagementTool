package document;

import java.util.List;

@SuppressWarnings("checkstyle:typename")
public interface DB_DocumentManagement {

    /**
     * Look if a Documentname is already used inside the DocumentDatabase.
     *
     * @param name The Documentname to search into the DocumentDatabase.
     *
     * @return True, iff the Documentname is already used in DocumentDatabase.
     */
    boolean isNameAlreadyUsed(String name);

    /**
     * Add a new {@link Document} to the database.
     *
     * @param document The new {@link Document}.
     *
     * @return True, iff it was successfully added.
     */
    boolean addDocument(Document document);

    /**
     * Delete a {@link Document} with the given name.
     *
     * @param name The name of the document.
     *
     * @return True, iff it was successfully deleted.
     */
    boolean deleteDocument(String name);

    /**
     * Updates a given document name.
     *
     * @param oldName The old name of the the {@link Document}.
     * @param newName The new name of the {@link Document}.
     *
     * @return True, iff it was successfully updated.
     */
    boolean updateDocument(String oldName, String newName);

    /**
     * Reconstructs a {@link Document} object with the given name from the database.
     *
     * @param name The name of the {@link Document}.
     *
     * @return the reconstructed {@link Document}.
     */
    Document getDocument(String name);

    /**
     * @return a list of all reconstructed {@link Document}s in the database.
     */
    List<Document> getAllDocuments();
}
