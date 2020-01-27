package document;

import com.google.gson.annotations.Expose;
import request.Requestable;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Document implements Requestable {

    private final File file;
    @Expose
    private final String name;
    @Expose
    private int revisionNumber = 1;
    private Lock lock = new ReentrantLock();

    /**
     * Create a Document Object with Name, Path, Revisionnumber. This constructor is used for changed Document with specific
     * RevisionNumber.
     *
     * @param path           the path of the document
     * @param name           the name of the document
     * @param revisionNumber the revisionNumber of the document
     */
    public Document(String path, String name, int revisionNumber) {
        this(path, name);
        this.revisionNumber = revisionNumber;
    }

    /**
     * Create  a new Document Object with Name and Path of the Document. In this constructor the Document is created the first time.
     * So the revisionNumber is automatically 1.
     *
     * @param path the path of the document
     * @param name the name of the document
     */
    public Document(String path, String name) {
        file = new File(path);
        this.name = name;
    }

    /**
     * Get Document Name.
     *
     * @return the name of the document
     */
    @Override
    public String getRequestableName() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Get RevisionNumber of the Document.
     *
     * @return the revisionNumber of the document
     */
    public Integer getRevisionNumber() {
        try {
            lock.lock();
            return this.revisionNumber;
        } finally {
            lock.unlock();
        }

    }

    /**
     * Get Path of the Document.
     *
     * @return the path of the document
     */
    public String getPath() {return this.file.getAbsolutePath();}

    public File getFile() {
        return file;
    }

    /**
     * Increase Revisionnumber, if the Document was updated.
     */
    public void incrementRevision() {
        lock.lock();
        revisionNumber++;
        lock.unlock();
    }
}
