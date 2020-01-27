package config;

import main.Conference;
import main.ConfigParser;
import org.junit.Test;
import voting.AnonymousVotingOption;
import voting.Voting;
import voting.VotingOption;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ConfigParserTests {

    @Test
    public void defaultValues() {
        String config = "name : 'SE Presentation\\#3' # mandatory field. Presents a possible escape sequence\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        Conference c = ConfigParser.parseConfigFile(config);

    }

    @Test
    public void multiKey() {
        String config = "name : 'SE Presentation\\#3'\n" +
                "name : 'Test'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        try {
            Conference c = ConfigParser.parseConfigFile(config);
            fail("Duplicated field name");
        } catch (IllegalArgumentException e) {

        }

    }

    @Test
    public void noAdmins() {
        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n";

        try {
            Conference c = ConfigParser.parseConfigFile(config);
            fail("Admins missing");
        } catch (IllegalArgumentException e) {

        }

    }


    @Test
    public void missingMandatory() {
        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "admin : 'name:email:group:residence:function' ";

        try {
            Conference c = ConfigParser.parseConfigFile(config);
            fail("Admins missing");
        } catch (IllegalArgumentException e) {

        }

    }


    @Test
    public void missingValue() {
        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        try {
            Conference c = ConfigParser.parseConfigFile(config);
            fail("Admins missing");
        } catch (IllegalArgumentException e) {

        }

    }

    @Test
    public void pastTime() {
        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '" + ((System.currentTimeMillis() / 1000) - 1000) + "' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        try {
            Conference c = ConfigParser.parseConfigFile(config);
            fail("Admins missing");
        } catch (IllegalArgumentException e) {

        }

    }

    @Test
    public void persistency() {
        File f = new File("./test.txt");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("Test");
            fw.close();
        } catch (IOException e) {

            fail("Broken test");
        }


        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        Conference c = ConfigParser.parseConfigFile(config);
        Conference finalC = c;
        c.getAllDocuments().forEach(d -> finalC.deleteDocument(d.getName()));
        c.updateDocument("test.txt", "txt", f, true);


        List<VotingOption> standardAnonymousOptions = new ArrayList<>();
        standardAnonymousOptions.add(new AnonymousVotingOption(0, "yes"));
        standardAnonymousOptions.add(new AnonymousVotingOption(1, "no"));

        Voting annon = new Voting(standardAnonymousOptions, "Is this the real life?", false, 100);

        c.addVoting(annon);

        c = ConfigParser.parseConfigFile(config);
        assertArrayEquals("Wrong document content", "Test".getBytes(), c.getDocumentContent("test.txt"));
        ;
        assertEquals("Missing vote", 0, c.getVotings().size());

    }

    @Test
    public void persistency2() {
        File f = new File("./root");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("Test");
            fw.close();
        } catch (IOException e) {

            fail("Broken test");
        }


        String config = "name : 'SE Presentation\\#3'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        Conference c = ConfigParser.parseConfigFile(config);
        Conference finalC = c;
        c.getAllDocuments().forEach(d -> finalC.deleteDocument(d.getName()));

        c.updateDocument("test.txt", "txt", f, true);


        List<VotingOption> standardAnonymousOptions = new ArrayList<>();
        standardAnonymousOptions.add(new AnonymousVotingOption(0, "yes"));
        standardAnonymousOptions.add(new AnonymousVotingOption(1, "no"));

        Voting annon = new Voting(standardAnonymousOptions, "Is this the real life?", false, 100);

        c.addVoting(annon);

        config = "name : 'New name'\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";


        c = ConfigParser.parseConfigFile(config);
        assertArrayEquals("Wrong document content", "Test".getBytes(), c.getDocumentContent("test.txt"));
        ;
        assertEquals("Missing vote", 0, c.getVotings().size());
        assertEquals("Wrong name", "New name", c.getName());

    }

    @Test
    public void adminChange() {
        String config = "name : 'SE Presentation\\#3' # mandatory field. Presents a possible escape sequence\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name1:email1:group:residence:function' \n" +
                "admin : 'name2:email2:group:residence:function' \n" +
                "admin : 'name3:email3:group:residence:function' ";

        Conference c = ConfigParser.parseConfigFile(config);

        config = "name : 'SE Presentation\\#3' # mandatory field. Presents a possible escape sequence\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name1:email1:group:residence:function' \n" +
                "admin : 'name4:email2:group:residence:function' \n" +
                "admin : 'name5:email3:group:residence:function' ";

        c = ConfigParser.parseConfigFile(config);

        c.getAllAdmins().forEach(a -> {
            if(a.getName().equals("name2") || a.getName().equals("name2")) {
                fail("found admin which should have been deleted");
            }
        });

        AtomicBoolean found1 = new AtomicBoolean(false);
        AtomicBoolean found2 = new AtomicBoolean(false);
        AtomicBoolean found3 = new AtomicBoolean(false);

        c.getAllAdmins().forEach(a -> {
            if(a.getName().equals("name1")) {
                found1.set(true);
            }
            if(a.getName().equals("name4")) {
                found2.set(true);
            }
            if(a.getName().equals("name5")) {
                found3.set(true);
            }

        });

        assertTrue("Admins missing", found1.get() && found2.get() && found3.get());


    }

    @Test
    public void escape() {
        String config = "name : 'e\\\\s\\\\\\'cap\\#e' # mandatory field. Presents a possible escape sequence\n" +
                "organizer : 'Group 17' # mandatory field\n" +
                "endTime : '1607731200' # A unix timestamp. This field is mandatory\n" +
                "url : 'http://localhost' # the url at which the conference is hosted \n" +
                "admin : 'name:email:group:residence:function' ";

        Conference c = ConfigParser.parseConfigFile(config);
        assertEquals("Wrong name", "e\\s\\'cap#e", c.getName());

    }

}
