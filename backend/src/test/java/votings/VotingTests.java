package votings;

import main.Conference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import user.Attendee;
import voting.AnonymousVotingOption;
import voting.NamedVotingOption;
import voting.Voting;
import voting.VotingOption;
import voting.VotingStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class VotingTests {

    Conference conf;
    List<VotingOption> standardAnonymousOptions;
    List<VotingOption> standardNamedOptions;
    int userCount = 100;
    int voteDuration = 10;
    Voting annon;
    Voting named;

    @Before
    public void createConference() {
        conf = new Conference(true);
        standardAnonymousOptions = new ArrayList<>();
        standardAnonymousOptions.add(new AnonymousVotingOption(0, "yes"));
        standardAnonymousOptions.add(new AnonymousVotingOption(1, "no"));

        standardNamedOptions = new ArrayList<>();
        standardNamedOptions.add(new NamedVotingOption(0, "yes"));
        standardNamedOptions.add(new NamedVotingOption(1, "no"));

        annon = new Voting(standardAnonymousOptions, "Is this the real life?", false, voteDuration);
        named = new Voting(standardNamedOptions, "Is this the real life?", true, voteDuration);
        System.out.println(conf.getAllAttendees().size());
        for(int i = 0; i < userCount; i++) {
            conf.addAttendee(
                    new Attendee("test" + i, "test" + i, "test" + i, "test" + i, "test" + i, "test" + i, i)
            );
        }
    }

    @Test
    public void addMultipleVotings() {


        conf.addVoting(annon);
        conf.addVoting(named);

        if(!conf.getVotings().contains(annon)) {
            fail("A voting did not get registered");
        }
        if(!conf.getVotings().contains(named)) {
            fail("A voting did not get registered");
        }

        if(annon.getStatus() != VotingStatus.Created) {
            fail("wrong voting status");
        }

        if(!conf.startVoting(annon)) {
            fail("Vote should have been started");
        }
        if(conf.startVoting(named)) {
            fail("Started two votes at once");
        }

        if(named.getStatus() != VotingStatus.Created) {
            fail("wrong voting status");
        }

        if(annon.getStatus() != VotingStatus.Running) {
            fail("wrong voting status");
        }


        try {
            Thread.sleep((voteDuration + 1) * 1000);
        } catch (InterruptedException e) {
            fail("broken test");
        }

        if(annon.getStatus() != VotingStatus.Closed) {
            fail("vote did not get closed");
        }

    }

    @Test
    public void multipleVotesRegular() {
        conf.addVoting(annon);
        conf.addVoting(named);

        conf.startVoting(annon);
        int voterCount = userCount / 2;
        for(int i = 0; i < voterCount; i++) {
            annon.addVote(i % 2, i, "Klaus");
        }
        Assert.assertEquals("Votes should be private at this point", 0, annon.getOptions().get(0).getCurrentResult());
        Assert.assertEquals("Votes should be private at this point", 0, annon.getOptions().get(1).getCurrentResult());

        try {
            Thread.sleep((voteDuration + 1) * 1000);
        } catch (InterruptedException e) {
            fail("broken test");
        }
        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, annon.getOptions().get(0).getCurrentResult());
        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, annon.getOptions().get(1).getCurrentResult());


        conf.startVoting(named);
        for(int i = 0; i < voterCount; i++) {
            named.addVote(i % 2, i, "Gerd");
        }
        Assert.assertEquals("Votes should be private at this point", 0, named.getOptions().get(0).getCurrentResult());
        Assert.assertEquals("Votes should be private at this point", 0, named.getOptions().get(1).getCurrentResult());
        Assert.assertEquals("Votes should be private at this point", 0, named.getOptions().get(0).getVoters().size());
        Assert.assertEquals("Votes should be private at this point", 0, named.getOptions().get(1).getVoters().size());
        try {
            Thread.sleep((voteDuration + 1) * 1000);
        } catch (InterruptedException e) {
            fail("broken test");
        }
        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, named.getOptions().get(0).getCurrentResult());
        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, named.getOptions().get(1).getCurrentResult());

        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, named.getOptions().get(0).getCurrentResult());
        Assert.assertEquals("Wrong vote count", (voterCount + 1) / 2, named.getOptions().get(1).getCurrentResult());

    }

    @Test
    public void multipleVotes() {
        conf.addVoting(annon);
        conf.addVoting(named);

        conf.startVoting(annon);
        annon.addVote(0, 0, "tutor");
        if(annon.addVote(1, 0, "othernames")) {
            fail("Managed to submit multiple votes");
        }

    }

    @Test
    public void multipleRunningVotes() {
        conf.addVoting(annon);
        conf.addVoting(named);

        conf.startVoting(annon);
        if(conf.startVoting(named)) {
            fail("Managed to start multiple votings at the same time");
        }

    }

    @Test
    public void editRunningVoting() {
        conf.addVoting(annon);
        conf.addVoting(named);

        conf.startVoting(annon);
        if(conf.update(annon)) {
            fail("Managed to update a running voting");
        }


    }

}
