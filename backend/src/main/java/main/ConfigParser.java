package main;

import document.Document;
import request.Request;
import user.Admin;
import voting.Voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigParser {

    static String[] validKeys = {"admin", "starttime", "endtime", "name", "organizer", "databasepath", "documentspath", "url"};

    public static Conference parseConfigFile(String s) {


        Map<String, String> map = new HashMap<>();
        List<List<String>> admins = new ArrayList<List<String>>();
        AtomicInteger i = new AtomicInteger(0);

        int lineNumber = 1;
        while(i.get() < s.length()) {
            parseLine(s, i, map, admins, lineNumber, i.get());
            lineNumber++;
        }

        for(String mandKey : new String[]{"name", "organizer", "endtime", "url"}) {
            if(!map.containsKey(mandKey)) {
                throw new IllegalArgumentException("Missing key " + mandKey);
            }
        }

        if(admins.isEmpty()) {
            throw new IllegalArgumentException("Having at least one admin is mandatory");
        }

        Conference conf = new Conference
                (map.get("name"),
                        map.get("organizer"),
                        Long.parseLong(map.getOrDefault("starttime", "" + (System.currentTimeMillis() / 1000))),
                        Long.parseLong(map.get("endtime")),
                        new HashMap<Integer, Admin>(),
                        new HashMap<Integer, Voting>(),
                        new HashMap<String, Document>(),
                        map.getOrDefault("documentspath", "./docs"),
                        new HashMap<Integer, Request>(),
                        null,
                        map.getOrDefault("databasepath", "./db/conference.db"),
                        map.get("url"),
                        false,
                        false
                );

        List<Admin> allAdmins = conf.getAllAdmins();
        conf.deleteAllAdmins();

        admins.forEach(a -> {
            Admin admin = new Admin(a.get(0), a.get(1), conf.getFreeUserName(a.get(0)), a.get(2), a.get(3), a.get(4));
            conf.addAdmin(admin);
            System.out.println(admin.toString());
            System.out.println("password: " + conf.getUserPassword(admin.getID()).second() + "\n");
        });

        conf.generateAllQRCodes();

        return conf;

    }

    private static void parseLine(String s, AtomicInteger i, Map<String, String> fields, List<List<String>> admins, int lineNumber, int indexAtLineStart) {
        skipWhitespace(s, i);

        if(s.charAt(i.get()) == '#') { //ignore comments
            parseComment(s, i);
            skipWhitespace(s, i);
            return;
        }

        int aux = i.get();
        String key = parseKey(s, i, lineNumber, (i.get() - aux));
        boolean contains = false;
        for(String validKey : validKeys) {
            if(validKey.equals(key)) {
                contains = true;
                break;
            }
        }
        if(!contains || fields.containsKey(key) || key.isBlank()) {
            throw new IllegalArgumentException("Illegal key '" + key + "' at " + lineNumber + ":" + (aux - indexAtLineStart));
        }
        skipWhitespace(s, i);

        if(s.charAt(i.get()) != '\'') {
            throw new IllegalArgumentException("Value does not start with ''' at " + lineNumber + ":" + (i.get() - indexAtLineStart));
        }
        i.incrementAndGet();
        if(key.equals("admin")) {
            admins.add(parseAdmin(s, i));
            skipWhitespace(s, i);
            if(i.get() < s.length() && s.charAt(i.get()) == '#') {
                parseComment(s, i);
            }

        } else {
            String value = parseNonAdminValue(s, i, '\'');
            skipWhitespace(s, i);
            if(i.get() < s.length() && s.charAt(i.get()) == '#') {
                parseComment(s, i);
            }

            fields.put(key, value);
        }

    }

    private static List<String> parseAdmin(String s, AtomicInteger i) {
        List<String> res = new ArrayList<String>();
        res.add(parseNonAdminValue(s, i, ':'));
        res.add(parseNonAdminValue(s, i, ':'));
        res.add(parseNonAdminValue(s, i, ':'));
        res.add(parseNonAdminValue(s, i, ':'));
        res.add(parseNonAdminValue(s, i, '\''));

        return res;
    }

    private static String parseKey(String s, AtomicInteger i, int lineNumber, int position) {
        StringBuilder sb = new StringBuilder();
        for(; i.get() < s.length(); i.incrementAndGet()) {
            if(Character.isAlphabetic(s.charAt(i.get()))) {
                sb.append(s.charAt(i.get()));
            } else if(s.charAt(i.get()) == ':') {
                i.incrementAndGet();
                break;
            } else if(!Character.isWhitespace(s.charAt(i.get()))) {
                throw new IllegalArgumentException("Malformed key at " + lineNumber + ":" + position);
            }
        }
        return sb.toString().toLowerCase();
    }

    private static String parseNonAdminValue(String s, AtomicInteger i, char endAt) {
        StringBuilder sb = new StringBuilder("");
        for(; i.get() < s.length(); i.incrementAndGet()) {
            if(s.charAt(i.get()) == '\\') {
                i.incrementAndGet();
            } else if(s.charAt(i.get()) == endAt) {
                i.getAndIncrement();
                return sb.toString();
            } else if(s.charAt(i.get()) == '#') {
                parseComment(s, i);
                break;
            }
            sb.append(s.charAt(i.get()));
        }
        throw new IndexOutOfBoundsException("File ended abruptly");

    }

    private static void parseComment(String s, AtomicInteger i) {
        while(i.get() < s.length() && s.charAt(i.get()) != '\n') {
            i.incrementAndGet();
        }
        i.incrementAndGet();
    }

    private static void skipWhitespace(String s, AtomicInteger i) {
        while(i.get() < s.length() && Character.isWhitespace(s.charAt(i.get()))) {
            i.incrementAndGet();
        }
    }

}
