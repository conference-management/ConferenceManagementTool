package main;


import communication.CommunicationManager;
import communication.CommunicationManagerFactory;
import user.Admin;
import user.Attendee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    static Conference conf;

    public static void main(String[] args) {
        parseArguments(args);
    }

    /**
     * Parse Arguments from main method and create a clean or persistent Conference
     *
     * @param args
     */
    private static void parseArguments(String[] args) {
        if(args.length != 2) {
            printUsage();
        } else {
            if(!args[0].toLowerCase().equals("test") && !args[0].toLowerCase().equals("start")) {
                printUsage();
            } else if(args[0].toLowerCase().equals("test")) {
                switch(args[1]) {
                    case "normal":
                        startNormalConference(true);
                        break;
                    case "normal-persistent":
                        startNormalConference(false);
                        break;
                }
            } else {
                try {
                    File f = new File(args[1]);
                    FileInputStream fis = new FileInputStream(f);
                    byte[] data = new byte[(int) f.length()];
                    fis.read(data);
                    fis.close();
                    String str = new String(data, StandardCharsets.UTF_8);
                    conf = ConfigParser.parseConfigFile(str);
                    CommunicationManager w = new CommunicationManagerFactory(conf).create();
                    w.start();
                    char c = (char) System.in.read();
                    while(c != 'q') {
                        c = (char) System.in.read();
                    }
                    w.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage : 'test' <testID> | 'start' <path-to-config-file>");
        System.out.println("Valid test ids : 'normal', 'normal-persistent'");
        System.exit(0);
    }

    /**
     * Starts a conference with a single admin (username and password 'admin')
     * and a single user (username and password 'user')
     **/
    private static void startNormalConference(boolean clean) {
        conf = new Conference(clean);
        if(conf.getAllAdmins().isEmpty()) {
            conf.addAdmin(new Admin("Admin Administrator", "admin@administrator", "admin", "NoGroup", "AdminAllee", "onlyAdmin"), "admin");
            conf.addAttendee(new Attendee("Test User", "user@test.de", "user", "Tester", "Testerhood", "TestUser"), "user");
        }

        conf.generateAllQRCodes();
        conf.generateAllQRCodes();
        conf.getAllQrCodes();

        CommunicationManager w = new CommunicationManagerFactory(conf).enableDebugging().create();
        w.start();
        System.out.println("Press 'q' to close the server");
        try {
            char c = (char) System.in.read();
            while(c != 'q') {
                c = (char) System.in.read();
            }
            w.stop();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
