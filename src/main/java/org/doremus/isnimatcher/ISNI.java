package org.doremus.isnimatcher;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ISNI {
  private static final String ISNI_API = "http://isni.oclc.nl/sru/DB=1.2/";
  private static final String ISNI_BASE = "http://isni.org/isni/";
  private final static String START_TAG = "<ISNIAssigned>";
  private final static String END_TAG = "</ISNIAssigned>";


  public static ISNIRecord get(String id) throws IOException {
    if (id.startsWith(ISNI_BASE))
      id = id.replace(ISNI_BASE, "");

    String query = "pica.isn = " + id;
    List<ISNIRecord> records = performQuery(query, 1);
    if (records == null) return null;
    return records.get(0);
  }

  public static ISNIRecord search(String name) throws IOException {
    return search(name, null);
  }

  public static ISNIRecord search(String name, String date) throws IOException {
    if (name.isEmpty()) throw new RuntimeException("Empty name for querying org.doremus.isnimatcher.ISNI");

    String query = "pica.nw = " + name.replaceAll("\\.", " ");
    int n = date == null ? 1 : 100;

    List<ISNIRecord> records = performQuery(query, n);
    if (records == null) return null;

    if (date == null) return records.get(0);

    return records.stream()
            .filter(r -> date.equals(r.getBirthYear()))
            .findFirst()
            .orElse(null);
  }


  private static List<ISNIRecord> performQuery(String query, int n) throws IOException {
    try {
      HttpRequest request = Unirest.get(ISNI_API)
              .queryString("query", query)
              .queryString("operation", "searchRetrieve")
              .queryString("recordSchema", "isni-b")
              .queryString("maximumRecords", 1);

      // System.out.println(request.getUrl());

      HttpResponse<String> response = request.asString();

      if (response.getStatus() != 200)
        throw new IOException(response.getStatus() + " | " + response.getStatusText());

      String body = response.getBody();
      List<ISNIRecord> records = new ArrayList<>();
      for (String s : splitBody(body)) {
        try {
          ISNIRecord isniRecord = ISNIRecord.fromString(s);
          if (isniRecord != null) records.add(isniRecord);
        } catch (JAXBException e) {
          e.printStackTrace();
        }
      }

      if (records.size() == 0) return null;
      return records;
    } catch (UnirestException e) {
      throw new IOException(e.getMessage());
    }
  }

  private static List<String> splitBody(String body) {
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


}
