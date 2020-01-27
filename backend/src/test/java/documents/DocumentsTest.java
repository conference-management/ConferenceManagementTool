package documents;

import document.Document;
import main.Conference;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class DocumentsTest {
    Conference conf;
    File f;

    @Before
    public void createConference() {
        conf = new Conference(true);
        String pathString = "src/test/resources/test.txt";
        f = new File(pathString);
        System.out.println(f.getAbsoluteFile());
        if(f.exists()) {
            f.delete();
        }

        try {
            if(!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathString));
            writer.write("Test data\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not initialize test environment");
        }

    }

    @Test
    public void singleDocumentUpload() {
        conf.updateDocument("test.txt", "txt", f, true);
        Document doc = conf.getDocument("test.txt");
        if(doc.getRevisionNumber() != 1) {
            fail("New documents should have revision number 1");
        }
        assertArrayEquals("Document content does not fit", conf.getDocumentContent("test.txt"), "Test data\n".getBytes());
    }

    @Test
    public void doubleDocumentUpload() {
        conf.updateDocument("test.txt", "txt", f, true);
        try {
            conf.updateDocument("test.txt", "txt", f, true);
            fail("Double document creation is illegal");
        } catch (IllegalArgumentException e) {
            Document doc = conf.getDocument("test.txt");
            if(doc.getRevisionNumber() != 1) {
                fail("New documents should have revision number 1");
            }
            assertArrayEquals("Document content does not fit", conf.getDocumentContent("test.txt"), "Test data\n".getBytes());
        }
    }

    @Test
    public void updateInexistent() {
        try {
            conf.updateDocument("test", "txt", f, false);
            fail("This document does not exist");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void updateInexistent2() {
        try {
            conf.updateDocument("test", "txt", f, true);
            conf.deleteDocument("test");
            conf.updateDocument("test", "txt", f, false);

            fail("This document does not exist");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void documentMultiUpdate() {
        conf.updateDocument("test.txt", "txt", f, true);
        int updateCount = 100;
        try {
            for(int i = 0; i < updateCount; i++) {
                FileWriter fw = new FileWriter(f);
                fw.write("Adding " + i);
                fw.close();
                conf.updateDocument("test.txt", "txt", f, false);
            }
        } catch (IOException e) {
            fail("This test seems to be broken. Sorry");
        }

        Document doc = conf.getDocument("test.txt");
        if(doc.getRevisionNumber() != 101) {
            fail("Document should have revision number 101");
        }
        assertArrayEquals("Document content does not fit", conf.getDocumentContent("test.txt"), "Adding 99".getBytes());


    }

    @Test
    public void deletedDocumentRecreate() {
        conf.updateDocument("test.txt", "txt", f, true);
        conf.deleteDocument("test.txt");

        String pathString = "src/test/resources/test.txt";
        f = new File(pathString);
        System.out.println(f.getAbsoluteFile());
        if(f.exists()) {
            f.delete();
        }

        try {
            if(!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathString));
            writer.write("Test data\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not initialize test environment");
        }

        conf.updateDocument("test.txt", "txt", f, true);

        Document doc = conf.getDocument("test.txt");
        if(doc.getRevisionNumber() != 1) {
            fail("New documents should have revision number 1");
        }
        assertArrayEquals("Document content does not fit", conf.getDocumentContent("test.txt"), "Test data\n".getBytes());
    }

    @Test
    public void uploadLarge() {
        String pathString = "src/test/resources/large.txt";
        f = new File(pathString);
        System.out.println(f.getAbsoluteFile());
        if(f.exists()) {
            f.delete();
        }

        try {
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathString));
            String s = "a".repeat(501);
            writer.write("");
            for(int i = 0; i < 1024 * 1024; i++) {
                writer.append(s);
            }

            writer.close();
        } catch (IOException e) {
            fail("Could not initialize test environment");
        }

        try {
            conf.updateDocument("test", "txt", f, true);
            fail("Managed to upload document of size " + f.length());
        } catch (IllegalArgumentException e) {

        }


    }


}
