package database;

import agenda.Agenda;
import agenda.DB_AgendaManagement;
import agenda.Topic;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AgendaManagementTests extends DatabaseTests {

    DB_AgendaManagement votA;

    @Before
    public void initVotingManger() {
        votA = this.getAgendaDB();
    }

    @Test
    public void updateValidAgendaWithoutPrematureReconstruction() {
        Agenda agenda = new Agenda();
        Topic firstTopic = new Topic("Käsebrot", agenda);
        Topic subTopic11 = new Topic("Käse", firstTopic.getSubTopics());
        Topic subTopic12 = new Topic("Brot", firstTopic.getSubTopics());

        agenda.addTopic(firstTopic, 0);
        firstTopic.getSubTopics().addTopic(subTopic11, 0);
        firstTopic.getSubTopics().addTopic(subTopic12, 1);

        votA.update(agenda);

        Agenda reconstructed = votA.getAgenda();

        Topic refFirstTopic = reconstructed.getTopic(0);
        Topic refFirstSubTopic = reconstructed.getTopic(0).getSubTopics().getTopic(0);
        Topic refSecondSubTopic = reconstructed.getTopic(0).getSubTopics().getTopic(1);

        assertEquals("Topic does not match", firstTopic.getName(), refFirstTopic.getName());
        assertEquals("Topic does not match", subTopic11.getName(), refFirstSubTopic.getName());
        assertEquals("Topic does not match", subTopic12.getName(), refSecondSubTopic.getName());

        assertEquals("toString() of reconstruction differs:", agenda.toString(), reconstructed.toString());

        Topic secondTopic = new Topic("Salamipizza", agenda);
        Topic subTopic21 = new Topic("Salami", secondTopic.getSubTopics());
        Topic subTopic22 = new Topic("Pizza", secondTopic.getSubTopics());

        agenda.addTopic(secondTopic, 1);
        secondTopic.getSubTopics().addTopic(subTopic21, 0);
        secondTopic.getSubTopics().addTopic(subTopic22, 1);

        Agenda reconstructed2 = votA.getAgenda();

        Topic refFirstTopicUA = reconstructed2.getTopic(0);
        Topic refFirstSubTopicUA = reconstructed2.getTopic(0).getSubTopics().getTopic(0);
        Topic refSecondSubTopicUA = reconstructed2.getTopic(0).getSubTopics().getTopic(1);

        assertEquals("Topic does not match", firstTopic.getName(), refFirstTopicUA.getName());
        assertEquals("Topic does not match", subTopic11.getName(), refFirstSubTopicUA.getName());
        assertEquals("Topic does not match", subTopic12.getName(), refSecondSubTopicUA.getName());

        try {
            reconstructed.getTopic(1);
            fail("Local Storage Agenda reconstruction should not be updated.");
        } catch (IllegalArgumentException e) {
            try {
                reconstructed2.getTopic(1);
                fail("Database Agenda should not be updated without calling update().");
            } catch (IllegalArgumentException ex) {

            }
        }

        assertTrue("Error while updating the agende in the database.", votA.update(agenda));

        Agenda fullAgenda = votA.getAgenda();

        Topic full1 = fullAgenda.getTopic(0);
        Topic full11 = fullAgenda.getTopic(0).getSubTopics().getTopic(0);
        Topic full12 = fullAgenda.getTopic(0).getSubTopics().getTopic(1);
        Topic full2 = fullAgenda.getTopic(1);
        Topic full21 = fullAgenda.getTopic(1).getSubTopics().getTopic(0);
        Topic full22 = fullAgenda.getTopic(1).getSubTopics().getTopic(1);

        assertEquals("Topic does not match", firstTopic.getName(), full1.getName());
        assertEquals("Topic does not match", subTopic11.getName(), full11.getName());
        assertEquals("Topic does not match", subTopic12.getName(), full12.getName());
        assertEquals("Topic does not match", secondTopic.getName(), full2.getName());
        assertEquals("Topic does not match", subTopic21.getName(), full21.getName());
        assertEquals("Topic does not match", subTopic22.getName(), full22.getName());

        assertEquals("toString() of reconstruction differs:", agenda.toString(), fullAgenda.toString());
    }

}
