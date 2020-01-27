package database;

import org.junit.Test;
import user.Admin;
import user.Attendee;
import user.DB_UserManagement;
import user.LoginResponse;
import user.TokenResponse;
import user.User;
import utils.Pair;

import java.util.List;

import static org.junit.Assert.*;

public class UserManagementTests extends DatabaseTests {

    @Test
    public void validAttendeeCredentials() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.checkLogin("MaxMustermann", "123");

        //Add right Attendee
        assertTrue("Attendee couldn't be added", dbGen.addAttendee(max, "1234", "42"));
        //Check if Token 42 is valid for an user
        assertEquals(TokenResponse.ValidAttendee, dbGen.checkToken("42"));
        //Check if Attendee is correctly stored in Db
        assertEquals(dbGen.getAttendeeData(max.getID()).getName(), max.getName());
        assertEquals(dbGen.getAttendeeData(max.getID()).getEmail(), max.getEmail());
        assertEquals(dbGen.getAttendeeData(max.getID()).getUserName(), max.getUserName());
        assertEquals(dbGen.getAttendeeData(max.getID()).getGroup(), max.getGroup());
        assertEquals(dbGen.getAttendeeData(max.getID()).getResidence(), max.getResidence());
        assertEquals(dbGen.getAttendeeData(max.getID()).getFunction(), max.getFunction());
        assertEquals(dbGen.getAttendeeData(max.getID()).getID(), max.getID());
    }

    @Test
    public void validAdminCredentials() {
        Admin stephan = new Admin("Stephan Mustermann", "email@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        //Add right Admin & Check if Admin Token is Valid
        assertTrue("Admin couldn't be added", dbGen.addAdmin(stephan, "rue1831978", "token"));
        assertEquals(LoginResponse.Valid, dbGen.checkLogin("AlmightyStephan", "rue1831978").first());
        assertEquals(TokenResponse.ValidAdmin, dbGen.checkToken("token"));

        //Check if Attendee is correctly stored in Db
        assertEquals(dbGen.getAdminData(stephan.getID()).getName(), stephan.getName());
        assertEquals(dbGen.getAdminData(stephan.getID()).getEmail(), stephan.getEmail());
        assertEquals(dbGen.getAdminData(stephan.getID()).getUserName(), stephan.getUserName());
        assertEquals(dbGen.getAdminData(stephan.getID()).getGroup(), stephan.getGroup());
        assertEquals(dbGen.getAdminData(stephan.getID()).getFunction(), stephan.getFunction());
        assertEquals(dbGen.getAdminData(stephan.getID()).getResidence(), stephan.getResidence());
        assertEquals(dbGen.getAdminData(stephan.getID()).getID(), stephan.getID());
    }

    @Test
    public void removeUser() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        Admin stephan2 = new Admin("Stephan2 Mustermann", "enmail2@email.muster", "Almi2ghtyStephan", "project23", "Winterwunderland", "group member", 2);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");
        List<Integer> ids = dbGen.getIDs();
        //Check if getIDs get the right ids
        assertTrue(ids.get(0) == max.getID());
        assertTrue(ids.get(1) == stephan.getID());
        int id = dbGen.tokenToID("42");
        // Check Removing Right Attendee and Admin
        assertTrue(dbGen.removeUser(id));
        int id1 = dbGen.tokenToID("9999");
        assertTrue(dbGen.removeUser(id1));
        // Check Removing false User
        assertFalse(dbGen.removeUser(9));
        //Check Removing all admins
        assertTrue(dbGen.addAdmin(stephan, "1111", "9999"));
        assertTrue(dbGen.addAdmin(stephan2, "2222", "9344999"));
        id = dbGen.tokenToID("9999");
        assertTrue(dbGen.removeAllAdmins());
        assertNull(dbGen.getAdminData(id));
    }

    @Test
    public void logoutUser() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");
        // Check token to ID & Logout Attendee and Admin
        int id = dbGen.tokenToID("42");
        assertTrue(dbGen.logoutUser(id, "klkh", "kjhlk"));
        int id1 = dbGen.tokenToID("9999");
        assertTrue(dbGen.logoutUser(id1, "134676", "kjhll"));
    }

    @Test
    public void getCorrectPasswords() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");
        List<Pair<User, String>> pw = dbGen.getAllPasswords();
        // Check Correctness of Passwords
        assertEquals(pw.get(0).second(), "1234");
        assertEquals(pw.get(0).first().getID(), max.getID());
        assertEquals(pw.get(1).second(), "1111");
        assertEquals(pw.get(1).first().getID(), stephan.getID());
    }

    @Test
    public void newTokenToUser() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");

        // Check Storing new Token to Attendee and Admin & if old Token does not exist
        assertTrue(dbGen.storeNewToken(0, "11"));
        assertTrue(dbGen.storeNewToken(1, "22"));
        assertEquals(dbGen.tokenToID("11"), 0);
        assertEquals(dbGen.tokenToID("22"), 1);
        try {
            int i = dbGen.tokenToID("42");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void newPasswordToUser() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");

        // Check Storing new Password to Attendee and Admin
        assertTrue(dbGen.storeNewPassword(0, "121"));
        assertTrue(dbGen.storeNewPassword(1, "2222"));
        assertEquals(dbGen.getAllPasswords().get(0).second(), "121");
        assertEquals(dbGen.getAllPasswords().get(1).second(), "2222");
    }

    @Test
    public void existUserName() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");
        // CHeck if a UserName exist/ dont exist in the Db
        assertTrue(dbGen.userNameAlreadyUsed(max.getUserName()));
        assertTrue(dbGen.userNameAlreadyUsed(stephan.getUserName()));
        assertFalse(dbGen.userNameAlreadyUsed("username"));
    }

    @Test
    public void severalAttendeesAndAdmins() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Attendee stephan = new Attendee("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        //Test creating and Adding more Admins and Attendees
        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAttendee(stephan, "1111", "9999");
        List<Attendee> attendees = dbGen.getAllAttendees();
        assertEquals(attendees.get(0).getID(), max.getID());
        assertEquals(attendees.get(1).getID(), stephan.getID());

        Admin Alex = new Admin("Alex Mustermann", "almail@email.muster", "Alex.Mustermann", "LOL", "Place", "Straßenkehrer", 2);
        Admin Kamran = new Admin("Kamran Mustermann", "kamnmail@email.muster", "Kamran", "project23", "Winterwdfgunderland", "group member", 3);
        dbGen.addAdmin(Alex, "345", "3456");
        dbGen.addAdmin(Kamran, "2345", "3245");
        List<Admin> admins = dbGen.getAllAdmins();
        assertEquals(admins.get(0).getID(), Alex.getID());
        assertEquals(admins.get(1).getID(), Kamran.getID());

        assertEquals(admins.get(0).getEmail(), Alex.getEmail());
        assertEquals(admins.get(0).getUserName(), Alex.getUserName());

        assertEquals(attendees.get(0).getEmail(), max.getEmail());
        assertEquals(attendees.get(0).getUserName(), max.getUserName());
    }

    @Test
    public void editUser() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");

        // Check editing an Attendee or an Admin works fine
        max.setName("MusterMax");
        dbGen.editAttendee(max);
        assertEquals(dbGen.getAttendeeData(0).getName(), max.getName());
        stephan.setEmail("test@mail.de");
        dbGen.editAdmin(stephan);
        assertEquals(dbGen.getAdminData(1).getEmail(), stephan.getEmail());
    }

    @Test
    public void diffrentCheckLoginAndTokenCases() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAdmin(stephan, "1111", "9999");

        // CHeck if the right LoginResponse / TokenResponse Value
        assertEquals(LoginResponse.WrongPassword, dbGen.checkLogin("AlmightyStephan", "rue1831ffsfdd978").first());
        assertEquals(TokenResponse.TokenDoesNotExist, dbGen.checkToken("token"));
        assertEquals(LoginResponse.UserDoesNotExist, dbGen.checkLogin("Stephan", "rue18dseggh31978").first());
        //assertEquals(TokenResponse., dbGen.checkToken("token"));;
        assertEquals(LoginResponse.Valid, dbGen.checkLogin("AlmightyStephan", "1111").first());
        assertEquals(LoginResponse.Valid, dbGen.checkLogin("AlmightyStephan", "1111").first());

        assertEquals(LoginResponse.Valid, dbGen.checkLogin("Max.Mustermann", "1234").first());
        assertEquals(LoginResponse.AccountAlreadyInUse, dbGen.checkLogin("Max.Mustermann", "1234").first());
    }

    @Test
    public void getallGroupsFromDb() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Attendee herbert = new Attendee("herbert Mustermann", "herbert@email.muster", "herbert.Mustermann", "LOL", "Place2", "Straßenkehrer2", 2);
        Attendee alex = new Attendee("alex Mustermann", "alex@email.muster", "alex.Mustermann", "Groupe23", "Place43", "Tester", 3);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "group member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAttendee(herbert, "123", "4245");
        dbGen.addAttendee(alex, "1653", "42qwe");
        dbGen.addAdmin(stephan, "1111", "9999");

        // Check if we can load all different groups from the Db without duplications
        List<String> groups = dbGen.getAllGroupsFromUser();
        assertEquals(3, groups.size());
        assertTrue(groups.contains(max.getGroup()));
        assertTrue(groups.contains(stephan.getGroup()));
        assertTrue(groups.contains(alex.getGroup()));
    }

    @Test
    public void getRightPresentValue() {
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Attendee herbert = new Attendee("herbert Mustermann", "herbert@email.muster", "herbert.Mustermann", "LOL", "Place2", "Straßenkehrer2", 2);
        Admin alex = new Admin("alex Mustermann", "alex@email.muster", "alex.Mustermann", "Groupe23", "Differten43", "Tester", 3);
        Admin stephan = new Admin("Stephan Mustermann", "enmail@email.muster", "AlmightyStephan", "project23", "Winterwunderland", "member", 1);
        DB_UserManagement dbGen = this.getGeneralUserDB();

        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAttendee(herbert, "123", "4245");
        dbGen.addAdmin(alex, "1653", "42qwe");
        dbGen.addAdmin(stephan, "1111", "9999");

        dbGen.checkLogin(max.getUserName(), "1234");
        dbGen.checkLogin(herbert.getUserName(), "1234");
        dbGen.checkLogin(alex.getUserName(), "1653");
        dbGen.checkLogin(stephan.getUserName(), "1234");

        assertFalse(dbGen.getAttendeeData(max.getID()).isPresent());
        assertFalse(dbGen.getAllAttendees().get(1).isPresent());
        assertTrue(dbGen.getAdminData(alex.getID()).isPresent());
        assertFalse(dbGen.getAllAdmins().get(0).isPresent());

        dbGen.setPresentValueofUser(max.getUserName(), false);
        assertFalse(dbGen.getAttendeeData(max.getID()).isPresent());
        dbGen.setPresentValueofUser(herbert.getUserName(), true);
        assertTrue(dbGen.getAttendeeData(herbert.getID()).isPresent());
    }

}
