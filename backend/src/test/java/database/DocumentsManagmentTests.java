package database;

import document.DB_DocumentManagement;
import document.Document;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class DocumentsManagmentTests extends DatabaseTests {

    @Test
    public void addAndGetDocuments() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertNull(docDb.getDocument("wrongname"));
        assertEquals(docDb.getDocument(testdoc.getName()).getName(), testdoc.getName());
        assertEquals(docDb.getDocument(testdoc.getName()).getRevisionNumber(), testdoc.getRevisionNumber());
        assertEquals(docDb.getDocument(testdoc.getName()).getPath(), testdoc.getPath());
        assertEquals(docDb.getAllDocuments().get(0).getName(), testdoc.getName());
        assertEquals(docDb.getAllDocuments().get(0).getRevisionNumber(), testdoc.getRevisionNumber());
        assertEquals(docDb.getAllDocuments().get(0).getPath(), testdoc.getPath());
    }

    @Test
    public void deleteDocuments() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertTrue(docDb.deleteDocument(testdoc.getName()));
        assertNull(docDb.getDocument(testdoc.getName()));
        List<Document> documents = new LinkedList<>();
        assertEquals(docDb.getAllDocuments(), documents);
    }

    @Test
    public void deleteWrongDocuments() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertFalse(docDb.deleteDocument("wrongname"));
        assertEquals(docDb.getDocument(testdoc.getName()).getName(), testdoc.getName());
    }

    @Test
    public void updateDocuments() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        Document testdoc2 = new Document("/db/test/documentsfolder2", "TestDocument2");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertTrue(docDb.addDocument(testdoc2));
        assertTrue(docDb.updateDocument(testdoc.getName(), "UpdateDocument"));
        assertEquals(docDb.getDocument("UpdateDocument").getName(), "UpdateDocument");
        assertTrue(docDb.getDocument("UpdateDocument").getRevisionNumber() == 2);
        assertFalse(docDb.updateDocument("wrongname", "newname"));
        assertFalse(docDb.updateDocument("UpdateDocument", "TestDocument2"));
    }

    @Test
    public void documentnameIsAlreadyUsed() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertTrue(docDb.isNameAlreadyUsed("TestDocument"));
        assertFalse(docDb.isNameAlreadyUsed("UpdateDocument"));
    }

    @Test
    public void addSameDocumenttwice() {
        Document testdoc = new Document("/db/test/documentsfolder", "TestDocument");
        DB_DocumentManagement docDb = this.getDocumentDB();

        assertTrue(docDb.addDocument(testdoc));
        assertFalse(docDb.addDocument(testdoc));


    }
}
