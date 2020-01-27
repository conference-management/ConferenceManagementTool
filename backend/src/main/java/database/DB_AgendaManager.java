package database;

import agenda.Agenda;
import agenda.AgendaObservable;
import agenda.DB_AgendaManagement;
import utils.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("checkstyle:typename")
public class DB_AgendaManager extends DB_Controller implements DB_AgendaManagement {

    private static String table = "agenda";

    public DB_AgendaManager(String url) {
        super(url);
    }

    /**
     * Initializes the agenda tables for the database
     */
    @Override
    protected void init() {
        String agendaTable = "CREATE TABLE IF NOT EXISTS agenda (\n"
                + "     topicPosition TEXT NOT NULL,\n"
                + "     topicName TEXT NOT NULL\n"
                + ");";
        Connection connection = openConnection();
        try {
            connection.createStatement().execute(agendaTable);
        } catch (SQLException e) {
            System.err.println("Database initialization failed!");
            System.err.println(e.getMessage());
        }
        closeConnection(connection);
    }

    /**
     * Observer for the Agenda. Updates the Agenda when the {@link AgendaObservable} changes.
     *
     * @param a The new {@link Agenda}.
     *
     * @return True, iff the agenda was updates properly.
     */
    @Override
    public boolean update(Agenda a) {
        Connection connection = this.openConnection();
        String sqlstatement = "DELETE FROM agenda";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement)) {
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println("An exception occurred while first deleting the agenda.");
            System.err.println(ex.getMessage());
            return false;
        } finally {
            this.closeConnection(connection);
        }
        Connection connection2 = this.openConnection();
        List<String> preOrder = a.preOrder();
        sqlstatement = "INSERT INTO agenda(topicPosition, topicName) VALUES(?,?)";
        try {
            for(String s : preOrder) {
                try (PreparedStatement stmt = connection2.prepareStatement(sqlstatement)) {
                    String name = a.getTopicFromPreorderString(s).getName();
                    stmt.setString(1, s);
                    stmt.setString(2, name);
                    stmt.execute();
                }
            }
        } catch (SQLException ex) {
            System.err.println("An exception occurred while updating the agenda.");
            System.err.println(ex.getMessage());
            return false;
        } finally {
            this.closeConnection(connection2);
        }
        return true;
    }

    /**
     * @return the {@link Agenda} object reconstructed from the database.
     */
    @Override
    public Agenda getAgenda() {
        Connection connection = this.openConnection();
        String sqlstatement = "SELECT * FROM agenda";
        try (PreparedStatement stmt = connection.prepareStatement(sqlstatement);
             ResultSet agenda = stmt.executeQuery()) {
            List<Pair<List<Integer>, String>> tops = new LinkedList<>();
            while(agenda.next()) {
                String ord = agenda.getString("topicPosition");
                List<Integer> order = new LinkedList<Integer>();
                Arrays.asList(ord.split("\\.")).forEach(s -> order.add(Integer.parseInt(s)));
                String name = agenda.getString("topicName");
                tops.add(new Pair<>(order, name));
            }
            return new Agenda(tops);
        } catch (SQLException ex) {
            System.err.println("An error occurred while reconstructing the agenda.");
            System.err.println(ex.getMessage());
            return null;
        } finally {
            this.closeConnection(connection);
        }
    }
}
