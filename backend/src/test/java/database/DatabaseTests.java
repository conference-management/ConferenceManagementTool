package database;

import agenda.DB_AgendaManagement;
import document.DB_DocumentManagement;
import org.junit.After;
import org.junit.Before;
import request.DB_RequestManagement;
import user.DB_UserManagement;
import voting.DB_VotingManagement;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public abstract class DatabaseTests {

    private URI path;

    private DB_UserManagement generalUserDB;
    private DB_AgendaManagement agendaDB;
    private DB_DocumentManagement documentDB;
    private DB_RequestManagement requestDB;
    private DB_VotingManagement votingDB;

    @Before
    public void init() throws IOException {
        String path = "testdb/database.db";

        this.generalUserDB = new DB_UserManager(path);
        this.agendaDB = new DB_AgendaManager(path);
        this.documentDB = new DB_DocumentManager(path);
        this.requestDB = new DB_RequestManager(path);
        this.votingDB = new DB_VotingManager(path);
    }

    @After
    public void cleanup() {
        path = Paths.get("testdb/database.db").toUri();
        new File(path).delete();
    }

    protected DB_UserManagement getGeneralUserDB() {
        return this.generalUserDB;
    }


    protected DB_AgendaManagement getAgendaDB() {
        return this.agendaDB;
    }

    protected DB_DocumentManagement getDocumentDB() {
        return this.documentDB;
    }

    protected DB_RequestManagement getRequestDB() {
        return this.requestDB;
    }

    protected DB_VotingManagement getVotingDB() {
        return this.votingDB;
    }

}
