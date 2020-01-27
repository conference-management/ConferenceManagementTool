package database;

import document.DB_DocumentManagement;
import document.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("checkstyle:typename")
public class DB_DocumentManager extends DB_Controller implements DB_DocumentManagement {

    public DB_DocumentManager(String url) {
        super(url);
    }

    /**
     * Initializes the documents tables for the database
     */
    @Override
    protected void init() {
        String documentTable = "CREATE TABLE IF NOT EXISTS documents (\n"
                + "     path TEXT NOT NULL,\n"
                + "     documentName TEXT NOT NULL UNIQUE,\n"
                + "     revision INTEGER NOT NULL\n"
                + ");";
        Connection connection = openConnection();
        try {
            connection.createStatement().execute(documentTable);
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            System.err.println(e.getMessage());
        }
        closeConnection(connection);
    }


    /**
     * Look if a Documentname is already used inside the DocumentDatabase.
     *
     * @param name The Documentname to search into the DocumentDatabase.
     *
     * @return True, iff the Documentname is already used in DocumentDatabase.
     */
    @Override
    public boolean isNameAlreadyUsed(String name) {
        List<Document> documentList = this.getAllDocuments();
        for(int i = 0; i < documentList.size(); i++) {
            String docname = documentList.get(i).getName();
            if(docname.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new {@link Document} to the database.
     *
     * @param document The new {@link Document}.
     *
     * @return True, iff it was successfully added.
     */
    @Override
    public boolean addDocument(Document document) {
        Connection connection = this.openConnection();
        String sqlstatement = "INSERT INTO documents(path, documentName, revision) VALUES(?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setString(1, document.getFile().getAbsolutePath());
            stmt.setString(2, document.getName());
            stmt.setInt(3, 1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("An error occurred while adding a new document.");
            System.err.println(ex.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }

    /**
     * Delete a {@link Document} with the given name.
     *
     * @param name The name of the document.
     *
     * @return True, iff it was successfully deleted.
     */
    @Override
    public boolean deleteDocument(String name) {
        if(!this.isNameAlreadyUsed(name)) {
            return false;
        }
        Connection connection = this.openConnection();
        String sqlstatement = "DELETE FROM documents WHERE documentName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("An error occurred while deleting a document.");
            System.err.println(e.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }

    /**
     * Updates a given document name.
     *
     * @param oldName The old name of the the {@link Document}.
     * @param newName The new name of the {@link Document}.
     *
     * @return True, iff it was successfully updated.
     */
    @Override
    public boolean updateDocument(String oldName, String newName) {
        if(!this.isNameAlreadyUsed(oldName)) {
            return false;
        }
        Connection connection = this.openConnection();
        String revisionNumber = "SELECT revision FROM documents WHERE documentName = ?";
        String sqlstatement = "UPDATE documents SET revision = ? , "
                + "documentName = ?"
                + " WHERE documentName = ?";
        try (PreparedStatement rev = connection.prepareStatement(revisionNumber)) {
            rev.setString(1, oldName);
            try (ResultSet res = rev.executeQuery();
                 PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
                stmt.setInt(1, res.getInt("revision") + 1);
                stmt.setString(2, newName);
                stmt.setString(3, oldName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while updating a document.");
            System.err.println(e.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        return true;
    }

    /**
     * Reconstructs a {@link Document} object with the given name from the database.
     *
     * @param name The name of the {@link Document}.
     *
     * @return the reconstructed {@link Document}.
     */
    @Override
    public Document getDocument(String name) {
        Connection connection = this.openConnection();
        String sqlstatement = "SELECT * FROM documents WHERE documentName = ?";
        Document document = null;
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.setString(1, name);
            try (ResultSet doc = stmt.executeQuery()) {
                document = new Document(doc.getString("path"),
                        doc.getString("documentName"),
                        doc.getInt("revision"));
            }
        } catch (SQLException ex) {
            System.err.println("An error occurred while reconstructing a document.");
            System.err.println(ex.getMessage());
        } finally {
            this.closeConnection(connection);
        }
        return document;
    }

    /**
     * @return a list of all reconstructed {@link Document}s in the database.
     */
    @Override
    public List<Document> getAllDocuments() {
        Connection connection = this.openConnection();
        List<Document> documents = new LinkedList<>();
        String sqlstatement = "SELECT * FROM documents";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement);
             ResultSet table = stmt.executeQuery()) {
            while(table.next()) {
                String name = table.getString("documentName");
                String url = table.getString("path");
                int revision = table.getInt("revision");
                documents.add(new Document(url, name, revision));
            }
        } catch (SQLException ex) {
            System.err.println("An error occurred while reconstructing all documents.");
            System.err.println(ex.getMessage());
            return null;
        } finally {
            this.closeConnection(connection);
        }
        return documents;
    }
}
