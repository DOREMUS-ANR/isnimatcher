package org.doremus.isnimatcher;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ISNI {
    private final static String ISNI_API = "http://isni.oclc.nl/sru/DB=1.2/";
    private final static String ISNI_BASE = "https://isni.org/isni/";
    private final static String START_TAG = "<ISNIAssigned>";
    private final static String END_TAG = "</ISNIAssigned>";

    private final static Pattern FLEX_DATE_PATTERN = Pattern.compile("[\\d?.]{0,4}");

    private static boolean debug = false;
    private static boolean bestViafBehavior = false;


    public static ISNIRecord get(String id) throws IOException {
        if (id.startsWith(ISNI_BASE))
            id = id.replace(ISNI_BASE, "");

        String query = "pica.isn = \"" + id + "\"";
        List<ISNIRecord> records = performQuery(query, 1);
        if (records == null) return null;
        return records.get(0);
    }

    public static ISNIRecord search(String name) throws IOException {
        return search(name, null);
    }

    public static ISNIRecord search(String name, String date) throws IOException {
        return search(null, name, date);
    }

    public static ISNIRecord search(String forename, String surname, String date) throws IOException {
        if (surname.isEmpty()) throw new RuntimeException("Empty surname for querying org.doremus.isnimatcher.ISNI");

        String name = surname;
        if (forename != null) name += ", " + forename;

        String query = "pica.nw = " + name.replaceAll("\\.", " ");

        int n = 10;

        List<ISNIRecord> records = performQuery(query, n);
        if (records == null) return null;

        if (debug) {
            System.out.println(records.size() + " records");
            for (ISNIRecord r : records) r.save("test/" + r.id + ".xml");
        }

        Stream<ISNIRecord> str = records.stream();

        if (bestViafBehavior)
            str = str.sorted(Comparator.comparingInt(ISNIRecord::getLinksNumber).reversed());

        str = str.filter(r -> r.hasName(forename, surname, true))
                .filter(r -> dateMatch(cleanDate(date), r.getBirthYear()));



        return str.findFirst() // tolerance for wrong name but not for wrong date
                .orElse(date == null ? records.get(0) : null);
    }

//    private static ISNIRecord getBestViaf(ISNIRecord base, List<ISNIRecord> records) {
//        // Among the records with the same VIAF id, return the one with more links
//        if (base == null) return null;
//        String viaf = base.getViafURI();
//        System.out.println(viaf);
//        if (viaf == null) return base;
//
//        return records.stream()
//                .filter(r -> viaf.equals(r.getViafURI()))
//                .max(Comparator.comparingInt(ISNIRecord::getLinksNumber))
//                .orElse(base);
//    }

    private static boolean dateMatch(String expected, String actual) {
        if (expected == null) return true;
        if (actual == null || actual.isEmpty()) return false;
        actual = cleanDate(actual);
        return actual.matches(expected.replaceAll("\\?", "."));
    }

    private static String cleanDate(String date) {
        if (date == null) return null;
        // Examples: nach 1980, 1947?, 182?
        Matcher m = FLEX_DATE_PATTERN.matcher(date);
        if (!m.find()) return "";
        else return m.group().replaceAll("\\?", ".");
    }

    private static List<ISNIRecord> performQuery(String query, int n) throws IOException {
        if (n < 1) n = 1;
        try {
            HttpRequest request = Unirest.get(ISNI_API)
                    .queryString("query", query)
                    .queryString("operation", "searchRetrieve")
                    .queryString("recordSchema", "isni-b")
                    .queryString("maximumRecords", n);

            if (debug) System.out.println(request.getUrl());

            HttpResponse<String> response = request.asString();

            if (response.getStatus() != 200)
                throw new IOException(response.getStatus() + " | " + response.getStatusText());

            String body = response.getBody();
            List<ISNIRecord> records = new ArrayList<>();
            for (String s : splitBody(body)) {
                ISNIRecord isniRecord = null;
                try {
                    isniRecord = ISNIRecord.fromString(s);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                if (isniRecord != null) records.add(isniRecord);
            }

            if (records.size() == 0) return null;
            return records;
        } catch (UnirestException e) {
            throw new IOException(e.getMessage());
        }
    }

    static List<String> splitBody(String body) {
        List<String> records = new ArrayList<>();
        if (body.isEmpty()) return records;

        while (true) {
            int start = body.indexOf(START_TAG);
            int end = body.indexOf(END_TAG) + END_TAG.length();

            // no results
            if (start == -1) break;

            String record = body.substring(start, end);
            records.add(record);
            body = body.replace(record, "");
        }

        return records;
    }


    public static void setDebug(boolean debug) {
        ISNI.debug = debug;
    }

    public static void setBestViafBehavior(boolean bestViafBehavior) {
        ISNI.bestViafBehavior = bestViafBehavior;
    }
}
