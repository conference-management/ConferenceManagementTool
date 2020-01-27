package database;

import org.junit.Before;
import org.junit.Test;
import voting.AnonymousVotingOption;
import voting.DB_VotingManagement;
import voting.NamedVotingOption;
import voting.Voting;
import voting.VotingOption;
import voting.VotingStatus;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class VotingManagementTests extends DatabaseTests {

    DB_VotingManagement votM;

    @Before
    public void initVotingManger() {
        votM = this.getVotingDB();
    }

    @Test
    public void checkSimpleVotings() {
        List<VotingOption> nl = new LinkedList<>();
        List<VotingOption> al = new LinkedList<>();

        NamedVotingOption nv1 = new NamedVotingOption(0, "foo");
        NamedVotingOption nv2 = new NamedVotingOption(1, "bar");
        NamedVotingOption nv3 = new NamedVotingOption(2, "foobar");

        AnonymousVotingOption av1 = new AnonymousVotingOption(0, "Anonymous foo");
        AnonymousVotingOption av2 = new AnonymousVotingOption(1, "Anonymous bar");
        AnonymousVotingOption av3 = new AnonymousVotingOption(2, "Anonymous foobar");

        nl.add(nv1);
        nl.add(nv2);
        nl.add(nv3);
        al.add(av1);
        al.add(av2);
        al.add(av3);

        Voting named = new Voting(nl, "Should we have a named voting?", true, 30);

        Voting anon = new Voting(al, "Should we keep it anonymous?", false, 30);

        nv1.setParent(named);
        nv2.setParent(named);
        nv3.setParent(named);
        av1.setParent(anon);
        av2.setParent(anon);
        av2.setParent(anon);

        named.startVote();
        anon.startVote();

        named.addVote(0, 0, "Alex");
        named.addVote(0, 1, "Jörg");
        named.addVote(0, 2, "simon");
        named.addVote(1, 4, "bernd");
        named.addVote(1, 7, "luk");

        anon.addVote(0, 1, "Jörg");
        anon.addVote(1, 2, "simon");

        named.endVote();
        anon.endVote();


        votM.addVoting(named);
        votM.addVoting(anon);

        Voting resn = votM.getVoting(named.getID());
        Voting resa = votM.getVoting(anon.getID());

        assertEquals("Different question than before.", named.getQuestion(), resn.getQuestion());
        assertEquals("Different question than before.", anon.getQuestion(), resa.getQuestion());

        assertEquals("Different amount of options than before.", 3, resn.getOptions().size());
        assertEquals("Different amount of options than before.", 3, resa.getOptions().size());

        assertEquals("Different name for VotingOptions than before",
                resn.getOptions().get(0).getName(), nv1.getName());
        assertEquals("Different name for VotingOptions than before",
                resn.getOptions().get(1).getName(), nv2.getName());
        assertEquals("Different name for VotingOptions than before",
                resn.getOptions().get(2).getName(), nv3.getName());
        assertEquals("Different name for VotingOptions than before",
                resa.getOptions().get(0).getName(), av1.getName());
        assertEquals("Different name for VotingOptions than before",
                resa.getOptions().get(1).getName(), av2.getName());
        assertEquals("Different name for VotingOptions than before",
                resa.getOptions().get(2).getName(), av3.getName());

        assertEquals("Different result for VotingOptions than before",
                resn.getOptions().get(0).getCurrentResult(), nv1.getCurrentResult());
        assertEquals("Different result for VotingOptions than before",
                resn.getOptions().get(1).getCurrentResult(), nv2.getCurrentResult());
        assertEquals("Different result for VotingOptions than before",
                resn.getOptions().get(2).getCurrentResult(), nv3.getCurrentResult());
        assertEquals("Different result for VotingOptions than before",
                resa.getOptions().get(0).getCurrentResult(), anon.getOptions().get(0).getCurrentResult());
        assertEquals("Different result for VotingOptions than before",
                resa.getOptions().get(1).getCurrentResult(), anon.getOptions().get(1).getCurrentResult());
        assertEquals("Different result for VotingOptions than before",
                resa.getOptions().get(2).getCurrentResult(), anon.getOptions().get(2).getCurrentResult());

        List<Integer> resN1 = resn.getOptions().get(0).getVoters();
        List<Integer> resN2 = resn.getOptions().get(1).getVoters();
        List<Integer> resN3 = resn.getOptions().get(2).getVoters();
        List<Integer> votersN1 = named.getOptions().get(0).getVoters();
        List<Integer> votersN2 = named.getOptions().get(1).getVoters();
        List<Integer> votersN3 = named.getOptions().get(2).getVoters();

        assertTrue("Wrong voters were saved.",
                resN1.containsAll(votersN1) && votersN1.containsAll(resN1));
        assertTrue("Wrong voters were saved.",
                resN2.containsAll(votersN2) && votersN2.containsAll(resN2));
        assertTrue("Wrong voters were saved.",
                resN3.containsAll(votersN3) && votersN3.containsAll(resN3));
    }

    @Test
    public void addOpenVoting() {
        Voting vot = new Voting(new LinkedList<>(), "Should we allow open votings in the database?", true, 30);

        assertEquals("Voting has incorrect status after creation.", VotingStatus.Created, vot.getStatus());
        votM.update(vot);
        assertEquals("Update method should do nothing because the voting has not yet started.",
                0, votM.getVotings().size());

        vot.startVote();
        assertEquals("Voting has incorrect status after start.", VotingStatus.Running, vot.getStatus());
        votM.update(vot);
        assertEquals("Update method should do nothing because the voting is not yet finished.",
                0, votM.getVotings().size());

        vot.endVote();
        assertEquals("Voting has incorrect status after start.", VotingStatus.Closed, vot.getStatus());
        votM.update(vot);
        assertEquals("Update method should succeed because the voting has now ended.",
                1, votM.getVotings().size());

        Voting res = votM.getVoting(vot.getID());
        assertEquals("Reconstructed voting has a different ID.", vot.getID(), res.getID());
        assertEquals("Reconstructed voting has a different question.", vot.getQuestion(), res.getQuestion());
        assertEquals("Reconstructed voting has a different amount of options.",
                vot.getOptions().size(), vot.getOptions().size());
        assertEquals("Reconstructed Voting Status should be closed.", VotingStatus.Closed, res.getStatus());

        assertFalse("A voting with this ID already exists. ", votM.addVoting(res));
        assertFalse("A voting with this ID already exists. ", votM.addVoting(vot));

        assertEquals("There were too many votings in the database.", 1, votM.getVotings().size());
    }
}
