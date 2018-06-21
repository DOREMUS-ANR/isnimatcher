import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class ISNI {
  private static final String ISNI_API = "http://isni.oclc.nl/sru/DB=1.2/";


  public static ISNIRecord getRecord(String name) throws IOException {
    if (name.isEmpty()) throw new RuntimeException("Empty name for querying ISNI");

    try {
      HttpRequest request = Unirest.get(ISNI_API)
              .queryString("query", "pica.nw = " + name)
              .queryString("operation", "searchRetrieve")
              .queryString("recordSchema", "isni-b")
              .queryString("maximumRecords", "1");

      // System.out.println(request.getUrl());

      HttpResponse<String> response = request.asString();

      if (response.getStatus() != 200) {
        throw new IOException(response.getStatus() + " | " + response.getStatusText());
      }

      String body = response.getBody();
      if (body.isEmpty()) return null;

      return ISNIRecord.fromString(body);
    } catch (UnirestException e) {
      throw new IOException(e.getMessage());
    } catch (JAXBException e) {
      System.out.println(e);
      throw new RuntimeException(e.getMessage());
    }
  }


}
