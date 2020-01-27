package communication.connctiontests;

import communication.CommunicationManager;
import communication.CommunicationManagerFactory;
import communication.packets.request.GetAgendaRequestPacket;
import communication.packets.request.LoginRequestPacket;
import main.Conference;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import user.Attendee;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionTests {

    private static AtomicInteger portCounter = new AtomicInteger(10000);
    private static int port = 17699;
    private static int maxClientConnections = 10;
    private static int stessTestAmount = 1000;
    private static CommunicationManager communicationManager;
    private static Conference conference;

    @BeforeClass
    public static void beforeClass() {
        port = portCounter.getAndIncrement();
        conference = new Conference(true);
        for(int i = 0; i < stessTestAmount; i++) {
            Attendee attendee = new Attendee("user", "user" + i, "user" + i, "user", "user", "");
            conference.addAttendee(attendee, "login" + i);
        }
        CommunicationManagerFactory factory = new CommunicationManagerFactory(conference).setPort(port).ignoreSecurityAlerts().setMaxUserConnections(maxClientConnections);
        communicationManager = factory.create();
        communicationManager.start();
    }

    @AfterClass
    public static void afterClass() {
        communicationManager.stop();
    }

    @Test(timeout = 10000)
    public void clientMaxConnectionLimitation() throws Exception {
        Attendee attendee = new Attendee("connectsALot", "connectsALot", "connectsALot", "connectsALot", "connectsALot", "connectsALot");
        conference.addAttendee(attendee);
        String password = conference.getUserPassword(attendee.getID()).second();
        String token = conference.login(attendee.getUserName(), password).second().first();
        GetAgendaRequestPacket packet = new GetAgendaRequestPacket();
        packet.setToken(token);
        WebSocketClient[] clients = new WebSocketClient[maxClientConnections];
        for(int i = 0; i < maxClientConnections; i++) {
            WebSocketClient client = new WebSocketClient(port);
            clients[i] = client;
            client.start();
            client.send(packet);
        }
        Thread.sleep(100);
        WebSocketClient client = new WebSocketClient(port);
        client.start();
        client.send(packet);
        Thread.sleep(100);
        Assert.assertFalse(client.isSuccessful());
        Assert.assertFalse(client.isConnected());
        for(int i = 0; i < maxClientConnections; i++) {
            Assert.assertTrue(clients[i].isSuccessful());
            Assert.assertTrue(clients[i].isConnected());
        }
        client.stop();
        for(int i = 0; i < maxClientConnections; i++) {
            clients[i].stop();
        }
    }

    @Test(timeout = 120000)
    public void stressTest() throws Exception {
        int amount = stessTestAmount;
        CountDownLatch latch = new CountDownLatch(amount);
        for(int i = 0; i < amount; i++) {
            WebSocketClient client = new WebSocketClient(port);
            client.start();
            Assert.assertTrue(client.isRunning());
            Assert.assertTrue(client.isConnected());
            LoginRequestPacket packet = new LoginRequestPacket("user" + i, "login" + i);
            client.send(packet);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                Assert.assertTrue("A client did not reveive ananser.", client.isSuccessful());
                client.stop();
                latch.countDown();
            }).start();
        }
        latch.await();
    }

    @Test(timeout = 15000)
    public void clientTimeout() throws Exception {
        WebSocketClient client = new WebSocketClient(port);
        long start = System.currentTimeMillis();
        client.start();
        client.getChannel().closeFuture().sync();
        long end = System.currentTimeMillis();
        long time = end - start;
        client.stop();
        Assert.assertTrue(time > 10000 && time < 12000);
    }

    @Test(timeout = 2000)
    public void maliciousRequest() throws Exception {
        WebSocketClient client = new WebSocketClient(port);
        client.start();
        String maliciousJson = "{\n" +
                "  \"array\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    3\n" +
                "  ],\n" +
                "  \"boolean\": true,\n" +
                "  \"color\": \"#82b92c\",\n" +
                "  \"null\": null,\n" +
                "  \"number\": 123,\n" +
                "  \"object\": {\n" +
                "    \"a\": \"b\",\n" +
                "    \"c\": \"d\",\n" +
                "    \"e\": \"f\"\n" +
                "  },\n" +
                "  \"string\": \"Hello World\"\n" +
                "}";
        client.send(maliciousJson);
        client.getChannel().closeFuture();
        client.stop();
        Assert.assertFalse(client.isSuccessful());
    }
}
